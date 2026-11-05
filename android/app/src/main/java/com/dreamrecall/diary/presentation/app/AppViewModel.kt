package com.dreamrecall.diary.presentation.app

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.core.navigation.Screen
import com.dreamrecall.diary.domain.repository.LockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(private val lockRepository: LockRepository) : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun onSplashFinished() {
        backStack.clear()
        _screen.value = if (lockRepository.hasPin()) Screen.Lock else Screen.Menu
        publish()
    }

    fun onUnlocked() {
        backStack.clear()
        _screen.value = Screen.Menu
        publish()
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash && current != Screen.Lock) backStack.add(current)
        _screen.value = target
        publish()
    }

    fun openResult(dreamId: String) {
        backStack.clear()
        backStack.add(Screen.Menu)
        _screen.value = Screen.Result(dreamId)
        publish()
    }

    fun replaceWith(target: Screen) {
        _screen.value = target
        publish()
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        publish()
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        publish()
    }

    private fun publish() {
        _canGoBack.value = backStack.isNotEmpty()
    }
}
