package com.smartwake.app.ui

import androidx.lifecycle.ViewModel
import com.smartwake.app.data.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** In-memory alarms plus the alarm being created; nothing survives the app process. */
class AlarmsViewModel : ViewModel() {
    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    private val _draft = MutableStateFlow(Alarm())
    val draft: StateFlow<Alarm> = _draft.asStateFlow()

    private var nextId = 1L

    fun startDraft() {
        _draft.value = Alarm()
    }

    fun updateDraft(transform: (Alarm) -> Alarm) {
        _draft.update(transform)
    }

    fun saveDraft() {
        val alarm = _draft.value.copy(id = nextId++)
        _alarms.update { it + alarm }
    }

    fun setEnabled(id: Long, enabled: Boolean) {
        _alarms.update { list -> list.map { if (it.id == id) it.copy(enabled = enabled) else it } }
    }

    fun delete(id: Long) {
        _alarms.update { list -> list.filterNot { it.id == id } }
    }
}
