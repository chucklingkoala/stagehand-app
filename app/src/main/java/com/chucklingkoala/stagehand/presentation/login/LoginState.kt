package com.chucklingkoala.stagehand.presentation.login

data class LoginState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
