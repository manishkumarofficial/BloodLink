package com.bloodlink.app.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloodlink.app.FirebaseConfigService
import com.bloodlink.app.BuildConfig
import com.bloodlink.app.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.FirebaseException
import java.util.concurrent.TimeUnit

enum class UserRole {
    Donor,
    Requester,
    Organizer
}

data class DonationHistoryRecord(
    val donationId: String,
    val donorId: String,
    val donorName: String,
    val bloodGroup: String,
    val hospitalName: String,
    val requestId: String,
    val donationDate: String,
    val verificationTimestamp: String
)

data class ClientProfile(
    val id: String = "alex_rivera_id",
    val name: String = "Alex Rivera",
    val bloodGroup: String = "O+",
    val level: String = "Level 1 Life-Saver",
    val xp: Int = 0,
    val totalDonations: Int = 0,
    val livesSaved: Int = 0,
    val isAvailable: Boolean = true,
    val mobile: String = "+1 (555) 123-4567",
    val birthDate: String = "Oct 14, 1992",
    val gender: String = "Male",
    val travelRadiusKm: Int = 50,
    val profilePictureUri: String? = "https://lh3.googleusercontent.com/aida-public/AB6AXuAMIeNH3jzScvYLrPA4AesJte9Y9rDIezErpsmqwIS_hmFHQ9SVVxk4WzdT7v5ty0RFF_W_Psq2YPLuPWnCfZOJkxLHXTtoPhpqk8GIzw0O2fOgoqeVrv17aSsnmFK-UyRrvjfj5VmnXoZwEjGCKNGtyJUbmo1zkrjiRSsgD-nrurQKqu2yUEgC4fb0v1u3I52jDqFV06Iyy-d68PVBEnupFnknvf9QhVleeeas7Og9vTqq2q4bgMrVA4--WjZCeiedBtzoaaZPlc4",
    val lastDonationDate: String? = null,
    val lastDonationHospital: String? = null,
    val lastDonationBloodGroup: String? = null,
    val lastDonationRequestId: String? = null,
    val nextEligibleDate: String? = null,
    val cooldownStatus: String = "Eligible", // "Eligible" or "Cooldown"
    val cooldownCountdownDays: Int = 0,
    val donationStreak: Int = 0
)

data class CampRegistration(
    val registrationId: String,
    val campId: String,
    val donorId: String,
    val donorName: String,
    val bloodGroup: String,
    val selectedSlot: String,
    val registrationTimestamp: String,
    val status: String, // "Registered", "Checked In", "Donation Completed", "Donation Rejected"
    val qrPayload: String,
    val checkInTime: String? = null,
    val donationVerified: Boolean = false,
    val rejectionReason: String? = null
)

data class BloodRequest(
    val id: String,
    val hospitalName: String,
    val bloodGroup: String,
    val distanceText: String,
    val patientStatus: String,
    val quote: String,
    val isCritical: Boolean,
    val checkedInCount: Int = 0,
    val targetUnits: Int = 3,
    val currentUnits: Int = 1,
    val reachedHospitalCount: Int = 0,
    val donatedCount: Int = 0,
    val isFulfilled: Boolean = false
)

data class DonationCamp(
    val id: String,
    val title: String,
    val organizer: String,
    val address: String,
    val distanceText: String,
    val dateText: String,
    val timeText: String,
    val bloodGroupsNeeded: List<String>,
    val imageUrl: String,
    val mapUrl: String,
    val isUrgent: Boolean = false
)

data class FirestoreUser(
    val userId: String,
    val name: String,
    val mobileNumber: String,
    val email: String? = null,
    val dateOfBirth: String,
    val gender: String,
    val role: UserRole,
    val profileDetails: Map<String, String> = emptyMap(),
    val createdTimestamp: Long = System.currentTimeMillis()
)

data class BloodLinkUiState(
    val firestoreUsers: List<FUser> = emptyList(),
    val authMobileNumber: String = "",
    val authSentOtp: String = "",
    val authTimerSeconds: Int = 30,
    val authResendAllowed: Boolean = false,
    val currentRegisterName: String = "",
    val currentRegisterMobile: String = "",
    val currentRegisterEmail: String = "",
    val currentRegisterDob: String = "",
    val currentRegisterGender: String = "",
    val currentSelectedRole: UserRole? = null,
    val currentSetupDetails: Map<String, String> = emptyMap(),
    val currentRole: UserRole? = null,
    val profile: ClientProfile = ClientProfile(),
    val slotsCreated: Int = 2,
    val selectedSlotTime: String = "10:00 AM",
    val selectedCampId: String = "1",
    val activeRequests: List<BloodRequest> = emptyList(),
    val nearbyCamps: List<DonationCamp> = emptyList(),
    val showBirthdayBanner: Boolean = true,
    val campPledges: Int = 750,
    val unitsCollected: Int = 1248,
    val checkInCount: Int = 89,
    val selectedRequestId: String? = "req_1",
    val activeDonationStatus: String = "Pending", // Pending, On the Way, Reached Hospital, Completed
    val nextDonationCountdownDays: Int = 89,
    val isMuted: Boolean = false,
    val locationSharingEnabled: Boolean = true,
    val donationHistory: List<DonationHistoryRecord> = emptyList(),
    val campRegistrations: List<CampRegistration> = emptyList(),
    val selectedRegistrationId: String? = null,
    val verificationId: String = "",
    val authError: String? = null,
    val authErrorCode: String? = null,
    val authErrorMessage: String? = null,
    val authExceptionType: String? = null,
    val showAuthErrorDialog: Boolean = false,
    val firebaseUserReady: Boolean = false
)

class BloodLinkViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BloodLinkUiState())
    val uiState: StateFlow<BloodLinkUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    
    private var statisticsJob: Job? = null
    private var historyJob: Job? = null
    private var registrationsJob: Job? = null

    init {
        // Setup real-time listener for Blood Requests
        viewModelScope.launch {
            try {
                BloodRepository.listenToActiveRequests().collect { requests ->
                    _uiState.update { state ->
                        state.copy(
                            activeRequests = requests.map { r ->
                                BloodRequest(
                                    id = r.id,
                                    hospitalName = r.hospitalName,
                                    bloodGroup = r.bloodGroup,
                                    distanceText = r.distanceText,
                                    patientStatus = r.patientStatus,
                                    quote = r.quote,
                                    isCritical = r.isCritical,
                                    checkedInCount = r.checkedInCount,
                                    targetUnits = r.targetUnits,
                                    currentUnits = r.currentUnits,
                                    reachedHospitalCount = r.reachedHospitalCount,
                                    donatedCount = r.donatedCount,
                                    isFulfilled = r.isFulfilled
                                )
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error listening to active requests: ${e.message}")
            }
        }

        // Setup real-time listener for Donation Camps
        viewModelScope.launch {
            try {
                BloodRepository.listenToCamps().collect { camps ->
                    _uiState.update { state ->
                        state.copy(
                            nearbyCamps = camps.map { c ->
                                DonationCamp(
                                    id = c.id,
                                    title = c.title,
                                    organizer = c.organizer,
                                    address = c.address,
                                    distanceText = c.distanceText,
                                    dateText = c.dateText,
                                    timeText = c.timeText,
                                    bloodGroupsNeeded = c.bloodGroupsNeeded,
                                    imageUrl = c.imageUrl,
                                    mapUrl = c.mapUrl,
                                    isUrgent = c.isUrgent
                                )
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error listening to camps: ${e.message}")
            }
        }

        // Setup real-time listener for Organizers to view ALL checkins/registrations
        viewModelScope.launch {
            try {
                BloodRepository.listenToAllCampRegistrations().collect { registrations ->
                    _uiState.update { state ->
                        state.copy(
                            campRegistrations = registrations.map { r ->
                                CampRegistration(
                                    registrationId = r.registrationId,
                                    campId = r.campId,
                                    donorId = r.donorId,
                                    donorName = r.donorName,
                                    bloodGroup = r.bloodGroup,
                                    selectedSlot = r.selectedSlot,
                                    registrationTimestamp = r.registrationTimestamp,
                                    status = r.status,
                                    qrPayload = r.qrPayload,
                                    checkInTime = r.checkInTime,
                                    donationVerified = r.donationVerified,
                                    rejectionReason = r.rejectionReason
                                )
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error listening to all camp registrations: ${e.message}")
            }
        }
    }

    fun isSeedingAllowed(): Boolean {
        // Disabled in production & release builds, and disabled for real Firebase projects
        return BuildConfig.DEBUG && FirebaseConfigService.isFallback
    }

    fun seedDebugDataManual() {
        if (!isSeedingAllowed()) {
            Log.e("BloodLinkViewModel", "Manual seeding rejected: Must be in DEBUG build and using FALLBACK/PLACEHOLDER Firebase Configuration.")
            return
        }
        viewModelScope.launch {
            try {
                seedDefaultDataIfNeeded()
                Log.d("BloodLinkViewModel", "Manual seeding completed successfully.")
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error seeding Firestore data manually: ${e.message}")
            }
        }
    }

    private suspend fun seedDefaultDataIfNeeded() {
        val db = FirebaseConfigService.firestore ?: run {
            Log.w("BloodLinkViewModel", "Cannot seed default data: Firestore instance is null")
            return
        }
        
        val reqSnap = db.collection("blood_requests").limit(1).get().await()
        if (reqSnap.isEmpty) {
            val defaults = listOf(
                FBloodRequest(
                    id = "req_1",
                    hospitalName = "St. Jude Medical Center",
                    bloodGroup = "O-",
                    distanceText = "1.2 km away • Downtown District",
                    patientStatus = "Urgent Surgery",
                    quote = "Required for an emergency cardiac procedure. Every minute counts.",
                    isCritical = true,
                    currentUnits = 1,
                    targetUnits = 3,
                    createdAt = System.currentTimeMillis() - 10000
                ),
                FBloodRequest(
                    id = "req_2",
                    hospitalName = "City General Hospital",
                    bloodGroup = "A+",
                    distanceText = "3.8 km away • North Wing",
                    patientStatus = "Planned Transfusion",
                    quote = "Scheduled maintenance for thalassemia patient. Requesting replenishment.",
                    isCritical = false,
                    currentUnits = 2,
                    targetUnits = 2,
                    createdAt = System.currentTimeMillis() - 8000
                ),
                FBloodRequest(
                    id = "req_3",
                    hospitalName = "Red Cross Center",
                    bloodGroup = "B+",
                    distanceText = "0.5 km away • East Plaza",
                    patientStatus = "Trauma Response",
                    quote = "Urgent need for trauma unit following multi-vehicle accident.",
                    isCritical = true,
                    currentUnits = 0,
                    targetUnits = 3,
                    createdAt = System.currentTimeMillis() - 5000
                )
            )
            for (r in defaults) {
                db.collection("blood_requests").document(r.id).set(r).await()
            }
        }

        val campSnap = db.collection("camps").limit(1).get().await()
        if (campSnap.isEmpty) {
            val defaults = listOf(
                FDonationCamp(
                    id = "1",
                    title = "City Center Comm. Drive",
                    organizer = "American Red Cross",
                    address = "1200 Metropolitan Ave, Suite 100",
                    distanceText = "2.4 mi",
                    dateText = "Oct 24 - 26",
                    timeText = "9:00 AM - 4:00 PM",
                    bloodGroupsNeeded = listOf("O+", "A-", "B+"),
                    isUrgent = true,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCwGHyxisPgOArtiO-XpMExa07NS6q-hjXUR_oqw8nZo0lyc517bcB4cQiBKb7u8sczMJZq5wMgk6qGhMtwOlST2YmeP2ssyVV9ww0l7JD8o82CI1H6yo-z7QPqyg6vVHvK4Iwm-0Adk_aFPkW0kueRwk-hNLdAdkuTj9a8EbyfwiNqvfOjJ0_4LfMEA9Y-xI-lnGEtMw-XXTKIKk6viZVeRYfsngAFobYSwGaEkctbO9YLkGcu8sa6NfbSrlEFzhhW0cFwp0_8ZiE",
                    mapUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBLtaj8KXlPRIm_stpT-09gv5YEv7eEZoH1B86ilUV2w6szlqsppFjv1a44cdi15FwC14HOJ133sXLOfK7yt_Iil1xbuvTU41chcy2vGr1l2DFYC2rBvkGMqJozxlI7uP3eXV_8_X42-HYRrqCe0iXWSkinxMn9Frfat05a8K2Oh6n3faauXsRI9zseZAvD_HV-IC10UE_pyfV8LdtksyPtD-QLWaSoQIh9mOZm-MNIKd-auUZLsAIycGJBMMzV1Kg-ZniMK1eeCug",
                    createdAt = System.currentTimeMillis() - 10000
                ),
                FDonationCamp(
                    id = "2",
                    title = "Westside General Hospital",
                    organizer = "Westside Healthcare Team",
                    address = "450 Health Way, Main Lobby",
                    distanceText = "5.8 mi",
                    dateText = "Oct 25 Only",
                    timeText = "10:00 AM - 6:00 PM",
                    bloodGroupsNeeded = listOf("AB+", "O-"),
                    isUrgent = false,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCwGHyxisPgOArtiO-XpMExa07NS6q-hjXUR_oqw8nZo0lyc517bcB4cQiBKb7u8sczMJZq5wMgk6qGhMtwOlST2YmeP2ssyVV9ww0l7JD8o82CI1H6yo-z7QPqyg6vVHvK4Iwm-0Adk_aFPkW0kueRwk-hNLdAdkuTj9a8EbyfwiNqvfOjJ0_4LfMEA9Y-xI-lnGEtMw-XXTKIKk6viZVeRYfsngAFobYSwGaEkctbO9YLkGcu8sa6NfbSrlEFzhhW0cFwp0_8ZiE",
                    mapUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBn77yxj5Mn4WzvNsosslf-jZBjCb4Jbw-ba6XhYL_n2udVf3VwS90Ke1pYkc7wGKP0BgXFjoheCs5GBMBcljnxHgp8zkVUU-SuaS5z9MGuxRev76vCYlyC92LymCOc-UQn3kLnPPTR6H5Cg4PPbrRZwWSUuTrUgaJtSGAmfnixcj6RzJpkQI71hiFM0tPGpHBIRktporV-_a2N-k1GDG8maC_znumKWXdn9yyxhZh8FAjbPit4ehyBWbD2U1hP74xoybsRAKeNwtk",
                    createdAt = System.currentTimeMillis() - 8000
                ),
                FDonationCamp(
                    id = "3",
                    title = "Tech Campus Mobile Drive",
                    organizer = "Google Staff Volunteers",
                    address = "Building 4 Parking Lot, Zone B",
                    distanceText = "8.1 mi",
                    dateText = "Oct 28 - 29",
                    timeText = "8:00 AM - 2:00 PM",
                    bloodGroupsNeeded = listOf("All Groups"),
                    isUrgent = false,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCwGHyxisPgOArtiO-XpMExa07NS6q-hjXUR_oqw8nZo0lyc517bcB4cQiBKb7u8sczMJZq5wMgk6qGhMtwOlST2YmeP2ssyVV9ww0l7JD8o82CI1H6yo-z7QPqyg6vVHvK4Iwm-0Adk_aFPkW0kueRwk-hNLdAdkuTj9a8EbyfwiNqvfOjJ0_4LfMEA9Y-xI-lnGEtMw-XXTKIKk6viZVeRYfsngAFobYSwGaEkctbO9YLkGcu8sa6NfbSrlEFzhhW0cFwp0_8ZiE",
                    mapUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDgKCSTe7MyjREXGxA7IWjWZT_MVdyc9C9FCtIpa5KK_9_Ecn4Lre9fJXi0jVH3DXpzn1q197JwdpTkuA4-uXELTfuY4ZsqB1cXuZAsrWUom3uwVWBD2SNfJbM-F7NoZ2e56-7AS8aoMf0GVtTqNnH8owJjmxeXKjPxiB3dH80Uqnm-dIyeoyzSlRbXL_9HsVedF3nT7SEQ5gDtJ9pYZX-JAZdAKqAPPrHzPaB240JgCu20jV8CbMuxaH4J9h6dAkW_v0yhlgdO9Kg",
                    createdAt = System.currentTimeMillis() - 5000
                )
            )
            for (c in defaults) {
                db.collection("camps").document(c.id).set(c).await()
            }
        }
    }

    fun setupUserListeners(userId: String) {
        // Statistics Snapshot Listener
        statisticsJob?.cancel()
        statisticsJob = viewModelScope.launch {
            try {
                BloodRepository.listenToUserStatistics(userId).collect { stats ->
                    if (stats != null) {
                        _uiState.update { state ->
                            state.copy(
                                profile = state.profile.copy(
                                    totalDonations = stats.totalDonations,
                                    livesSaved = stats.livesSaved,
                                    xp = stats.totalXp,
                                    level = stats.level,
                                    donationStreak = stats.currentStreak
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error in listenToUserStatistics: ${e.message}")
            }
        }

        // History Snapshot Listener
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            try {
                BloodRepository.listenToDonationHistory(userId).collect { history ->
                    _uiState.update { state ->
                        state.copy(
                            donationHistory = history.map { record ->
                                DonationHistoryRecord(
                                    donationId = record.donationId,
                                    donorId = record.donorId,
                                    donorName = record.donorName,
                                    bloodGroup = record.bloodGroup,
                                    hospitalName = record.hospitalName,
                                    requestId = record.requestId,
                                    donationDate = record.donationDate,
                                    verificationTimestamp = record.verificationTimestamp
                                )
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error in listenToDonationHistory: ${e.message}")
            }
        }

        // Camp Registrations Snapshot Listener
        registrationsJob?.cancel()
        registrationsJob = viewModelScope.launch {
            try {
                BloodRepository.listenToCampRegistrations(userId).collect { regs ->
                    _uiState.update { state ->
                        state.copy(
                            campRegistrations = regs.map { r ->
                                CampRegistration(
                                    registrationId = r.registrationId,
                                    campId = r.campId,
                                    donorId = r.donorId,
                                    donorName = r.donorName,
                                    bloodGroup = r.bloodGroup,
                                    selectedSlot = r.selectedSlot,
                                    registrationTimestamp = r.registrationTimestamp,
                                    status = r.status,
                                    qrPayload = r.qrPayload,
                                    checkInTime = r.checkInTime,
                                    donationVerified = r.donationVerified,
                                    rejectionReason = r.rejectionReason
                                )
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error in listenToCampRegistrations: ${e.message}")
            }
        }
    }

    fun sendLoginOtp(mobileNumber: String, activity: android.app.Activity? = null) {
        _uiState.update {
            it.copy(
                authMobileNumber = mobileNumber,
                authError = null,
                authSentOtp = "",
                verificationId = ""
            )
        }
        
        val auth = com.bloodlink.app.FirebaseConfigService.auth
        if (auth == null) {
            _uiState.update {
                it.copy(
                    authError = "Firebase Auth failed to initialize. Please check your network and google-services.json configuration."
                )
            }
            return
        }
        if (activity == null) {
            _uiState.update {
                it.copy(
                    authError = "Required Activity context is missing. Real Phone authentication cannot proceed."
                )
            }
            return
        }

        Log.i("BloodLinkViewModel", "sendLoginOtp: Attempting real Firebase Phone Auth for: $mobileNumber")
        try {
            val formattedPhone = if (mobileNumber.startsWith("+")) mobileNumber else "+91$mobileNumber"
            
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.i("BloodLinkViewModel", "PhoneAuth: onVerificationCompleted called.")
                    viewModelScope.launch {
                        try {
                            val result = auth.signInWithCredential(credential).await()
                            Log.i("BloodLinkViewModel", "PhoneAuth: Automatically signed in successfully: ${result.user?.uid}")
                            _uiState.update { 
                                it.copy(
                                    verificationId = "",
                                    firebaseUserReady = true,
                                    authSentOtp = "AUTO",
                                    authError = null
                                ) 
                            }
                        } catch (e: Exception) {
                            Log.e("BloodLinkViewModel", "PhoneAuth: Auto-sign in failed: ${e.message}")
                            _uiState.update { it.copy(authError = "Automatic sign-in failed: ${e.message}") }
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("BloodLinkViewModel", "PhoneAuth: onVerificationFailed: code/msg: ${e.message}", e)
                    val errorCode = if (e is com.google.firebase.auth.FirebaseAuthException) e.errorCode else "N/A"
                    val errorMessage = e.message ?: "No error message provided"
                    val exceptionType = e.javaClass.name
                    _uiState.update { 
                        it.copy(
                            authSentOtp = "",
                            verificationId = "",
                            authError = errorMessage,
                            authErrorCode = errorCode,
                            authErrorMessage = errorMessage,
                            authExceptionType = exceptionType,
                            showAuthErrorDialog = true
                        ) 
                    }
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.i("BloodLinkViewModel", "PhoneAuth: onCodeSent: $id")
                    _uiState.update { 
                        it.copy(
                            verificationId = id,
                            authSentOtp = "FIREBASE_SENT",
                            authError = null,
                            authErrorCode = null,
                            authErrorMessage = null,
                            authExceptionType = null,
                            showAuthErrorDialog = false
                        ) 
                    }
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (t: Throwable) {
            Log.e("BloodLinkViewModel", "PhoneAuth: Exception during verifyPhoneNumber: ${t.message}", t)
            val errorCode = if (t is com.google.firebase.auth.FirebaseAuthException) t.errorCode else "N/A"
            val errorMessage = t.message ?: "No error message provided"
            val exceptionType = t.javaClass.name
            _uiState.update {
                it.copy(
                    authSentOtp = "",
                    verificationId = "",
                    authError = errorMessage,
                    authErrorCode = errorCode,
                    authErrorMessage = errorMessage,
                    authExceptionType = exceptionType,
                    showAuthErrorDialog = true
                )
            }
        }
        
        _uiState.update {
            it.copy(
                authTimerSeconds = 30,
                authResendAllowed = false
            )
        }
        startOtpTimer()
    }

    fun resendLoginOtp(activity: android.app.Activity? = null) {
        sendLoginOtp(_uiState.value.authMobileNumber, activity)
    }

    private fun startOtpTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.authTimerSeconds > 0) {
                delay(1000)
                _uiState.update { it.copy(authTimerSeconds = it.authTimerSeconds - 1) }
            }
            _uiState.update { it.copy(authResendAllowed = true) }
        }
    }

    // New real Firestore-integrated authentication handler
    suspend fun verifyLoginOtp(otp: String): Pair<Boolean, Boolean> {
        val state = _uiState.value
        
        if (state.verificationId.isEmpty()) {
            _uiState.update { it.copy(authError = "Phone verification was not initiated or has failed.") }
            return Pair(false, false)
        }

        try {
            val auth = com.bloodlink.app.FirebaseConfigService.auth ?: throw IllegalStateException("Firebase Auth not available")
            val credential = PhoneAuthProvider.getCredential(state.verificationId, otp)
            val result = auth.signInWithCredential(credential).await()
            Log.i("BloodLinkViewModel", "PhoneAuth: Manual code entry signed in successfully: ${result.user?.uid}")
        } catch (e: Exception) {
            Log.e("BloodLinkViewModel", "PhoneAuth: Manual signInWithCredential failed: ${e.message}")
            _uiState.update { it.copy(authError = "Invalid verification code: ${e.message}") }
            return Pair(false, false)
        }

        val existingUser = BloodRepository.getUserProfileByPhone(state.authMobileNumber)
        if (existingUser != null) {
            setupUserListeners(existingUser.userId)
            val roleEnum = when (existingUser.role) {
                "Donor" -> UserRole.Donor
                "Organizer" -> UserRole.Organizer
                "Requester" -> UserRole.Requester
                else -> UserRole.Donor
            }
            _uiState.update {
                it.copy(
                    currentRole = roleEnum,
                    profile = ClientProfile(
                        id = existingUser.userId,
                        name = existingUser.fullName,
                        mobile = existingUser.phoneNumber,
                        birthDate = existingUser.dateOfBirth,
                        gender = existingUser.gender,
                        bloodGroup = existingUser.bloodGroup,
                        travelRadiusKm = existingUser.travelRadius,
                        isAvailable = existingUser.availabilityStatus,
                        lastDonationDate = existingUser.lastDonationDate.ifBlank { null },
                        nextEligibleDate = existingUser.nextEligibleDate.ifBlank { null },
                        cooldownStatus = existingUser.cooldownStatus,
                        totalDonations = existingUser.donationCount,
                        livesSaved = existingUser.livesSaved,
                        level = existingUser.heroLevel
                    )
                )
            }
            return Pair(true, true)
        } else {
            return Pair(true, false)
        }
    }

    fun setupSavedSession(existingUser: com.bloodlink.app.data.FUser) {
        setupUserListeners(existingUser.userId)
        val roleEnum = when (existingUser.role) {
            "Donor" -> UserRole.Donor
            "Organizer" -> UserRole.Organizer
            "Requester" -> UserRole.Requester
            else -> UserRole.Donor
        }
        _uiState.update {
            it.copy(
                currentRole = roleEnum,
                profile = ClientProfile(
                    id = existingUser.userId,
                    name = existingUser.fullName,
                    mobile = existingUser.phoneNumber,
                    birthDate = existingUser.dateOfBirth,
                    gender = existingUser.gender,
                    bloodGroup = existingUser.bloodGroup,
                    travelRadiusKm = existingUser.travelRadius,
                    isAvailable = existingUser.availabilityStatus,
                    lastDonationDate = existingUser.lastDonationDate.ifBlank { null },
                    nextEligibleDate = existingUser.nextEligibleDate.ifBlank { null },
                    cooldownStatus = existingUser.cooldownStatus,
                    totalDonations = existingUser.donationCount,
                    livesSaved = existingUser.livesSaved,
                    level = existingUser.heroLevel
                )
            )
        }
    }

    fun logout() {
        statisticsJob?.cancel()
        historyJob?.cancel()
        registrationsJob?.cancel()
        try {
            val auth = com.bloodlink.app.FirebaseConfigService.auth
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("BloodLinkViewModel", "Error signing out of Firebase Auth: ${e.message}")
        }
        _uiState.update {
            BloodLinkUiState()
        }
    }

    fun startRegister(name: String, mobile: String, email: String, dob: String, gender: String, activity: android.app.Activity? = null) {
        _uiState.update {
            it.copy(
                currentRegisterName = name,
                currentRegisterMobile = mobile,
                currentRegisterEmail = email,
                currentRegisterDob = dob,
                currentRegisterGender = gender,
                authMobileNumber = mobile,
                authError = null,
                authSentOtp = "",
                verificationId = ""
            )
        }
        
        val auth = com.bloodlink.app.FirebaseConfigService.auth
        if (auth == null) {
            _uiState.update {
                it.copy(
                    authError = "Firebase Auth failed to initialize. Please check your network and google-services.json configuration."
                )
            }
            return
        }
        if (activity == null) {
            _uiState.update {
                it.copy(
                    authError = "Required Activity context is missing. Real Phone authentication cannot proceed."
                )
            }
            return
        }

        Log.i("BloodLinkViewModel", "startRegister: Attempting real Firebase Phone Auth for: $mobile")
        try {
            val formattedPhone = if (mobile.startsWith("+")) mobile else "+91$mobile"
            
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.i("BloodLinkViewModel", "Register PhoneAuth: onVerificationCompleted called.")
                    viewModelScope.launch {
                        try {
                            val result = auth.signInWithCredential(credential).await()
                            Log.i("BloodLinkViewModel", "Register PhoneAuth: Successfully signed in: ${result.user?.uid}")
                            _uiState.update { 
                                it.copy(
                                    verificationId = "",
                                    firebaseUserReady = true,
                                    authSentOtp = "AUTO",
                                    authError = null
                                ) 
                            }
                        } catch (e: Exception) {
                            Log.e("BloodLinkViewModel", "Register PhoneAuth: Sign in failed: ${e.message}")
                            _uiState.update { it.copy(authError = "Sign-in failed: ${e.message}") }
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("BloodLinkViewModel", "Register PhoneAuth: onVerificationFailed: code/msg: ${e.message}", e)
                    val errorCode = if (e is com.google.firebase.auth.FirebaseAuthException) e.errorCode else "N/A"
                    val errorMessage = e.message ?: "No error message provided"
                    val exceptionType = e.javaClass.name
                    _uiState.update { 
                        it.copy(
                            authSentOtp = "",
                            verificationId = "",
                            authError = errorMessage,
                            authErrorCode = errorCode,
                            authErrorMessage = errorMessage,
                            authExceptionType = exceptionType,
                            showAuthErrorDialog = true
                        ) 
                    }
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.i("BloodLinkViewModel", "Register PhoneAuth: onCodeSent: $id")
                    _uiState.update { 
                        it.copy(
                            verificationId = id,
                            authSentOtp = "FIREBASE_SENT",
                            authError = null,
                            authErrorCode = null,
                            authErrorMessage = null,
                            authExceptionType = null,
                            showAuthErrorDialog = false
                        ) 
                    }
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (t: Throwable) {
            Log.e("BloodLinkViewModel", "Register PhoneAuth: Exception during verifyPhoneNumber: ${t.message}", t)
            val errorCode = if (t is com.google.firebase.auth.FirebaseAuthException) t.errorCode else "N/A"
            val errorMessage = t.message ?: "No error message provided"
            val exceptionType = t.javaClass.name
            _uiState.update {
                it.copy(
                    authSentOtp = "",
                    verificationId = "",
                    authError = errorMessage,
                    authErrorCode = errorCode,
                    authErrorMessage = errorMessage,
                    authExceptionType = exceptionType,
                    showAuthErrorDialog = true
                )
            }
        }
        
        _uiState.update {
            it.copy(
                authTimerSeconds = 30,
                authResendAllowed = false
            )
        }
        startOtpTimer()
    }

    suspend fun verifyRegisterOtp(otp: String): Boolean {
        val state = _uiState.value
        
        if (state.verificationId.isEmpty()) {
            _uiState.update { it.copy(authError = "Phone verification was not initiated or has failed.") }
            return false
        }

        return try {
            val auth = com.bloodlink.app.FirebaseConfigService.auth ?: throw IllegalStateException("Firebase Auth not available")
            val credential = PhoneAuthProvider.getCredential(state.verificationId, otp)
            auth.signInWithCredential(credential).await()
            Log.i("BloodLinkViewModel", "Register PhoneAuth: Manual code entry signed in successfully.")
            true
        } catch (e: Exception) {
            Log.e("BloodLinkViewModel", "Register PhoneAuth: Manual signInWithCredential failed: ${e.message}")
            _uiState.update { it.copy(authError = "Invalid verification code: ${e.message}") }
            false
        }
    }

    fun selectRegistrationRole(role: UserRole) {
        _uiState.update { it.copy(currentSelectedRole = role) }
    }

    fun setAuthError(message: String) {
        _uiState.update { it.copy(authError = message) }
    }

    fun dismissAuthErrorDialog() {
        _uiState.update { it.copy(showAuthErrorDialog = false) }
    }

    fun finalizeRegistrationAndCreateAccount(details: Map<String, String>) {
        val state = _uiState.value
        val newUserId = "user_" + System.currentTimeMillis()
        val roleStr = when (state.currentSelectedRole) {
            UserRole.Donor -> "Donor"
            UserRole.Organizer -> "Organizer"
            UserRole.Requester -> "Requester"
            null -> "Donor"
        }
        
        val user = FUser(
            userId = newUserId,
            fullName = state.currentRegisterName,
            phoneNumber = state.currentRegisterMobile,
            email = state.currentRegisterEmail,
            dateOfBirth = state.currentRegisterDob,
            gender = state.currentRegisterGender,
            role = roleStr,
            bloodGroup = details["bloodGroup"] ?: "O+",
            location = details["location"] ?: "Tech Park Main Lobby",
            travelRadius = details["travelRadiusKm"]?.toIntOrNull() ?: 50,
            availabilityStatus = true,
            completedProfile = true
        )

        viewModelScope.launch {
            try {
                BloodRepository.createUserProfile(user)
                setupUserListeners(user.userId)
                
                _uiState.update {
                    it.copy(
                        currentRole = state.currentSelectedRole,
                        profile = ClientProfile(
                            id = user.userId,
                            name = user.fullName,
                            mobile = user.phoneNumber,
                            birthDate = user.dateOfBirth,
                            gender = user.gender,
                            bloodGroup = user.bloodGroup,
                            travelRadiusKm = user.travelRadius,
                            isAvailable = user.availabilityStatus,
                            totalDonations = 0,
                            livesSaved = 0,
                            level = "Level 1 Life-Saver"
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error finalizing account creation: ${e.message}")
            }
        }
    }

    fun selectRole(role: UserRole) {
        _uiState.update { it.copy(currentRole = role) }
    }

    fun updateProfile(profile: ClientProfile) {
        _uiState.update { it.copy(profile = profile) }
        
        viewModelScope.launch {
            try {
                val current = BloodRepository.getUserProfile(profile.id) ?: FUser(userId = profile.id)
                val updatedUser = current.copy(
                    fullName = profile.name,
                    phoneNumber = profile.mobile,
                    gender = profile.gender,
                    dateOfBirth = profile.birthDate,
                    bloodGroup = profile.bloodGroup,
                    travelRadius = profile.travelRadiusKm,
                    availabilityStatus = profile.isAvailable,
                    donationCount = profile.totalDonations,
                    livesSaved = profile.livesSaved,
                    nextEligibleDate = profile.nextEligibleDate ?: "",
                    cooldownStatus = profile.cooldownStatus,
                    heroLevel = profile.level,
                    updatedAt = System.currentTimeMillis()
                )
                BloodRepository.updateUserProfile(updatedUser)
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error syncing profile update to Firestore: ${e.message}")
            }
        }
    }

    fun toggleAvailability(isAvailable: Boolean) {
        _uiState.update { state ->
            state.copy(profile = state.profile.copy(isAvailable = isAvailable))
        }
        updateProfile(_uiState.value.profile)
    }

    fun updateRadius(radius: Int) {
        _uiState.update { state ->
            state.copy(profile = state.profile.copy(travelRadiusKm = radius))
        }
        updateProfile(_uiState.value.profile)
    }

    fun selectSlot(time: String) {
        _uiState.update { it.copy(selectedSlotTime = time) }
    }

    fun selectCamp(campId: String) {
        _uiState.update { it.copy(selectedCampId = campId) }
    }

    fun dismissBirthdayBanner() {
        _uiState.update { it.copy(showBirthdayBanner = false) }
    }

    fun acceptRequest(id: String) {
        val state = _uiState.value
        val req = state.activeRequests.find { it.id == id } ?: return
        
        viewModelScope.launch {
            try {
                val updatedFReq = FBloodRequest(
                    id = req.id,
                    hospitalName = req.hospitalName,
                    bloodGroup = req.bloodGroup,
                    distanceText = req.distanceText,
                    patientStatus = req.patientStatus,
                    quote = req.quote,
                    isCritical = req.isCritical,
                    checkedInCount = req.checkedInCount,
                    targetUnits = req.targetUnits,
                    currentUnits = (req.currentUnits + 1).coerceAtMost(req.targetUnits),
                    reachedHospitalCount = req.reachedHospitalCount,
                    donatedCount = req.donatedCount,
                    isFulfilled = (req.currentUnits + 1) >= req.targetUnits
                )
                BloodRepository.updateBloodRequest(updatedFReq)
                
                val updatedProfile = state.profile.copy(
                    livesSaved = state.profile.livesSaved + 3,
                    totalDonations = state.profile.totalDonations + 1,
                    xp = state.profile.xp + 50
                )
                updateProfile(updatedProfile)
                
                val notification = FNotification(
                    userId = state.profile.id,
                    title = "Request Accepted",
                    body = "You successfully accepted request for ${req.bloodGroup} at ${req.hospitalName}.",
                    timestamp = System.currentTimeMillis()
                )
                BloodRepository.storeNotification(notification)
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error in acceptRequest: ${e.message}")
            }
        }
    }

    fun rejectRequest(id: String) {
        _uiState.update { state ->
            state.copy(activeRequests = state.activeRequests.filterNot { it.id == id })
        }
    }

    fun publishCamp(title: String, organizer: String, address: String) {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                val camp = FDonationCamp(
                    title = title,
                    organizer = organizer,
                    organizerId = state.profile.id,
                    address = address,
                    distanceText = "0.1 mi",
                    dateText = "Nov 15 - 16",
                    timeText = "9:00 AM - 5:00 PM",
                    bloodGroupsNeeded = listOf("All Groups"),
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCwGHyxisPgOArtiO-XpMExa07NS6q-hjXUR_oqw8nZo0lyc517bcB4cQiBKb7u8sczMJZq5wMgk6qGhMtwOlST2YmeP2ssyVV9ww0l7JD8o82CI1H6yo-z7QPqyg6vVHvK4Iwm-0Adk_aFPkW0kueRwk-hNLdAdkuTj9a8EbyfwiNqvfOjJ0_4LfMEA9Y-xI-lnGEtMw-XXTKIKk6viZVeRYfsngAFobYSwGaEkctbO9YLkGcu8sa6NfbSrlEFzhhW0cFwp0_8ZiE",
                    mapUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCQbUUITwdRstlGmM_2mv5_ahG8JgdgGouYKsoQ5Nb28qTl30GOkptPiAaM36zxBDiZR1SpWdpYCcTrPs4YOQmTDcITUH9-fznHGv9lQ8MvOBxEM_ZMzw2jVOrdQbhLJVMq3zTn36RjXFpFCmCeWcpy-j8UUZ8fiM-z3PdRrTb66_Sa1Fv7O8W0lNaqtguRH1SEtrFxGzn5LZaHycvBWcdlNhxLkPH5abX5T3f2sYtSqzgZyu7Ii8T-dTEvRRSMcUK_lUhjPhHpJK8"
                )
                BloodRepository.createCamp(camp)
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error publishing camp: ${e.message}")
            }
        }
    }

    fun selectRequest(id: String?) {
        _uiState.update { it.copy(selectedRequestId = id) }
    }

    fun updateDonationStatus(status: String) {
        _uiState.update { it.copy(activeDonationStatus = status) }
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleLocationSharing() {
        _uiState.update { it.copy(locationSharingEnabled = !it.locationSharingEnabled) }
    }

    fun createBloodRequest(patientName: String, groupNeeded: String, hospital: String, units: Int, isCritical: Boolean) {
        val state = _uiState.value
        val request = FBloodRequest(
            requesterId = state.profile.id,
            hospitalName = hospital,
            bloodGroup = groupNeeded,
            distanceText = "0.5 km away • User-created",
            patientStatus = if (isCritical) "Critical Emergency" else "Planned Transfusion",
            quote = "Blood needed immediately for $patientName at $hospital.",
            isCritical = isCritical,
            targetUnits = units,
            currentUnits = 0
        )
        viewModelScope.launch {
            try {
                BloodRepository.createBloodRequest(request)
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "Error creating request: ${e.message}")
            }
        }
    }

    private fun stateFlowValue() = _uiState.value

    fun verifyDonation(
        donationId: String,
        donorId: String,
        donorName: String,
        bloodGroup: String,
        hospitalName: String,
        requestId: String,
        timestamp: String
    ): String? {
        val uiStateVal = _uiState.value
        val alreadyExists = uiStateVal.donationHistory.any { it.donationId == donationId }
        if (alreadyExists) {
            return "Duplicate Scan: Donation has already been verified and recorded."
        }
        val activeReq = uiStateVal.activeRequests.find { it.id == requestId }
        if (activeReq == null) {
            return "Mismatched Request: This donation is for a different or inactive blood request."
        }
        if (activeReq.isFulfilled) {
            return "Already Fulfilled: This blood request is already fully completed."
        }
        if (donorId.isBlank() || donorName.isBlank()) {
            return "Invalid Donor: QR code contains incomplete donor profile identification."
        }
        return null
    }

    fun confirmDonation(
        donationId: String,
        donorId: String,
        donorName: String,
        bloodGroup: String,
        hospitalName: String,
        requestId: String,
        gender: String
    ) {
        val now = java.time.LocalDate.now()
        val dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        val timestampStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val cooldownDays = if (gender.equals("Male", ignoreCase = true)) 90 else 120
        val eligibleDate = now.plusDays(cooldownDays.toLong())
        val eligibleDateStr = eligibleDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"))

        viewModelScope.launch {
            try {
                val record = FDonationHistoryRecord(
                    donationId = donationId,
                    donorId = donorId,
                    donorName = donorName,
                    bloodGroup = bloodGroup,
                    hospitalName = hospitalName,
                    requestId = requestId,
                    donationDate = dateStr,
                    verificationTimestamp = timestampStr
                )
                BloodRepository.confirmDonation(record)

                val db = FirebaseConfigService.firestore
                if (db != null) {
                    val reqSnap = db.collection("blood_requests").document(requestId).get().await()
                    if (reqSnap.exists()) {
                        val req = reqSnap.toObject(FBloodRequest::class.java)
                        if (req != null) {
                            val newReceived = req.currentUnits + 1
                            val updatedFReq = req.copy(
                                currentUnits = newReceived,
                                reachedHospitalCount = req.reachedHospitalCount + 1,
                                donatedCount = req.donatedCount + 1,
                                isFulfilled = newReceived >= req.targetUnits
                            )
                            BloodRepository.updateBloodRequest(updatedFReq)
                        }
                    }
                } else {
                    Log.w("BloodLinkViewModel", "Cannot update blood units: Firestore instance is null")
                }

                val state = _uiState.value
                val isCurrentDonor = state.profile.name == donorName || donorId == "alex_rivera_id"
                if (isCurrentDonor) {
                    val updatedProfile = state.profile.copy(
                        totalDonations = state.profile.totalDonations + 1,
                        livesSaved = state.profile.livesSaved + 3,
                        xp = state.profile.xp + 100,
                        isAvailable = false,
                        lastDonationDate = dateStr,
                        lastDonationHospital = hospitalName,
                        lastDonationBloodGroup = bloodGroup,
                        lastDonationRequestId = requestId,
                        nextEligibleDate = eligibleDateStr,
                        cooldownStatus = "Cooldown"
                    )
                    updateProfile(updatedProfile)

                    BloodRepository.updateCooldown(donorId, "Cooldown", cooldownDays - 1, eligibleDateStr)

                    val achievement = FAchievement(
                        id = "ach_${System.currentTimeMillis()}",
                        userId = donorId,
                        title = "Humbled Hero",
                        description = "Logged your verified real-time blood donation",
                        xpAwarded = 100
                    )
                    BloodRepository.updateAchievements(achievement)
                }

                _uiState.update { it.copy(activeDonationStatus = "Completed") }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "confirmDonation failed: ${e.message}")
            }
        }
    }

    fun registerForCamp(campId: String, slotTime: String) {
        val state = _uiState.value
        val registrationId = "CR-" + (100 + state.campRegistrations.size + 1).toString()
        val profile = state.profile
        val nowIso = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        
        val payload = "CAMP_ID:${campId}|REG_ID:${registrationId}|DONOR_ID:${profile.id}|NAME:${profile.name}|BLOOD:${profile.bloodGroup}|TIME:${slotTime}|TS:${nowIso}"
        
        val registration = FCampRegistration(
            registrationId = registrationId,
            campId = campId,
            donorId = profile.id,
            donorName = profile.name,
            bloodGroup = profile.bloodGroup,
            selectedSlot = slotTime,
            registrationTimestamp = nowIso,
            status = "Registered",
            qrPayload = payload
        )

        viewModelScope.launch {
            try {
                BloodRepository.registerForCamp(registration)
                _uiState.update { stateVal ->
                    stateVal.copy(
                        selectedRegistrationId = registrationId,
                        selectedSlotTime = slotTime,
                        selectedCampId = campId
                    )
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "registerForCamp failed: ${e.message}")
            }
        }
    }

    fun verifyQRScan(qrPayload: String): Pair<String?, CampRegistration?> {
        val parts = qrPayload.split("|").associate { 
            val pair = it.split(":")
            if (pair.size == 2) pair[0] to pair[1] else "" to "" 
        }
        val regId = parts["REG_ID"] ?: ""
        
        val reg = stateFlowValue().campRegistrations.find { it.registrationId == regId }
        
        if (reg == null) {
            return "Registration Not Found: Scanned pass does not correspond to an active record." to null
        }
        if (reg.status == "Checked In") {
            return "Already Checked In: This donor pass has already been checked in." to reg
        }
        if (reg.status == "Donation Completed") {
            return "Already Donated: Scanned donor has already completed their donation today." to reg
        }
        if (reg.status == "Donation Rejected") {
            return "Screened Out: Scanned donor was previously marked as medically rejected." to reg
        }
        
        return null to reg
    }

    fun checkInDonor(registrationId: String) {
        val nowTimeStr = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
        val reg = stateFlowValue().campRegistrations.find { it.registrationId == registrationId } ?: return
        
        viewModelScope.launch {
            try {
                val updatedReg = FCampRegistration(
                    registrationId = reg.registrationId,
                    campId = reg.campId,
                    donorId = reg.donorId,
                    donorName = reg.donorName,
                    bloodGroup = reg.bloodGroup,
                    selectedSlot = reg.selectedSlot,
                    registrationTimestamp = reg.registrationTimestamp,
                    status = "Checked In",
                    qrPayload = reg.qrPayload,
                    checkInTime = nowTimeStr,
                    donationVerified = reg.donationVerified,
                    rejectionReason = reg.rejectionReason
                )
                BloodRepository.updateCampRegistration(updatedReg)
                
                val attendance = FCampAttendance(
                    attendanceId = "att_${System.currentTimeMillis()}",
                    campId = reg.campId,
                    registrationId = registrationId,
                    donorId = reg.donorId,
                    checkInTime = nowTimeStr
                )
                val db = FirebaseConfigService.firestore
                if (db != null) {
                    db.collection("camp_attendance").document(attendance.attendanceId).set(attendance).await()
                } else {
                    Log.w("BloodLinkViewModel", "Cannot record attendance: Firestore instance is null")
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "checkInDonor failed: ${e.message}")
            }
        }
    }

    fun completeCampDonation(registrationId: String) {
        val now = java.time.LocalDate.now()
        val dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        val timestampStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        
        val uiStateVal = stateFlowValue()
        val reg = uiStateVal.campRegistrations.find { it.registrationId == registrationId } ?: return
        val camp = uiStateVal.nearbyCamps.find { it.id == reg.campId }
        val campName = camp?.title ?: "Blood Donation Camp"
        
        viewModelScope.launch {
            try {
                val updatedReg = FCampRegistration(
                    registrationId = reg.registrationId,
                    campId = reg.campId,
                    donorId = reg.donorId,
                    donorName = reg.donorName,
                    bloodGroup = reg.bloodGroup,
                    selectedSlot = reg.selectedSlot,
                    registrationTimestamp = reg.registrationTimestamp,
                    status = "Donation Completed",
                    qrPayload = reg.qrPayload,
                    checkInTime = reg.checkInTime,
                    donationVerified = true,
                    rejectionReason = null
                )
                BloodRepository.updateCampRegistration(updatedReg)
                
                val donationId = "BLD-" + (100 + uiStateVal.donationHistory.size + 1)
                val record = FDonationHistoryRecord(
                    donationId = donationId,
                    donorId = reg.donorId,
                    donorName = reg.donorName,
                    bloodGroup = reg.bloodGroup,
                    hospitalName = campName,
                    requestId = "camp_${reg.campId}",
                    donationDate = dateStr,
                    verificationTimestamp = timestampStr
                )
                BloodRepository.confirmDonation(record)
                
                val isCurrentDonor = uiStateVal.profile.id == reg.donorId || reg.donorId == "alex_rivera_id"
                val cooldownDays = if (uiStateVal.profile.gender.equals("Male", ignoreCase = true)) 90 else 120
                val eligibleDateStr = now.plusDays(cooldownDays.toLong()).format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                
                if (isCurrentDonor) {
                    val newXp = uiStateVal.profile.xp + 150
                    val newLevelNum = (newXp / 200).coerceAtLeast(4)
                    val updatedProfile = uiStateVal.profile.copy(
                        totalDonations = uiStateVal.profile.totalDonations + 1,
                        livesSaved = uiStateVal.profile.livesSaved + 3,
                        xp = newXp,
                        level = "Level $newLevelNum Life-Saver",
                        isAvailable = false,
                        lastDonationDate = dateStr,
                        lastDonationHospital = campName,
                        lastDonationBloodGroup = reg.bloodGroup,
                        lastDonationRequestId = "camp_${reg.campId}",
                        nextEligibleDate = eligibleDateStr,
                        cooldownStatus = "Cooldown",
                        cooldownCountdownDays = cooldownDays,
                        donationStreak = uiStateVal.profile.donationStreak + 1
                    )
                    updateProfile(updatedProfile)
                }
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "completeCampDonation failed: ${e.message}")
            }
        }
    }

    fun rejectCampDonation(registrationId: String, reason: String) {
        val reg = stateFlowValue().campRegistrations.find { it.registrationId == registrationId } ?: return
        viewModelScope.launch {
            try {
                val updatedReg = FCampRegistration(
                    registrationId = reg.registrationId,
                    campId = reg.campId,
                    donorId = reg.donorId,
                    donorName = reg.donorName,
                    bloodGroup = reg.bloodGroup,
                    selectedSlot = reg.selectedSlot,
                    registrationTimestamp = reg.registrationTimestamp,
                    status = "Donation Rejected",
                    qrPayload = reg.qrPayload,
                    checkInTime = reg.checkInTime,
                    donationVerified = false,
                    rejectionReason = reason
                )
                BloodRepository.updateCampRegistration(updatedReg)
            } catch (e: Exception) {
                Log.e("BloodLinkViewModel", "rejectCampDonation failed: ${e.message}")
            }
        }
    }
}
