package com.qingguang.qingnote.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class SwipeResult {
    ACCEPTED, REJECTED
}

@Composable
fun DraggableCard(
    item: Any,
    modifier: Modifier = Modifier,
    onSwiped: (Any, Any) -> Unit,
    content: @Composable () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    // 增加飞出距离，确保彻底划走
    val swipeXRight = (screenWidth.value * 1.5).toFloat() 
    val swipeX = remember { Animatable(0f) }
    
    // 只要卡片还没飞出可见范围，就渲染它
    if (abs(swipeX.value) < swipeXRight) {
        val rotationFraction = (swipeX.value / 60).coerceIn(-40f, 40f)
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = modifier
                .dragContent(
                    swipeX = swipeX,
                    maxX = swipeXRight
                )
                .graphicsLayer(
                    translationX = swipeX.value,
                    rotationZ = rotationFraction,
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            content()
        }
    } else {
        // 彻底划走后通知上层
        val swipeResult = if (swipeX.value > 0) SwipeResult.ACCEPTED else SwipeResult.REJECTED
        onSwiped(swipeResult, item)
    }
}

fun Modifier.dragContent(
    swipeX: Animatable<Float, AnimationVector1D>,
    maxX: Float
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragCancel = {
                coroutineScope.launch { swipeX.animateTo(0f) }
            },
            onDragEnd = {
                coroutineScope.apply {
                    // 如果拖拽距离不足 1/4，回弹；否则飞出
                    if (abs(swipeX.value) < abs(maxX) / 4) {
                        launch { swipeX.animateTo(0f, tween(300)) }
                    } else {
                        launch {
                            val target = if (swipeX.value > 0) maxX * 1.5f else -maxX * 1.5f
                            swipeX.animateTo(target, tween(300))
                        }
                    }
                }
            }
        ) { change, dragAmount ->
            change.consume()
            coroutineScope.launch {
                // 使用 snapTo 解决阻尼感，让卡片紧随手指
                swipeX.snapTo(swipeX.value + dragAmount)
            }
        }
    }
}
