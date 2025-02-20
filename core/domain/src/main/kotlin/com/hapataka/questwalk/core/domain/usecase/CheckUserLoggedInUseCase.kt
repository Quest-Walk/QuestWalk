package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class CheckUserLoggedInUseCase @Inject constructor(
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(): Boolean {
        return userRepository.checkUserLoggedIn()
    }
}