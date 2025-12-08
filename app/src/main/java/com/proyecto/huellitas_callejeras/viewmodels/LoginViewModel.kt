package com.proyecto.huellitas_callejeras.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.huellitas_callejeras.remote.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val token: String? = null
)

class LoginViewModel(private val context: Context) : ViewModel() {

    private val authService = AuthService(context)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val NOMBRE_USUARIO = "Yadira Lizbeth Castro Velasco"

    fun login(contrasena: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            val result = authService.login(NOMBRE_USUARIO, contrasena)

            result.fold(
                onSuccess = { loginResponse ->
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = true,
                        token = loginResponse.token
                    )
                },
                onFailure = { exception ->
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = exception.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}