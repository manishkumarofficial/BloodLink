package com.bloodlink.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.bloodlink.app.ui.UserRole
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(2500, easing = LinearEasing)
        ) { value, _ -> progress = value }
        onNavigateNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Decorative glows
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopStart)
                .offset((-80).dp, (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(96.dp)
                    .animateContentSize()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "BloodLink AI",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Connecting Blood Donors and Patients in Real-Time, Saving Lives Faster.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 320.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))
            Surface(
                modifier = Modifier.size(260.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
                border = borderStroke()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC-glY8Hr-lOvAz-XqAYPSUCipxJU6dbC6WtEFg38VeWHrEuBhRk5Yw6VuSwnHviH8nszm88TwX0QvqSaAZn5r_yOQcZ74qJ7sJ0KpLa580CO4ZEYftSTcSbIC-xsE8tBIZNmtaFka4kn_leAgasXWnz1qrQo7ZM6vL3-w6Qu3g8USzwwJj6kVdASk_E3esuPl9pOwkIkYcF8Zl0UwPuMGyG9jHTdx1USfKQMpxVF-uXRWScihySNGeEIe0-VAwQ_gS4S8iS_mLsw4",
                        contentDescription = "Thematic droplet globe",
                        modifier = Modifier.size(180.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { progress },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier
                    .width(180.dp)
                    .height(6.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "INITIALIZING SYSTEM",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            )
        }
    }
}

private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageUrl: String
)

@Composable
fun OnboardingCarousel(onNavigateNext: () -> Unit) {
    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Find Blood Faster",
                description = "Instantly locate compatible blood donors in your area during critical emergencies.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDqwGa9tWGEXEBGJ1fSn_M6ku2TeYy-24X0tOKTGftWxILn_ZDjBA0PrjR8i9uaVSX2ZMdbc6Cbx5oAR_NuzU9ffuw1iqzoKbqSwkOjZqCdfqe5N_xpDSOM8ye9S-RxUFCSm-7T7r4tyAeytWdIBZrx-VW_1nqbMs8jIXwfm10N0kRSYUdTy2DzT0zZBNUSNeMdY53saazwx0GHrkIyCTy15Wqcz3tLLZcoYV4p-fWUZv5qtecUZPKnrgK17rJd85PdCHuOILlGQRI"
            ),
            OnboardingPage(
                title = "Help Save Lives",
                description = "Your single donation can make the difference between live and death. Be a hero.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB0GH2LYlDT0nmDXFq3xnrCbHvVNljT7wtesrai9cXP6uMbviY5pX2ZEcrLEAYXHIeqNgctEMD7m2R_K9IDJ17IQa1trojrhWnIs2SWHIUBB5VlPJX7O-PVt5i0tTGANdySGTKpva27Scr4KSMDHdQM1tVVTE-DaMkauYpI7AZfbs2iMgnKOky_Dlhv2lzZfcPjW3yhOXx1Fcn8EspK4cEafzCwCu8gTJ6z3fET8JDz41VeA3TJBFVo6qDYtqL1u_KA1V73V6QCHoQ"
            ),
            OnboardingPage(
                title = "Join the Community",
                description = "Connect with thousands of verified donors and hospitals in a secure, reliable network.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDvzqZJT51k1WD3_J6hqgZCLL_G7wk53prxABJI2nTFxFRd7hHXYfweOx0t1GlVvAWeYKA2W0Xe9jY_nmHQ6wl9WFSdqDvuM8UVn2pdzWQE8OuX6jSd2mCmZ0tLVqpxyL3R4U7sS3h-Qq4GnWurDfp45ly8L9ZQu4PVGUaJs9H905b3kNC3jAX6eUNIl_z3OaTZ_YDtJ8gQ7R-Vi2sTlw4hPWYWc3RiiU4nbiHVGa0nmLXEaEJVHzAOKeS-VzOYQ4HecGw25cgbAUA"
            )
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = onNavigateNext) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.forEachIndexed { index, _ ->
                        val width by animateDpAsState(
                            targetValue = if (index == selectedIndex) 32.dp else 8.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "width"
                        )
                        val color by animateColorAsState(
                            targetValue = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            label = "color"
                        )

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (selectedIndex < pages.lastIndex) {
                            selectedIndex++
                        } else {
                            onNavigateNext()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (selectedIndex == pages.lastIndex) "Get Started" else "Continue",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pages[selectedIndex].imageUrl,
                    contentDescription = pages[selectedIndex].title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = pages[selectedIndex].title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pages[selectedIndex].description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 300.dp)
            )
        }
    }
}

