package com.proyecto.huellitas_callejeras

import android.app.Application
import com.proyecto.huellitas_callejeras.data.auth.AuthInterceptor
import com.proyecto.huellitas_callejeras.data.auth.SessionManager
import com.proyecto.huellitas_callejeras.data.remote.ApiClient
import okhttp3.OkHttpClient

class HuellitasApplication : Application() {

    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()

        sessionManager = SessionManager(this)

        val authInterceptor = AuthInterceptor(sessionManager)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        ApiClient.initialize(okHttpClient)
      //aqui intente implementar el token
        sessionManager.authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6Imh1ZWxsaXRhcy1jYWxsZWplcmFzLWFwaSIsInJlc2NhdGlzdGFJZCI6IjRmNGIyOGZkLTQzNTktNDI0Ny05YjEyLWU5ODc5M2I4NmQ1NyIsIm5vbWJyZSI6IllhZGlyYSBMaXpiZXRoIENhc3RybyBWZWxhc2NvIiwiZXhwIjoxNzY3ODMyMjczfQ.Is02x8ekP0RjujlI5hrr3vx5qp8m0X1MrXoFd2rGt-k"
    }
}
