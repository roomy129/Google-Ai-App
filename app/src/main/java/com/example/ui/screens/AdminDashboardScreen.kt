package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CampusBlue
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBusy
import com.example.ui.theme.StatusInClass
import com.example.ui.theme.StatusInMeeting
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.adminStats.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val buildings by viewModel.buildings.collectAsState()
    val roomsWithBuildings by viewModel.roomsWithBuildings.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val allFacultyItems by viewModel.allFaculty.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Accounts", "Campus Infrastructure", "Timetables", "Activity Logs")

    // Dialog state for adding Faculty
    var showAddFacultyDialog by remember { mutableStateOf(false) }
    var newFacultyName by remember { mutableStateOf("") }
    var newFacultyEmail by remember { mutableStateOf("") }
    var newFacultyDeptId by remember { mutableStateOf(1L) }
    var newFacultyDesignation by remember { mutableStateOf("Assistant Professor") }
    var newFacultyCode by remember { mutableStateOf("") }

    // Dialog state for adding Room
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var newRoomBuildingId by remember { mutableStateOf(1L) }
    var newRoomFloor by remember { mutableStateOf("Second Floor") }
    var newRoomNumber by remember { mutableStateOf("") }
    var newRoomName by remember { mutableStateOf("") }

    // Dialog state for adding Timetable
    var showAddTimetableDialog by remember { mutableStateOf(false) }
    var ttFacultyId by remember { mutableStateOf(3L) }
    var ttDay by remember { mutableStateOf("Monday") }
    var ttStart by remember { mutableStateOf("09:00 AM") }
    var ttEnd by remember { mutableStateOf("10:30 AM") }
    var ttSubject by remember { mutableStateOf("") }
    var ttRoomId by remember { mutableStateOf(1L) }

    // Confirmation dialog for deletion
    var userToDelete by remember { mutableStateOf<User?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("admin_dashboard_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Admin Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Administrator Management Console",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Monitor live campus faculty presence, control account permissions, configure timetable records, and inspect audit logs.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 2. Summary Metric Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminMetricCard(
                            label = "Total Faculty",
                            value = "${stats.totalFaculty}",
                            sublabel = "Registered accounts",
                            accentColor = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricCard(
                            label = "Total Students",
                            value = "${stats.totalStudents}",
                            sublabel = "Enrolled users",
                            accentColor = CampusBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminMetricCard(
                            label = "Available Now",
                            value = "${stats.availableCount}",
                            sublabel = "Free for queries",
                            accentColor = StatusAvailable,
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricCard(
                            label = "In Class / Lab",
                            value = "${stats.inClassCount}",
                            sublabel = "Teaching sessions",
                            accentColor = StatusInClass,
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricCard(
                            label = "Outdated Loc",
                            value = "${stats.outdatedLocationCount}",
                            sublabel = ">45 min no update",
                            accentColor = StatusInMeeting,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. Tabbed Navigation Bar
            item {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }
            }

            // Tab 0: User Accounts Management
            if (selectedTabIndex == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Faculty & Student Accounts (${users.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyDark
                        )
                        Button(
                            onClick = { showAddFacultyDialog = true },
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("add_faculty_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Faculty", fontSize = 12.sp)
                        }
                    }
                }

                items(users, key = { it.id }) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NavyDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (user.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (user.isActive) "Active" else "Deactivated",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (user.isActive) StatusAvailable else StatusBusy
                                        )
                                    }
                                }
                                Text(
                                    text = "${user.email} • Role: ${user.role} (${user.facultyCode ?: "N/A"})",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = { viewModel.toggleUserStatus(user) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                                        contentDescription = "Toggle Active",
                                        tint = if (user.isActive) StatusInMeeting else StatusAvailable,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                if (user.role != UserRole.ADMIN.name) {
                                    IconButton(
                                        onClick = { userToDelete = user },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = StatusBusy,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 1: Campus Infrastructure (Buildings & Rooms)
            if (selectedTabIndex == 1) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Campus Buildings & Rooms",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyDark
                        )
                        Button(
                            onClick = { showAddRoomDialog = true },
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("add_room_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Room", fontSize = 12.sp)
                        }
                    }
                }

                items(roomsWithBuildings, key = { it.room.id }) { rw ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${rw.building.buildingName} - ${rw.room.floorNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Room ${rw.room.roomNumber}: ${rw.room.roomName} (${rw.building.campusName})",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteRoom(rw.room.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Tab 2: Timetables Management
            if (selectedTabIndex == 2) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Timetable & Schedule Entries",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyDark
                        )
                        Button(
                            onClick = { showAddTimetableDialog = true },
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("add_timetable_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Slot", fontSize = 12.sp)
                        }
                    }
                }

                val allSlots = allFacultyItems.flatMap { f ->
                    f.todayTimetable.map { t -> f to t }
                }

                items(allSlots, key = { "${it.first.user.id}_${it.second.entry.id}" }) { pair ->
                    val faculty = pair.first
                    val tt = pair.second
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${tt.entry.subjectName} (${tt.entry.dayOfWeek})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Faculty: ${faculty.displayName} • ${tt.entry.startTime} - ${tt.entry.endTime}",
                                    fontSize = 11.sp,
                                    color = CampusBlue
                                )
                                Text(
                                    text = tt.locationDisplay,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteTimetableEntry(tt.entry.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusBusy, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Tab 3: Activity & Audit Logs
            if (selectedTabIndex == 3) {
                item {
                    Text(
                        text = "System Activity & Location Audit Log",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDark
                    )
                }

                val sdf = SimpleDateFormat("h:mm a, dd MMM", Locale.getDefault())
                items(activityLogs, key = { it.id }) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, tint = CampusBlue, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.action,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sdf.format(Date(log.createdAt)),
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Faculty Dialog
        if (showAddFacultyDialog) {
            AlertDialog(
                onDismissRequest = { showAddFacultyDialog = false },
                title = { Text("Add New Faculty Account", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newFacultyName,
                            onValueChange = { newFacultyName = it },
                            label = { Text("Full Name (e.g. Dr. Jane Doe)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newFacultyEmail,
                            onValueChange = { newFacultyEmail = it },
                            label = { Text("Campus Email") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newFacultyCode,
                            onValueChange = { newFacultyCode = it },
                            label = { Text("Faculty Code (e.g. FAC-109)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newFacultyDesignation,
                            onValueChange = { newFacultyDesignation = it },
                            label = { Text("Designation") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newFacultyName.isNotBlank() && newFacultyEmail.isNotBlank()) {
                                viewModel.addFaculty(
                                    name = newFacultyName,
                                    email = newFacultyEmail,
                                    deptId = newFacultyDeptId,
                                    designation = newFacultyDesignation,
                                    code = if (newFacultyCode.isNotBlank()) newFacultyCode else "FAC-${(100..999).random()}"
                                )
                                showAddFacultyDialog = false
                                newFacultyName = ""
                                newFacultyEmail = ""
                                newFacultyCode = ""
                            }
                        }
                    ) {
                        Text("Create Account")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddFacultyDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Add Room Dialog
        if (showAddRoomDialog) {
            AlertDialog(
                onDismissRequest = { showAddRoomDialog = false },
                title = { Text("Add Room to Building", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newRoomFloor,
                            onValueChange = { newRoomFloor = it },
                            label = { Text("Floor (e.g. Second Floor)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newRoomNumber,
                            onValueChange = { newRoomNumber = it },
                            label = { Text("Room Number (e.g. 206)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newRoomName,
                            onValueChange = { newRoomName = it },
                            label = { Text("Room Name / Purpose") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newRoomNumber.isNotBlank() && newRoomName.isNotBlank()) {
                                viewModel.addRoom(
                                    buildingId = newRoomBuildingId,
                                    floor = newRoomFloor,
                                    number = newRoomNumber,
                                    name = newRoomName
                                )
                                showAddRoomDialog = false
                                newRoomNumber = ""
                                newRoomName = ""
                            }
                        }
                    ) {
                        Text("Save Room")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddRoomDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Add Timetable Dialog
        if (showAddTimetableDialog) {
            AlertDialog(
                onDismissRequest = { showAddTimetableDialog = false },
                title = { Text("Add Faculty Timetable Slot", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ttSubject,
                            onValueChange = { ttSubject = it },
                            label = { Text("Subject / Activity Name") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = ttDay,
                            onValueChange = { ttDay = it },
                            label = { Text("Day of Week (e.g. Monday)") },
                            singleLine = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = ttStart,
                                onValueChange = { ttStart = it },
                                label = { Text("Start Time") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ttEnd,
                                onValueChange = { ttEnd = it },
                                label = { Text("End Time") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (ttSubject.isNotBlank()) {
                                viewModel.addTimetableEntry(
                                    facultyId = ttFacultyId,
                                    day = ttDay,
                                    start = ttStart,
                                    end = ttEnd,
                                    subject = ttSubject,
                                    roomId = ttRoomId
                                )
                                showAddTimetableDialog = false
                                ttSubject = ""
                            }
                        }
                    ) {
                        Text("Add Schedule")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTimetableDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Delete Confirmation Dialog
        userToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = { Text("Confirm Account Deletion", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete ${target.fullName} (${target.email})? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteUser(target)
                            userToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusBusy)
                    ) {
                        Text("Delete Record")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun AdminMetricCard(
    label: String,
    value: String,
    sublabel: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sublabel, fontSize = 9.sp, color = TextMuted, maxLines = 1)
        }
    }
}
