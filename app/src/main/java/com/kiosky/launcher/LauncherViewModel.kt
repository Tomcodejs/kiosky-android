package com.kiosky.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiosky.launcher.data.ButtonRepository
import com.kiosky.launcher.data.KioskyButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LauncherViewModel(private val repository: ButtonRepository) : ViewModel() {

    private val _buttons = MutableStateFlow<List<KioskyButton>>(emptyList())
    val buttons: StateFlow<List<KioskyButton>> = _buttons

    private val _editMode = MutableStateFlow(false)
    val editMode: StateFlow<Boolean> = _editMode

    init {
        viewModelScope.launch {
            repository.buttons.collect { _buttons.value = it }
        }
    }

    fun toggleEditMode() {
        _editMode.update { !it }
    }

    fun addButton() {
        val new = KioskyButton(label = "Nouveau bouton", x = 0.1f, y = 0.1f)
        persist(_buttons.value + new)
    }

    fun updateButton(updated: KioskyButton) {
        persist(_buttons.value.map { if (it.id == updated.id) updated else it })
    }

    fun deleteButton(id: String) {
        persist(_buttons.value.filterNot { it.id == id })
    }

    fun moveButton(id: String, newX: Float, newY: Float) {
        persist(
            _buttons.value.map {
                if (it.id == id) it.copy(x = newX.coerceIn(0f, 1f), y = newY.coerceIn(0f, 1f)) else it
            }
        )
    }

    private fun persist(list: List<KioskyButton>) {
        _buttons.value = list
        viewModelScope.launch { repository.save(list) }
    }
}
