package com.proyecto.huellitas_callejeras.data.auth

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authedRequestBuilder = originalRequest.newBuilder()


        sessionManager.authToken?.let { token ->
            authedRequestBuilder.addHeader("Authorization", "Bearer $token")
        }


        val bodyString = bodyToString(originalRequest)
        logRequestDetails(originalRequest, bodyString)

        val newRequest: Request
        if (originalRequest.body() != null) {

            var contentType = originalRequest.body()?.contentType()
            if (contentType == null) {
                contentType = MediaType.parse("application/json; charset=utf-8")
                authedRequestBuilder.header("Content-Type", contentType.toString())
            }
            val newBody = RequestBody.create(contentType, bodyString)
            newRequest = authedRequestBuilder.method(originalRequest.method(), newBody).build()
        } else {
            newRequest = authedRequestBuilder.build()
        }

        val response: Response
        try {
            response = chain.proceed(newRequest)
        } catch (e: Exception) {
            Log.e("DepuracionAPI", "<-- HTTP FAILED: $e")
            throw e
        }

        return logResponse(response)
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "(body reading failed)"
        }
    }

    private fun logRequestDetails(request: Request, bodyString: String) {
        Log.d("DepuracionAPI", "----------------- Request --------------------------")
        Log.d("DepuracionAPI", "--> ${request.method()} ${request.url()}")
        Log.d("DepuracionAPI", "Headers:\n" + request.headers())
        if (bodyString.isNotEmpty()) {
            Log.d("DepuracionAPI", "Body: $bodyString")
        } else {
            Log.d("DepuracionAPI", "Body: (no body)")
        }
        Log.d("DepuracionAPI", "--> END ${request.method()}")
        Log.d("DepuracionAPI", "----------------------------------------------------")
    }

    private fun logResponse(response: Response): Response {
        try {
            Log.d("DepuracionAPI", "----------------- Response -------------------------")
            Log.d("DepuracionAPI", "<-- ${response.code()} ${response.message()} for ${response.request().url()}")
            Log.d("DepuracionAPI", "Headers:\n" + response.headers())

            val bodyString = response.peekBody(Long.MAX_VALUE).string()
            if (bodyString.isNotEmpty()) {
                Log.d("DepuracionAPI", "Body: $bodyString")
            } else {
                Log.d("DepuracionAPI", "Body: (is empty)")
            }
            Log.d("DepuracionAPI", "<-- END HTTP (${response.receivedResponseAtMillis() - response.sentRequestAtMillis()}ms)")
        } catch (e: Exception) {
            Log.e("DepuracionAPI", "Error while logging response", e)
        } finally {
            Log.d("DepuracionAPI", "----------------------------------------------------")
        }
        return response
    }
}
