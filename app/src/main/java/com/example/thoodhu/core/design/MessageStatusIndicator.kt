package com.example.thoodhu.core.design

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EagleGold
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusRead
import com.example.ui.theme.StatusSent

enum class MessageDeliveryState {
    PENDING,
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

/**
 * Original THOODHU visual indicators symbolizing the Eagle's delivery journey.
 * Never copies WhatsApp's tick icons.
 */
@Composable
fun MessageStatusIndicator(
    state: MessageDeliveryState,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    tint: Color? = null
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            MessageDeliveryState.PENDING -> {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Pending delivery",
                    tint = tint ?: StatusPending,
                    modifier = Modifier.size(size)
                )
            }
            MessageDeliveryState.SENDING -> {
                val infiniteTransition = rememberInfiniteTransition(label = "flight_anim")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )
                Canvas(modifier = Modifier.size(size)) {
                    val strokeW = 1.8.dp.toPx()
                    drawArc(
                        color = tint ?: Color(0xFF94A3B8),
                        startAngle = rotation,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }
            }
            MessageDeliveryState.SENT -> {
                // Original single feathered wing stroke (Eagle has taken flight)
                Canvas(modifier = Modifier.size(size)) {
                    val w = this.size.width
                    val h = this.size.height
                    val path = Path().apply {
                        moveTo(w * 0.2f, h * 0.55f)
                        cubicTo(w * 0.4f, h * 0.65f, w * 0.6f, h * 0.45f, w * 0.85f, h * 0.25f)
                        lineTo(w * 0.65f, h * 0.65f)
                    }
                    drawPath(
                        path = path,
                        color = tint ?: StatusSent,
                        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            MessageDeliveryState.DELIVERED -> {
                // Dual soaring wings in silver/sky tone (Message reached destination)
                Canvas(modifier = Modifier.size(size)) {
                    val w = this.size.width
                    val h = this.size.height
                    val color = tint ?: StatusDelivered
                    val stroke = 1.6.dp.toPx()

                    // Left wing
                    val path1 = Path().apply {
                        moveTo(w * 0.1f, h * 0.55f)
                        cubicTo(w * 0.28f, h * 0.65f, w * 0.45f, h * 0.45f, w * 0.65f, h * 0.28f)
                    }
                    // Right wing
                    val path2 = Path().apply {
                        moveTo(w * 0.35f, h * 0.55f)
                        cubicTo(w * 0.52f, h * 0.65f, w * 0.70f, h * 0.45f, w * 0.90f, h * 0.28f)
                    }
                    drawPath(path1, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
                    drawPath(path2, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
                }
            }
            MessageDeliveryState.READ -> {
                // Illuminated dual Azure eagle wings with luminous eye accent
                Canvas(modifier = Modifier.size(size)) {
                    val w = this.size.width
                    val h = this.size.height
                    val azureColor = tint ?: StatusRead
                    val stroke = 1.9.dp.toPx()

                    val path1 = Path().apply {
                        moveTo(w * 0.08f, h * 0.55f)
                        cubicTo(w * 0.28f, h * 0.68f, w * 0.46f, h * 0.42f, w * 0.66f, h * 0.25f)
                    }
                    val path2 = Path().apply {
                        moveTo(w * 0.32f, h * 0.55f)
                        cubicTo(w * 0.52f, h * 0.68f, w * 0.70f, h * 0.42f, w * 0.92f, h * 0.25f)
                    }
                    drawPath(path1, color = azureColor, style = Stroke(width = stroke, cap = StrokeCap.Round))
                    drawPath(path2, color = azureColor, style = Stroke(width = stroke, cap = StrokeCap.Round))

                    // Eagle eye beacon
                    drawCircle(
                        color = EagleGold,
                        radius = 1.4.dp.toPx(),
                        center = Offset(w * 0.92f, h * 0.25f)
                    )
                }
            }
            MessageDeliveryState.FAILED -> {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Failed to deliver",
                    tint = tint ?: StatusFailed,
                    modifier = Modifier.size(size)
                )
            }
        }
    }
}
