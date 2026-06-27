package com.bloodlink.app.ui.screens

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.bloodlink.app.ui.CampRegistration
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
    onOpenDashboard: (String) -> Unit,
    onEditCamp: (String, String, String, String) -> Unit,
    onDeleteCamp: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Upcoming") }

    // Dialog state for Editing a camp
    var showEditDialogForCamp by remember { mutableStateOf<DonationCamp?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }

    // Dialog state for Deleting a camp
    var showDeleteConfirmCamp by remember { mutableStateOf<DonationCamp?>(null) }

    val myCamps = state.nearbyCamps.filter { it.organizerId == state.profile.id || it.organizer == state.profile.name }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("➕ Organize Camp", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)) },
                icon = { Icon(imageVector = Icons.Default.Bloodtype, contentDescription = "Organize Camp") },
                onClick = onNavigateCreateCamp,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp).testTag("organize_camp_fab")
            )
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
                    "Upcoming" -> camp.status.equals("Active", ignoreCase = true) || camp.status.equals("Upcoming", ignoreCase = true) || camp.status.isBlank()
                    "Ongoing" -> camp.status.equals("Ongoing", ignoreCase = true) || camp.isUrgent
                    "Past" -> camp.status.equals("Completed", ignoreCase = true) || camp.status.equals("Past", ignoreCase = true)
                    else -> true
                }
                matchesSearch && matchesTab
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Section 1: My Organized Camps
                if (state.currentRole == com.bloodlink.app.ui.UserRole.CampOrganizer) {
                    if (myCamps.isNotEmpty()) {
                        item {
                            Text(
                                text = "My Organized Camps",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                    items(myCamps.size) { index ->
                        val camp = myCamps[index]
                        val campRegs = state.campRegistrations.filter { it.campId == camp.id }
                        val countRegistered = campRegs.size
                        val countCheckedIn = campRegs.filter { it.status == "Checked In" || it.status == "Donation Completed" || it.status == "Donation Rejected" }.size
                        val countDonated = campRegs.filter { it.status == "Donation Completed" }.size
                        val availableSlots = (camp.maxParticipants - countRegistered).coerceAtLeast(0)

                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("my_camp_card_${camp.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                                    AsyncImage(
                                        model = if (camp.bannerUrl.isNotBlank()) camp.bannerUrl else (if (camp.imageUrl.isNotBlank()) camp.imageUrl else "https://images.unsplash.com/photo-1615461066841-6116ecdccd04?auto=format&fit=crop&q=80&w=600"),
                                        contentDescription = camp.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    val statusColor = when (camp.status) {
                                        "Cancelled" -> MaterialTheme.colorScheme.error
                                        "Paused" -> Color(0xFFFFA000)
                                        "Closed" -> Color.Gray
                                        else -> Color(0xFF4CAF50)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(12.dp)
                                            .background(statusColor, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (camp.status.isNotBlank()) camp.status.uppercase() else "ACTIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = camp.title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                        Text(text = camp.dateText, style = MaterialTheme.typography.bodyMedium)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                        Text(text = camp.timeText, style = MaterialTheme.typography.bodyMedium)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                        Text(text = camp.address, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    // Statistics Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                            Text("Registrations", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(countRegistered.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                            Text("Check-ins", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(countCheckedIn.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)))
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                            Text("Donations", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(countDonated.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFFFA000)))
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                            Text("Available Slots", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(availableSlots.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary))
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    // Actions
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onOpenDashboard(camp.id) },
                                            modifier = Modifier.weight(1.5f).height(40.dp).testTag("open_dashboard_${camp.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(imageVector = Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Dashboard", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                showEditDialogForCamp = camp
                                                editTitle = camp.title
                                                editAddress = camp.address
                                                editDate = camp.dateText
                                            },
                                            modifier = Modifier.weight(1f).height(40.dp).testTag("edit_camp_${camp.id}"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Edit", style = MaterialTheme.typography.labelSmall)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val shareText = "Host of ${camp.title} at ${camp.address} on ${camp.dateText}! Register now to donate blood and save lives on BloodLink!"
                                                val intent = android.content.Intent().apply {
                                                    action = android.content.Intent.ACTION_SEND
                                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(android.content.Intent.createChooser(intent, "Share Camp Details"))
                                            },
                                            modifier = Modifier.weight(1f).height(40.dp).testTag("share_camp_${camp.id}"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Share", style = MaterialTheme.typography.labelSmall)
                                        }

                                        IconButton(
                                            onClick = { showDeleteConfirmCamp = camp },
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                .size(40.dp)
                                                .testTag("delete_camp_${camp.id}")
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    } else {
                        // Empty State for Organizer
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("empty_organized_camps_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "🏥 No Camps Organized Yet",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "You haven't organized any blood donation camps yet.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = onNavigateCreateCamp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("organize_first_camp_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("❤️ Organize Your First Camp", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                }

                // Section 2: Public Camps
                item {
                    Text(
                        text = "Public Campaigns",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                if (filteredCamps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No camps found for \"$selectedTab\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
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

    // Dialog: Edit Camp
    showEditDialogForCamp?.let { camp ->
        AlertDialog(
            onDismissRequest = { showEditDialogForCamp = null },
            title = { Text("Edit Camp Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Camp Name") },
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
                        label = { Text("Camp Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEditCamp(camp.id, editTitle, editAddress, editDate)
                        showEditDialogForCamp = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialogForCamp = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Delete Camp Confirmation
    showDeleteConfirmCamp?.let { camp ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmCamp = null },
            title = { Text("Delete Blood Drive") },
            text = {
                Text("Are you sure you want to permanently delete \"${camp.title}\"? All registered participants, attendance lists, and stats for this drive will be deleted immediately. This cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCamp(camp.id)
                        showDeleteConfirmCamp = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmCamp = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DonationCampCard(
    camp: DonationCamp,
    isRegistered: Boolean,
    onClick: () -> Unit,
    onRegister: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
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

                // Status Badge on the Top Left
                val statusLabel = when (camp.status) {
                    "Cancelled" -> "Cancelled"
                    "Paused" -> "Paused"
                    "Closed" -> "Closed"
                    else -> "Live Now"
                }
                val statusBgColor = when (camp.status) {
                    "Cancelled" -> MaterialTheme.colorScheme.error
                    "Paused" -> Color(0xFFFFA000)
                    "Closed" -> Color.Gray
                    else -> Color(0xFF4CAF50)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(statusBgColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = statusLabel.uppercase(), style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                }

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Details")
                    }

                    Button(
                        onClick = onRegister,
                        enabled = !isRegistered && camp.status != "Completed" && camp.status != "Past" && camp.status != "Cancelled",
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegistered) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isRegistered) "Registered" else "Register Now")
                    }

                    IconButton(
                        onClick = {
                            val shareText = "Help save lives! Join our blood donation campaign: ${camp.title} on ${camp.dateText} at ${camp.address} organized by ${camp.organizer}. Register today using BloodLink!"
                            val intent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Share Blood Camp"))
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .size(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.secondary)
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
    onUpdateCamp: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onPauseCamp: (String) -> Unit = {},
    onResumeCamp: (String) -> Unit = {},
    onCloseCamp: (String) -> Unit = {},
    onNotifyCamp: (String, String, String) -> Unit = { _, _, _ -> },
    onDuplicateCamp: (String, String, String) -> Unit = { _, _, _ -> }
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

    // Sub-Screen navigation state: "Main", "Participants", "Verify", "Analytics"
    var activeDashboardTab by remember { mutableStateOf("Main") }

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

    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("Urgent Update") }
    var broadcastMessage by remember { mutableStateOf("") }
    var isBroadcastSuccess by remember { mutableStateOf(false) }

    var showDuplicateDialog by remember { mutableStateOf(false) }
    var duplicateTitle by remember { mutableStateOf("$campTitle (Rescheduled)") }
    var duplicateDate by remember { mutableStateOf(campDate) }
    var isDuplicateSuccess by remember { mutableStateOf(false) }

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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Portal Header
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
                Column {
                    Text(text = "Camp Portal", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text(text = if (activeDashboardTab == "Main") "Overview" else activeDashboardTab, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            IconButton(
                onClick = {
                    if (activeDashboardTab != "Main") {
                        activeDashboardTab = "Main"
                    } else {
                        onNavigateBack()
                    }
                }
            ) {
                Icon(imageVector = if (activeDashboardTab != "Main") Icons.Default.ArrowBack else Icons.Default.Close, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Render sub-screens dynamically inside a weight column to let it scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (activeDashboardTab) {
                "Main" -> {
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

                    // Quick Action Grid (6 major modules)
                    Text(text = "QUICK ACTIONS CONTROL CENTER", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                title = "QR Scanner",
                                icon = Icons.Default.QrCodeScanner,
                                color = MaterialTheme.colorScheme.primary,
                                onClick = {
                                    showScanner = true
                                    scanErrorText = null
                                    scannedRegistration = null
                                    showScanSuccess = false
                                },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "Participants",
                                icon = Icons.Default.People,
                                color = MaterialTheme.colorScheme.secondary,
                                onClick = { activeDashboardTab = "Participants" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                title = "Verify Donations",
                                icon = Icons.Default.Verified,
                                color = Color(0xFF4CAF50),
                                onClick = { activeDashboardTab = "Verify" },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "Analytics",
                                icon = Icons.Default.Analytics,
                                color = Color(0xFFFFA000),
                                onClick = { activeDashboardTab = "Analytics" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                title = "Notifications",
                                icon = Icons.Default.Campaign,
                                color = Color(0xFF00ACC1),
                                onClick = { showBroadcastDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "Edit Camp",
                                icon = Icons.Default.Edit,
                                color = Color(0xFFE91E63),
                                onClick = { showEditDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                title = "Create Another Camp",
                                icon = Icons.Default.Add,
                                color = MaterialTheme.colorScheme.primary,
                                onClick = onNavigateCreateCamp,
                                modifier = Modifier.weight(1f).testTag("quick_action_create_another_camp")
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    // Active drive information
                    Text(text = "ACTIVE DRIVE INFORMATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp))

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
                                val statusLabel = when (campStatus) {
                                    "Cancelled" -> "Cancelled"
                                    "Paused" -> "Paused"
                                    "Closed" -> "Closed"
                                    else -> "Live Now"
                                }
                                val statusBgColor = when (campStatus) {
                                    "Cancelled" -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    "Paused" -> Color(0xFFFFA000).copy(alpha = 0.1f)
                                    "Closed" -> Color.Gray.copy(alpha = 0.1f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                }
                                val statusTextColor = when (campStatus) {
                                    "Cancelled" -> MaterialTheme.colorScheme.error
                                    "Paused" -> Color(0xFFFFA000)
                                    "Closed" -> Color.DarkGray
                                    else -> MaterialTheme.colorScheme.primary
                                }
                                Box(modifier = Modifier.background(
                                    statusBgColor, 
                                    RoundedCornerShape(12.dp)
                                ).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                    Text(
                                        text = statusLabel, 
                                        color = statusTextColor, 
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

                    // Registration controls
                    if (campStatus != "Cancelled") {
                        Text(text = "REGISTRATION SETTINGS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                            border = borderStroke()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (campStatus == "Active") {
                                        Button(
                                            onClick = { onPauseCamp(campId) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(16.dp))
                                                Text("Pause", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                        Button(
                                            onClick = { onCloseCamp(campId) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Lock, contentDescription = "Close Reg", modifier = Modifier.size(16.dp))
                                                Text("Close", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    } else if (campStatus == "Paused") {
                                        Button(
                                            onClick = { onResumeCamp(campId) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(16.dp))
                                                Text("Resume", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                        Button(
                                            onClick = { onCloseCamp(campId) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Lock, contentDescription = "Close Reg", modifier = Modifier.size(16.dp))
                                                Text("Close", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    } else if (campStatus == "Closed") {
                                        Button(
                                            onClick = { onResumeCamp(campId) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Re-open", modifier = Modifier.size(16.dp))
                                                Text("Re-open Registrations", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showBroadcastDialog = true },
                                        modifier = Modifier.weight(1.2f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(imageVector = Icons.Default.Campaign, contentDescription = "Broadcast", modifier = Modifier.size(16.dp))
                                            Text("Broadcast", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                    }

                                    Button(
                                        onClick = { showDuplicateDialog = true },
                                        modifier = Modifier.weight(1.2f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Duplicate", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                            Text("Reschedule", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Participants" -> {
                    var query by remember { mutableStateOf("") }
                    var filterStatus by remember { mutableStateOf("All") }

                    Text(text = "PARTICIPANTS REGISTRY", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    // Local search bar
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search by donor name...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Filter row
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Registered", "Checked In", "Donation Completed", "Donation Rejected").forEach { label ->
                            FilterChip(
                                selected = filterStatus == label,
                                onClick = { filterStatus = label },
                                label = { Text(label) }
                            )
                        }
                    }

                    val filteredRegs = registrations.filter { reg ->
                        val matchesQuery = reg.donorName.contains(query, ignoreCase = true)
                        val matchesStatus = if (filterStatus == "All") true else reg.status == filterStatus
                        matchesQuery && matchesStatus
                    }

                    if (filteredRegs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No participants matched the filter.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            filteredRegs.forEach { reg ->
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
                                                Text(text = "Slot: ${reg.selectedSlot}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                                                    text = "Disqualified: ${reg.rejectionReason}",
                                                    modifier = Modifier.padding(8.dp),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }

                                        // Inline Actions
                                        when (reg.status) {
                                            "Registered" -> {
                                                Button(
                                                    onClick = { onCheckInDonor(reg.registrationId) },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                                ) {
                                                    Text("Check-In Donor", fontWeight = FontWeight.Bold)
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
                                                        Text("Perform Screening", fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Verify" -> {
                    Text(text = "DONOR VERIFICATION HEALTH SCREENINGS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = "Select verified checked-in donors below to perform medical checks and authorize blood collections.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    val checkedInRegsOnly = registrations.filter { it.status == "Checked In" }

                    if (checkedInRegsOnly.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No donors are currently in Checked-In status.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            checkedInRegsOnly.forEach { reg ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(text = reg.donorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                                Text("Group: ${reg.bloodGroup} • Pass: ${reg.registrationId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("READY FOR CLINIC", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                            Text("Appointment slot: ${reg.selectedSlot} • Check-in time: ${reg.checkInTime ?: "N/A"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }

                                        Button(
                                            onClick = { showScreeningDialogForReg = reg },
                                            modifier = Modifier.fillMaxWidth().height(44.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(imageVector = Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Authorize & Complete Screening", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Analytics" -> {
                    Text(text = "CAMP PERFORMANCE & DIAGNOSTICS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    // Card 1: Target units collected vs. expect limit
                    val targetUnits = camp?.maxParticipants ?: 100
                    val targetPercent = if (targetUnits > 0) (countCompleted.toFloat() / targetUnits.toFloat() * 100).toInt() else 0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = borderStroke()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Campaign Target Achievements", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "$countCompleted / $targetUnits units", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("Blood collections secured today", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "$targetPercent%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer))
                                }
                            }

                            LinearProgressIndicator(
                                progress = { if (targetUnits > 0) countCompleted.toFloat() / targetUnits.toFloat() else 0f },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }

                    // Card 2: Demographic blood distribution grid
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = borderStroke()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Secured Blood Group Distributions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            
                            val bloodGroupsMap = registrations.filter { it.status == "Donation Completed" }.groupBy { it.bloodGroup }.mapValues { it.value.size }
                            val defaultGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                defaultGroups.chunked(4).forEach { chunk ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        chunk.forEach { grp ->
                                            val qty = bloodGroupsMap[grp] ?: 0
                                            Card(
                                                modifier = Modifier.weight(1f),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.15f))
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(8.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Icon(imageVector = Icons.Default.Bloodtype, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                                    Text(grp, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                                    Text("$qty Units", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Card 3: Screenings & Rejection Analysis
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = borderStroke()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Rejection & Screening Analytics", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
                            
                            val rejectionByReason = registrations.filter { it.status == "Donation Rejected" && it.rejectionReason != null }.groupBy { it.rejectionReason!! }.mapValues { it.value.size }

                            if (rejectionByReason.isEmpty()) {
                                Text("No screening rejections reported. Clinical acceptance rate is 100%.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    rejectionByReason.forEach { (reason, count) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.error, CircleShape))
                                                Text(reason, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                                            }
                                            Text("$count Donors", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
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
                        modifier = Modifier.testTag("verify_donation_btn"),
                        onClick = {
                            onCompleteDonation(reg.registrationId)
                            showScreeningDialogForReg = null
                        },
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

    if (showBroadcastDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { 
            showBroadcastDialog = false 
            isBroadcastSuccess = false
            broadcastMessage = ""
        }) {
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
                        text = "Broadcast Announcement",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "This will send an emergency notification immediately to all $countRegistered registered donor participants for this drive.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Notification Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        label = { Text("Announcement Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isBroadcastSuccess) {
                        Text(
                            text = "✓ Announcement successfully broadcasted!",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                showBroadcastDialog = false 
                                isBroadcastSuccess = false
                                broadcastMessage = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (broadcastMessage.isNotBlank()) {
                                    onNotifyCamp(campId, broadcastTitle, broadcastMessage)
                                    isBroadcastSuccess = true
                                }
                            },
                            enabled = broadcastMessage.isNotBlank() && !isBroadcastSuccess,
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Send")
                        }
                    }
                }
            }
        }
    }

    if (showDuplicateDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { 
            showDuplicateDialog = false 
            isDuplicateSuccess = false
        }) {
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
                        text = "Duplicate & Reschedule Camp",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Create a copy of this camp setup. This is ideal for recurring donation drives or rescheduling with a new date.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = duplicateTitle,
                        onValueChange = { duplicateTitle = it },
                        label = { Text("New Camp Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = duplicateDate,
                        onValueChange = { duplicateDate = it },
                        label = { Text("New Date & Time") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isDuplicateSuccess) {
                        Text(
                            text = "✓ Camp duplicated successfully!",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                showDuplicateDialog = false 
                                isDuplicateSuccess = false
                             },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (duplicateTitle.isNotBlank() && duplicateDate.isNotBlank()) {
                                    onDuplicateCamp(campId, duplicateTitle, duplicateDate)
                                    isDuplicateSuccess = true
                                }
                            },
                            enabled = duplicateTitle.isNotBlank() && duplicateDate.isNotBlank() && !isDuplicateSuccess,
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Duplicate")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCampScreen(
    state: BloodLinkUiState,
    onPublish: (com.bloodlink.app.data.FDonationCamp) -> Unit,
    onNavigateDashboard: (String) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(1) }
    var isSubmittedSuccessfully by remember { mutableStateOf(false) }
    var generatedCampId by remember { mutableStateOf("") }

    // Step 1: Organizer Details
    var organizerName by remember { mutableStateOf(state.profile.name) }
    var organizationName by remember { mutableStateOf("") }
    var organizationType by remember { mutableStateOf("Hospital") }
    var organizerPhone by remember { mutableStateOf(state.profile.mobile) }
    var organizerEmail by remember { mutableStateOf("") }
    var showOrgTypeDropdown by remember { mutableStateOf(false) }

    // Step 2: Verification Details
    var orgRegNo by remember { mutableStateOf("") }
    var bloodBankLicense by remember { mutableStateOf("") }
    var hospitalRegNo by remember { mutableStateOf("") }
    var govApprovalNo by remember { mutableStateOf("") }
    var uploadedDocs by remember { mutableStateOf(listOf<String>()) }
    var isUploadingDoc by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    // Step 3: Camp Details
    var campNameInput by remember { mutableStateOf("") }
    var campDesc by remember { mutableStateOf("") }
    var expectedDonors by remember { mutableStateOf("50") }
    var maxRegistrations by remember { mutableStateOf("100") }
    val bloodGroupsList = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var selectedBloodGroups by remember { mutableStateOf(bloodGroupsList.toSet()) }
    var isAllGroupsSelected by remember { mutableStateOf(true) }

    // Step 4: Location
    var venueName by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var stateName by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(40.7128) }
    var longitude by remember { mutableStateOf(-74.0060) }

    // Step 5: Date & Time
    var campDate by remember { mutableStateOf("Oct 28, 2026") }
    var closingDate by remember { mutableStateOf("Oct 27, 2026") }
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("05:00 PM") }

    // Step 6: Facilities
    var isMedicalTeam by remember { mutableStateOf(true) }
    var isDoctor by remember { mutableStateOf(true) }
    var isAmbulance by remember { mutableStateOf(false) }
    var isRefreshments by remember { mutableStateOf(true) }
    var isRestArea by remember { mutableStateOf(true) }
    var isParking by remember { mutableStateOf(true) }
    var isWheelchair by remember { mutableStateOf(false) }
    var isFirstAid by remember { mutableStateOf(true) }

    // Step 7: Contact Person
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var altContactPhone by remember { mutableStateOf("") }

    // Step 8: Camp Banner
    val defaultBanners = listOf(
        "https://images.unsplash.com/photo-1615461066841-6116ecdccd04?auto=format&fit=crop&q=80&w=600",
        "https://images.unsplash.com/photo-1536856788636-1cf9eaade906?auto=format&fit=crop&q=80&w=600",
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCwGHyxisPgOArtiO-XpMExa07NS6q-hjXUR_oqw8nZo0lyc517bcB4cQiBKb7u8sczMJZq5wMgk6qGhMtwOlST2YmeP2ssyVV9ww0l7JD8o82CI1H6yo-z7QPqyg6vVHvK4Iwm-0Adk_aFPkW0kueRwk-hNLdAdkuTj9a8EbyfwiNqvfOjJ0_4LfMEA9Y-xI-lnGEtMw-XXTKIKk6viZVeRYfsngAFobYSwGaEkctbO9YLkGcu8sa6NfbSrlEFzhhW0cFwp0_8ZiE"
    )
    var selectedBannerUrl by remember { mutableStateOf(defaultBanners[0]) }
    var isUploadingBanner by remember { mutableStateOf(false) }
    var bannerUploadProgress by remember { mutableStateOf(0f) }

    // Step 9: Terms
    var termsAccepted by remember { mutableStateOf(false) }

    // Validation helpers
    fun isStepValid(step: Int): Boolean {
        return when (step) {
            1 -> organizerName.isNotBlank() && organizationName.isNotBlank() && organizerPhone.isNotBlank() && organizerEmail.isNotBlank()
            2 -> true // optional credentials
            3 -> campNameInput.isNotBlank() && campDesc.isNotBlank() && expectedDonors.isNotBlank() && maxRegistrations.isNotBlank() && selectedBloodGroups.isNotEmpty()
            4 -> venueName.isNotBlank() && fullAddress.isNotBlank() && city.isNotBlank() && stateName.isNotBlank() && pincode.isNotBlank()
            5 -> campDate.isNotBlank() && closingDate.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()
            6 -> true // facilities optional
            7 -> contactName.isNotBlank() && contactPhone.isNotBlank()
            8 -> true // banner optional
            9 -> termsAccepted
            else -> false
        }
    }

    if (isSubmittedSuccessfully) {
        // Success Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "❤️ Camp Created Successfully",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your blood donation camp \"$campNameInput\" has been created successfully and is pending verification.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "The camp is now available in your Camp Management section.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { onNavigateDashboard(generatedCampId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("success_go_to_dashboard"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Dashboard, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Organizer Dashboard", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    currentStep = 1
                    isSubmittedSuccessfully = false
                    generatedCampId = ""
                    
                    // Clear fields
                    organizerName = state.profile.name
                    organizationName = ""
                    organizationType = "Hospital"
                    organizerPhone = state.profile.mobile
                    organizerEmail = ""
                    orgRegNo = ""
                    bloodBankLicense = ""
                    hospitalRegNo = ""
                    govApprovalNo = ""
                    campNameInput = ""
                    campDesc = ""
                    expectedDonors = "50"
                    maxRegistrations = "100"
                    venueName = ""
                    fullAddress = ""
                    city = ""
                    stateName = ""
                    pincode = ""
                    campDate = "Oct 28, 2026"
                    closingDate = "Oct 27, 2026"
                    startTime = "09:00 AM"
                    endTime = "05:00 PM"
                    contactName = ""
                    contactPhone = ""
                    altContactPhone = ""
                    termsAccepted = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("success_create_another_camp"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Another Camp", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("success_back_to_camps"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back To Camps", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    } else {
        // Multi-Step Form Screen
        Scaffold(
            topBar = {
                OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Organize Blood Camp", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
            ) {
                // Step Progress Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentStep) {
                                1 -> "Organizer Details"
                                2 -> "Verification Credentials"
                                3 -> "Camp Configuration"
                                4 -> "Venue & Location"
                                5 -> "Schedule Timeline"
                                6 -> "Amenities & Facilities"
                                7 -> "Emergency Coordinators"
                                8 -> "Campaign Banner"
                                9 -> "Review & Terms"
                                else -> ""
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "Step $currentStep of 9",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LinearProgressIndicator(
                        progress = { currentStep.toFloat() / 9f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // Step content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (currentStep) {
                            1 -> {
                                Text(
                                    text = "Organizer Information",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Identify the hosting party and organization credentials.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = organizerName,
                                    onValueChange = { organizerName = it },
                                    label = { Text("Organizer Full Name *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_organizer_name"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = organizationName,
                                    onValueChange = { organizationName = it },
                                    label = { Text("Organization Name *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Business, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_org_name"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Custom Dropdown for Organization Type
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = organizationType,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Organization Type *") },
                                        trailingIcon = {
                                            IconButton(onClick = { showOrgTypeDropdown = !showOrgTypeDropdown }) {
                                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("input_org_type"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    DropdownMenu(
                                        expanded = showOrgTypeDropdown,
                                        onDismissRequest = { showOrgTypeDropdown = false },
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        val types = listOf("Hospital", "NGO", "College", "Corporate", "Government Organization", "Social Service Group", "Blood Bank", "Other")
                                        types.forEach { type ->
                                            DropdownMenuItem(
                                                text = { Text(type) },
                                                onClick = {
                                                    organizationType = type
                                                    showOrgTypeDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = organizerPhone,
                                    onValueChange = { organizerPhone = it },
                                    label = { Text("Organizer Mobile Number *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_organizer_phone"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = organizerEmail,
                                    onValueChange = { organizerEmail = it },
                                    label = { Text("Organizer Email Address *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_organizer_email"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            2 -> {
                                Text(
                                    text = "Verification Details (Optional)",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Providing verification details establishes higher priority and user trust. Fake camps are strictly audited.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = orgRegNo,
                                    onValueChange = { orgRegNo = it },
                                    label = { Text("Organization Registration Number (Optional)") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_org_reg"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = bloodBankLicense,
                                    onValueChange = { bloodBankLicense = it },
                                    label = { Text("Blood Bank License Number (Optional)") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_bb_license"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = hospitalRegNo,
                                    onValueChange = { hospitalRegNo = it },
                                    label = { Text("Hospital Registration Number (Optional)") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_hospital_reg"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = govApprovalNo,
                                    onValueChange = { govApprovalNo = it },
                                    label = { Text("Government Approval Number (Optional)") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_gov_approval"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Text(
                                    text = "Upload Supporting Documents",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isUploadingDoc = true
                                            uploadProgress = 0f
                                            while (uploadProgress < 1f) {
                                                kotlinx.coroutines.delay(100)
                                                uploadProgress += 0.2f
                                            }
                                            val docTypes = listOf("Organization ID", "Authorization Letter", "Government Certificate", "Supporting Docs")
                                            val newDoc = "${docTypes.random()}_${(100..999).random()}.pdf"
                                            uploadedDocs = uploadedDocs + newDoc
                                            isUploadingDoc = false
                                        }
                                    },
                                    enabled = !isUploadingDoc,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.AttachFile, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Simulate Upload PDF/Image Document")
                                }

                                if (isUploadingDoc) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        LinearProgressIndicator(progress = { uploadProgress }, modifier = Modifier.fillMaxWidth())
                                        Text("Uploading file... ${(uploadProgress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                if (uploadedDocs.isNotEmpty()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("Uploaded Documents:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            uploadedDocs.forEach { docName ->
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                                    Text(docName, style = MaterialTheme.typography.bodyMedium)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> {
                                Text(
                                    text = "Camp Configuration Details",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )

                                OutlinedTextField(
                                    value = campNameInput,
                                    onValueChange = { campNameInput = it },
                                    label = { Text("Camp Campaign Title *") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_camp_title"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = campDesc,
                                    onValueChange = { campDesc = it },
                                    label = { Text("Camp Description / Campaign Objective *") },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth().testTag("input_camp_desc"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = expectedDonors,
                                        onValueChange = { expectedDonors = it },
                                        label = { Text("Expected Donors *") },
                                        modifier = Modifier.weight(1f).testTag("input_expected_donors"),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    OutlinedTextField(
                                        value = maxRegistrations,
                                        onValueChange = { maxRegistrations = it },
                                        label = { Text("Max Registrations *") },
                                        modifier = Modifier.weight(1f).testTag("input_max_participants"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Required Blood Groups *", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        isAllGroupsSelected = !isAllGroupsSelected
                                        selectedBloodGroups = if (isAllGroupsSelected) bloodGroupsList.toSet() else emptySet()
                                    }
                                ) {
                                    Checkbox(
                                        checked = isAllGroupsSelected,
                                        onCheckedChange = {
                                            isAllGroupsSelected = it
                                            selectedBloodGroups = if (it) bloodGroupsList.toSet() else emptySet()
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("All Blood Groups Required")
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(12.dp))
                                        .border(borderStroke(), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    // Blood Group Multi-Select Buttons
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        val chunkedGroups = bloodGroupsList.chunked(4)
                                        chunkedGroups.forEach { rowGroups ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowGroups.forEach { bg ->
                                                    val isSelected = selectedBloodGroups.contains(bg)
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(48.dp)
                                                            .background(
                                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .clickable {
                                                                val updated = selectedBloodGroups.toMutableSet()
                                                                if (isSelected) {
                                                                    updated.remove(bg)
                                                                    isAllGroupsSelected = false
                                                                } else {
                                                                    updated.add(bg)
                                                                    if (updated.size == bloodGroupsList.size) isAllGroupsSelected = true
                                                                }
                                                                selectedBloodGroups = updated
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = bg,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            4 -> {
                                Text(
                                    text = "Venue Location",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )

                                OutlinedTextField(
                                    value = venueName,
                                    onValueChange = { venueName = it },
                                    label = { Text("Venue / Landmark Name *") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_venue_name"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = fullAddress,
                                    onValueChange = { fullAddress = it },
                                    label = { Text("Full Address *") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_address"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = city,
                                        onValueChange = { city = it },
                                        label = { Text("City *") },
                                        modifier = Modifier.weight(1.2f).testTag("input_city"),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    OutlinedTextField(
                                        value = stateName,
                                        onValueChange = { stateName = it },
                                        label = { Text("State *") },
                                        modifier = Modifier.weight(1f).testTag("input_state"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                OutlinedTextField(
                                    value = pincode,
                                    onValueChange = { pincode = it },
                                    label = { Text("PIN / Postal Code *") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_pincode"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Map Coordinates Selector", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clickable {
                                            // Simulate selecting a random pinpoint nearby
                                            latitude = 40.7128 + ((1..100).random() - 50) * 0.001
                                            longitude = -74.0060 + ((1..100).random() - 50) * 0.001
                                        },
                                    border = borderStroke(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // A visual mockup of map with gridlines
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val w = size.width
                                            val h = size.height
                                            // draw lines
                                            for (i in 1..8) {
                                                drawLine(Color.LightGray.copy(alpha = 0.4f), start = androidx.compose.ui.geometry.Offset(0f, h * i / 8f), end = androidx.compose.ui.geometry.Offset(w, h * i / 8f))
                                                drawLine(Color.LightGray.copy(alpha = 0.4f), start = androidx.compose.ui.geometry.Offset(w * i / 8f, 0f), end = androidx.compose.ui.geometry.Offset(w * i / 8f, h))
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Text(
                                                "TAP MAP TO PLACE TARGET PIN",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    "Latitude: ${"%.5f".format(latitude)} | Longitude: ${"%.5f".format(longitude)}",
                                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            5 -> {
                                Text(
                                    text = "Date & Time Schedule",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )

                                OutlinedTextField(
                                    value = campDate,
                                    onValueChange = { campDate = it },
                                    label = { Text("Camp Date (e.g., Oct 28, 2026) *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_camp_date"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = closingDate,
                                    onValueChange = { closingDate = it },
                                    label = { Text("Registration Closing Date *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_closing_date"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = startTime,
                                        onValueChange = { startTime = it },
                                        label = { Text("Start Time *") },
                                        leadingIcon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = null) },
                                        modifier = Modifier.weight(1f).testTag("input_start_time"),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    OutlinedTextField(
                                        value = endTime,
                                        onValueChange = { endTime = it },
                                        label = { Text("End Time *") },
                                        leadingIcon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = null) },
                                        modifier = Modifier.weight(1f).testTag("input_end_time"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                            6 -> {
                                Text(
                                    text = "Amenities & Camp Facilities",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Select checkmarks representing amenities available on-site at the donation camp venue.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                val checkList = listOf(
                                    "Medical Team Available" to isMedicalTeam,
                                    "Doctor Available" to isDoctor,
                                    "Ambulance Available" to isAmbulance,
                                    "Refreshments Available" to isRefreshments,
                                    "Rest Area Available" to isRestArea,
                                    "Parking Available" to isParking,
                                    "Wheelchair Accessible" to isWheelchair,
                                    "First Aid Available" to isFirstAid
                                )

                                checkList.forEach { (label, value) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                when (label) {
                                                    "Medical Team Available" -> isMedicalTeam = !isMedicalTeam
                                                    "Doctor Available" -> isDoctor = !isDoctor
                                                    "Ambulance Available" -> isAmbulance = !isAmbulance
                                                    "Refreshments Available" -> isRefreshments = !isRefreshments
                                                    "Rest Area Available" -> isRestArea = !isRestArea
                                                    "Parking Available" -> isParking = !isParking
                                                    "Wheelchair Accessible" -> isWheelchair = !isWheelchair
                                                    "First Aid Available" -> isFirstAid = !isFirstAid
                                                }
                                            }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Checkbox(
                                            checked = value,
                                            onCheckedChange = {
                                                when (label) {
                                                    "Medical Team Available" -> isMedicalTeam = it
                                                    "Doctor Available" -> isDoctor = it
                                                    "Ambulance Available" -> isAmbulance = it
                                                    "Refreshments Available" -> isRefreshments = it
                                                    "Rest Area Available" -> isRestArea = it
                                                    "Parking Available" -> isParking = it
                                                    "Wheelchair Accessible" -> isWheelchair = it
                                                    "First Aid Available" -> isFirstAid = it
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(label, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }
                            }
                            7 -> {
                                Text(
                                    text = "Emergency Coordinator Contacts",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )

                                OutlinedTextField(
                                    value = contactName,
                                    onValueChange = { contactName = it },
                                    label = { Text("Contact Person Name *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_contact_name"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = contactPhone,
                                    onValueChange = { contactPhone = it },
                                    label = { Text("Contact Mobile Number *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_contact_phone"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = altContactPhone,
                                    onValueChange = { altContactPhone = it },
                                    label = { Text("Alternative Contact Number (Optional)") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_alt_phone"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            8 -> {
                                Text(
                                    text = "Campaign Banner & Cover Image",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Choose from preset gorgeous campaign banners or trigger a mock image upload.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = borderStroke()
                                ) {
                                    AsyncImage(
                                        model = selectedBannerUrl,
                                        contentDescription = "Selected Banner",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Choose Banner Theme Preset", fontWeight = FontWeight.Bold)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    defaultBanners.forEachIndexed { idx, url ->
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(64.dp)
                                                .clickable { selectedBannerUrl = url }
                                                .border(
                                                    width = if (selectedBannerUrl == url) 3.dp else 1.dp,
                                                    color = if (selectedBannerUrl == url) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            AsyncImage(
                                                model = url,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isUploadingBanner = true
                                            bannerUploadProgress = 0f
                                            while (bannerUploadProgress < 1f) {
                                                kotlinx.coroutines.delay(100)
                                                bannerUploadProgress += 0.25f
                                            }
                                            selectedBannerUrl = "https://images.unsplash.com/photo-1615461066841-6116ecdccd04?auto=format&fit=crop&q=80&w=600"
                                            isUploadingBanner = false
                                        }
                                    },
                                    enabled = !isUploadingBanner,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Simulate Custom Banner Upload")
                                }

                                if (isUploadingBanner) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        LinearProgressIndicator(progress = { bannerUploadProgress }, modifier = Modifier.fillMaxWidth())
                                        Text("Uploading banner image... ${(bannerUploadProgress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            9 -> {
                                Text(
                                    text = "Confirm Declaration & Guidelines",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(
                                            "Host Guidelines Checklist:",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                        )
                                        val declarations = listOf(
                                            "All information provided is true, correct, and matching our organization credentials.",
                                            "Required legal & healthcare permissions have been obtained from district councils.",
                                            "We agree to strictly follow the national blood donation safety & sterility guidelines.",
                                            "We understand that fake or unverified camps will be immediately removed and reported."
                                        )
                                        declarations.forEach { decl ->
                                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                                                Text(decl, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { termsAccepted = !termsAccepted }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Checkbox(
                                        checked = termsAccepted,
                                        onCheckedChange = { termsAccepted = it },
                                        modifier = Modifier.testTag("input_terms")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("I accept all blood camp hosting terms & guidelines *", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Bottom Action Buttons
                Surface(
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep > 1) {
                            OutlinedButton(
                                onClick = { currentStep-- },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_prev")
                            ) {
                                Text("Previous")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        val isStepValid = isStepValid(currentStep)
                        Button(
                            onClick = {
                                if (currentStep < 9) {
                                    currentStep++
                                } else {
                                    // Submit
                                    val randomId = "camp_" + (100000..999999).random().toString()
                                    generatedCampId = randomId

                                    // Build facilities list
                                    val finalFacilities = mutableListOf<String>()
                                    if (isMedicalTeam) finalFacilities.add("Medical Team Available")
                                    if (isDoctor) finalFacilities.add("Doctor Available")
                                    if (isAmbulance) finalFacilities.add("Ambulance Available")
                                    if (isRefreshments) finalFacilities.add("Refreshments Available")
                                    if (isRestArea) finalFacilities.add("Rest Area Available")
                                    if (isParking) finalFacilities.add("Parking Available")
                                    if (isWheelchair) finalFacilities.add("Wheelchair Accessible")
                                    if (isFirstAid) finalFacilities.add("First Aid Available")

                                    val finalCamp = com.bloodlink.app.data.FDonationCamp(
                                        id = randomId,
                                        campId = randomId,
                                        title = campNameInput,
                                        campName = campNameInput,
                                        organizer = organizerName,
                                        organizerId = state.profile.id,
                                        organizerName = organizerName,
                                        organizationName = organizationName,
                                        organizationType = organizationType,
                                        organizationRegistrationNumber = orgRegNo,
                                        bloodBankLicenseNumber = bloodBankLicense,
                                        hospitalRegistrationNumber = hospitalRegNo,
                                        governmentApprovalNumber = govApprovalNo,
                                        documents = uploadedDocs,
                                        address = fullAddress,
                                        venue = venueName,
                                        city = city,
                                        state = stateName,
                                        pincode = pincode,
                                        latitude = latitude,
                                        longitude = longitude,
                                        dateText = campDate,
                                        date = campDate,
                                        registrationClosingDate = closingDate,
                                        timeText = "$startTime - $endTime",
                                        startTime = startTime,
                                        endTime = endTime,
                                        expectedDonors = expectedDonors.toIntOrNull() ?: 50,
                                        participantsCount = 0,
                                        maxParticipants = maxRegistrations.toIntOrNull() ?: 100,
                                        maximumParticipants = maxRegistrations.toIntOrNull() ?: 100,
                                        bloodGroupsNeeded = selectedBloodGroups.toList(),
                                        requiredBloodGroups = selectedBloodGroups.toList(),
                                        facilities = finalFacilities,
                                        contactPerson = contactName,
                                        contactNumber = contactPhone,
                                        alternateContact = altContactPhone,
                                        imageUrl = selectedBannerUrl,
                                        bannerUrl = selectedBannerUrl,
                                        status = "Upcoming",
                                        verificationStatus = "Pending",
                                        createdAt = System.currentTimeMillis(),
                                        updatedAt = System.currentTimeMillis()
                                    )

                                    onPublish(finalCamp)
                                    isSubmittedSuccessfully = true
                                }
                            },
                            enabled = isStepValid,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp)
                                .testTag("btn_next")
                        ) {
                            Text(
                                text = if (currentStep == 9) "Submit Camp & Publish" else "Next Step",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampManagementScreen(
    state: BloodLinkUiState,
    onNavigateCreateCamp: () -> Unit,
    onOpenDashboard: (String) -> Unit,
    onEditCamp: (String, String, String, String) -> Unit,
    onDeleteCamp: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val myCamps = state.nearbyCamps.filter { it.organizerId == state.profile.id || it.organizer == state.profile.name }

    var showEditDialogForCamp by remember { mutableStateOf<DonationCamp?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Campaigns", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = onNavigateCreateCamp,
                        modifier = Modifier.testTag("toolbar_create_new_camp_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("Create New Camp", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            if (myCamps.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏥 No Camps Organized Yet",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "You haven't organized any blood donation camps yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateCreateCamp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("empty_management_organize_first_camp"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("❤️ Organize Your First Camp", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(myCamps) { camp ->
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
                                        .height(120.dp)
                                ) {
                                    AsyncImage(
                                        model = camp.imageUrl,
                                        contentDescription = camp.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    val statusColor = when (camp.status) {
                                        "Cancelled" -> MaterialTheme.colorScheme.error
                                        "Paused" -> Color(0xFFFFA000)
                                        "Closed" -> Color.Gray
                                        else -> Color(0xFF4CAF50)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(12.dp)
                                            .background(statusColor, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (camp.status.isNotBlank()) camp.status.uppercase() else "ACTIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = camp.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                        Text(text = camp.dateText, style = MaterialTheme.typography.bodyMedium)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                        Text(text = camp.address, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }

                                    val campRegs = state.campRegistrations.filter { it.campId == camp.id }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = "Participants: ${campRegs.size} registered / ${camp.maxParticipants} max slots",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    // Action Buttons Row: Open Dashboard, Edit, Share, Delete
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onOpenDashboard(camp.id) },
                                            modifier = Modifier.weight(1.5f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Text("Dashboard", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                showEditDialogForCamp = camp
                                                editTitle = camp.title
                                                editAddress = camp.address
                                                editDate = camp.dateText
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Text("Edit", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val shareText = "Join our blood drive: ${camp.title} at ${camp.address} on ${camp.dateText}! Register on BloodLink app."
                                                val intent = android.content.Intent().apply {
                                                    action = android.content.Intent.ACTION_SEND
                                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(android.content.Intent.createChooser(intent, "Share Camp Details"))
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Text("Share", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteCamp(camp.id) },
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                .size(40.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Camp dialog
    if (showEditDialogForCamp != null) {
        val editingCamp = showEditDialogForCamp!!
        AlertDialog(
            onDismissRequest = { showEditDialogForCamp = null },
            title = { Text("Edit Camp Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        label = { Text("Date (e.g. Oct 28, 2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEditCamp(editingCamp.id, editTitle, editAddress, editDate)
                        showEditDialogForCamp = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialogForCamp = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

