package com.smartwake.app.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToInt

/**
 * Made-up travel times (there's no routing API), fixed so every screen shows the same numbers.
 * Driving minutes between places; other transports scale from these.
 */
object TravelTimes {
    private val drivingMinutes = mapOf(
        setOf("actual", "oficina") to 25,
        setOf("actual", "gimnasio") to 15,
        setOf("actual", "casa") to 10,
        setOf("oficina", "gimnasio") to 12,
        setOf("oficina", "casa") to 25,
        setOf("gimnasio", "casa") to 15,
    )

    private fun factor(transport: Transport) = when (transport) {
        Transport.CAR -> 1.0
        Transport.BUS -> 1.2
        Transport.BIKE -> 1.4
        Transport.WALK -> 3.0
    }

    fun driving(from: Place, to: Place): Int =
        if (from.id == to.id) 0 else drivingMinutes[setOf(from.id, to.id)] ?: 0

    fun travel(from: Place, to: Place, transport: Transport): Int =
        (driving(from, to) * factor(transport)).roundToInt()

    /** Only road transport gets a traffic buffer: half the driving time. */
    fun traffic(from: Place, to: Place, transport: Transport): Int = when (transport) {
        Transport.CAR, Transport.BUS -> driving(from, to) / 2
        Transport.BIKE, Transport.WALK -> 0
    }
}

data class Schedule(
    val wake: LocalTime,
    val prepMinutes: Int,
    val departure: LocalTime,
    val travelMinutes: Int,
    val trafficMinutes: Int,
    val arrival: LocalTime,
) {
    /** Travel including the traffic buffer, as shown in the Resumen timeline. */
    val totalTravelMinutes: Int get() = travelMinutes + trafficMinutes
}

/** wake = arrival − travel − traffic − prep. */
fun Alarm.schedule(): Schedule {
    val travel = destination?.let { TravelTimes.travel(origin, it, transport) } ?: 0
    val traffic = destination?.let { TravelTimes.traffic(origin, it, transport) } ?: 0
    val departure = arrival.minusMinutes((travel + traffic).toLong())
    return Schedule(
        wake = departure.minusMinutes(prepMinutes.toLong()),
        prepMinutes = prepMinutes,
        departure = departure,
        travelMinutes = travel,
        trafficMinutes = traffic,
        arrival = arrival,
    )
}

/** The next date the alarm will ring after [now], following the selected repeat days. */
fun Alarm.nextRingDate(now: LocalDateTime): LocalDate {
    val wake = schedule().wake
    val days = if (repeat && repeatDays.isNotEmpty()) repeatDays else null
    return generateSequence(now.toLocalDate()) { it.plusDays(1) }
        .take(8)
        .first { date ->
            (days == null || date.dayOfWeek in days) && LocalDateTime.of(date, wake).isAfter(now)
        }
}
