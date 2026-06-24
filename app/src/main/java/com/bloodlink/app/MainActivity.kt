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
    Settings("settings")
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
            TabItem("Impact", Screen.Achievements.route, Icons.Default.MilitaryTech),
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

    if (state.showAuthErrorDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAuthErrorDialog() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Debug Error",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Firebase Phone Auth Failure",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "A phone verification failure has occurred during the authentication flow. Below are the precise technical details retrieved directly from the Firebase exception.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Error Code:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.authErrorCode ?: "N/A",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Error Message:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.authErrorMessage ?: "No message provided",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Exception Type:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.authExceptionType ?: "N/A",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dismissAuthErrorDialog() }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

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
                            val sharedPref = context.getSharedPreferences("bloodlink_session", android.content.Context.MODE_PRIVATE)
                            val savedPhone = sharedPref.getString("session_phone", null)
                            if (!savedPhone.isNullOrBlank()) {
                                try {
                                    val user = com.bloodlink.app.data.BloodRepository.getUserProfileByPhone(savedPhone)
                                    if (user != null) {
                                        viewModel.setupSavedSession(user)
                                        val targetRoute = when (user.role) {
                                            "Organizer" -> Screen.OrganizerDashboard.route
                                            "Requester" -> Screen.CreateRequest.route
                                            else -> Screen.HomeDashboard.route
                                        }
                                        navController.navigate(targetRoute) {
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
                val activity = context as? android.app.Activity
                LoginScreen(
                    onSendOtp = { mobile ->
                        viewModel.sendLoginOtp(mobile, activity)
                        navController.navigate(Screen.LoginOtpVerification.route)
                    },
                    onCreateAccount = {
                        navController.navigate(Screen.Registration.route)
                    },
                    onGoogleSignIn = {
                        viewModel.setAuthError("Google Sign-In has not been configured in the Google Console for this build. Please sign in using your mobile number and standard Firebase Phone OTP.")
                    }
                )
            }

            composable(Screen.LoginOtpVerification.route) {
                val scope = rememberCoroutineScope()
                LoginOtpVerificationScreen(
                    mobileNumber = state.authMobileNumber,
                    timerSeconds = state.authTimerSeconds,
                    resendAllowed = state.authResendAllowed,
                    simulatedOtp = state.authSentOtp,
                    onVerify = { otp ->
                        scope.launch {
                            val result = viewModel.verifyLoginOtp(otp)
                            if (result.first) {
                                if (result.second) {
                                    val sharedPref = context.getSharedPreferences("bloodlink_session", android.content.Context.MODE_PRIVATE)
                                    sharedPref.edit().putString("session_phone", state.authMobileNumber).apply()

                                    val targetRoute = when (viewModel.uiState.value.currentRole) {
                                        UserRole.Donor -> Screen.HomeDashboard.route
                                        UserRole.Organizer -> Screen.OrganizerDashboard.route
                                        UserRole.Requester -> Screen.CreateRequest.route
                                        null -> Screen.HomeDashboard.route
                                    }
                                    navController.navigate(targetRoute) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.Registration.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    onResend = {
                        val activity = context as? android.app.Activity
                        viewModel.resendLoginOtp(activity)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Registration.route) {
                val scope = rememberCoroutineScope()
                RegistrationScreen(
                    onContinue = { name, mobile, email, dob, gender ->
                        scope.launch {
                            try {
                                val existingUser = com.bloodlink.app.data.BloodRepository.getUserProfileByPhone(mobile)
                                if (existingUser != null) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "An account with this mobile number already exists. Please log in.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Registration.route) { inclusive = true }
                                    }
                                } else {
                                    val activity = context as? android.app.Activity
                                    viewModel.startRegister(name, mobile, email, dob, gender, activity)
                                    navController.navigate(Screen.RegistrationOtpVerification.route)
                                }
                            } catch (e: Exception) {
                                val activity = context as? android.app.Activity
                                viewModel.startRegister(name, mobile, email, dob, gender, activity)
                                navController.navigate(Screen.RegistrationOtpVerification.route)
                            }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.RegistrationOtpVerification.route) {
                RegistrationOtpVerificationScreen(
                    mobileNumber = state.authMobileNumber,
                    timerSeconds = state.authTimerSeconds,
                    resendAllowed = state.authResendAllowed,
                    simulatedOtp = state.authSentOtp,
                    onVerify = { otp ->
                        viewModel.verifyRegisterOtp(otp)
                    },
                    onResend = {
                        val activity = context as? android.app.Activity
                        viewModel.resendLoginOtp(activity)
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    onSuccess = {
                        navController.navigate(Screen.RoleSelection.route)
                    }
                )
            }

            composable(Screen.RoleSelection.route) {
                RoleSelectionScreen(
                    onRoleSelected = { chosenRole ->
                        viewModel.selectRegistrationRole(chosenRole)
                        val targetRoute = when (chosenRole) {
                            UserRole.Donor -> Screen.RoleSetupDonor.route
                            UserRole.Requester -> Screen.RoleSetupRequester.route
                            UserRole.Organizer -> Screen.RoleSetupOrganizer.route
                        }
                        navController.navigate(targetRoute)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.RoleSetupDonor.route) {
                RoleSetupDonorScreen(
                    onComplete = { details ->
                        viewModel.finalizeRegistrationAndCreateAccount(details)
                        val sharedPref = context.getSharedPreferences("bloodlink_session", android.content.Context.MODE_PRIVATE)
                        sharedPref.edit().putString("session_phone", state.currentRegisterMobile).apply()
                        navController.navigate(Screen.AccountCreationSuccess.route)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.RoleSetupRequester.route) {
                RoleSetupRequesterScreen(
                    onComplete = { details ->
                        viewModel.finalizeRegistrationAndCreateAccount(details)
                        val sharedPref = context.getSharedPreferences("bloodlink_session", android.content.Context.MODE_PRIVATE)
                        sharedPref.edit().putString("session_phone", state.currentRegisterMobile).apply()
                        navController.navigate(Screen.AccountCreationSuccess.route)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.RoleSetupOrganizer.route) {
                RoleSetupOrganizerScreen(
                    onComplete = { details ->
                        viewModel.finalizeRegistrationAndCreateAccount(details)
                        val sharedPref = context.getSharedPreferences("bloodlink_session", android.content.Context.MODE_PRIVATE)
                        sharedPref.edit().putString("session_phone", state.currentRegisterMobile).apply()
                        navController.navigate(Screen.AccountCreationSuccess.route)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AccountCreationSuccess.route) {
                AccountCreationSuccessScreen(
                    onNavigateDashboard = {
                        val targetRoute = when (viewModel.uiState.value.currentRole) {
                            UserRole.Donor -> Screen.HomeDashboard.route
                            UserRole.Organizer -> Screen.OrganizerDashboard.route
                            UserRole.Requester -> Screen.CreateRequest.route
                            null -> Screen.HomeDashboard.route
                        }
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Login.route) { inclusive = true }
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
                    onBack = { navController.popBackStack() }
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
                    onRejectDonation = { regId, reason -> viewModel.rejectCampDonation(regId, reason) }
                )
            }

            composable(Screen.CreateCamp.route) {
                CreateCampScreen(
                    state = state,
                    onPublish = { title, host, address ->
                        viewModel.publishCamp(title, host, address)
                        navController.navigate(Screen.OrganizerDashboard.route) {
                            popUpTo(Screen.OrganizerDashboard.route) { inclusive = true }
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
                    isSeedingAllowed = viewModel.isSeedingAllowed(),
                    onSeedData = {
                        viewModel.seedDebugDataManual()
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
