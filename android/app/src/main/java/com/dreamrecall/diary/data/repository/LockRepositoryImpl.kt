package com.dreamrecall.diary.data.repository

import com.dreamrecall.diary.data.local.LockStore
import com.dreamrecall.diary.domain.repository.LockRepository

class LockRepositoryImpl(private val store: LockStore) : LockRepository {

    override fun hasPin(): Boolean = store.hasPin()

    override fun definePin(pin: String) = store.definePin(pin)

    override fun clearPin() = store.clearPin()

    override fun matches(pin: String): Boolean = store.matches(pin)
}
