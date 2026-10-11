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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
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

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    var email by remember { mutableStateOf("student@campus.edu") }
    var password by remember { mutableStateOf("student123") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Register states
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val departments by viewModel.departments.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("auth_screen_list"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Presentation Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CampusBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Campus Icon",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Faculty Location Tracking System",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "A Smart Campus Solution for Real-Time Faculty Availability and Location",
                            color = Color(0xFFBAE6FD),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF1E3A8A))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "“Find the Right Faculty, at the Right Place, at the Right Time.”",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Quick 1-Tap Demo Access for Evaluators & Presentations
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚡ Instant Demo Evaluation Access",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NavyDark
                        )
                        Text(
                            text = "Choose any role below to enter directly with preloaded data:",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Student Demo
                            OutlinedButton(
                                onClick = { viewModel.loginWithDemoRole(UserRole.STUDENT) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("demo_student_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = CampusBlue, modifier = Modifier.size(18.dp))
                                    Text("Student", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Faculty Demo
                            OutlinedButton(
                                onClick = { viewModel.loginWithDemoRole(UserRole.FACULTY) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("demo_faculty_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = StatusAvailable, modifier = Modifier.size(18.dp))
                                    Text("Faculty", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Admin Demo
                            OutlinedButton(
                                onClick = { viewModel.loginWithDemoRole(UserRole.ADMIN) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("demo_admin_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                                    Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Authentication Form (Login / Register Tabs)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TabRow(
                            selectedTabIndex = selectedAuthTab,
                            containerColor = Color.Transparent,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        ) {
                            Tab(
                                selected = selectedAuthTab == 0,
                                onClick = { selectedAuthTab = 0 },
                                text = { Text("Account Login", fontWeight = FontWeight.SemiBold) }
                            )
                            Tab(
                                selected = selectedAuthTab == 1,
                                onClick = { selectedAuthTab = 1 },
                                text = { Text("Student Registration", fontWeight = FontWeight.SemiBold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (errorMessage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEE2E2))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    color = StatusBusy,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (selectedAuthTab == 0) {
                            // Login Form
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = null },
                                label = { Text("Campus Email") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CampusBlue) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_email_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; errorMessage = null },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CampusBlue) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_password_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (email.isBlank() || password.isBlank()) {
                                        errorMessage = "Please enter both email and password."
                                    } else {
                                        viewModel.login(email, password) { success, msg ->
                                            if (!success) errorMessage = msg
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("login_submit_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                            ) {
                                Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            // Registration Form
                            OutlinedTextField(
                                value = regName,
                                onValueChange = { regName = it; errorMessage = null },
                                label = { Text("Student Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CampusBlue) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_name_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it; errorMessage = null },
                                label = { Text("College Email (@campus.edu)") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CampusBlue) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_email_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it; errorMessage = null },
                                label = { Text("Create Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CampusBlue) },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (regName.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                                        errorMessage = "All registration fields are required."
                                    } else {
                                        viewModel.registerStudent(
                                            fullName = regName,
                                            email = regEmail,
                                            passwordRaw = regPassword,
                                            deptId = departments.firstOrNull()?.id ?: 1L
                                        ) { success, msg ->
                                            if (!success) errorMessage = msg
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("register_submit_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CampusBlue)
                            ) {
                                Text("Create Student Account", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // Key System Architecture Highlights
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Campus Solution Highlights",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FeatureRow("Real-Time Presence", "Instant availability indicators (Available, In Class, In Lab, In Meeting)")
                        FeatureRow("Campus Floor & Room Radar", "Building, floor number, room coordinates, and department identification")
                        FeatureRow("Timetable & Schedule Sync", "Distinguishes between actual check-in location and expected scheduled class")
                        FeatureRow("Privacy Control", "Zero GPS/continuous background tracking; faculty-controlled location sharing")
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = StatusAvailable,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = NavyDark)
            Text(text = desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 14.sp)
        }
    }
}
