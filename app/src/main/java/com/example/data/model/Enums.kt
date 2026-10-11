package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusAvailableBg
import com.example.ui.theme.StatusBusy
import com.example.ui.theme.StatusBusyBg
import com.example.ui.theme.StatusInClass
import com.example.ui.theme.StatusInClassBg
import com.example.ui.theme.StatusInLab
import com.example.ui.theme.StatusInLabBg
import com.example.ui.theme.StatusInMeeting
import com.example.ui.theme.StatusInMeetingBg
import com.example.ui.theme.StatusNotAvailable
import com.example.ui.theme.StatusNotAvailableBg

enum class UserRole {
    STUDENT,
    FACULTY,
    ADMIN
}

enum class AvailabilityStatus(
    val displayName: String,
    val textColor: Color,
    val bgColor: Color,
    val description: String
) {
    AVAILABLE("Available", StatusAvailable, StatusAvailableBg, "Available for student consultations"),
    IN_CLASS("In Class", StatusInClass, StatusInClassBg, "Currently teaching a scheduled class"),
    IN_LABORATORY("In Laboratory", StatusInLab, StatusInLabBg, "Conducting experiments or lab supervision"),
    IN_MEETING("In Meeting", StatusInMeeting, StatusInMeetingBg, "Attending faculty/departmental meeting"),
    BUSY("Busy", StatusBusy, StatusBusyBg, "Currently occupied with high-priority work"),
    NOT_AVAILABLE("Not Available", StatusNotAvailable, StatusNotAvailableBg, "Off-campus or on leave");

    companion object {
        fun fromString(value: String): AvailabilityStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: AVAILABLE
        }
    }
}

enum class LocationFreshness(
    val label: String,
    val description: String
) {
    CURRENT("Current Location", "Updated recently (< 45 mins ago)"),
    POSSIBLY_OUTDATED("Possibly Outdated", "Updated more than 45 minutes ago"),
    LAST_KNOWN("Last Known Location", "Previous saved location update"),
    NOT_UPDATED("Location not updated yet", "Faculty has not posted a location update today")
}
