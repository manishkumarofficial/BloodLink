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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bloodlink.app.ui.BloodLinkUiState
import com.bloodlink.app.ui.BloodRequest

@Composable
fun CreateRequestScreen(
    state: BloodLinkUiState,
    onNavigateNext: () -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var hospital by remember { mutableStateOf("") }
    var unitsNeeded by remember { mutableIntStateOf(2) }
    var contact by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Critical") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateNext) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFp7D1AdF-uWhR_XAjP9yFhNxBW1lf8XR2g_JOgpjwGPu1mYVAUDOfeO3FTMfzqUrzGSWgyME5_gQonTzR0KHAlCl_5dAEgMvTL0MCrJp8MS2JKJHkzo-p5jyqvArgBYWq21Th1_l5-Tl0tkLRtPwftpNZyghq4ZDDY_eGlql0j8A46-fQ91iuDt38httqOBdPaesqQCwyz6g7cuhqo5BbxtqoF7KI6cDi2w6Xfslt8PjfRtUylBaU6-kRRAaZHvtTvydaJioPIlE",
                    contentDescription = "Doctor avatar",
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                )
                Text(text = "BloodLink AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Text(text = "Create Blood Request", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
        Text(text = "Please provide accurate details to ensure a rapid response.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Name") },
                    placeholder = { Text("Full name of patient") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Blood Group Needed", style = MaterialTheme.typography.labelLarge)
                        Text(text = "Help me decide", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val groups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
                    FlowRow(groups) { selected -> bloodGroup = selected }
                }

                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    label = { Text("Hospital Name") },
                    placeholder = { Text("Search hospital...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Loc") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1.0f)) {
                        Text(text = "Units Needed", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (unitsNeeded > 1) unitsNeeded-- }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Minus")
                            }
                            Text(
                                text = unitsNeeded.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { unitsNeeded++ }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Plus")
                            }
                        }
                    }

                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Contact Number") },
                        placeholder = { Text("+1 (555) 000-0000") },
                        modifier = Modifier.weight(1.0f).height(74.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column {
                    Text(text = "Urgency Level", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("Normal", "Urgent", "Critical").forEach { level ->
                            val active = urgency == level
                            val containerColor = if (active) {
                                if (level == "Critical") MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            } else MaterialTheme.colorScheme.surfaceContainer
                            val contentColor = if (active) {
                                if (level == "Critical") MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                            } else MaterialTheme.colorScheme.onSurfaceVariant

                            Button(
                                onClick = { urgency = level },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor
                                ),
                                border = if (active) androidx.compose.foundation.BorderStroke(1.dp, contentColor) else null
                            ) {
                                Text(text = level, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onNavigateNext,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                        Text(text = "Request Blood Now", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Tips Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(text = "Emergency Guidelines", fontWeight = FontWeight.Bold)
                    Text(text = "For critical requests, our AI prioritizes donors within a 5-mile radius. Ensure your contact number is active and ready to receive calls from verifying hospitals.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun FlowRow(items: List<String>, onSelected: (String) -> Unit) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items.take(4).forEach { item ->
                var selected by remember { mutableStateOf(item == "O+") }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceContainerLow)
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .clickable {
                            selected = true
                            onSelected(item)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items.drop(4).forEach { item ->
                var selected by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceContainerLow)
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .clickable {
                            selected = true
                            onSelected(item)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RequestProgressDashboardScreen(
    state: BloodLinkUiState,
    onVerifyDonation: (donationId: String, donorId: String, donorName: String, bloodGroup: String, hospitalName: String, requestId: String, timestamp: String) -> String?,
    onConfirmDonation: (donationId: String, donorId: String, donorName: String, bloodGroup: String, hospitalName: String, requestId: String, gender: String) -> Unit,
    onNavigateSuccess: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    val activeRequest = state.activeRequests.find { it.id == "req_1" } ?: state.activeRequests.firstOrNull()

    var showScanner by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var scannerErrorMessage by remember { mutableStateOf<String?>(null) }
    
    var parsedDonationId by remember { mutableStateOf("") }
    var parsedDonorId by remember { mutableStateOf("") }
    var parsedDonorName by remember { mutableStateOf("") }
    var parsedBloodGroup by remember { mutableStateOf("") }
    var parsedHospitalName by remember { mutableStateOf("") }
    var parsedRequestId by remember { mutableStateOf("") }

    if (showScanner) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showScanner = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Scan Donor QR",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { showScanner = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close scanner")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "laser")
                        val floatAnimation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "laser"
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val laserY = size.height * floatAnimation
                            drawLine(
                                color = Color(0xFFE53935),
                                start = androidx.compose.ui.geometry.Offset(0f, laserY),
                                end = androidx.compose.ui.geometry.Offset(size.width, laserY),
                                strokeWidth = 3.dp.toPx()
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = "Target scan frame",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(80.dp)
                            )
                        }

                        Text(
                            text = "Align donor QR code inside viewport",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                        )
                    }

                    if (scannerErrorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = scannerErrorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "EMULATOR SCANNING SIMULATOR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    val error = onVerifyDonation(
                                        "BLD-902",
                                        "alex_rivera_id",
                                        "Alex Rivera",
                                        activeRequest?.bloodGroup ?: "O+",
                                        activeRequest?.hospitalName ?: "City Center Center",
                                        activeRequest?.id ?: "req_1",
                                        "2026-06-22T21:56:12Z"
                                    )
                                    if (error != null) {
                                        scannerErrorMessage = error
                                    } else {
                                        parsedDonationId = "BLD-902"
                                        parsedDonorId = "alex_rivera_id"
                                        parsedDonorName = "Alex Rivera"
                                        parsedBloodGroup = activeRequest?.bloodGroup ?: "O+"
                                        parsedHospitalName = activeRequest?.hospitalName ?: "City Center Center"
                                        parsedRequestId = activeRequest?.id ?: "req_1"
                                        
                                        showScanner = false
                                        showConfirmDialog = true
                                    }
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                            ) {
                                Text("Scan Valid QR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val error = onVerifyDonation(
                                        "BLD-902", // reuse same ID to trigger duplicate error
                                        "alex_rivera_id",
                                        "Alex Rivera",
                                        activeRequest?.bloodGroup ?: "O+",
                                        activeRequest?.hospitalName ?: "City Center Center",
                                        activeRequest?.id ?: "req_1",
                                        "2026-06-22T21:56:12Z"
                                    )
                                    if (error != null) {
                                        scannerErrorMessage = error
                                    } else {
                                        // If not a duplicate yet (meaning we didn't confirm the first scan), let's simulate a totally mismatched ID
                                        val parseErr = onVerifyDonation(
                                            "BLD-ERR",
                                            "stranger_id",
                                            "Michael Vance",
                                            "B-",
                                            "General trauma Ward",
                                            "mismatched_req_id",
                                            "2026-06-22T21:56:12Z"
                                        )
                                        scannerErrorMessage = parseErr ?: "Mismatched scan error."
                                    }
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                            ) {
                                Text("Scan Invalid/Dup", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "Verify", tint = MaterialTheme.colorScheme.primary)
                    Text("Donation Verified", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Blood donation has been successfully verified and recorded.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Donor Name: $parsedDonorName", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text("Blood Group: $parsedBloodGroup", style = MaterialTheme.typography.bodySmall)
                            Text("Hospital: $parsedHospitalName", style = MaterialTheme.typography.bodySmall)
                            Text("Request ID: $parsedRequestId", style = MaterialTheme.typography.bodySmall)
                            Text("Donation ID: $parsedDonationId", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onConfirmDonation(
                            parsedDonationId,
                            parsedDonorId,
                            parsedDonorName,
                            parsedBloodGroup,
                            parsedHospitalName,
                            parsedRequestId,
                            state.profile.gender
                        )
                        onNavigateSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.secondary)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    }

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
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back")
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDRq_nITFfXRWF1x8gMrxwJOayo88_h-GN_8kySoh3D1bPqSEdtuD4-83xRTvqie7rsQNZkc-KamJGdaRTSFDT_UlPWpCC2EhTgQTX8bKUFM3Y_UoQVMNzCYD3I2SxI6PNkfXePbLoba0PTEtcJFHNJMKoCCurywMK4-GUSssxxV9WlvwnMTRSoS5BJPOyksK-NtVGl5TyWUDX47JyBZzIqOAkwBFOrtn-kvw07SvLeRRd6bvWV-ey6fb2NNHxedS6KPBlKee6r594",
                    contentDescription = "Host profile",
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                )
                Text(text = "BloodLink AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    Text(text = "ACTIVE EMERGENCY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                }
                Spacer(modifier = Modifier.height(4.dp))
                val patientText = if (activeRequest?.id == "req_1") "Elena Rodriguez" else "Critical Patient"
                Text(text = "Request for $patientText", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
            }
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "Live Updating...", style = MaterialTheme.typography.labelSmall)
            }
        }

        val requestWithStats = activeRequest ?: BloodRequest("req_1", "City Center Hospital", "O+", "2.4 km away", "Critical", "Elena Rodriguez needs blood.", true, targetUnits = 3, currentUnits = 1)
        val progressFraction = (requestWithStats.currentUnits.toFloat() / requestWithStats.targetUnits.toFloat()).coerceIn(0f, 1f)
        val isCompleted = requestWithStats.isFulfilled || requestWithStats.currentUnits >= requestWithStats.targetUnits

        if (isCompleted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "Blood Requirement Successfully Fulfilled",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Amazing team effort! All target units received, and registered donors have been notified of successful case wrap-up.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Live stats card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = requestWithStats.hospitalName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Trauma Ward, 3rd Floor", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = requestWithStats.bloodGroup, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "UNITS REQUIRED", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = "${requestWithStats.currentUnits} / ${requestWithStats.targetUnits} Received", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Message Donors", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call Hospital")
                    }
                }
            }
        }

        // QR SCANNER TRIGGER ROW
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "QR Scanner", tint = MaterialTheme.colorScheme.primary)
                    Text(text = "Verify Donor Arrival", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Text(
                    text = "Hospital staff must scan the donor's QR attendance code once they complete their whole blood donation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { 
                        scannerErrorMessage = null
                        showScanner = true 
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan Donor QR", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Feed Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = borderStroke()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Live Feed", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                HorizontalDivider()

                FeedRow(title = "1 Donor (Sam R.) Accepted", desc = "Just now • Arriving in 15m", icon = Icons.Default.CheckCircle, color = MaterialTheme.colorScheme.primary)
                FeedRow(title = "4 Donors Viewed Request", desc = "2 mins ago", icon = Icons.Default.Visibility, color = MaterialTheme.colorScheme.secondary)
                FeedRow(title = "32 Donors Notified", desc = "5 mins ago • Within 5km radius", icon = Icons.Default.Campaign, color = MaterialTheme.colorScheme.secondary)
            }
        }

        TextButton(onClick = onCancel, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Cancel Request", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun FeedRow(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(36.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            }
        }
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(text = desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ActiveRequestsListScreen(
    state: BloodLinkUiState,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val onCooldown = state.profile.cooldownCountdownDays > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(text = "Emergency Requests", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            }

            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Chip filters list row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All Requests", "O- Negative", "Critical Only", "Under 5km").forEachIndexed { i, label ->
                val active = i == 0
                FilterChip(
                    selected = active,
                    onClick = {},
                    label = { Text(text = label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.activeRequests.size) { index ->
                val request = state.activeRequests[index]
                EmergencyRequestCard(
                    request = request,
                    onAccept = { onAccept(request.id) },
                    onReject = { onReject(request.id) },
                    disabled = onCooldown
                )
            }
        }
    }
}

@Composable
fun EmergencyRequestCard(
    request: BloodRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    disabled: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = borderStroke()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = if (request.isCritical) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = request.bloodGroup,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (request.isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Column {
                        Text(text = request.hospitalName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = request.distanceText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (request.isCritical) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "CRITICAL", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Patient Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = request.patientStatus, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                }
                Text(text = "\"${request.quote}\"", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onAccept,
                    enabled = !disabled,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (disabled) Color.LightGray else MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.6f),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Accept")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accept", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Reject")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reject")
                }
            }

            if (disabled) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Cooldown Active",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Acceptance disabled during active donation cooldown rest phase.",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationAlertCenterScreen(state: BloodLinkUiState, onBack: () -> Unit) {
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
            Text(text = "Notifications", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            TextButton(onClick = {}) {
                Text(text = "Mark Read", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Text(text = "Stay updated on emergencies, camps, and your impact.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Notification Cards List
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            NotificationTile(
                title = "Critical Emergency",
                desc = "URGENT: O- Negative needed at St. Jude Medical Center (1.2km away). Patient in surgery.",
                icon = Icons.Default.Warning,
                color = MaterialTheme.colorScheme.error,
                timeText = "2m ago"
            )

            NotificationTile(
                title = "Community Update",
                desc = "Central Park Mobile Drive was a success! 42 units collected yesterday.",
                icon = Icons.Default.Campaign,
                color = MaterialTheme.colorScheme.primary,
                timeText = "1h ago"
            )

            NotificationTile(
                title = "Achievement Unlocked",
                desc = "New Badge Unlocked! You've earned the \"Rapid Responder\" badge for answering emergency within 1 hour.",
                icon = Icons.Default.MilitaryTech,
                color = MaterialTheme.colorScheme.secondary,
                timeText = "Yesterday"
            )
        }
    }
}

@Composable
fun NotificationTile(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    timeText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = borderStroke()
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = color)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = color)
                    Text(text = timeText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))

@Composable
fun AcceptedRequestDetailsScreen(
    state: BloodLinkUiState,
    onNavigateQR: () -> Unit,
    onBack: () -> Unit
) {
    val req = state.activeRequests.find { it.id == state.selectedRequestId } ?: state.activeRequests.firstOrNull()
    
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
                    text = "Request Approved",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { paddingValues ->
        if (req != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (req.isCritical) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (req.isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = req.bloodGroup,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                        }
                        
                        Text(
                            text = if (req.isCritical) "CRITICAL EMERGENCY" else "URGENT TRANSFUSION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                            color = if (req.isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "Requirement: " + req.targetUnits + " Units Needed (" + req.currentUnits + " Received)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Patient details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = borderStroke()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = "person", tint = MaterialTheme.colorScheme.primary)
                                    Text("Patient Name", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text("Sanjay Kumar (Simulated)", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.LocalHospital, contentDescription = "hospital", tint = MaterialTheme.colorScheme.primary)
                                    Text("Hospital", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(req.hospitalName, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "loc", tint = MaterialTheme.colorScheme.primary)
                                    Text("Location", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text("Indiranagar District", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.PinDrop, contentDescription = "dist", tint = MaterialTheme.colorScheme.primary)
                                    Text("Distance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(req.distanceText, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call Patient")
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = "Maps")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Maps")
                    }
                }

                Button(
                    onClick = onNavigateQR,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CropFree, contentDescription = "QR Code", tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "GENERATE DONATION QR",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No request selected.")
            }
        }
    }
}

@Composable
fun QRVerificationScreen(
    state: BloodLinkUiState,
    onUpdateStatus: (String) -> Unit,
    onNavigateSuccess: () -> Unit,
    onBack: () -> Unit
) {
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
                    text = "Verify Donation",
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = "Show this QR at the reception desk to verify your attendance and log donation metrics.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.size(280.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCQbUUITwdRstlGmM_2mv5_ahG8JgdgGouYKsoQ5Nb28qTl30GOkptPiAaM36zxBDiZR1SpWdpYCcTrPs4YOQmTDcITUH9-fznHGv9lQ8MvOBxEM_ZMzw2jVOrdQbhLJVMq3zTn36RjXFpFCmCeWcpy-j8UUZ8fiM-z3PdRrTb66_Sa1Fv7O8W0lNaqtguRH1SEtrFxGzn5LZaHycvBWcdlNhxLkPH5abX5T3f2sYtSqzgZyu7Ii8T-dTEvRRSMcUK_lUhjPhHpJK8",
                            contentDescription = "Donation Code",
                            modifier = Modifier.size(170.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DONATION ID: BLD-902",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray)
                        )
                    }
                }
            }

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
                    Text(
                        text = "ENCODED QR TICKETING STRUCTURE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    
                    val payloadFields = listOf(
                        "Donor ID" to "alex_rivera_id",
                        "Request ID" to (state.selectedRequestId ?: "req_1"),
                        "Donation ID" to "BLD-902",
                        "Timestamp" to "2026-06-22T21:56:12Z",
                        "Blood Group" to state.profile.bloodGroup
                    )
                    
                    payloadFields.forEach { (name, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = borderStroke()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "VERIFICATION STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val steps = listOf("Pending", "Check-in Approved", "Completed")
                        val currentStepIndex = when (state.activeDonationStatus) {
                            "Pending" -> 0
                            "Reached Hospital" -> 1
                            "Completed" -> 2
                            else -> 0
                        }

                        steps.forEachIndexed { i, step ->
                            val done = i <= currentStepIndex
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (done) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(14.dp))
                                    } else {
                                        Text(text = (i + 1).toString(), style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (done) FontWeight.Bold else FontWeight.Normal),
                                    color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                            if (i < steps.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(0.5f).padding(bottom = 12.dp),
                                    color = if (done) MaterialTheme.colorScheme.primary else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.activeDonationStatus == "Pending") {
                    Button(
                        onClick = { onUpdateStatus("Reached Hospital") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("SIMULATE: REACH HOSPITAL", fontWeight = FontWeight.Bold)
                    }
                } else if (state.activeDonationStatus == "Reached Hospital") {
                    Button(
                        onClick = {
                            onUpdateStatus("Completed")
                            onNavigateSuccess()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("SIMULATE: RECORD COMPLETION", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onNavigateSuccess,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("PROCEED TO SUCCESS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DonationSuccessScreen(
    state: BloodLinkUiState,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "Success Life",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Text(
                    text = "Thank You, Hero! ❤️",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color.White),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "You have completed your blood donation. A life is being saved as a direct result of your generosity.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.9f)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "DONATION SUMMARY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Blood Group", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                        Text(text = state.profile.bloodGroup, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Date", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                        Text(text = "Today (June 21, 2026)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Points Earned", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                        Text(text = "+50 XP (Level Up Progress)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)))
                    }
                }
            }

            Text(
                text = "“Heroes don't always wear capes. Sometimes they donate blood.”",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.8f)),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "BACK TO HOME",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
