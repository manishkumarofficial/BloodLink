package com.bloodlink.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bloodlink.app.ui.BloodLinkUiState
import com.bloodlink.app.ui.ClientProfile
import com.bloodlink.app.ui.UserRole
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.net.Uri
import androidx.compose.ui.window.Dialog

@Composable
fun DonorRegistrationScreen(
    state: BloodLinkUiState,
    onComplete: (ClientProfile) -> Unit
) {
    var name by remember { mutableStateOf(state.profile.name) }
    var dob by remember { mutableStateOf("1992-10-14") }
    var gender by remember { mutableStateOf("Male") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var radius by remember { mutableFloatStateOf(50f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(24.dp, 8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                Box(modifier = Modifier.size(24.dp, 8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                Box(modifier = Modifier.size(24.dp, 8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Donor Registration",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Complete your profile to start saving lives in your community.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Personal details
        Text(
            text = "PERSONAL DETAILS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of Birth") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Gender") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Blood Group Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BLOOD GROUP",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "I don't know",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {  }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val groups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (row in 0..1) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..3) {
                        val index = row * 4 + col
                        if (index < groups.size) {
                            val gp = groups[index]
                            val isSelected = bloodGroup == gp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLowest)
                                    .border(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    .clickable { bloodGroup = gp },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gp,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Contact & Availability
        Text(
            text = "CONTACT & AVAILABILITY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Location Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Pin", tint = MaterialTheme.colorScheme.primary)
                        Text(text = "Current Location", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Text(
                        text = "Locate Me",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCQbUUITwdRstlGmM_2mv5_ahG8JgdgGouYKsoQ5Nb28qTl30GOkptPiAaM36zxBDiZR1SpWdpYCcTrPs4YOQmTDcITUH9-fznHGv9lQ8MvOBxEM_ZMzw2jVOrdQbhLJVMq3zTn36RjXFpFCmCeWcpy-j8UUZ8fiM-z3PdRrTb66_Sa1Fv7O8W0lNaqtguRH1SEtrFxGzn5LZaHycvBWcdlNhxLkPH5abX5T3f2sYtSqzgZyu7Ii8T-dTEvRRSMcUK_lUhjPhHpJK8",
                        contentDescription = "Map location",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "123 Health Ave, Metro City",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Distance range
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Willing to travel up to:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = "${radius.toInt()} km", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = radius,
            onValueChange = { radius = it },
            valueRange = 5f..100f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onComplete(
                    state.profile.copy(
                        name = name,
                        bloodGroup = bloodGroup,
                        travelRadiusKm = radius.toInt()
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Complete Profile", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Complete")
            }
        }
    }
}

@Composable
fun HomeDashboardScreen(
    state: BloodLinkUiState,
    onNavigateBack: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onToggleAvailability: (Boolean) -> Unit,
    onNavigateCamps: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateRequestBlood: () -> Unit,
    onDismissBirthday: () -> Unit,
    onNavigateCampPass: () -> Unit
) {
    if (state.showBirthdayBanner) {
        AlertDialog(
            onDismissRequest = onDismissBirthday,
            confirmButton = {
                Button(
                    onClick = {
                        onDismissBirthday()
                        onNavigateCamps()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Gift a Donation", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissBirthday) {
                    Text("Maybe later", color = MaterialTheme.colorScheme.secondary)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Cake,
                    contentDescription = "Cake",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Happy Birthday, ${state.profile.name}!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
            },
            text = {
                Text(
                    text = "Your birthday is a celebration of life. Why not celebrate by saving three more today?",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Screen header (Greeting Card Base)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF1F3F4),
                        modifier = Modifier.size(44.dp)
                    ) {
                        if (state.profile.profilePictureUri != null) {
                            AsyncImage(
                                model = state.profile.profilePictureUri,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFC62828),
                                                Color(0xFFEF5350)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.profile.name.split(" ")
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .take(2)
                                        .joinToString(""),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                    Column {
                        Text(text = "Good Morning,", style = MaterialTheme.typography.labelSmall, color = Color(0xFF616161))
                        Text(text = state.profile.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)))
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFDFE1E5)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onNavigateNotifications() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFFC62828))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cooldown alert if active
            if (state.profile.cooldownCountdownDays > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFDFE1E5))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFB00020),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.HourglassBottom, contentDescription = "Cooldown", tint = Color.White)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rest Period Active: ${state.profile.cooldownCountdownDays} Days Left",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                            )
                            Text(
                                text = "To ensure body recuperation, you must wait between blood units. Your availability is paused.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF616161)
                            )
                        }
                    }
                }
            }

            // GREETING HERO CARD with BLOOD GROUP BADGE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFDFE1E5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                            Text(
                                text = "LIFE-SAVER PROFILE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFC62828), letterSpacing = 1.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Every donor is a local hero.",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // Availability Switch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Switch(
                                checked = state.profile.isAvailable,
                                onCheckedChange = { onToggleAvailability(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFC62828),
                                    uncheckedThumbColor = Color(0xFF9E9E9E),
                                    uncheckedTrackColor = Color(0xFFF1F3F4)
                                )
                            )
                            Text(
                                text = if (state.profile.isAvailable) "Available for Requests" else "Unavailable (Offline)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (state.profile.isAvailable) Color(0xFFC62828) else Color(0xFF616161)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // BLOOD GROUP BADGE
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color(0xFFEF5350), Color(0xFFC62828))
                                    ),
                                    shape = CircleShape
                                )
                                .shadow(2.dp, shape = CircleShape)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Bloodtype,
                                    contentDescription = "Blood drop",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = state.profile.bloodGroup,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Blood Group",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF616161))
                        )
                    }
                }
            }

            // DONATION STATISTICS SECTION
            Text(
                text = "Donation Statistics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFDFE1E5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Total Donations", tint = Color(0xFFC62828), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.profile.totalDonations.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        )
                        Text(text = "Total Donations", style = MaterialTheme.typography.labelSmall, color = Color(0xFF616161))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFDFE1E5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = "Lives Saved", tint = Color(0xFFC62828), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.profile.livesSaved.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        )
                        Text(text = "Lives Saved", style = MaterialTheme.typography.labelSmall, color = Color(0xFF616161))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Level Progress Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFDFE1E5))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        val strokeColor = Color(0xFFC62828)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = Color(0xFFF1F3F4), style = Stroke(width = 4.dp.toPx()))
                            drawArc(
                                color = strokeColor,
                                startAngle = -90f,
                                sweepAngle = (state.profile.xp.toFloat() / 1000f) * 360f,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = "Level progress badge", tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                    }

                    Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(text = state.profile.level, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)))
                        Text(text = "Level Progress", style = MaterialTheme.typography.labelSmall, color = Color(0xFF616161))
                    }
                    Text(text = "${state.profile.xp} / 1000 XP", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)))
                }
            }

            // NEARBY REQUESTS SECTION with RED ACCENT STRIPS
            Text(
                text = "Nearby Emergency Requests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
            )

            if (state.activeRequests.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFDFE1E5))
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No urgent requests nearby.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF616161))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.activeRequests.take(3).forEach { request ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFDFE1E5))
                        ) {
                            Box {
                                // RED ACCENT STRIP ON THE LEFT
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .height(110.dp)
                                        .width(6.dp)
                                        .background(Color(0xFFC62828))
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 18.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFB00020), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (request.isCritical) "CRITICAL" else "URGENT",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                                )
                                            }
                                            Text(
                                                text = "• ${request.distanceText}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF616161)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = request.hospitalName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                                        )
                                        Text(
                                            text = "Status: ${request.patientStatus}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF616161)
                                        )
                                    }

                                    // Blood badge
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(Color(0xFFF8F9FA), shape = CircleShape)
                                            .border(1.5.dp, Color(0xFFC62828), shape = CircleShape)
                                    ) {
                                        Text(
                                            text = request.bloodGroup,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // UPCOMING CAMPS SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Blood Drives",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                )
                TextButton(onClick = onNavigateCamps) {
                    Text("View All", color = Color(0xFFC62828), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            if (state.nearbyCamps.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFDFE1E5))
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No active camps scheduled.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF616161))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.nearbyCamps.take(2).forEach { camp ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateCamps() },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFDFE1E5))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8F9FA),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Camp icon", tint = Color(0xFFC62828), modifier = Modifier.size(28.dp))
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = camp.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                                    )
                                    Text(
                                        text = camp.organizer,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF616161)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${camp.dateText} • ${camp.timeText}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFC62828)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // RECENT NOTIFICATIONS SECTION
            Text(
                text = "Recent Notifications",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFDFE1E5))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE53935).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = "System Update", tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(text = "System Update: BloodLink Redesign Live", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)))
                            Text(text = "Enjoy our completely new premium healthcare design system.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF616161))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F3F4))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF2E7D32).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Status check", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(text = "Eligibility Confirmed", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF212121)))
                            Text(text = "You are currently eligible and available to donate blood.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF616161))
                        }
                    }
                }
            }
        }

        // Float emergency FAB (16dp rounded corners, elevation, white text, deep blood red background)
        Button(
            onClick = onNavigateRequestBlood,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
                .height(56.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Emergency, contentDescription = "Emergency", tint = Color.White)
                Text("REQUEST BLOOD", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp, color = Color.White))
            }
        }
    }
}