@Composable
fun LoginScreen(
    onSendOtp: (mobile: String) -> Unit,
    onCreateAccount: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    var mobileNo by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Firebase Status Indicator Badge
            val isFirebaseConnected = remember { com.bloodlink.app.FirebaseConfigService.isInitialized }
            Surface(
                color = if (isFirebaseConnected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isFirebaseConnected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (isFirebaseConnected) {
                                    androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                } else {
                                    androidx.compose.ui.graphics.Color(0xFFFF9800)
                                },
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (isFirebaseConnected) "Firebase SDK: Connected (Real DB)" else "Firebase SDK: Local Simulation",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isFirebaseConnected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(68.dp)
                    .border(1.2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = "Drop",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to BloodLink",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue your life-saving journey.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Mobile Number",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = mobileNo,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() } && it.length <= 10) {
                            mobileNo = it
                            validationError = null
                        }
                    },
                    placeholder = { Text("Enter your 10-digit number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = "Phone"
                        )
                    },
                    isError = validationError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                if (validationError != null) {
                    Text(
                        text = validationError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (mobileNo.length != 10) {
                            validationError = "Mobile number must contain exactly 10 digits."
                        } else {
                            validationError = null
                            onSendOtp(mobileNo)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Send OTP",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Arrow",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text = "Or continue with",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Login,
                        contentDescription = "Google",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Google Sign In",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.clickable { onCreateAccount() }
                )
            }
        }
    }
}

