package com.smartwake.app.data

import androidx.annotation.DrawableRes
import com.smartwake.app.R
import java.time.DayOfWeek
import java.time.LocalTime

enum class Transport { BUS, CAR, BIKE, WALK }

data class Place(val id: String, val name: String, val address: String)

data class PrepTask(val id: String, val name: String, val minutes: Int, @DrawableRes val icon: Int)

data class Alarm(
    val id: Long = 0,
    val name: String = "",
    val origin: Place = Places.CurrentLocation,
    val destination: Place? = null,
    val arrival: LocalTime = LocalTime.of(7, 0),
    val prepTaskIds: Set<String> = PrepTasks.defaultEnabledIds,
    val prepMinutes: Int = PrepTasks.minutesFor(PrepTasks.defaultEnabledIds),
    val transport: Transport = Transport.BUS,
    val repeat: Boolean = true,
    val repeatDays: Set<DayOfWeek> = DayOfWeek.entries.take(5).toSet(),
    val departureReminder: Boolean = true,
    val enabled: Boolean = true,
) {
    /** Guardar needs at least a name and somewhere to go. */
    val isComplete: Boolean get() = name.isNotBlank() && destination != null
}

object Places {
    val CurrentLocation = Place("actual", "Mi ubicación actual", "")
    val favorites = listOf(
        Place("oficina", "Oficina", "Calle Principal #123, Torre Financiera"),
        Place("gimnasio", "Gimnasio", "Av. Central 456, Centro Comercial Fit"),
        Place("casa", "Casa", "Urb. Los Olivos, Calle 8 #45"),
    )
}

object PrepTasks {
    val all = listOf(
        PrepTask("ducha", "Ducha", 15, R.drawable.ic_shower_head),
        PrepTask("desayuno", "Desayuno", 20, R.drawable.ic_coffee),
        PrepTask("meditacion", "Meditación", 10, R.drawable.ic_brain_circuit),
        PrepTask("vestirse", "Vestirse", 10, R.drawable.ic_shirt),
    )
    val defaultEnabledIds = setOf("ducha", "desayuno", "vestirse")

    fun minutesFor(ids: Set<String>): Int = all.filter { it.id in ids }.sumOf { it.minutes }
}
