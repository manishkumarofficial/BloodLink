package com.bloodlink.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.bloodlink.app.ui.BloodLinkViewModel
import com.bloodlink.app.ui.UserRole
import com.bloodlink.app.ui.screens.*
import com.bloodlink.app.ui.theme.MyApplicationTheme

enum class Screen(val route: String) {
    Splash("splash"),
    Onboarding("onboarding"),
    Login("login"),
    LoginOtpVerification("login_otp_verification"),
    Registration("registration"),
    RegistrationOtpVerification("registration_otp_verification"),
    RoleSelection("role_selection"),
    RoleSetupDonor("role_setup_donor"),
    RoleSetupRequester("role_setup_requester"),
    RoleSetupOrganizer("role_setup_organizer"),
    AccountCreationSuccess("account_creation_success"),
    DonorRegistration("donor_registration"),
    HomeDashboard("home_dashboard"),
    ActiveRequests("active_requests"),
    NearbyCamps("nearby_camps"),
    CampDetails("camp_details"),
    RegistrationSuccess("registration_success"),
    Notifications("notifications"),
    CreateRequest("create_request"),
    RequestDashboard("request_dashboard"),
    UserProfile("profile"),
    Achievements("achievements"),
    OrganizerDashboard("organizer_dashboard"),
    CreateCamp("create_camp"),
    AcceptedRequestDetails("accepted_request_details"),
    QRVerification("qr_verification"),
    DonationSuccess("donation_success"),
    Settings("settings"),
    CampManagement("camp_management")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            FirebaseConfigService.initialize(applicationContext)
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "Error during MainActivity Firebase initialization: ${t.message}", t)
        }
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BloodLinkApp()
            }
        }
    }
}

