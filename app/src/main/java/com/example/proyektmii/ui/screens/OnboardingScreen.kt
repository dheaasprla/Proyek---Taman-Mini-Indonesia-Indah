package com.example.proyektmii.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyektmii.R
import com.example.proyektmii.ui.theme.ProyekTMIITheme
import androidx.compose.ui.tooling.preview.Preview // Impor untuk @Preview
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(onboardingComplete: () -> Unit) {
    var isAnimating by remember { mutableStateOf(false) }
    val alpha = animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
    )
    val scale = animateFloatAsState(
        targetValue = if (isAnimating) 1.5f else 0.5f,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        isAnimating = true
        delay(3000)
        onboardingComplete()
    }

    ProyekTMIITheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tmii_logo),
                contentDescription = "Logo TMII",
                modifier = Modifier
                    .size((100 * scale.value).dp)
                    .alpha(alpha.value) // Pastikan ini menggunakan Modifier.alpha
            )
        }
    }
}

@Preview(showBackground = true) // Anotasi untuk preview
@Composable
fun OnboardingScreenPreview() {
    ProyekTMIITheme {
        OnboardingScreen {}
    }
}