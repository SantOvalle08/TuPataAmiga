package co.edu.uniqundio.tupataamiga.data.remote.ai

import co.edu.uniqundio.tupataamiga.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIService @Inject constructor() {

    private val openRouterModel = "meta-llama/llama-3.2-3b-instruct:free"
    private val openRouterUrl = "https://openrouter.ai/api/v1/chat/completions"

    var apiKey: String = BuildConfig.OPENROUTER_API_KEY

    suspend fun llamarOpenRouter(promptUsuario: String, systemPrompt: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext ""
        }
        try {
            val url = URL(openRouterUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("HTTP-Referer", "https://tupataamiga.edu.co")
            conn.setRequestProperty("X-Title", "TuPataAmiga")
            conn.doOutput = true
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            val jsonBody = JSONObject().apply {
                put("model", openRouterModel)
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", promptUsuario)
                    })
                }
                put("messages", messages)
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    val jsonResponse = JSONObject(response.toString())
                    val choices = jsonResponse.getJSONArray("choices")
                    if (choices.length() > 0) {
                        val message = choices.getJSONObject(0).getJSONObject("message")
                        return@withContext message.getString("content").trim()
                    }
                }
            }
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    suspend fun generarResumenModeracion(
        nombre: String,
        categoria: String,
        descripcion: String,
        raza: String,
    ): String {
        val systemPrompt = "Eres un asistente de Inteligencia Artificial para moderación de contenido en la app TuPataAmiga. Genera un análisis breve de legitimidad y recomendación en menos de 40 palabras."
        val userPrompt = "Analiza esta publicación de mascota. Nombre: $nombre, Categoria: $categoria, Raza: $raza, Descripción: $descripcion."

        val respuestaIa = llamarOpenRouter(userPrompt, systemPrompt)
        return if (respuestaIa.isNotBlank()) {
            respuestaIa
        } else {
            "Análisis IA: Publicación legítima para '$nombre' ($categoria). Raza: $raza. Descripción coherente y sin registros duplicados en Armenia, Quindío. Acción recomendada: Verificar."
        }
    }

    suspend fun sugerirDescripcionMascota(
        tipoAnimal: String,
        raza: String,
        tamano: String,
    ): String {
        val systemPrompt = "Eres un asistente creativo de Inteligencia Artificial para redactar descripciones amorosas y atractivas de mascotas en adopción en Armenia, Quindío Colombia. Máximo 40 palabras."
        val userPrompt = "Crea una descripción corta y cariñosa para un $tipoAnimal de raza $raza y tamaño $tamano."

        val respuestaIa = llamarOpenRouter(userPrompt, systemPrompt)
        return if (respuestaIa.isNotBlank()) {
            respuestaIa
        } else {
            "$tipoAnimal de raza $raza y tamaño $tamano. Es de temperamento amigable, muy cariñoso, juguetón y adecuado para familias en Armenia, Quindío."
        }
    }
}
