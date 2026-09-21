package com.smartwake.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwake.app.data.Alarm
import com.smartwake.app.data.schedule
import java.time.LocalDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** In-memory alarms plus the alarm being created; nothing survives the app process. */
class AlarmsViewModel : ViewModel() {
    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    private val _draft = MutableStateFlow(Alarm())
    val draft: StateFlow<Alarm> = _draft.asStateFlow()

    private val _alarmFlow = MutableStateFlow<AlarmFlow?>(null)
    val alarmFlow: StateFlow<AlarmFlow?> = _alarmFlow.asStateFlow()
    private val _alarmFlowId = MutableStateFlow<Long?>(null)
    val alarmFlowId: StateFlow<Long?> = _alarmFlowId.asStateFlow()

    private var nextId = 1L
    private var lastTriggeredKey: String? = null

    init {
        viewModelScope.launch {
            while (true) {
                checkForDueAlarm(LocalDateTime.now())
                delay(1_000)
            }
        }
    }

    fun startDraft() {
        _draft.value = Alarm()
    }

    fun updateDraft(transform: (Alarm) -> Alarm) {
        _draft.update(transform)
    }

    fun saveDraft() {
        val draft = _draft.value
        val schedule = draft.schedule()
        val wakeAt = LocalDateTime.now()
            .plusMinutes(2)
            .withSecond(0)
            .withNano(0)
        val arrivalAt = wakeAt.plusMinutes(
            (draft.prepMinutes + schedule.totalTravelMinutes).toLong(),
        )
        val alarm = draft.copy(
            id = nextId++,
            arrival = arrivalAt.toLocalTime(),
            repeat = false,
        )
        _alarms.update { it + alarm }
    }

    fun setEnabled(id: Long, enabled: Boolean) {
        _alarms.update { list -> list.map { if (it.id == id) it.copy(enabled = enabled) else it } }
    }

    fun delete(id: Long) {
        _alarms.update { list -> list.filterNot { it.id == id } }
    }

    fun advanceAlarmFlow() {
        _alarmFlow.update { flow ->
            when (flow) {
                AlarmFlow.RINGING -> AlarmFlow.VOICE_NOTIFICATION
                AlarmFlow.VOICE_NOTIFICATION -> AlarmFlow.POST_ALARM
                AlarmFlow.POST_ALARM, AlarmFlow.DEPARTURE, AlarmFlow.UPCOMING, null -> null
            }
        }
    }

    fun setAlarmFlow(flow: AlarmFlow) {
        _alarmFlow.value = flow
    }

    fun dismissAlarmFlow() {
        _alarmFlow.value = null
        _alarmFlowId.value = null
    }

    private fun checkForDueAlarm(now: LocalDateTime) {
        if (_alarmFlow.value != null) return
        val minuteKey = now.toLocalDate().toString() + ":" + now.hour + ":" + now.minute
        val dueAlarm = _alarms.value.firstOrNull { alarm ->
            if (!alarm.enabled || (alarm.repeat && now.dayOfWeek !in alarm.repeatDays)) return@firstOrNull false
            val schedule = alarm.schedule()
            val wakeDue = schedule.wake.hour == now.hour && schedule.wake.minute == now.minute
            val departureDue = alarm.departureReminder &&
                schedule.departure.hour == now.hour && schedule.departure.minute == now.minute
            wakeDue || departureDue
        } ?: return
        val key = "$minuteKey:${dueAlarm.id}"
        if (key == lastTriggeredKey) return
        lastTriggeredKey = key
        val schedule = dueAlarm.schedule()
        _alarmFlow.value = if (
            dueAlarm.departureReminder &&
            schedule.departure.hour == now.hour &&
            schedule.departure.minute == now.minute
        ) AlarmFlow.DEPARTURE else AlarmFlow.RINGING
        _alarmFlowId.value = dueAlarm.id
    }
}

enum class AlarmFlow {
    RINGING,
    VOICE_NOTIFICATION,
    POST_ALARM,
    DEPARTURE,
    UPCOMING,
}
