package com.bloodlink.app.data

import android.util.Log
import com.bloodlink.app.FirebaseConfigService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object BloodRepository {
    private const val TAG = "BloodRepository"

    private val db: FirebaseFirestore
        get() = FirebaseConfigService.firestore ?: FirebaseFirestore.getInstance()

    // Real-Time Listeners using flow

    fun listenToActiveRequests(): Flow<List<FBloodRequest>> = callbackFlow {
        Log.d(TAG, "Listening to active blood requests...")
        val listener = db.collection("blood_requests")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore Permission or Network Error in listenToActiveRequests: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val requests = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FBloodRequest::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "Mapping error: ${e.message}")
                            null
                        }
                    }
                    trySend(requests)
                }
            }
        awaitClose { listener.remove() }
    }

    fun listenToCamps(): Flow<List<FDonationCamp>> = callbackFlow {
        Log.d(TAG, "Listening to donation camps...")
        val listener = db.collection("camps")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore Failure in listenToCamps: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val camps = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FDonationCamp::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(camps)
                }
            }
        awaitClose { listener.remove() }
    }

    fun listenToCampRegistrations(donorId: String): Flow<List<FCampRegistration>> = callbackFlow {
        Log.d(TAG, "Listening to camp registrations for donor: $donorId")
        val listener = db.collection("camp_registrations")
            .whereEqualTo("donorId", donorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val regs = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FCampRegistration::class.java)?.copy(registrationId = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(regs)
                }
            }
        awaitClose { listener.remove() }
    }

    fun listenToAllCampRegistrations(): Flow<List<FCampRegistration>> = callbackFlow {
        Log.d(TAG, "Listening to all camp registrations for organizer workflow")
        val listener = db.collection("camp_registrations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val regs = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FCampRegistration::class.java)?.copy(registrationId = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(regs)
                }
            }
        awaitClose { listener.remove() }
    }

    fun listenToNotifications(userId: String): Flow<List<FNotification>> = callbackFlow {
        Log.d(TAG, "Listening to notifications for user: $userId")
        val listener = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore Failure in listenToNotifications: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FNotification::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(notifications)
                }
            }
        awaitClose { listener.remove() }
    }

    fun listenToUserStatistics(userId: String): Flow<FUserStatistics?> = callbackFlow {
        Log.d(TAG, "Listening to statistics for user: $userId")
        val listener = db.collection("user_statistics")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(FUserStatistics::class.java))
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun listenToDonationHistory(userId: String): Flow<List<FDonationHistoryRecord>> = callbackFlow {
        Log.d(TAG, "Listening to donation history for user: $userId")
        val listener = db.collection("donation_history")
            .whereEqualTo("donorId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val records = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FDonationHistoryRecord::class.java)?.copy(donationId = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(records)
                }
            }
        awaitClose { listener.remove() }
    }

    // CRUD Operations

    suspend fun createUserProfile(user: FUser) {
        FirebaseConfigService.runLoggedWrite("users", user.userId, user) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.userId)
                .set(user)
                .await()
        }
        Log.d(TAG, "Successfully created user profile: ${user.userId}")
        
        // Intialize user statistics and cooldown
        val initialStats = FUserStatistics(
            userId = user.userId,
            totalDonations = user.donationCount,
            livesSaved = user.livesSaved,
            level = user.heroLevel
        )
        val initialCooldown = FCooldown(
            userId = user.userId,
            status = user.cooldownStatus,
            nextEligibleDate = user.nextEligibleDate
        )
        FirebaseConfigService.runLoggedWrite("user_statistics", user.userId, initialStats) {
            FirebaseFirestore.getInstance()
                .collection("user_statistics")
                .document(user.userId)
                .set(initialStats)
                .await()
        }
        FirebaseConfigService.runLoggedWrite("cooldowns", user.userId, initialCooldown) {
            FirebaseFirestore.getInstance()
                .collection("cooldowns")
                .document(user.userId)
                .set(initialCooldown)
                .await()
        }
        
        logAction("CREATE_USER", user.userId, user.userId, "User registration initialized successfully")
    }

    suspend fun getUserProfile(userId: String): FUser? {
        try {
            val doc = db.collection("users").document(userId).get().await()
            if (doc.exists()) {
                return doc.toObject(FUser::class.java)?.copy(userId = doc.id)
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user profile for $userId: ${e.message}", e)
            return null
        }
    }

    suspend fun getUserProfileByPhone(phone: String): FUser? {
        try {
            val query = db.collection("users").whereEqualTo("phoneNumber", phone).limit(1).get().await()
            if (!query.isEmpty) {
                val doc = query.documents.first()
                return doc.toObject(FUser::class.java)?.copy(userId = doc.id)
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user by phone $phone: ${e.message}", e)
            return null
        }
    }

    suspend fun getUserProfileByEmail(email: String): FUser? {
        try {
            val query = db.collection("users").whereEqualTo("email", email).limit(1).get().await()
            if (!query.isEmpty) {
                val doc = query.documents.first()
                return doc.toObject(FUser::class.java)?.copy(userId = doc.id)
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user by email $email: ${e.message}", e)
            return null
        }
    }

    suspend fun updateUserProfile(user: FUser) {
        FirebaseConfigService.runLoggedWrite("users", user.userId, user) {
            db.collection("users").document(user.userId).set(user).await()
        }
        Log.d(TAG, "Successfully updated user profile: ${user.userId}")
        logAction("UPDATE_USER", user.userId, user.userId, "User profile updated fields")
    }

    suspend fun createBloodRequest(request: FBloodRequest) {
        val docRef = if (request.id.isBlank()) {
            db.collection("blood_requests").document()
        } else {
            db.collection("blood_requests").document(request.id)
        }
        val requestWithId = request.copy(id = docRef.id)
        FirebaseConfigService.runLoggedWrite("blood_requests", requestWithId.id, requestWithId) {
            docRef.set(requestWithId).await()
        }
        Log.d(TAG, "Successfully created blood request: ${requestWithId.id}")
        logAction("CREATE_BLOOD_REQUEST", requestWithId.requesterId, requestWithId.id, "Blood request generated for group ${requestWithId.bloodGroup}")
    }

    suspend fun updateBloodRequest(request: FBloodRequest) {
        FirebaseConfigService.runLoggedWrite("blood_requests", request.id, request) {
            db.collection("blood_requests").document(request.id).set(request).await()
        }
        Log.d(TAG, "Updated blood request: ${request.id}")
    }

    suspend fun createCamp(camp: FDonationCamp) {
        val docRef = if (camp.id.isBlank()) db.collection("camps").document() else db.collection("camps").document(camp.id)
        val campWithId = camp.copy(id = docRef.id, campId = docRef.id)
        FirebaseConfigService.runLoggedWrite("camps", campWithId.id, campWithId) {
            docRef.set(campWithId).await()
        }
        Log.d(TAG, "Successfully published donation camp: ${campWithId.id}")
        logAction("CREATE_CAMP", campWithId.organizerId, campWithId.id, "Published camp ${campWithId.title}")
    }

    suspend fun registerForCamp(registration: FCampRegistration) {
        val docRef = if (registration.registrationId.isBlank()) {
            db.collection("camp_registrations").document()
        } else {
            db.collection("camp_registrations").document(registration.registrationId)
        }
        val regWithId = registration.copy(registrationId = docRef.id)
        FirebaseConfigService.runLoggedWrite("camp_registrations", regWithId.registrationId, regWithId) {
            docRef.set(regWithId).await()
        }
        Log.d(TAG, "Donor registered successfully for camp: ${regWithId.registrationId}")
        logAction("REGISTER_CAMP", regWithId.donorId, regWithId.registrationId, "Registered slot ${regWithId.selectedSlot} at camp ${regWithId.campId}")
    }

    suspend fun updateCampRegistration(registration: FCampRegistration) {
        FirebaseConfigService.runLoggedWrite("camp_registrations", registration.registrationId, registration) {
            db.collection("camp_registrations").document(registration.registrationId).set(registration).await()
        }
    }

    suspend fun verifyDonation(donationId: String): FDonationHistoryRecord? {
        try {
            val doc = db.collection("donation_history").document(donationId).get().await()
            if (doc.exists()) {
                return doc.toObject(FDonationHistoryRecord::class.java)
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error checking verification for donation code $donationId: ${e.message}")
            return null
        }
    }

    suspend fun confirmDonation(record: FDonationHistoryRecord) {
        val docRef = db.collection("donation_history").document(record.donationId)
        
        // Handle duplicate detection safely
        if (docRef.get().await().exists()) {
            throw IllegalStateException("Duplicate Record: Donation ID ${record.donationId} has already been verified and logged.")
        }
        
        FirebaseConfigService.runLoggedWrite("donation_history", record.donationId, record) {
            docRef.set(record).await()
        }
        Log.d(TAG, "Successfully confirmed verification and logged donation: ${record.donationId}")
        
        // Log verification trace explicitly
        logAction("DONATION_CONFIRM", record.donorId, record.donationId, "Log verification completes for requestId ${record.requestId}")
    }

    suspend fun updateCooldown(userId: String, status: String, remainingDays: Int, nextEligibleDate: String) {
        try {
            val cd = FCooldown(userId, status, remainingDays, nextEligibleDate)
            FirebaseConfigService.runLoggedWrite("cooldowns", userId, cd) {
                db.collection("cooldowns").document(userId).set(cd).await()
            }
            
            // Also sink to user document
            val userRef = db.collection("users").document(userId)
            val userSnap = userRef.get().await()
            if (userSnap.exists()) {
                val user = userSnap.toObject(FUser::class.java)
                if (user != null) {
                    val updatedUser = user.copy(
                        cooldownStatus = status,
                        nextEligibleDate = nextEligibleDate
                    )
                    FirebaseConfigService.runLoggedWrite("users", userId, updatedUser) {
                        userRef.set(updatedUser).await()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating cooldown log: ${e.message}", e)
        }
    }

    suspend fun updateAchievements(achievement: FAchievement) {
        try {
            FirebaseConfigService.runLoggedWrite("achievements", achievement.id, achievement) {
                db.collection("achievements").document(achievement.id).set(achievement).await()
            }
            Log.d(TAG, "Locked/Unlocked Achievement stored: ${achievement.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error storing achievements: ${e.message}")
        }
    }

    suspend fun storeNotification(notification: FNotification) {
        try {
            val docRef = if (notification.id.isBlank()) db.collection("notifications").document() else db.collection("notifications").document(notification.id)
            val updatedNotification = notification.copy(id = docRef.id)
            FirebaseConfigService.runLoggedWrite("notifications", updatedNotification.id, updatedNotification) {
                docRef.set(updatedNotification).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error storing notification record: ${e.message}")
        }
    }

    suspend fun updateUserStatistics(stats: FUserStatistics) {
        try {
            FirebaseConfigService.runLoggedWrite("user_statistics", stats.userId, stats) {
                db.collection("user_statistics").document(stats.userId).set(stats).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating statistics: ${e.message}")
        }
    }

    // Explicit VerificationLogs store
    private suspend fun logAction(action: String, actorId: String, targetId: String, details: String) {
        try {
            val logId = "log_" + System.currentTimeMillis()
            val log = FVerificationLog(
                logId = logId,
                action = action,
                actorId = actorId,
                targetId = targetId,
                details = details,
                timestamp = System.currentTimeMillis()
            )
            FirebaseConfigService.runLoggedWrite("verification_logs", logId, log) {
                db.collection("verification_logs").document(logId).set(log).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed logging action trace: ${e.message}")
        }
    }
}
