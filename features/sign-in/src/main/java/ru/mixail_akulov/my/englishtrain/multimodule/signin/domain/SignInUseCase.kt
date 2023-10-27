package ru.mixail_akulov.my.englishtrain.multimodule.signin.domain

import ru.mixail_akulov.my.englishtrain.multimodule.signin.domain.exceptions.EmptyEmailException
import ru.mixail_akulov.my.englishtrain.multimodule.signin.domain.exceptions.EmptyPasswordException
import ru.mixail_akulov.my.englishtrain.multimodule.signin.domain.repositories.AuthServiceRepository
import ru.mixail_akulov.my.englishtrain.multimodule.signin.domain.repositories.AuthTokenRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authTokenRepository: AuthTokenRepository,
    private val authServiceRepository: AuthServiceRepository,
) {

    /**
     * Войдите в приложение по логину и паролю и сохраните токен авторизации.
     * @throws EmptyEmailException if email is blank
     * @throws EmptyPasswordException if password is blank
     */
    suspend fun signIn(email: String, password: String) {
        if (email.isBlank()) throw EmptyEmailException()
        if (password.isBlank()) throw EmptyPasswordException()

        val token = authServiceRepository.signIn(email, password)

        authTokenRepository.setToken(token)
    }

}