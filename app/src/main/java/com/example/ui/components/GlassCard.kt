package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.GoldPrimary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderColor: Color = Color.White.copy(alpha = 0.08f),
    highlightGold: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val effectiveBorder = if (highlightGold) GoldPrimary.copy(alpha = 0.4f) else borderColor
    val effectiveBackground = if (highlightGold) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF0F172A).copy(alpha = 0.95f),
                Color(0xFF1E293B).copy(alpha = 0.85f),
                Color(0xFF020617).copy(alpha = 0.9f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF0F172A).copy(alpha = 0.7f),
                Color(0xFF020617).copy(alpha = 0.8f)
            )
        )
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        effectiveBorder,
                        effectiveBorder.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .then(clickableModifier),
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .background(brush = effectiveBackground)
                .padding(18.dp),
            content = content
        )
    }
}

