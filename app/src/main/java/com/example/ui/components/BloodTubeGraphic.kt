package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodTube

@Composable
fun BloodTubeGraphic(
    tube: BloodTube,
    modifier: Modifier = Modifier.size(width = 54.dp, height = 130.dp)
) {
    val stopperColor = try {
        Color(android.graphics.Color.parseColor(tube.colorHex))
    } catch (e: Exception) {
        Color(0xFF8B5CF6)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Draw Translucent Glass Body with Round Bottom
            val glassPath = Path().apply {
                moveTo(w * 0.25f, h * 0.22f)
                lineTo(w * 0.75f, h * 0.22f)
                lineTo(w * 0.75f, h * 0.85f)
                cubicTo(w * 0.75f, h * 0.98f, w * 0.25f, h * 0.98f, w * 0.25f, h * 0.85f)
                close()
            }

            // Glass background fill
            drawPath(
                path = glassPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xAA334155),
                        Color(0xDD475569),
                        Color(0xAA334155)
                    )
                )
            )

            // Blood / Liquid Fill
            val liquidPath = Path().apply {
                moveTo(w * 0.27f, h * 0.45f)
                lineTo(w * 0.73f, h * 0.45f)
                lineTo(w * 0.73f, h * 0.84f)
                cubicTo(w * 0.73f, h * 0.96f, w * 0.27f, h * 0.96f, w * 0.27f, h * 0.84f)
                close()
            }
            drawPath(
                path = liquidPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF991B1B), Color(0xFF450A0A))
                )
            )

            // Gel separator if SST / Gold tube
            if (tube.id.contains("yellow") || tube.id.contains("gold") || tube.additiveEn.contains("Gel", true)) {
                drawRoundRect(
                    color = Color(0xFFFDE047),
                    topLeft = Offset(w * 0.27f, h * 0.80f),
                    size = Size(w * 0.46f, h * 0.08f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }

            // Glass Outline & Highlight
            drawPath(
                path = glassPath,
                color = Color.White.copy(alpha = 0.4f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
            )

            // Measurement Scale Markings
            for (i in 1..4) {
                val markY = h * (0.35f + i * 0.10f)
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(w * 0.30f, markY),
                    end = Offset(w * 0.45f, markY),
                    strokeWidth = 2f
                )
            }

            // 2. Draw Rubber Stopper Cap
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(stopperColor, stopperColor.copy(alpha = 0.75f))
                ),
                topLeft = Offset(w * 0.18f, 0f),
                size = Size(w * 0.64f, h * 0.22f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Cap ridges
            drawLine(
                color = Color.Black.copy(alpha = 0.3f),
                start = Offset(w * 0.18f, h * 0.10f),
                end = Offset(w * 0.82f, h * 0.10f),
                strokeWidth = 2f
            )

            // Cap Rim Ring
            drawRoundRect(
                color = stopperColor.copy(alpha = 0.9f),
                topLeft = Offset(w * 0.14f, h * 0.18f),
                size = Size(w * 0.72f, h * 0.05f),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}