@Composable
fun LoginOtpVerificationScreen(
    mobileNumber: String,
    timerSeconds: Int,
    resendAllowed: Boolean,
    simulatedOtp: String,
    onVerify: (otp: String) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .size(68.dp)
                    .border(1.2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "OTP Verification",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "OTP sent to:\n+91 " + mobileNumber.chunked(5).joinToString(" "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (simulatedOtp == "FIREBASE_SENT") {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "A secure verification code has been sent via SMS by Firebase Authentication.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (simulatedOtp == "AUTO") {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Phone number automatically verified via Firebase instant validation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (simulatedOtp.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Simulated SMS Gateway OTP:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = simulatedOtp,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 4.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = otpValue,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() } && it.length <= 6) {
                            otpValue = it
                            otpError = null
                        }
                    },
                    placeholder = { Text("Enter 6-digit OTP") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Pin, contentDescription = "OTP Icon")
                    },
                    isError = otpError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                if (otpError != null) {
                    Text(
                        text = otpError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otpValue.length != 6) {
                            otpError = "Please enter 6 digits."
                        } else {
                            onVerify(otpValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Verify OTP",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (resendAllowed) {
                    TextButton(onClick = {
                        otpValue = ""
                        onResend()
                    }) {
                        Text("Resend OTP", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "Resend OTP in " + timerSeconds + "s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RegistrationScreen(
    onContinue: (name: String, mobile: String, email: String, dob: String, gender: String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var mobileError by remember { mutableStateOf(false) }
    var dobError by remember { mutableStateOf(false) }
    var genderError by remember { mutableStateOf(false) }

    val genders = listOf("Male", "Female", "Prefer not to say")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Enter your profile details to proceed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column {
                    Text("Full Name *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = false },
                        placeholder = { Text("E.g., Alex Rivera") },
                        isError = nameError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (nameError) {
                        Text("Name is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Column {
                    Text("Mobile Number *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() } && it.length <= 10) {
                                mobile = it
                                mobileError = false
                            }
                        },
                        placeholder = { Text("10-digit number") },
                        isError = mobileError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (mobileError) {
                        Text("10-digit mobile number is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Column {
                    Text("Email (Optional)", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("E.g., alex@example.com") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column {
                    Text("Date of Birth *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it; dobError = false },
                        placeholder = { Text("E.g., Oct 14, 1992") },
                        isError = dobError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (dobError) {
                        Text("Date of birth is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Column {
                    Text("Gender *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genders.forEach { option ->
                            val isSelected = gender == option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { gender = option; genderError = false },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    if (genderError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gender selection is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    nameError = name.isBlank()
                    mobileError = mobile.length != 10
                    dobError = dob.isBlank()
                    genderError = gender.isBlank()

                    if (!nameError && !mobileError && !dobError && !genderError) {
                        onContinue(name, mobile, email, dob, gender)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Continue", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun RegistrationOtpVerificationScreen(
    mobileNumber: String,
    timerSeconds: Int,
    resendAllowed: Boolean,
    simulatedOtp: String,
    onVerify: suspend (otp: String) -> Boolean,
    onResend: () -> Unit,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var otpValue by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .size(68.dp)
                    .border(1.2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verify Mobile",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Verification SMS sent to:\n+91 " + mobileNumber.chunked(5).joinToString(" "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (simulatedOtp == "FIREBASE_SENT") {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "A secure verification code has been sent via SMS by Firebase Authentication.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (simulatedOtp == "AUTO") {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Phone number automatically verified via Firebase instant validation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (simulatedOtp.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Simulated Register OTP:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = simulatedOtp,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 4.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = otpValue,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() } && it.length <= 6) {
                            otpValue = it
                            otpError = null
                        }
                    },
                    placeholder = { Text("Enter 6-digit OTP") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Pin, contentDescription = "OTP")
                    },
                    isError = otpError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                if (otpError != null) {
                    Text(
                        text = otpError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otpValue.length != 6) {
                            otpError = "Please enter 6 digits."
                        } else {
                            isLoading = true
                            scope.launch {
                                val success = onVerify(otpValue)
                                if (success) {
                                    isLoading = false
                                    onSuccess()
                                } else {
                                    isLoading = false
                                    otpError = "Invalid OTP. Please try again."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Verify & Proceed",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (resendAllowed) {
                    TextButton(onClick = {
                        otpValue = ""
                        onResend()
                    }) {
                        Text("Resend OTP", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "Resend OTP in " + timerSeconds + "s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    onBack: () -> Unit
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Choose Your Path",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select your primary role within the BloodLink AI ecosystem to customize your experience and tools.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleCard(
                    title = "Blood Donor",
                    description = "I want to help and save lives by donating blood regularly.",
                    icon = Icons.Default.VolunteerActivism,
                    isSelected = selectedRole == UserRole.Donor,
                    onClick = { selectedRole = UserRole.Donor }
                )

                RoleCard(
                    title = "Blood Requester",
                    description = "I need blood for a patient or a specific medical emergency.",
                    icon = Icons.Default.LocalHospital,
                    isSelected = selectedRole == UserRole.Requester,
                    onClick = { selectedRole = UserRole.Requester }
                )

                RoleCard(
                    title = "Camp Organizer",
                    description = "I want to organize a donation drive and mobilize the community.",
                    icon = Icons.Default.Campaign,
                    isSelected = selectedRole == UserRole.Organizer,
                    onClick = { selectedRole = UserRole.Organizer }
                )
            }

            Button(
                onClick = {
                    selectedRole?.let { onRoleSelected(it) }
                },
                enabled = selectedRole != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward"
                    )
                }
            }
        }
    }
}

@Composable
fun RoleSetupDonorScreen(
    onComplete: (details: Map<String, String>) -> Unit,
    onBack: () -> Unit
) {
    var bloodGroup by remember { mutableStateOf("O+") }
    var location by remember { mutableStateOf("") }
    var travelRadius by remember { mutableFloatStateOf(25f) }
    var emergencyContact by remember { mutableStateOf("") }

    var locationError by remember { mutableStateOf(false) }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Donor Profile Setup",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Configure your preferences to begin saving lives.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Select Blood Group", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(bloodGroups) { _, bg ->
                            val isSel = bloodGroup == bg
                            Surface(
                                onClick = { bloodGroup = bg },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = bg,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Column {
                    Text("Your Location *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it; locationError = false },
                        placeholder = { Text("E.g., Tech Park, Suite 200") },
                        isError = locationError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (locationError) {
                        Text("Location is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Column {
                    Text("Travel Radius: ${travelRadius.toInt()} km", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = travelRadius,
                        onValueChange = { travelRadius = it },
                        valueRange = 5f..100f,
                        steps = 19
                    )
                }

                Column {
                    Text("Emergency Contact (Optional)", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        placeholder = { Text("Mobile number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (location.isBlank()) {
                        locationError = true
                    } else {
                        onComplete(
                            mapOf(
                                "bloodGroup" to bloodGroup,
                                "location" to location,
                                "travelRadiusKm" to travelRadius.toInt().toString(),
                                "emergencyContact" to emergencyContact
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Complete Profile Setup", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun RoleSetupRequesterScreen(
    onComplete: (details: Map<String, String>) -> Unit,
    onBack: () -> Unit
) {
    var location by remember { mutableStateOf("") }
    var defaultHospital by remember { mutableStateOf("") }

    var locationError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Requester Setup",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Configure details so we can assist your blood request queries swiftly.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Your Base Location *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it; locationError = false },
                        placeholder = { Text("E.g., North Wing District") },
                        isError = locationError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (locationError) {
                        Text("Location is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Column {
                    Text("Default Hospital / Care Center (Optional)", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = defaultHospital,
                        onValueChange = { defaultHospital = it },
                        placeholder = { Text("E.g., St. Jude Medical Center") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (location.isBlank()) {
                        locationError = true
                    } else {
                        onComplete(
                            mapOf(
                                "location" to location,
                                "defaultHospital" to defaultHospital
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Complete Profile Setup", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun RoleSetupOrganizerScreen(
    onComplete: (details: Map<String, String>) -> Unit,
    onBack: () -> Unit
) {
    var organizationName by remember { mutableStateOf("") }
    var organizationType by remember { mutableStateOf("Non-Profit NGO") }
    var location by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }

    val types = listOf("Non-Profit NGO", "Corporate Sponsor", "Medical Center", "Community Group")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Organizer Setup",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Establish your organizational credentials to host mobile blood drives.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Organization Name *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = organizationName,
                        onValueChange = { organizationName = it; nameError = false },
                        placeholder = { Text("E.g., Lions Club Metro") },
                        isError = nameError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (nameError) {
                        Text("Organization name is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Column {
                    Text("Organization Type", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        types.take(2).forEach { option ->
                            val isSel = organizationType == option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { organizationType = option },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
                                border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text(option, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        types.drop(2).forEach { option ->
                            val isSel = organizationType == option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { organizationType = option },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
                                border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text(option, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                Column {
                    Text("Organization Location Headquarter *", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it; locationError = false },
                        placeholder = { Text("E.g., Suite 400, Professional Blvd") },
                        isError = locationError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (locationError) {
                        Text("Location headquarter is required", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Button(
                onClick = {
                    val hasNameError = organizationName.isBlank()
                    val hasLocationError = location.isBlank()
                    nameError = hasNameError
                    locationError = hasLocationError

                    if (!hasNameError && !hasLocationError) {
                        onComplete(
                            mapOf(
                                "organizationName" to organizationName,
                                "organizationType" to organizationType,
                                "location" to location
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Complete Profile Setup", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun AccountCreationSuccessScreen(
    onNavigateDashboard: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to BloodLink",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your account is successfully registered and secured. You are now part of our live-saving community network.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNavigateDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Go To Dashboard", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
        ),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = if (isSelected) 4.dp else 2.dp
    ) {
        Box(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
