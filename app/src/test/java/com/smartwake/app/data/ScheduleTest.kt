package com.smartwake.app.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleTest {
    private val oficina = Places.favorites.first { it.id == "oficina" }

    @Test
    fun defaultDraftToOficinaByBus() {
        val schedule = Alarm(destination = oficina).schedule()

        assertEquals(45, schedule.prepMinutes)
        assertEquals(30, schedule.travelMinutes)
        assertEquals(12, schedule.trafficMinutes)
        assertEquals(LocalTime.of(6, 18), schedule.departure)
        assertEquals(LocalTime.of(5, 33), schedule.wake)
        assertEquals(LocalTime.of(7, 0), schedule.arrival)
    }

    @Test
    fun bikeAndWalkHaveNoTrafficBuffer() {
        val bike = Alarm(destination = oficina, transport = Transport.BIKE).schedule()
        val walk = Alarm(destination = oficina, transport = Transport.WALK).schedule()

        assertEquals(35, bike.travelMinutes)
        assertEquals(0, bike.trafficMinutes)
        assertEquals(75, walk.travelMinutes)
        assertEquals(0, walk.trafficMinutes)
    }

    @Test
    fun samePlaceMeansNoTravel() {
        val schedule = Alarm(origin = oficina, destination = oficina, prepMinutes = 30).schedule()

        assertEquals(0, schedule.totalTravelMinutes)
        assertEquals(LocalTime.of(6, 30), schedule.wake)
    }

    @Test
    fun wakeTimeWrapsPastMidnight() {
        val schedule = Alarm(destination = oficina, arrival = LocalTime.of(0, 30)).schedule()

        assertEquals(LocalTime.of(23, 3), schedule.wake)
    }

    @Test
    fun nextRingDateSkipsDaysNotSelected() {
        // Friday 2026-09-18 at 20:00; weekdays only, so the next ring is Monday.
        val now = LocalDateTime.of(2026, 9, 18, 20, 0)
        val alarm = Alarm(destination = oficina)

        assertEquals(LocalDate.of(2026, 9, 21), alarm.nextRingDate(now))
    }

    @Test
    fun nextRingDateIsTodayWhenWakeTimeIsStillAhead() {
        val now = LocalDateTime.of(2026, 9, 18, 5, 0)
        val alarm = Alarm(destination = oficina, repeatDays = setOf(DayOfWeek.FRIDAY))

        assertEquals(LocalDate.of(2026, 9, 18), alarm.nextRingDate(now))
    }

    @Test
    fun nextRingDateWithoutRepeatIsTomorrowOnceTimeHasPassed() {
        val now = LocalDateTime.of(2026, 9, 19, 9, 0)
        val alarm = Alarm(destination = oficina, repeat = false)

        assertEquals(LocalDate.of(2026, 9, 20), alarm.nextRingDate(now))
    }
}
