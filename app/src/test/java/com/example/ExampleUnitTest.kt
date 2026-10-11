package com.example

import com.example.data.model.AvailabilityStatus
import com.example.data.model.LocationFreshness
import com.example.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAvailabilityStatusParsing() {
        assertEquals(AvailabilityStatus.AVAILABLE, AvailabilityStatus.fromString("Available"))
        assertEquals(AvailabilityStatus.IN_CLASS, AvailabilityStatus.fromString("IN_CLASS"))
        assertEquals(AvailabilityStatus.IN_LABORATORY, AvailabilityStatus.fromString("In Laboratory"))
        assertEquals(AvailabilityStatus.IN_MEETING, AvailabilityStatus.fromString("In Meeting"))
        assertEquals(AvailabilityStatus.BUSY, AvailabilityStatus.fromString("Busy"))
        assertEquals(AvailabilityStatus.NOT_AVAILABLE, AvailabilityStatus.fromString("Not Available"))
    }

    @Test
    fun testUserRoleEnum() {
        assertEquals("STUDENT", UserRole.STUDENT.name)
        assertEquals("FACULTY", UserRole.FACULTY.name)
        assertEquals("ADMIN", UserRole.ADMIN.name)
    }

    @Test
    fun testLocationFreshnessLabels() {
        assertEquals("Current Location", LocationFreshness.CURRENT.label)
        assertEquals("Possibly Outdated", LocationFreshness.POSSIBLY_OUTDATED.label)
        assertEquals("Last Known Location", LocationFreshness.LAST_KNOWN.label)
    }
}
