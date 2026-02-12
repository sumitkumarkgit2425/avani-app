package com.example.navya.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.navya.data.models.onboardingPages
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
        navController: NavHostController,
        viewModel: OnboardingViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
        val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
        val scope = rememberCoroutineScope()

        val navyaDiagonalGradient =
                Brush.linearGradient(
                        colors =
                                listOf(
                                        Color(0xFF66BB6A).copy(alpha = 0.5f),
                                        Color(0xFF2E7D32).copy(alpha = 0.8f)
                                ),
                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                )

        Box(modifier = Modifier.fillMaxSize().background(navyaDiagonalGradient)) {
                Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentPadding =
                                        PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                                pageSpacing = 16.dp
                        ) { pageIndex ->
                                val page = onboardingPages[pageIndex]

                                Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Card(
                                                modifier = Modifier.fillMaxWidth().weight(1f),
                                                shape = RoundedCornerShape(24.dp),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor = Color.White
                                                        ),
                                                elevation =
                                                        CardDefaults.cardElevation(
                                                                defaultElevation = 6.dp
                                                        )
                                        ) {
                                                Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.fillMaxSize()
                                                ) {
                                                        Image(
                                                                painter =
                                                                        painterResource(
                                                                                id = page.imageRes
                                                                        ),
                                                                contentDescription = null,
                                                                modifier =
                                                                        Modifier.fillMaxSize()
                                                                                .padding(24.dp),
                                                                contentScale = ContentScale.Crop
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(24.dp),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor = Color.White
                                                        ),
                                                elevation =
                                                        CardDefaults.cardElevation(
                                                                defaultElevation = 6.dp
                                                        )
                                        ) {
                                                Column(
                                                        modifier =
                                                                Modifier.padding(24.dp)
                                                                        .verticalScroll(
                                                                                rememberScrollState()
                                                                        ),
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally
                                                ) {
                                                        Text(
                                                                text = page.title,
                                                                fontSize = 24.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF2E7D32),
                                                                textAlign = TextAlign.Center
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                                text = page.description,
                                                                fontSize = 16.sp,
                                                                color = Color.Gray,
                                                                textAlign = TextAlign.Center,
                                                                lineHeight = 22.sp
                                                        )
                                                }
                                        }
                                }
                        }

                        Button(
                                onClick = {
                                        if (pagerState.currentPage < 2) {
                                                scope.launch {
                                                        pagerState.animateScrollToPage(
                                                                pagerState.currentPage + 1
                                                        )
                                                }
                                        } else {

                                                viewModel.completeOnboarding()

                                                navController.navigate("register_screen") {
                                                        popUpTo("onboarding") { inclusive = true }
                                                }
                                        }
                                },
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 32.dp, vertical = 16.dp)
                                                .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2E7D32)
                                        )
                        ) {
                                Text(
                                        text =
                                                if (pagerState.currentPage == 2) "Get Started"
                                                else "Next",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                }
        }
}
