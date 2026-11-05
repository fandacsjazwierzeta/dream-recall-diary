package com.dreamrecall.diary.domain.repository

interface LockRepository {
    fun hasPin(): Boolean
    fun definePin(pin: String)
    fun clearPin()
    fun matches(pin: String): Boolean
}
