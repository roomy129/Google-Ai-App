package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.model.UserRole
import com.example.ui.components.TopCampusAppBar
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FacultyDashboardScreen
import com.example.ui.screens.StudentDashboardScreen
import com.example.ui.theme.FacultyFinderTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FacultyFinderTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearToast()
        }
    }

    // Hardware/Predictive Back Navigation Handler
    BackHandler(enabled = currentScreen != ScreenDestination.STUDENT_DASHBOARD && currentScreen != ScreenDestination.LANDING) {
        if (currentUser != null) {
            viewModel.navigateTo(ScreenDestination.STUDENT_DASHBOARD)
        } else {
            viewModel.navigateTo(ScreenDestination.LANDING)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != ScreenDestination.LANDING && currentScreen != ScreenDestination.LOGIN) {
                TopCampusAppBar(
                    currentUser = currentUser,
                    onSwitchRole = { newRole ->
                        viewModel.loginWithDemoRole(newRole)
                    },
                    onLogout = {
                        viewModel.logout()
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenDestination.LANDING,
                ScreenDestination.LOGIN -> {
                    AuthScreen(viewModel = viewModel)
                }
                ScreenDestination.STUDENT_DASHBOARD -> {
                    StudentDashboardScreen(viewModel = viewModel)
                }
                ScreenDestination.FACULTY_DASHBOARD -> {
                    FacultyDashboardScreen(viewModel = viewModel)
                }
                ScreenDestination.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
