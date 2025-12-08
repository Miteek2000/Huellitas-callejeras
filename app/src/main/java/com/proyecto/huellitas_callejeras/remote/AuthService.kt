package com.proyecto.huellitas_callejeras.remote

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.proyecto.huellitas_callejeras.models.LoginRequest
import com.proyecto.huellitas_callejeras.models.LoginResponse
import kotlinx.coroutines.flow.first
import retrofit2.Response

private val Context.dataStore by preferencesDataStore(name = "auth_preferences")

class AuthService(private val context: Context) {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    suspend fun login(nombre: String, contrasena: String): Result<LoginResponse> {
        return runCatching {
            val request = LoginRequest(nombre, contrasena)
            val response: Response<LoginResponse> = RetrofitClient.apiService.login(request)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception("Error HTTP: ${response.code()} - ${errorBody ?: "Sin detalles"}")
            }

            val loginResponse: LoginResponse = response.body()
                ?: throw Exception("El servidor devolvió una respuesta vacía")

            if (!loginResponse.success) {
                throw Exception(loginResponse.message ?: "Credenciales incorrectas")
            }

            val token = loginResponse.token
            if (token.isNullOrBlank()) {
                throw Exception("El servidor no devolvió un token válido")
            }

            saveToken(token)
            loginResponse
        }
    }

    private suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}