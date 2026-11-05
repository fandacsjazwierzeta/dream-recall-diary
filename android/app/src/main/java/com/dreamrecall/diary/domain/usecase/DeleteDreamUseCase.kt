package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.domain.repository.DreamRepository

class DeleteDreamUseCase(private val repository: DreamRepository) {
    operator fun invoke(id: String) = repository.remove(id)
}