@Composable
fun BentoCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = borderStroke()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun UserProfileScreen(
    state: BloodLinkUiState,
    onSave: (ClientProfile) -> Unit,
    onLogout: () -> Unit,
    onNavigateSettings: () -> Unit,
    onBack: () -> Unit,
    onManageCamp: (String) -> Unit = {},
    onViewCamp: (String) -> Unit = {}
) {
    var isAvailable by remember { mutableStateOf(state.profile.isAvailable) }
    var radius by remember { mutableFloatStateOf(state.profile.travelRadiusKm.toFloat()) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var showFullscreenPhoto by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onSave(state.profile.copy(profilePictureUri = uri.toString()))
        }
    }

    val avatarPresets = listOf(
        "https://lh3.googleusercontent.com/aida-public/AB6AXuAMIeNH3jzScvYLrPA4AesJte9Y9rDIezErpsmqwIS_hmFHQ9SVVxk4WzdT7v5ty0RFF_W_Psq2YPLuPWnCfZOJkxLHXTtoPhpqk8GIzw0O2fOgoqeVrv17aSsnmFK-UyRrvjfj5VmnXoZwEjGCKNGtyJUbmo1zkrjiRSsgD-nrurQKqu2yUEgC4fb0v1u3I52jDqFV06Iyy-d68PVBEnupFnknvf9QhVleeeas7Og9vTqq2q4bgMrVA4--WjZCeiedBtzoaaZPlc4",
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCQbUUITwdRstlGmM_2mv5_ahG8JgdgGouYKsoQ5Nb28qTl30GOkptPiAaM36zxBDiZR1SpWdpYCcTrPs4YOQmTDcITUH9-fznHGv9lQ8MvOBxEM_ZMzw2jVOrdQbhLJVMq3zTn36RjXFpFCmCeWcpy-j8UUZ8fiM-z3PdRrTb66_Sa1Fv7O8W0lNaqtguRH1SEtrFxGzn5LZaHycvBWcdlNhxLkPH5abX5T3f2sYtSqzgZyu7Ii8T-dTEvRRSMcUK_lUhjPhHpJK8",
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCwGHyxisPgOArtiO-XpMExa07NS6q-hjXUR_oqw8nZo0lyc517bcB4cQiBKb7u8sczMJZq5wMgk6qGhMtwOlST2YmeP2ssyVV9ww0l7JD8o82CI1H6yo-z7QPqyg6vVHvK4Iwm-0Adk_aFPkW0kueRwk-hNLdAdkuTj9a8EbyfwiNqvfOjJ0_4LfMEA9Y-xI-lnGEtMw-XXTKIKk6viZVeRYfsngAFobYSwGaEkctbO9YLkGcu8sa6NfbSrlEFzhhW0cFwp0_8ZiE",
        "https://lh3.googleusercontent.com/aida-public/AB6AXuBLtaj8KXlPRIm_stpT-09gv5YEv7eEZoH1B86ilUV2w6szlqsppFjv1a44cdi15FwC14HOJ133sXLOfK7yt_Iil1xbuvTU41chcy2vGr1l2DFYC2rBvkGMqJozxlI7uP3eXV_8_X42-HYRrqCe0iXWSkinxMn9Frfat05a8K2Oh6n3faauXsRI9zseZAvD_HV-IC10UE_pyfV8LdtksyPtD-QLWaSoQIh9mOZm-MNIKd-auUZLsAIycGJBMMzV1Kg-ZniMK1eeCug",
        "https://lh3.googleusercontent.com/aida-public/AB6AXuBn77yxj5Mn4WzvNsosslf-jZBjCb4Jbw-ba6XhYL_n2udVf3VwS90Ke1pYkc7wGKP0BgXFjoheCs5GBMBcljnxHgp8zkVUU-SuaS5z9MGuxRev76vCYlyC92LymCOc-UQn3kLnPPTR6H5Cg4PPbrRZwWSUuTrUgaJtSGAmfnixcj6RzJpkQI71hiFM0tPGpHBIRktporV-_a2N-k1GDG8maC_znumKWXdn9yyxhZh8FAjbPit4ehyBWbD2U1hP74xoybsRAKeNwtk",
        "https://lh3.googleusercontent.com/aida-public/AB6AXuDgKCSTe7MyjREXGxA7IWjWZT_MVdyc9C9FCtIpa5KK_9_Ecn4Lre9fJXi0jVH3DXpzn1q197JwdpTkuA4-uXELTfuY4ZsqB1cXuZAsrWUom3uwVWBD2SNfJbM-F7NoZ2e56-7AS8aoMf0GVtTqNnH8owJjmxeXKjPxiB3dH80Uqnm-dIyeoyzSlRbXL_9HsVedF3nT7SEQ5gDtJ9pYZX-JAZdAKqAPPrHzPaB240JgCu20jV8CbMuxaH4J9h6dAkW_v0yhlgdO9Kg"
    )
    val presetNames = listOf("Alex (Default)", "Crimson", "Guardian", "Life Map", "Care Team", "Tech Drive")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(126.dp)
                .clickable { showPhotoDialog = true }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(4.dp, MaterialTheme.colorScheme.primaryContainer),
                shadowElevation = 8.dp
            ) {
                if (state.profile.profilePictureUri != null) {
                    AsyncImage(
                        model = state.profile.profilePictureUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.profile.name.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString(""),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(34.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Edit Profile Photo", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { showPhotoDialog = true }) {
            Icon(imageVector = Icons.Default.Portrait, contentDescription = "Portrait Image", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Manage Profile Picture", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = state.profile.name, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = { Text("Gold Hero") },
                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            )
            AssistChip(onClick = {}, label = { Text("O+ Positive") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "12", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text(text = "Donations", style = MaterialTheme.typography.labelSmall)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "36", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text(text = "Lives Saved", style = MaterialTheme.typography.labelSmall)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "4", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text(text = "Level", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Personal Info", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

            InfoRow(label = "Mobile Number", value = state.profile.mobile, icon = Icons.Default.Phone)
            InfoRow(label = "Date of Birth", value = state.profile.birthDate, icon = Icons.Default.CalendarToday)
            InfoRow(label = "Gender", value = state.profile.gender, icon = Icons.Default.Person)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Donor Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Donor Availability", fontWeight = FontWeight.Bold)
                            Text(text = "Receive emergency blood requests", style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(
                            checked = isAvailable,
                            onCheckedChange = { 
                                isAvailable = it 
                                onSave(state.profile.copy(isAvailable = it, travelRadiusKm = radius.toInt()))
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Travel Radius", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${radius.toInt()} km",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = radius,
                        onValueChange = { 
                            radius = it 
                            onSave(state.profile.copy(isAvailable = isAvailable, travelRadiusKm = it.toInt()))
                        },
                        valueRange = 5f..100f
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Emergency Contacts", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Primary", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        Column {
                            Text(text = "Sarah Rivera", fontWeight = FontWeight.Bold)
                            Text(text = "Spouse • Primary", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "My Blood Camps", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

            // Filter camps organized by this user
            val organizedCamps = state.nearbyCamps.filter { it.organizerId == state.profile.id || it.organizer == state.profile.name }
            // Filter camps joined/registered by this user
            val registeredCamps = state.nearbyCamps.filter { camp ->
                state.campRegistrations.any { it.campId == camp.id && it.donorId == state.profile.id }
            }

            if (organizedCamps.isEmpty() && registeredCamps.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = borderStroke()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "You haven't organized or registered for any camps yet.", 
                            color = Color.Gray, 
                            style = MaterialTheme.typography.bodyMedium, 
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                if (organizedCamps.isNotEmpty()) {
                    Text(text = "Organized Campaigns", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    organizedCamps.forEach { camp ->
                        val regCount = state.campRegistrations.count { it.campId == camp.id }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                            border = borderStroke()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = camp.title, fontWeight = FontWeight.Bold)
                                    Text(text = "${camp.dateText} • ${camp.address}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(
                                        text = if (camp.status == "Cancelled") "Cancelled" else "$regCount Donors Registered", 
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (camp.status == "Cancelled") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }

                                Button(
                                    onClick = { onManageCamp(camp.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Manage", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }

                if (registeredCamps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Registered Appointments", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)
                    registeredCamps.forEach { camp ->
                        val registration = state.campRegistrations.find { it.campId == camp.id && it.donorId == state.profile.id }
                        val regStatus = registration?.status ?: "Registered"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                            border = borderStroke()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = camp.title, fontWeight = FontWeight.Bold)
                                    Text(text = "${camp.dateText} • Venue: ${camp.address}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(
                                        text = "Status: $regStatus", 
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when(regStatus) {
                                            "Donation Completed" -> Color(0xFF4CAF50)
                                            "Donation Rejected" -> MaterialTheme.colorScheme.error
                                            "Checked In" -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                }

                                Button(
                                    onClick = { onViewCamp(camp.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("View Pass", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateSettings() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Column {
                            Text(text = "App Settings", fontWeight = FontWeight.Bold)
                            Text(text = "Privacy, language, and notification options", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(text = "Log Out", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showPhotoDialog) {
        Dialog(onDismissRequest = { showPhotoDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Manage Profile Picture",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { showPhotoDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Dialog")
                        }
                    }

                    // Large Circular Preview
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clickable {
                                showFullscreenPhoto = true
                                showPhotoDialog = false
                            }
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            if (state.profile.profilePictureUri != null) {
                                AsyncImage(
                                    model = state.profile.profilePictureUri,
                                    contentDescription = "Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.secondary
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.profile.name.split(" ")
                                            .mapNotNull { it.firstOrNull()?.toString() }
                                            .take(2)
                                            .joinToString(""),
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "View Fullscreen", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Text(
                        text = "Click photo to view full size",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    HorizontalDivider()

                    // Selection Methods
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                showPhotoDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Upload", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = {
                                val mockSelfies = listOf(
                                    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=300",
                                    "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=300",
                                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300",
                                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=300"
                                )
                                val chosen = mockSelfies.random()
                                onSave(state.profile.copy(profilePictureUri = chosen))
                                showPhotoDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Selfie", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Selfie Sim", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (state.profile.profilePictureUri != null) {
                        OutlinedButton(
                            onClick = {
                                onSave(state.profile.copy(profilePictureUri = null))
                                showPhotoDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remove Picture", fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "Or choose a donor avatar preset",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        avatarPresets.forEachIndexed { idx, url ->
                            val isSelected = state.profile.profilePictureUri == url
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        onSave(state.profile.copy(profilePictureUri = url))
                                        showPhotoDialog = false
                                    }
                                    .padding(4.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    modifier = Modifier.size(60.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                                    )
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Preset ${presetNames[idx]}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = presetNames[idx],
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFullscreenPhoto) {
        Dialog(onDismissRequest = { showFullscreenPhoto = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Photo Viewer",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { 
                            showFullscreenPhoto = false 
                            showPhotoDialog = true 
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Fullscreen", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        color = Color.DarkGray
                    ) {
                        if (state.profile.profilePictureUri != null) {
                            AsyncImage(
                                model = state.profile.profilePictureUri,
                                contentDescription = "Full Size Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.profile.name,
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                showFullscreenPhoto = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Change")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change")
                        }

                        if (state.profile.profilePictureUri != null) {
                            Button(
                                onClick = {
                                    onSave(state.profile.copy(profilePictureUri = null))
                                    showFullscreenPhoto = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = borderStroke()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Column {
                    Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun AchievementsProgressScreen(
    state: BloodLinkUiState,
    onNavigateCamps: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("History Timeline", "Levels & Badges", "Countdown", "Hero Wall")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Impact Dashboard", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Box(modifier = Modifier.size(48.dp))
        }

        // Horizontal Navigation Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTab) {
                1 -> { // Levels & Badges (Gamification)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Current level progress banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                            border = borderStroke()
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Silver Hero", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                    Text(text = "${state.profile.xp} / 1000 XP", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                LinearProgressIndicator(
                                    progress = { state.profile.xp.toFloat() / 1000f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceContainer
                                )
                                Text(
                                    text = "Excellent work! Elevate to Gold Hero by accumulating 250 more XP.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Grid containing Badge collection
                        Text(text = "Your Badges Collection", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                BadgeItem(title = "First Drop", desc = "Signed up & Donated", icon = Icons.Default.WaterDrop, active = true, modifier = Modifier.weight(1f))
                                BadgeItem(title = "Rapid Responder", desc = "Replied within 1 hour", icon = Icons.Default.Bolt, active = true, modifier = Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                BadgeItem(title = "Camp Champion", desc = "Attended camp drive", icon = Icons.Default.Campaign, active = true, modifier = Modifier.weight(1f))
                                BadgeItem(title = "Emergency Hero", desc = "Critical response (Locked)", icon = Icons.Default.Bolt, active = false, modifier = Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                BadgeItem(title = "Blood Guardian", desc = "10 Lives Saved (Locked)", icon = Icons.Default.Favorite, active = false, modifier = Modifier.weight(1f))
                                BadgeItem(title = "Legend Donor", desc = "50+ Donations (Locked)", icon = Icons.Default.MilitaryTech, active = false, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                2 -> { // Eligible Countdown (Countdown screen)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Large beautiful countdown dial
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = Color.LightGray.copy(alpha = 0.2f), style = Stroke(width = 12.dp.toPx()))
                                drawArc(
                                    color = Color(0xFFE53935),
                                    startAngle = -90f,
                                    sweepAngle = (state.nextDonationCountdownDays.toFloat() / 90f) * 360f,
                                    useCenter = false,
                                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = state.nextDonationCountdownDays.toString(),
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = "Days Remaining",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "Next Donation Cooldown Timer",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        
                        Text(
                            text = "Medical regulations advise a 90-day rest period between consecutive whole blood donations to protect donor health. You are eligible again on September 18, 2026.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Action item to find camps
                        Button(
                            onClick = onNavigateCamps,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Camps")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Find Camps & Pre-register Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                0 -> { // Donation History Timeline
                    var yearFilter by remember { mutableStateOf("All Years") }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Filters row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All Years", "2026", "2025").forEach { yr ->
                                FilterChip(
                                    selected = yearFilter == yr,
                                    onClick = { yearFilter = yr },
                                    label = { Text(yr) }
                                )
                            }
                        }

                        // Complete chronological list of logged donation events (Real-time history timeline)
                        val history = state.donationHistory

                        androidx.compose.foundation.lazy.LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(history.size) { i ->
                                val log = history[i]
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                                    border = borderStroke(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(imageVector = Icons.Default.Bloodtype, contentDescription = "Blood type", tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = log.hospitalName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                            Text(text = log.donationDate, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = "Donation ID: " + log.donationId, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(text = log.bloodGroup, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> { // Hero Wall (Community Impact Feed)
                    val cheerCounts = remember { mutableStateListOf(42, 28, 19, 11) }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Live Hero Feed (Anonymous)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val feed = listOf(
                            FeedItem("O+ Donation Completed", "St. Jude Medical Center, Bangalore", "Fulfills active hospital emergency request", "2 mins ago"),
                            FeedItem("B- Unit Delivered", "Red Cross East Plaza, Delhi", "Replenished trauma operations storage unit", "1 hour ago"),
                            FeedItem("O- Blood Requirement Fulfilled", "City General, Mumbai", "AI verified match - Saved critical patient", "5 hours ago"),
                            FeedItem("A+ Volunteer Camp Registration", "Tech Park Drive, Chennai", "Community camp pledge success", "Yesterday")
                        )

                        androidx.compose.foundation.lazy.LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(feed.size) { i ->
                                val item = feed[i]
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                                    border = borderStroke()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = item.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                                            Text(text = item.time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        }

                                        Text(text = item.location, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                                        Text(text = item.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "❤️ Anonymous Hero", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Gray)
                                            
                                            // Cheer Button action
                                            IconButton(
                                                onClick = { cheerCounts[i] = cheerCounts[i] + 1 }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Cheer", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                    Text(text = cheerCounts[i].toString(), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
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
        }
    }
}

@Composable
fun SettingsScreen(
    state: BloodLinkUiState,
    onToggleMute: () -> Unit,
    onToggleLocationSharing: () -> Unit,
    onChangeRole: () -> Unit,
    isSeedingAllowed: Boolean = false,
    onSeedData: () -> Unit = {},
    onLogout: () -> Unit = {},
    onBack: () -> Unit
) {
    var pushEnabled by remember { mutableStateOf(true) }
    var privacyModePublic by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "Preferences", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

            // Notifications Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Push Notifications", fontWeight = FontWeight.Bold)
                            Text(text = "Get real-time nearby alerts", style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(checked = pushEnabled, onCheckedChange = { pushEnabled = it }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Mute Sound Alerts", fontWeight = FontWeight.Bold)
                            Text(text = "Silence notification ringtone", style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(checked = state.isMuted, onCheckedChange = { onToggleMute() }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
                    }
                }
            }

            // Privacy & Location
            Text(text = "Privacy & Location", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Location Sharing", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Allows patients to see distance", style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(checked = state.locationSharingEnabled, onCheckedChange = { onToggleLocationSharing() }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Anonymous Community Post", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Keep my name hidden on Hero Wall", style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(checked = !privacyModePublic, onCheckedChange = { privacyModePublic = !it }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
                    }
                }
            }

            // Account & Role settings
            Text(text = "Account", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Card(
                modifier = Modifier.fillMaxWidth().testTag("change_role_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChangeRole() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Change Role", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "Current role: ${
                                    when (state.currentRole) {
                                        UserRole.Donor -> "Donor"
                                        UserRole.Requester -> "Requester"
                                        UserRole.CampOrganizer -> "Camp Organiser"
                                        else -> "Not Selected"
                                    }
                                }",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Change Role")
                    }
                }
            }

            // Regional & Language settings
            Text(text = "System", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "App Language", fontWeight = FontWeight.Bold)
                            Text(text = "Select primary application dialect", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("English", "Hindi").forEach { lang ->
                                val selected = selectedLanguage == lang
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedLanguage = lang },
                                    label = { Text(text = lang) }
                                )
                            }
                        }
                    }
                }
            }

            if (isSeedingAllowed) {
                Text(text = "Developer Options", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("developer_options_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = borderStroke()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Sandbox Environment Seeding", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "This option is only available on debug builds running local placeholder Firebase databases. Seed standard development data into the fallback database.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = onSeedData,
                            modifier = Modifier.fillMaxWidth().testTag("seed_debug_data_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Build, contentDescription = "Seed")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Seed Debug Data")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("settings_logout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }

            // Quick App Metadata / Version
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "BloodLink AI v1.2.0-PROD", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(text = "Secured with end-to-end healthcare compliance", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun BadgeItem(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (active) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceContainer),
        border = borderStroke()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Text(text = desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

data class HistoryLog(val date: String, val hospital: String, val group: String, val id: String)
data class FeedItem(val title: String, val location: String, val detail: String, val time: String)

private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
