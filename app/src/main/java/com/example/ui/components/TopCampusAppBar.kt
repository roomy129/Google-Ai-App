package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.CampusBlue
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCampusAppBar(
    currentUser: User?,
    onSwitchRole: (UserRole) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NavyPrimary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Title & Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CampusBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Faculty Finder",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Smart Campus Location Tracking",
                        color = Color(0xFFBAE6FD),
                        fontSize = 11.sp
                    )
                }
            }

            // Quick Role Switcher Pill for instant demo & evaluation
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NavyLight)
                        .clickable { showRoleMenu = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("role_switcher_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val roleIcon = when (currentUser?.role) {
                        UserRole.FACULTY.name -> Icons.Default.Person
                        UserRole.ADMIN.name -> Icons.Default.AdminPanelSettings
                        else -> Icons.Default.School
                    }
                    Icon(
                        imageVector = roleIcon,
                        contentDescription = "Role",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "Role",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                }

                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("🎓 Student Dashboard") },
                        onClick = {
                            showRoleMenu = false
                            onSwitchRole(UserRole.STUDENT)
                        },
                        modifier = Modifier.testTag("switch_to_student_menu")
                    )
                    DropdownMenuItem(
                        text = { Text("👨‍🏫 Faculty Dashboard") },
                        onClick = {
                            showRoleMenu = false
                            onSwitchRole(UserRole.FACULTY)
                        },
                        modifier = Modifier.testTag("switch_to_faculty_menu")
                    )
                    DropdownMenuItem(
                        text = { Text("⚙️ Admin Dashboard") },
                        onClick = {
                            showRoleMenu = false
                            onSwitchRole(UserRole.ADMIN)
                        },
                        modifier = Modifier.testTag("switch_to_admin_menu")
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Logout icon
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("logout_icon_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
