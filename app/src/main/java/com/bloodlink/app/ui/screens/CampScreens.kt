package com.bloodlink.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import com.bloodlink.app.ui.BloodLinkUiState
import com.bloodlink.app.ui.DonationCamp

@Composable
fun NearbyCampsScreen(
    state: BloodLinkUiState,
    onNavigateCampDetails: (String) -> Unit,
    onNavigateCreateCamp: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Upcoming") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateCreateCamp,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp).testTag("organize_camp_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Organize")
                    Text("Organize Camp", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCK_nMDb8Z_tA5-a_o2fS6vU3W6bI_yXz_qEG8n_8H6m1_f69C97X_s92tVp_z4XG6K5N_287vU8sXLdf7O8Y_f_a4-4vTU-aX5-u9N8_Uo_v-1",
                        contentDescription = "Camps Avatar",
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                    Text(text = "Blood Camps", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                }

                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Search text field input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                placeholder = { Text("Search by camp name or organizer...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Tabs Row
            TabRow(
                selectedTabIndex = when (selectedTab) {
                    "Upcoming" -> 0
                    "Ongoing" -> 1
                    "Past" -> 2
                    else -> 0
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                listOf("Upcoming", "Ongoing", "Past").forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Filters row (Optional extra filters)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All Drives", "Urgent Only", "Within 10 miles").forEachIndexed { index, label ->
                    val selected = index == 0
                    FilterChip(
                        selected = selected,
                        onClick = {},
                        label = { Text(text = label) }
                    )
                }
            }

            val filteredCamps = state.nearbyCamps.filter { camp ->
                val matchesSearch = camp.title.contains(searchQuery, ignoreCase = true) || camp.organizer.contains(searchQuery, ignoreCase = true)
                val matchesTab = when (selectedTab) {
                    "Upcoming" -> camp.status.equals("Active", ignoreCase = true) || camp.status.isBlank()
                    "Ongoing" -> camp.status.equals("Ongoing", ignoreCase = true) || camp.isUrgent
                    "Past" -> camp.status.equals("Completed", ignoreCase = true) || camp.status.equals("Past", ignoreCase = true)
                    else -> true
                }
                matchesSearch && matchesTab
            }

            if (filteredCamps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No camps found for \"$selectedTab\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredCamps.size) { index ->
                        val camp = filteredCamps[index]
                        val isRegistered = state.campRegistrations.any { it.campId == camp.id && it.donorId == state.profile.id }
                        DonationCampCard(
                            camp = camp,
                            isRegistered = isRegistered,
                            onClick = { onNavigateCampDetails(camp.id) },
                            onRegister = { onNavigateCampDetails(camp.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DonationCampCard(
    camp: DonationCamp,
    isRegistered: Boolean,
    onClick: () -> Unit,
    onRegister: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("camp_card_${camp.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = borderStroke()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = camp.imageUrl,
                    contentDescription = camp.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (camp.isUrgent) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "URGENT DRIVE", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = camp.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = camp.distanceText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }

                Text(text = "Hosted by ${camp.organizer}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Schedule", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Text(text = camp.dateText, style = MaterialTheme.typography.labelMedium)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = "Hours", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Text(text = camp.timeText, style = MaterialTheme.typography.labelMedium)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Address", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Text(text = camp.address, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = "Slots", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        val availableSlots = (camp.maxParticipants - camp.participantsCount).coerceAtLeast(0)
                        Text(text = "Slots: $availableSlots left (${camp.participantsCount} registered)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Bloodtype, contentDescription = "Requested Groups", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Text(text = camp.bloodGroupsNeeded.joinToString(", "), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Open Details")
                    }

                    Button(
                        onClick = onRegister,
                        enabled = !isRegistered && camp.status != "Completed" && camp.status != "Past",
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegistered) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isRegistered) "Registered" else "Register Now")
                    }
                }
            }
        }
    }
}

@Composable
fun CampDetailsScreen(
    campId: String,
    state: BloodLinkUiState,
    onNavigateSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    val camp = state.nearbyCamps.find { it.id == campId } ?: state.nearbyCamps.first()
    var selectedSlot by remember { mutableStateOf("10:00 AM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Camp Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Box(modifier = Modifier.size(48.dp))
        }

        // Feature Camp details header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = camp.imageUrl,
                        contentDescription = camp.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = camp.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = camp.organizer, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text(text = camp.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Map Preview
        Text(text = "LOCATION & DIRECTIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = camp.mapUrl,
                    contentDescription = "Map view",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Slot Picker Section
        Text(text = "SELECT APPOINTMENT SLOT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val morningSlots = listOf("9:00 AM", "10:00 AM", "11:00 AM", "11:30 AM")
            val afternoonSlots = listOf("1:00 PM", "2:00 PM", "3:00 PM", "3:30 PM")

            Text(text = "Morning Slots", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                morningSlots.forEach { slot ->
                    val selected = selectedSlot == slot
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLowest)
                            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .clickable { selectedSlot = slot },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = slot, fontWeight = FontWeight.Bold, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Afternoon Slots", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                afternoonSlots.forEach { slot ->
                    val selected = selectedSlot == slot
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLowest)
                            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .clickable { selectedSlot = slot },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = slot, fontWeight = FontWeight.Bold, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        val onCooldown = state.profile.cooldownCountdownDays > 0

        Spacer(modifier = Modifier.height(12.dp))

        if (onCooldown) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Cooldown Active",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Registration Locked",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Blood donation cooldown acts as body rest. Try again in ${state.profile.cooldownCountdownDays} days.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onNavigateSuccess(selectedSlot) },
            enabled = !onCooldown,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (onCooldown) Color.LightGray else MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.LightGray.copy(alpha = 0.4f),
                disabledContentColor = Color.Gray
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Book")
                Text(text = if (onCooldown) "Registration Locked" else "Register for Camp", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun RegistrationSuccessScreen(
    slotTime: String,
    state: BloodLinkUiState,
    onNavigateBack: () -> Unit
) {
    val reg = state.campRegistrations.find { it.registrationId == state.selectedRegistrationId }
        ?: state.campRegistrations.find { it.donorId == state.profile.id && it.status != "Donation Completed" }
        ?: state.campRegistrations.firstOrNull { it.donorId == state.profile.id }

    val camp = state.nearbyCamps.find { it.id == (reg?.campId ?: state.selectedCampId) }
    val campName = camp?.title ?: "City Center Comm. Drive"
    val campDate = camp?.dateText ?: "Oct 24 - 26, 2026"
    val registrationId = reg?.registrationId ?: "CR-105"
    val displaySlot = reg?.selectedSlot ?: slotTime
    val status = reg?.status ?: "Registered"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 25.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (status) {
                        "Donation Completed" -> Icons.Default.CheckCircle
                        "Donation Rejected" -> Icons.Default.Cancel
                        else -> Icons.Default.QrCode
                    },
                    contentDescription = "Success",
                    tint = when (status) {
                        "Donation Completed" -> Color(0xFF4CAF50)
                        "Donation Rejected" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = when (status) {
                "Checked In" -> "Checked In at Camp!"
                "Donation Completed" -> "Donation Verified!"
                "Donation Rejected" -> "Screening Complete"
                else -> "Registration Confirmed!"
            },
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        )
        
        Text(
            text = when (status) {
                "Checked In" -> "Your arrival is verified. Please wait for health screening before donation."
                "Donation Completed" -> "Amazing! Your donation is processed and body recovery countdown started."
                "Donation Rejected" -> "Thank you for volunteering. You are screened but temporarily ineligible."
                else -> "Your appointment has been registered. Present this QR pass at the welcome desk."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Custom Certificate Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "OFFICIAL CAMP PASS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp), color = MaterialTheme.colorScheme.primary)
                    
                    // Status Chip
                    val statusColor = when (status) {
                        "Checked In" -> MaterialTheme.colorScheme.primary
                        "Donation Completed" -> Color(0xFF4CAF50)
                        "Donation Rejected" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status.uppercase(),
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                
                HorizontalDivider()

                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDRuD-y_1b75kG4Qd0G589Bq68qXq0XlD7wSre6b6Mvx3q0F8C5-P2q_b9Y5Q3e7kGn8P8ZzP4gQ5wZ7mGq6F7XFf3S",
                            contentDescription = "QR Pass mockup",
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SECURE PASS PORTAL",
                            fontSize = 8.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.profile.name.uppercase(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text(text = "Registration ID: $registrationId", style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                HorizontalDivider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                        Text(text = "CAMP LOCATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = campName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "BLOOD GROUP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = state.profile.bloodGroup, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = "DATE Text", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = campDate, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "APPOINTMENT SLOT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = displaySlot, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (status == "Donation Rejected") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Medical Screening Out Reason:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = reg?.rejectionReason ?: "Temporary health restriction",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.AddToHomeScreen, contentDescription = "Wallet")
                Text(text = "Add to Google Wallet", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Back to Home Dashboard", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun OrganizerDashboardScreen(
    state: BloodLinkUiState,
    onNavigateCreateCamp: () -> Unit,
    onNavigateBack: () -> Unit,
    onCheckInDonor: (String) -> Unit,
    onCompleteDonation: (String) -> Unit,
    onRejectDonation: (String, String) -> Unit,
    onCancelCamp: (String) -> Unit = {},
    onDeleteCamp: (String) -> Unit = {},
    onUpdateCamp: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val campId = if (state.selectedCampId.isNotBlank()) state.selectedCampId else "1"
    val registrations = state.campRegistrations.filter { it.campId == campId }
    val countRegistered = registrations.size
    val countCheckedIn = registrations.filter { it.status == "Checked In" || it.status == "Donation Completed" || it.status == "Donation Rejected" }.size
    val countCompleted = registrations.filter { it.status == "Donation Completed" }.size
    val countRejected = registrations.filter { it.status == "Donation Rejected" }.size
    val unitsCollected = countCompleted

    val camp = state.nearbyCamps.find { it.id == campId }
    val campTitle = camp?.title ?: "Downtown Blood Drive"
    val campOrganizer = camp?.organizer ?: "Red Cross Center"
    val campDate = camp?.dateText ?: "Oct 24 - 26"
    val campAddress = camp?.address ?: "Tech Park Main Lobby"
    val campStatus = camp?.status ?: "Active"

    var showScanner by remember { mutableStateOf(false) }
    var scanErrorText by remember { mutableStateOf<String?>(null) }
    var scannedRegistration: com.bloodlink.app.ui.CampRegistration? by remember { mutableStateOf(null) }
    var showScanSuccess by remember { mutableStateOf(false) }

    var showScreeningDialogForReg: com.bloodlink.app.ui.CampRegistration? by remember { mutableStateOf(null) }
    var selectedRejectionReason by remember { mutableStateOf("Low Hemoglobin") }

    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(campTitle) }
    var editAddress by remember { mutableStateOf(campAddress) }
    var editDate by remember { mutableStateOf(campDate) }

    var showExportDialog by remember { mutableStateOf(false) }

    val rejectionReasons = listOf(
        "Low Hemoglobin Count",
        "High/Low Blood Pressure",
        "Weight below 50kg Requirements",
        "Temporary Medication/Illness",
        "Recent Tattoo/Body Piercing (under 6 months)",
        "Travel Restrictions - Active malaria zone"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCK_nMDb8Z_tA5-a_o2fS6vU3W6bI_yXz_qEG8n_8H6m1_f69C97X_s92tVp_z4XG6K5N_287vU8sXLdf7O8Y_f_a4-4vTU-aX5-u9N8_Uo_v-1",
                    contentDescription = "Organizer Logo",
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Text(text = "Camp Portal", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            }

            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.error)
            }
        }

        Text(text = "Manage your donation campaigns, donor checkins, and health screening.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Live Statistics Dashboard
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE STATISTICS DASHBOARD",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "Real-time Update", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OrganizerMetricCard(title = "Registered", value = countRegistered.toString(), modifier = Modifier.weight(1f))
                    OrganizerMetricCard(title = "Checked In", value = countCheckedIn.toString(), modifier = Modifier.weight(1f))
                    OrganizerMetricCard(title = "Donated", value = countCompleted.toString(), modifier = Modifier.weight(1f))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OrganizerMetricCard(title = "Rejected", value = countRejected.toString(), modifier = Modifier.weight(1f))
                    OrganizerMetricCard(title = "Collected (Units)", value = unitsCollected.toString(), modifier = Modifier.weight(1f))
                }
            }
        }

        // Action scanner button
        Button(
            onClick = {
                showScanner = true
                scanErrorText = null
                scannedRegistration = null
                showScanSuccess = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan")
                Text(text = "Scan Donor QR Code", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        // Active drive information
        Text(text = "ACTIVE DRIVE MANAGEMENT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = campTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Organized by $campOrganizer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(modifier = Modifier.background(
                        if (campStatus == "Cancelled") MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), 
                        RoundedCornerShape(12.dp)
                    ).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(
                            text = if (campStatus == "Cancelled") "Cancelled" else "Live Now", 
                            color = if (campStatus == "Cancelled") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, 
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                HorizontalDivider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Venue Address:", style = MaterialTheme.typography.labelSmall)
                    Text(text = campAddress, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Date & Time:", style = MaterialTheme.typography.labelSmall)
                    Text(text = "$campDate", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }

                HorizontalDivider()

                // Management Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text("Edit", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                        }
                    }

                    IconButton(
                        onClick = { onCancelCamp(campId) },
                        modifier = Modifier
                            .weight(1.5f)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        enabled = campStatus != "Cancelled"
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Text(if (campStatus == "Cancelled") "Cancelled" else "Cancel", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error))
                        }
                    }

                    IconButton(
                        onClick = { 
                            onDeleteCamp(campId) 
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Text("Delete", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error))
                        }
                    }

                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier
                            .weight(1.2f)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Export", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Text("Export", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary))
                        }
                    }
                }
            }
        }

        // Camp Registry list
        Text(text = "DONOR REGISTRY & HEALTH SCREENING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (registrations.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No donors registered yet.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                registrations.forEach { reg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = borderStroke()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = reg.donorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = "ID: ${reg.registrationId} • Blood: ${reg.bloodGroup}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                val statusColor = when (reg.status) {
                                    "Registered" -> MaterialTheme.colorScheme.secondary
                                    "Checked In" -> MaterialTheme.colorScheme.primary
                                    "Donation Completed" -> Color(0xFF4CAF50)
                                    "Donation Rejected" -> MaterialTheme.colorScheme.error
                                    else -> Color.DarkGray
                                }

                                Box(
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = reg.status.uppercase(),
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Text(text = "Scheduled Slot: ${reg.selectedSlot}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }

                                if (reg.checkInTime != null) {
                                    Text(text = "Checked-in: ${reg.checkInTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (reg.rejectionReason != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f))
                                ) {
                                    Text(
                                        text = "Medical screening failed: ${reg.rejectionReason}",
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            // Actions
                            when (reg.status) {
                                "Registered" -> {
                                    Button(
                                        onClick = { onCheckInDonor(reg.registrationId) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Text("Manual Check-In", fontWeight = FontWeight.Bold)
                                    }
                                }
                                "Checked In" -> {
                                    Button(
                                        onClick = { showScreeningDialogForReg = reg },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Text("Perform Screening Report", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Organize drive action section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Text(text = "Host a Donation Drive", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                Text(text = "Our AI system will automatically match and alert compatible donors within a 10 km radius instantly when your camp gets published.", style = MaterialTheme.typography.bodyMedium)

                Button(
                    onClick = onNavigateCreateCamp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Create")
                        Text(text = "Organize New Camp", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // SIMULATED QR CAMERA SCANNER DIALOG
    if (showScanner) {
        AlertDialog(
            onDismissRequest = { showScanner = false },
            confirmButton = {
                TextButton(onClick = { showScanner = false }) {
                    Text("Close Scanner", color = MaterialTheme.colorScheme.error)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Dynamic QR Camera Scanner")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Align donor ticket QR code inside the green viewport target below to proceed.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Mock camera viewfinder
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .background(Color.Black, RoundedCornerShape(16.dp))
                            .border(3.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scan guidelines
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(110.dp)
                        )
                        
                        // Animated pulsing red line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color.Red)
                        )
                    }

                    if (scanErrorText != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text(text = scanErrorText!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    val currentScannedReg = scannedRegistration
                    if (showScanSuccess && currentScannedReg != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Text(
                                text = "✓ Check-in Successful!\nVerified: ${currentScannedReg.donorName}\nTime: ${currentScannedReg.selectedSlot}",
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(text = "SIMULATE SCAN SELECTION:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))

                    // Simulated scans dropdown or list
                    val nonCheckedInRegs = state.campRegistrations.filter { it.status == "Registered" }
                    if (nonCheckedInRegs.isEmpty()) {
                        Text("All registered camp donors already checked in!", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 130.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(nonCheckedInRegs.size) { index ->
                                val reg = nonCheckedInRegs[index]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // Perform simulation
                                            onCheckInDonor(reg.registrationId)
                                            scannedRegistration = reg
                                            showScanSuccess = true
                                            scanErrorText = null
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = reg.donorName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            Text(text = "Slot: ${reg.selectedSlot} • Group: ${reg.bloodGroup}", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Button to test duplicate scans
                    val checkedInRegs = state.campRegistrations.filter { it.status != "Registered" }
                    if (checkedInRegs.isNotEmpty()) {
                        Text(text = "SIMULATE DUPLICATE ERROR SCAN:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        checkedInRegs.take(2).forEach { r ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scanErrorText = "Already Scanned: This pass has been checked in before under database registration ID: ${r.registrationId}."
                                        showScanSuccess = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = "Scan Duplicate: ${r.donorName} (${r.status})",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    }

    // DONOR MEDICAL SCREENING DIALOG
    val currentScreeningReg = showScreeningDialogForReg
    if (currentScreeningReg != null) {
        val reg = currentScreeningReg
        AlertDialog(
            onDismissRequest = { showScreeningDialogForReg = null },
            confirmButton = {
                TextButton(onClick = { showScreeningDialogForReg = null }) {
                    Text("Cancel")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Health Screening: ${reg.donorName}")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Report health metrics screening for ${reg.donorName} (${reg.bloodGroup}). Eligibility selection modifies body indicators.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    HorizontalDivider()

                    Text(
                        text = "1. VERIFY COMPLETED DONATION Successfully",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF2E7D32)
                    )

                    Button(
                        onClick = {
                            onCompleteDonation(reg.registrationId)
                            showScreeningDialogForReg = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null)
                            Text("Donation Completed", fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "2. REPORT REJECTED screening out",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = "Choose medical disqualification reason:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        rejectionReasons.forEach { reason ->
                            val isSelected = selectedRejectionReason == reason
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.error else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedRejectionReason = reason }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.error else Color.LightGray.copy(alpha = 0.5f),
                                            CircleShape
                                        )
                                )
                                Text(text = reason, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onRejectDonation(reg.registrationId, selectedRejectionReason)
                            showScreeningDialogForReg = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                            Text("Reject Donation", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    }

    if (showEditDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Edit Camp Details",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Camp Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("Venue Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editDate,
                        onValueChange = { editDate = it },
                        label = { Text("Date & Time") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showEditDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                onUpdateCamp(campId, editTitle, editAddress, editDate)
                                showEditDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showExportDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Export Donor Registry",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = "Below is the CSV preview of all registered donors for this drive.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // CSV Preview Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val csvContent = buildString {
                            appendLine("Registration ID,Name,Blood Group,Status,Appointment")
                            registrations.forEach { r ->
                                appendLine("${r.registrationId},\"${r.donorName}\",${r.bloodGroup},${r.status},${r.selectedSlot}")
                            }
                        }
                        Text(
                            text = csvContent,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    var isExported by remember { mutableStateOf(false) }

                    if (isExported) {
                        Text(
                            text = "✓ CSV Exported successfully to Downloads folder!",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showExportDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close")
                        }

                        Button(
                            onClick = {
                                isExported = true
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Text("Download CSV", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrganizerMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = borderStroke()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
        }
    }
}

@Composable
fun CreateCampScreen(
    state: BloodLinkUiState,
    onPublish: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("American Red Cross") }
    var address by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Organize Donation Camp", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Box(modifier = Modifier.size(48.dp))
        }

        Text(text = "Setup and publish blood drive details.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Camp Campaign Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("Host Organization") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Physical Address") },
                    leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Pin") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onPublish(title, host, address) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Publish Camp Mobile Drive", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
