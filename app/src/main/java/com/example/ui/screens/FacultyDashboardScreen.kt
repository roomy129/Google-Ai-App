package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvailabilityStatus
import com.example.ui.components.AvailabilityBadge
import com.example.ui.components.CampusMapVisualizer
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CampusBlue
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBusy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FacultyDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val buildings by viewModel.buildings.collectAsState()
    val roomsWithBuildings by viewModel.roomsWithBuildings.collectAsState()
    val allFaculty by viewModel.allFaculty.collectAsState()
    val locationHistory by viewModel.facultyLocationHistory.collectAsState()
    val facultyPrivacy by viewModel.facultyPrivacy.collectAsState()

    // Find detailed info for current logged in faculty
    val currentFacultyItem = allFaculty.find { it.user.id == currentUser?.id }

    // Location selection state
    var selectedBuildingId by remember(buildings) {
        mutableStateOf(buildings.firstOrNull()?.id ?: 1L)
    }
    val filteredRooms = roomsWithBuildings.filter { it.building.id == selectedBuildingId }

    var selectedRoomId by remember(filteredRooms) {
        mutableStateOf(filteredRooms.firstOrNull()?.room?.id ?: 1L)
    }

    var selectedAvailability by remember {
        mutableStateOf(currentFacultyItem?.availabilityStatus ?: AvailabilityStatus.AVAILABLE)
    }

    var statusMessageText by remember {
        mutableStateOf(currentFacultyItem?.statusMessage ?: "")
    }

    var buildingDropdownExpanded by remember { mutableStateOf(false) }
    var roomDropdownExpanded by remember { mutableStateOf(false) }

    val isSharingLocation = facultyPrivacy?.locationSharingEnabled ?: true

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("faculty_dashboard_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Welcome & Status Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Faculty Control Center",
                                    color = Color(0xFFBAE6FD),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = currentUser?.fullName ?: "Faculty Member",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${currentUser?.designation ?: "Professor"} • ${currentUser?.facultyCode ?: "FAC"}",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp
                                )
                            }
                            AvailabilityBadge(
                                status = currentFacultyItem?.availabilityStatus ?: AvailabilityStatus.AVAILABLE
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Live status summary
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Current",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Current Display: ${currentFacultyItem?.displayLocationText ?: "Not Set"}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 2. Section: Update Current Campus Location
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Update Location",
                                tint = NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Update Campus Location",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NavyDark
                            )
                        }

                        Text(
                            text = "Select your current campus building, floor, and room number to inform students.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Building Selector Dropdown
                        val currentBuilding = buildings.find { it.id == selectedBuildingId }
                        ExposedDropdownMenuBox(
                            expanded = buildingDropdownExpanded,
                            onExpandedChange = { buildingDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = currentBuilding?.buildingName ?: "Select Building",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Campus Building / Block") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("building_selector_field"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CampusBlue,
                                    unfocusedBorderColor = BorderSubtle
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = buildingDropdownExpanded,
                                onDismissRequest = { buildingDropdownExpanded = false }
                            ) {
                                buildings.forEach { b ->
                                    DropdownMenuItem(
                                        text = { Text("${b.buildingName} (${b.campusName})") },
                                        onClick = {
                                            selectedBuildingId = b.id
                                            buildingDropdownExpanded = false
                                            val firstRoom = roomsWithBuildings.firstOrNull { it.building.id == b.id }
                                            if (firstRoom != null) {
                                                selectedRoomId = firstRoom.room.id
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Room Selector Dropdown
                        val currentRoomObj = roomsWithBuildings.find { it.room.id == selectedRoomId }
                        ExposedDropdownMenuBox(
                            expanded = roomDropdownExpanded,
                            onExpandedChange = { roomDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = currentRoomObj?.let { "${it.room.floorNumber} - Room ${it.room.roomNumber} (${it.room.roomName})" }
                                    ?: "Select Floor & Room",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Floor & Room Number") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roomDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("room_selector_field"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CampusBlue,
                                    unfocusedBorderColor = BorderSubtle
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = roomDropdownExpanded,
                                onDismissRequest = { roomDropdownExpanded = false }
                            ) {
                                filteredRooms.forEach { rw ->
                                    DropdownMenuItem(
                                        text = { Text("${rw.room.floorNumber} - Rm ${rw.room.roomNumber}: ${rw.room.roomName}") },
                                        onClick = {
                                            selectedRoomId = rw.room.id
                                            roomDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Optional short message
                        OutlinedTextField(
                            value = statusMessageText,
                            onValueChange = { statusMessageText = it },
                            label = { Text("Optional Status Message (e.g. In consultation until 2 PM)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("faculty_status_message_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CampusBlue,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.updateLocation(
                                    roomId = selectedRoomId,
                                    status = selectedAvailability,
                                    message = statusMessageText
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("update_location_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Update Location & Broadcast", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 3. Section: Update Availability Status (Independent)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Availability",
                                tint = CampusBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "My Availability Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NavyDark
                            )
                        }

                        Text(
                            text = "Update your current availability independently of room coordinates.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Status selection chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AvailabilityStatus.entries.forEach { status ->
                                val isSelected = selectedAvailability == status
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) status.textColor else BorderSubtle,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .background(if (isSelected) status.bgColor else Color.White)
                                        .clickable { selectedAvailability = status }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("status_chip_${status.name.lowercase()}")
                                ) {
                                    Text(
                                        text = status.displayName,
                                        color = if (isSelected) status.textColor else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.updateAvailabilityOnly(
                                    status = selectedAvailability,
                                    message = statusMessageText
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("update_availability_only_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Update Status Only", fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                        }
                    }
                }
            }

            // 4. Section: Privacy & Location Sharing Control
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Privacy",
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Location Sharing Privacy",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = NavyDark
                                    )
                                    Text(
                                        text = if (isSharingLocation) "Sharing with authorized students" else "Location details hidden from students",
                                        fontSize = 11.sp,
                                        color = if (isSharingLocation) StatusAvailable else StatusBusy,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Switch(
                                checked = isSharingLocation,
                                onCheckedChange = { viewModel.toggleLocationSharing(it) },
                                modifier = Modifier.testTag("location_sharing_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CampusBlue
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Privacy Assurance: This system never tracks GPS coordinates or continuous background movement. Location updates are completely manual and voluntary.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // 5. Section: Visual Campus Building Layout
            item {
                val bCode = currentFacultyItem?.currentBuilding?.code
                val rName = currentFacultyItem?.currentRoom?.let { "Rm ${it.roomNumber}" }
                CampusMapVisualizer(highlightBuildingCode = bCode, highlightRoomName = rName)
            }

            // 6. Section: Location Update History
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recent Location Updates History",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (locationHistory.isEmpty()) {
                            Text(
                                text = "No previous location updates recorded today.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                                locationHistory.take(5).forEach { loc ->
                                    val r = roomsWithBuildings.find { it.room.id == loc.roomId }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF8FAFC))
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = r?.fullDisplayName ?: "Room ID #${loc.roomId}",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = NavyDark
                                                )
                                                if (!loc.message.isNullOrBlank()) {
                                                    Text(
                                                        text = "\"${loc.message}\"",
                                                        fontSize = 11.sp,
                                                        color = TextSecondary,
                                                        fontStyle = FontStyle.Italic
                                                    )
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = loc.availabilityStatus,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CampusBlue
                                                )
                                                Text(
                                                    text = sdf.format(Date(loc.updatedAt)),
                                                    fontSize = 10.sp,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
