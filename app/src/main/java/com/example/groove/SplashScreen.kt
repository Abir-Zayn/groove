package com.example.groove

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.groove.ui.theme.GrooveDark
import com.example.groove.ui.theme.GrooveDarkSurface
import com.example.groove.ui.theme.GrooveDimGreen
import com.example.groove.ui.theme.GrooveSageGreen
import com.example.groove.ui.theme.GrooveTextSecondary
import com.example.groove.ui.theme.GrooveWhiteSmoke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        GrooveDark,
        GrooveDarkSurface,
        GrooveDark
    )
)

private val AccentPurple  = GrooveSageGreen
private val DimPurple     = GrooveDimGreen
private val TextPrimary   = GrooveWhiteSmoke
private val TextSecondary = GrooveTextSecondary

@Composable
fun SplashScreen(onGetStarted: () -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3(onGetStarted = onGetStarted)
            }
        }

        // Dot indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val isSelected = pagerState.currentPage == index
                val dotWidth by animateDpAsState(
                    targetValue = if (isSelected) 28.dp else 8.dp,
                    label = "dot_$index"
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(if (isSelected) AccentPurple else DimPurple)
                )
            }
        }

        // Skip button — visible on page 1 only
        AnimatedVisibility(
            visible = pagerState.currentPage == 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 44.dp)
        ) {
            TextButton(onClick = { /* TODO: handle skip */ }) {
                Text(
                    text = "Skip",
                    color = AccentPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage1() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Illustration placeholder
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(GrooveDimGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✨", fontSize = 72.sp)
        }

        Spacer(modifier = Modifier.height(52.dp))

        Text(
            text = "Unlock Your Potential",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Whether summarizing content, paraphrasing or generating a story — we have your backup. Use it for free.",
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.weight(1.5f))
    }
}

@Composable
private fun OnboardingPage2() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(GrooveDimGreen.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(text = "🚀", fontSize = 52.sp)
                Text(
                    text = "Placeholder",
                    color = AccentPurple,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage3(onGetStarted: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 48.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPurple
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0C0820)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
