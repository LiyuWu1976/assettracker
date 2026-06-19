package com.fh.msd.assettracker.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fh.msd.assettracker.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, nextRoute: String) {
    // 1. 底色#17777E
    // 2. 图标logo.png一开始在中间处于隐身状态，在2s中慢慢显现清晰出来
    // 3. 图标logo.png 宽占据的大小为屏幕的1/3，不要拉伸此图标，保持宽高比相同
    
    val alpha = remember { Animatable(0f) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val logoWidth = screenWidth / 3

    LaunchedEffect(key1 = true) {
        // 2s内慢慢显现
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000)
        )
        // 动画结束后停留一小会儿再跳转
        delay(500)
        navController.navigate(nextRoute) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF17777E)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .width(logoWidth)
                .alpha(alpha.value),
            contentScale = ContentScale.Fit // 保持宽高比，不拉伸
        )
    }
}
