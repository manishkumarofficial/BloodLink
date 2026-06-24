package com.bloodlink.app.data

import com.bloodlink.app.ui.UserRole

data class FUser(
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val role: String = "Donor", // "Donor", "Requester", "Organizer"
    val bloodGroup: String = "O+",
    val location: String = "Tech Park Main Lobby",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val travelRadius: Int = 50,
    val emergencyContact: String = "",
    val availabilityStatus: Boolean = true,
    val completedProfile: Boolean = false,
    val lastDonationDate: String = "",
    val nextEligibleDate: String = "",
    val cooldownStatus: String = "Eligible", // "Eligible", "Cooldown"
    val donationCount: Int = 0,
    val livesSaved: Int = 0,
    val heroLevel: String = "Level 1 Life-Saver",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class FBloodRequest(
    val id: String = "",
    val requesterId: String = "",
    val hospitalName: String = "",
    val bloodGroup: String = "",
    val distanceText: String = "",
    val patientStatus: String = "",
    val quote: String = "",
    val isCritical: Boolean = false,
    val checkedInCount: Int = 0,
    val targetUnits: Int = 3,
    val currentUnits: Int = 0,
    val reachedHospitalCount: Int = 0,
    val donatedCount: Int = 0,
    val isFulfilled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class FDonationCamp(
    val id: String = "",
    val title: String = "",
    val organizer: String = "",
    val organizerId: String = "",
    val address: String = "",
    val distanceText: String = "",
    val dateText: String = "",
    val timeText: String = "",
    val bloodGroupsNeeded: List<String> = emptyList(),
    val imageUrl: String = "",
    val mapUrl: String = "",
    val isUrgent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class FCampRegistration(
    val registrationId: String = "",
    val campId: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val bloodGroup: String = "",
    val selectedSlot: String = "",
    val registrationTimestamp: String = "",
    val status: String = "Registered", // "Registered", "Checked In", "Donation Completed", "Donation Rejected"
    val qrPayload: String = "",
    val checkInTime: String? = null,
    val donationVerified: Boolean = false,
    val rejectionReason: String? = null
)

data class FDonationHistoryRecord(
    val donationId: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val bloodGroup: String = "",
    val hospitalName: String = "",
    val requestId: String = "",
    val donationDate: String = "",
    val verificationTimestamp: String = ""
)

data class FCampAttendance(
    val attendanceId: String = "",
    val campId: String = "",
    val registrationId: String = "",
    val donorId: String = "",
    val checkInTime: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class FNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class FAchievement(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val xpAwarded: Int = 0,
    val unlockedAt: Long = System.currentTimeMillis()
)

data class FUserStatistics(
    val userId: String = "",
    val totalDonations: Int = 0,
    val livesSaved: Int = 0,
    val currentStreak: Int = 0,
    val totalXp: Int = 0,
    val level: String = "Level 1 Life-Saver"
)

data class FCooldown(
    val userId: String = "",
    val status: String = "Eligible", // "Eligible" or "Cooldown"
    val remainingDays: Int = 0,
    val nextEligibleDate: String = ""
)

data class FVerificationLog(
    val logId: String = "",
    val action: String = "", // "QR_VERIFY", "DONATION_CONFIRM", "CAMP_CHECKIN"
    val actorId: String = "",
    val targetId: String = "",
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
