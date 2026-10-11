package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvailabilityStatus
import com.example.ui.components.FacultyCard
import com.example.ui.components.FacultyDetailDialog
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CampusBlue
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun StudentDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDeptId by viewModel.selectedDepartmentId.collectAsState()
    val selectedAvailability by viewModel.selectedAvailability.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val filteredFaculty by viewModel.filteredFaculty.collectAsState()
    val selectedFacultyDetail by viewModel.selectedFacultyDetail.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("student_dashboard_list"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Welcome Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Find the Right Faculty, at the Right Place, at the Right Time.",
                            color = Color(0xFFBAE6FD),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hello, ${currentUser?.fullName ?: "Student"}!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Real-time faculty tracking across campus blocks, laboratories, lecture halls, and department rooms.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 2. Prominent Search Bar
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_faculty_input"),
                        placeholder = {
                            Text(
                                "Search Faculty by Name, ID, or Department...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = CampusBlue
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampusBlue,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }

            // 3. Department Filters (Horizontal Scroll)
            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Department Filter",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedDeptId == null,
                            onClick = { viewModel.selectedDepartmentId.value = null },
                            label = { Text("All Departments (${departments.size})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                        departments.forEach { dept ->
                            FilterChip(
                                selected = selectedDeptId == dept.id,
                                onClick = {
                                    viewModel.selectedDepartmentId.value =
                                        if (selectedDeptId == dept.id) null else dept.id
                                },
                                label = { Text(dept.code, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // 4. Availability Status Filters
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Availability Status",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedAvailability == null,
                            onClick = { viewModel.selectedAvailability.value = null },
                            label = { Text("All Statuses", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                        AvailabilityStatus.entries.forEach { status ->
                            FilterChip(
                                selected = selectedAvailability == status,
                                onClick = {
                                    viewModel.selectedAvailability.value =
                                        if (selectedAvailability == status) null else status
                                },
                                label = { Text(status.displayName, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CampusBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // 5. Result Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Faculty Directory (${filteredFaculty.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDark
                    )
                    if (searchQuery.isNotBlank() || selectedDeptId != null || selectedAvailability != null) {
                        Text(
                            text = "Filters Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CampusBlue
                        )
                    }
                }
            }

            // 6. Faculty List or Empty State
            if (filteredFaculty.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonSearch,
                                contentDescription = "No Faculty Found",
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No faculty members match your criteria",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NavyDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try adjusting your search terms or clearing department/status filters.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredFaculty, key = { it.user.id }) { faculty ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        FacultyCard(
                            faculty = faculty,
                            onViewDetails = { viewModel.selectFacultyDetail(it) }
                        )
                    }
                }
            }
        }

        // Full Detailed Profile Dialog
        selectedFacultyDetail?.let { faculty ->
            FacultyDetailDialog(
                faculty = faculty,
                onDismiss = { viewModel.selectFacultyDetail(null) }
            )
        }
    }
}
