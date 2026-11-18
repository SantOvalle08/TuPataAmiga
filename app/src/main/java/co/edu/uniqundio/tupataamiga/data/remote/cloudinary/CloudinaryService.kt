package co.edu.uniqundio.tupataamiga.data.remote.cloudinary

import android.content.Context
import android.net.Uri
import co.edu.uniqundio.tupataamiga.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryService @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun uploadImage(uri: Uri): String = withContext(Dispatchers.IO) {
        require(BuildConfig.CLOUDINARY_CLOUD_NAME.isNotBlank()) {
            "Cloudinary cloud name no configurado"
        }
        require(BuildConfig.CLOUDINARY_UPLOAD_PRESET.isNotBlank()) {
            "Cloudinary upload preset no configurado"
        }

        val boundary = "----TuPataAmiga${UUID.randomUUID()}"
        val connection = (URL(
            "https://api.cloudinary.com/v1_1/${BuildConfig.CLOUDINARY_CLOUD_NAME}/image/upload",
        ).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15000
            readTimeout = 30000
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        }

        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: error("No se pudo leer la imagen seleccionada")
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "mascota.jpg"

            DataOutputStream(connection.outputStream).use { output ->
                fun writeField(name: String, value: String) {
                    output.writeBytes("--$boundary\r\n")
                    output.writeBytes("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
                    output.writeBytes("$value\r\n")
                }

                writeField("upload_preset", BuildConfig.CLOUDINARY_UPLOAD_PRESET)
                output.writeBytes("--$boundary\r\n")
                output.writeBytes(
                    "Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"\r\n",
                )
                output.writeBytes("Content-Type: image/*\r\n\r\n")
                input.use { it.copyTo(output) }
                output.writeBytes("\r\n--$boundary--\r\n")
            }

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                error("Cloudinary rechazó la imagen (HTTP $responseCode)")
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            JSONObject(response).getString("secure_url")
        } finally {
            connection.disconnect()
        }
    }
}
