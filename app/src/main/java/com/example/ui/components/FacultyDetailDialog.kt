package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FacultyDetailedItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CampusBlue
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusInClass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FacultyDetailDialog(
    faculty: FacultyDetailedItem,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("faculty_detail_dialog"),
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Action Bar with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Faculty Profile & Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NavyDark
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_dialog_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Faculty Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val initials = faculty.displayName
                        .split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .joinToString("")
                        .ifEmpty { "FC" }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = faculty.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = NavyDark
                        )
                        Text(
                            text = "${faculty.designation} (${faculty.facultyCode})",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = faculty.departmentName,
                            fontSize = 12.sp,
                            color = CampusBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Badges
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AvailabilityBadge(status = faculty.availabilityStatus)
                    LocationFreshnessBadge(freshness = faculty.locationFreshness)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Campus Location Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (faculty.isSharingLocation) Icons.Default.LocationOn else Icons.Default.LocationOff,
                                contentDescription = "Location Status",
                                tint = if (faculty.isSharingLocation) NavyPrimary else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Current Campus Location",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (faculty.isSharingLocation) {
                            Text(
                                text = faculty.displayLocationText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            if (faculty.currentBuilding != null && faculty.currentRoom != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Block: ${faculty.currentBuilding.buildingName} • Campus: ${faculty.currentBuilding.campusName}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Floor: ${faculty.currentRoom.floorNumber} • Room: ${faculty.currentRoom.roomNumber} (${faculty.currentRoom.roomName})",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            if (!faculty.statusMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE0F2FE))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "\"${faculty.statusMessage}\"",
                                        fontSize = 12.sp,
                                        color = Color(0xFF0369A1),
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }

                            val timeStr = if (faculty.lastUpdatedTimestamp != null) {
                                val sdf = SimpleDateFormat("EEEE, h:mm a", Locale.getDefault())
                                "Last Updated: ${sdf.format(Date(faculty.lastUpdatedTimestamp!!))}"
                            } else {
                                "Location not updated today"
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = timeStr,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        } else {
                            Text(
                                text = "Location sharing has been paused by the faculty member. Room coordinates are concealed.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }

                // Expected Timetable Location vs Actual Location
                Spacer(modifier = Modifier.height(14.dp))
                if (faculty.expectedLocationFromSchedule != null) {
                    val exp = faculty.expectedLocationFromSchedule
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Scheduled",
                                    tint = StatusInClass,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Expected Location (From Timetable)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = StatusInClass
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${exp.entry.subjectName} (${exp.entry.startTime} - ${exp.entry.endTime})",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = NavyDark
                            )
                            Text(
                                text = exp.locationDisplay,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Note: System displays both updated check-in and scheduled timetable location for complete clarity.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }

                // Campus Map visualizer
                Spacer(modifier = Modifier.height(14.dp))
                val buildingCode = if (faculty.isSharingLocation) faculty.currentBuilding?.code else null
                val roomName = if (faculty.isSharingLocation) faculty.currentRoom?.let { "Rm ${it.roomNumber}" } else null
                CampusMapVisualizer(
                    highlightBuildingCode = buildingCode,
                    highlightRoomName = roomName
                )

                // Today's Timetable Section
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = "Timetable",
                        tint = NavyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Weekly Schedule & Timetable",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (faculty.todayTimetable.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Timetable not available for this faculty member.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontStyle = FontStyle.Italic
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        faculty.todayTimetable.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.entry.subjectName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = NavyDark
                                        )
                                        Text(
                                            text = "${item.entry.dayOfWeek} • ${item.entry.startTime} - ${item.entry.endTime}",
                                            fontSize = 11.sp,
                                            color = CampusBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = item.locationDisplay,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_detail_dialog"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Close Profile", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