@Composable
fun BloodLinkApp() {
    val navController = rememberNavController()
    val viewModel: BloodLinkViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Tab items for standard Bottom Navigation Bar
    val tabs = remember {
        listOf(
            TabItem("Home", Screen.HomeDashboard.route, Icons.Default.Home),
            TabItem("Requests", Screen.ActiveRequests.route, Icons.Default.Bloodtype),
            TabItem("Camps", Screen.NearbyCamps.route, Icons.Default.Campaign),
            TabItem("History", Screen.Achievements.route, Icons.Default.History),
            TabItem("Profile", Screen.UserProfile.route, Icons.Default.Person)
        )
    }

    // Standardize navigation bar across these main hubs
    val showBottomBar = currentRoute in listOf(
        Screen.HomeDashboard.route,
        Screen.ActiveRequests.route,
        Screen.NearbyCamps.route,
        Screen.Achievements.route,
        Screen.UserProfile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    tonalElevation = 8.dp
                ) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(Screen.HomeDashboard.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                )
                            },
                            label = { Text(text = tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                val scope = rememberCoroutineScope()
                SplashScreen(
                    onNavigateNext = {
                        scope.launch {
                            val isLoggedInVal = viewModel.isLoggedIn.first()
                            val userEmailVal = viewModel.userEmail.first()
                            if (isLoggedInVal && !userEmailVal.isNullOrBlank()) {
                                try {
                                    val user = com.bloodlink.app.data.BloodRepository.getUserProfileByEmail(userEmailVal)
                                    if (user != null) {
                                        viewModel.setupSavedSession(user)
                                        navController.navigate(Screen.HomeDashboard.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                        return@launch
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("MainActivity", "Session auto-login error: ${e.message}")
                                }
                            }
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingCarousel(
                    onNavigateNext = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

             composable(Screen.Login.route) {
                LoginScreen(
                    onLogin = { email, password ->
                        viewModel.login(email, password) { userRole ->
                            navController.navigate(Screen.HomeDashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    },
                    onCreateAccount = {
                        navController.navigate(Screen.Registration.route)
                    },
                    errorMessage = state.authError
                )
            }

            composable(Screen.Registration.route) {
                RegistrationScreen(
                    onCreateAccount = { name, email, password, phone, bg, gender, loc ->
                        viewModel.register(
                            fullName = name,
                            email = email,
                            passwordPlain = password,
                            phoneNumber = phone,
                            bloodGroup = bg,
                            gender = gender,
                            location = loc
                        ) { userRole ->
                            navController.navigate(Screen.HomeDashboard.route) {
                                popUpTo(Screen.Registration.route) { inclusive = true }
                            }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    errorMessage = state.authError
                )
            }

            composable(Screen.RoleSelection.route) {
                RoleSelectionScreen(
                    onRoleSelected = { selectedRole ->
                        viewModel.updateUserRole(selectedRole) {
                            val targetRoute = when (selectedRole) {
                                UserRole.CampOrganizer -> Screen.OrganizerDashboard.route
                                UserRole.Requester -> Screen.RequestDashboard.route
                                UserRole.Donor -> Screen.HomeDashboard.route
                            }
                            navController.navigate(targetRoute) {
                                popUpTo(Screen.RoleSelection.route) { inclusive = true }
                            }
                        }
                    },
                    onBack = {
                        if (state.currentRole == null) {
                            viewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(Screen.DonorRegistration.route) {
                DonorRegistrationScreen(
                    state = state,
                    onComplete = { updatedProfile ->
                        viewModel.updateProfile(updatedProfile)
                        navController.navigate(Screen.HomeDashboard.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.HomeDashboard.route) {
                HomeDashboardScreen(
                    state = state,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateNotifications = { navController.navigate(Screen.Notifications.route) },
                    onToggleAvailability = { viewModel.toggleAvailability(it) },
                    onNavigateCamps = { navController.navigate(Screen.NearbyCamps.route) },
                    onNavigateHistory = { navController.navigate(Screen.Achievements.route) },
                    onNavigateRequestBlood = { navController.navigate(Screen.CreateRequest.route) },
                    onDismissBirthday = { viewModel.dismissBirthdayBanner() },
                    onNavigateCampPass = { navController.navigate(Screen.RegistrationSuccess.route) }
                )
            }

            composable(Screen.ActiveRequests.route) {
                ActiveRequestsListScreen(
                    state = state,
                    onAccept = { reqId ->
                        viewModel.selectRequest(reqId)
                        viewModel.acceptRequest(reqId)
                        navController.navigate(Screen.AcceptedRequestDetails.route)
                    },
                    onReject = { reqId -> viewModel.rejectRequest(reqId) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NearbyCamps.route) {
                NearbyCampsScreen(
                    state = state,
                    onNavigateCampDetails = { campId ->
                        viewModel.selectCamp(campId)
                        navController.navigate(Screen.CampDetails.route)
                    },
                    onNavigateCreateCamp = {
                        navController.navigate(Screen.CreateCamp.route)
                    },
                    onOpenDashboard = { campId ->
                        viewModel.selectCamp(campId)
                        navController.navigate(Screen.OrganizerDashboard.route)
                    },
                    onEditCamp = { campId, title, address, date ->
                        viewModel.updateCamp(campId, title, address, date)
                    },
                    onDeleteCamp = { campId ->
                        viewModel.deleteCamp(campId)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CampDetails.route) {
                CampDetailsScreen(
                    campId = state.selectedCampId,
                    state = state,
                    onNavigateSuccess = { time ->
                        viewModel.registerForCamp(state.selectedCampId, time)
                        navController.navigate(Screen.RegistrationSuccess.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.RegistrationSuccess.route) {
                RegistrationSuccessScreen(
                    slotTime = state.selectedSlotTime,
                    state = state,
                    onNavigateBack = {
                        navController.navigate(Screen.HomeDashboard.route) {
                            popUpTo(Screen.HomeDashboard.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationAlertCenterScreen(
                    state = state,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CreateRequest.route) {
                CreateRequestScreen(
                    state = state,
                    onNavigateNext = {
                        navController.navigate(Screen.RequestDashboard.route)
                    }
                )
            }

            composable(Screen.RequestDashboard.route) {
                RequestProgressDashboardScreen(
                    state = state,
                    onVerifyDonation = { donationId, donorId, donorName, bloodGroup, hospitalName, requestId, timestamp ->
                        viewModel.verifyDonation(donationId, donorId, donorName, bloodGroup, hospitalName, requestId, timestamp)
                    },
                    onConfirmDonation = { donationId, donorId, donorName, bloodGroup, hospitalName, requestId, gender ->
                        viewModel.confirmDonation(donationId, donorId, donorName, bloodGroup, hospitalName, requestId, gender)
                    },
                    onNavigateSuccess = {
                        navController.navigate(Screen.DonationSuccess.route)
                    },
                    onCancel = {
                        navController.navigate(Screen.HomeDashboard.route) {
                            popUpTo(Screen.HomeDashboard.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.UserProfile.route) {
                UserProfileScreen(
                    state = state,
                    onSave = { updated -> viewModel.updateProfile(updated) },
                    onLogout = {
                        val sharedPref = context.getSharedPreferences("bloodlink_session", android.content.Context.MODE_PRIVATE)
                        sharedPref.edit().remove("session_phone").apply()
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onBack = { navController.popBackStack() },
                    onManageCamp = { campId ->
                        viewModel.selectCamp(campId)
                        navController.navigate(Screen.OrganizerDashboard.route)
                    },
                    onViewCamp = { campId ->
                        viewModel.selectCamp(campId)
                        navController.navigate(Screen.CampDetails.route)
                    }
                )
            }

            composable(Screen.Achievements.route) {
                AchievementsProgressScreen(
                    state = state,
                    onNavigateCamps = {
                        navController.navigate(Screen.NearbyCamps.route) {
                            popUpTo(Screen.HomeDashboard.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.OrganizerDashboard.route) {
                OrganizerDashboardScreen(
                    state = state,
                    onNavigateCreateCamp = { navController.navigate(Screen.CreateCamp.route) },
                    onNavigateBack = { navController.popBackStack() },
                    onCheckInDonor = { regId -> viewModel.checkInDonor(regId) },
                    onCompleteDonation = { regId -> viewModel.completeCampDonation(regId) },
                    onRejectDonation = { regId, reason -> viewModel.rejectCampDonation(regId, reason) },
                    onCancelCamp = { campId -> viewModel.cancelCamp(campId) },
                    onDeleteCamp = { campId -> viewModel.deleteCamp(campId) },
                    onUpdateCamp = { campId, title, address, date -> viewModel.updateCamp(campId, title, address, date) },
                    onPauseCamp = { campId -> viewModel.pauseCampRegistration(campId) },
                    onResumeCamp = { campId -> viewModel.resumeCampRegistration(campId) },
                    onCloseCamp = { campId -> viewModel.closeCampRegistration(campId) },
                    onNotifyCamp = { campId, title, msg -> viewModel.notifyCampParticipants(campId, title, msg) },
                    onDuplicateCamp = { campId, title, date -> viewModel.duplicateCamp(campId, title, date) }
                )
            }

            composable(Screen.CampManagement.route) {
                CampManagementScreen(
                    state = state,
                    onNavigateCreateCamp = { navController.navigate(Screen.CreateCamp.route) },
                    onOpenDashboard = { campId ->
                        viewModel.selectCamp(campId)
                        navController.navigate(Screen.OrganizerDashboard.route)
                    },
                    onEditCamp = { campId, title, address, date ->
                        viewModel.updateCamp(campId, title, address, date)
                    },
                    onDeleteCamp = { campId ->
                        viewModel.deleteCamp(campId)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CreateCamp.route) {
                CreateCampScreen(
                    state = state,
                    onPublish = { camp ->
                        viewModel.publishCamp(camp)
                    },
                    onNavigateDashboard = { campId ->
                        viewModel.selectCamp(campId)
                        navController.navigate(Screen.OrganizerDashboard.route) {
                            popUpTo(Screen.NearbyCamps.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AcceptedRequestDetails.route) {
                AcceptedRequestDetailsScreen(
                    state = state,
                    onNavigateQR = {
                        navController.navigate(Screen.QRVerification.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.QRVerification.route) {
                QRVerificationScreen(
                    state = state,
                    onUpdateStatus = { status ->
                        viewModel.updateDonationStatus(status)
                    },
                    onNavigateSuccess = {
                        navController.navigate(Screen.DonationSuccess.route) {
                            popUpTo(Screen.HomeDashboard.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.DonationSuccess.route) {
                DonationSuccessScreen(
                    state = state,
                    onNavigateBack = {
                        navController.navigate(Screen.HomeDashboard.route) {
                            popUpTo(Screen.HomeDashboard.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    state = state,
                    onToggleMute = {
                        viewModel.toggleMute()
                    },
                    onToggleLocationSharing = {
                        viewModel.toggleLocationSharing()
                    },
                    onChangeRole = {
                        navController.navigate(Screen.RoleSelection.route)
                    },
                    isSeedingAllowed = viewModel.isSeedingAllowed(),
                    onSeedData = {
                        viewModel.seedDebugDataManual()
                    },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

data class TabItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
