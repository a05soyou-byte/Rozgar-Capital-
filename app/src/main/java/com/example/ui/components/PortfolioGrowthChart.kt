package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.roundToInt

data class ChartDataPoint(val timeLabel: String, val forexValue: Float, val commodityValue: Float, val realEstateValue: Float)

@Composable
fun PortfolioGrowthChart(
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("1M") }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    var showForexLine by remember { mutableStateOf(true) }
    var showCommoditiesLine by remember { mutableStateOf(true) }
    var showRealEstateLine by remember { mutableStateOf(true) }

    // Historical data points generated according to timeframe
    val dataPoints = remember(selectedTimeframe) {
        when (selectedTimeframe) {
            "1D" -> listOf(
                ChartDataPoint("00:00", 100f, 100f, 100f),
                ChartDataPoint("06:00", 101.2f, 100.8f, 100.2f),
                ChartDataPoint("12:00", 102.8f, 102.1f, 100.5f),
                ChartDataPoint("18:00", 103.5f, 101.9f, 100.9f),
                ChartDataPoint("24:00", 104.2f, 103.4f, 101.2f)
            )
            "1W" -> listOf(
                ChartDataPoint("Mon", 100f, 100f, 100f),
                ChartDataPoint("Tue", 102.5f, 101.8f, 101.0f),
                ChartDataPoint("Wed", 105.1f, 104.2f, 101.8f),
                ChartDataPoint("Thu", 108.4f, 106.0f, 102.5f),
                ChartDataPoint("Fri", 112.0f, 109.5f, 103.1f),
                ChartDataPoint("Sat", 115.3f, 111.2f, 104.0f),
                ChartDataPoint("Sun", 118.6f, 114.8f, 104.8f)
            )
            "1M" -> listOf(
                ChartDataPoint("Week 1", 100f, 100f, 100f),
                ChartDataPoint("Week 2", 108.5f, 105.4f, 102.1f),
                ChartDataPoint("Week 3", 118.2f, 112.9f, 105.3f),
                ChartDataPoint("Week 4", 129.8f, 122.0f, 109.0f),
                ChartDataPoint("Today", 142.5f, 131.6f, 113.5f)
            )
            "1Y" -> listOf(
                ChartDataPoint("Q1", 100f, 100f, 100f),
                ChartDataPoint("Q2", 132.0f, 120.5f, 112.0f),
                ChartDataPoint("Q3", 175.4f, 154.2f, 128.5f),
                ChartDataPoint("Q4", 230.8f, 195.0f, 148.0f)
            )
            else -> listOf(
                ChartDataPoint("2024", 100f, 100f, 100f),
                ChartDataPoint("2025", 185.0f, 160.0f, 130.0f),
                ChartDataPoint("2026", 310.0f, 245.0f, 175.0f)
            )
        }
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        highlightGold = true
    ) {
        // Chart Header with Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(GoldPrimary.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoGraph,
                        contentDescription = "Growth Trend",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "HISTORICAL GROWTH TRENDS",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Real-time performance of Forex, Gold & Real Estate assets",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Timeframe Pills Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("1D", "1W", "1M", "1Y", "ALL").forEach { tf ->
                val isSelected = selectedTimeframe == tf
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) GoldPrimary else DarkSurfaceVariant
                        )
                        .clickable {
                            selectedTimeframe = tf
                            selectedPointIndex = null
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tf,
                        color = if (isSelected) DarkBackground else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Legend Filters (Toggleable line visibility)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LegendPill(
                label = "Forex (+42.5%)",
                color = EmeraldSecondary,
                isActive = showForexLine,
                onClick = { showForexLine = !showForexLine }
            )
            LegendPill(
                label = "Commodities (+31.6%)",
                color = GoldPrimary,
                isActive = showCommoditiesLine,
                onClick = { showCommoditiesLine = !showCommoditiesLine }
            )
            LegendPill(
                label = "Real Estate (+13.5%)",
                color = Color(0xFFA855F7),
                isActive = showRealEstateLine,
                onClick = { showRealEstateLine = !showRealEstateLine }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Point Tooltip Bar (if user touches chart)
        val activeIndex = selectedPointIndex ?: (dataPoints.size - 1)
        val activePoint = dataPoints[activeIndex]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF020617).copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                .border(0.5.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "POINT: ${activePoint.timeLabel}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showForexLine) {
                        Text(
                            text = "FX: ${activePoint.forexValue}%",
                            color = EmeraldSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (showCommoditiesLine) {
                        Text(
                            text = "GOLD: ${activePoint.commodityValue}%",
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (showRealEstateLine) {
                        Text(
                            text = "RE: ${activePoint.realEstateValue}%",
                            color = Color(0xFFA855F7),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // THE CANVAS REAL-TIME CHART
        val minVal = 95f
        val maxVal = remember(dataPoints) {
            val fMax = dataPoints.maxOf { it.forexValue }
            val cMax = dataPoints.maxOf { it.commodityValue }
            val rMax = dataPoints.maxOf { it.realEstateValue }
            maxOf(fMax, cMax, rMax) + 10f
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF020617).copy(alpha = 0.6f))
                .border(0.5.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                .pointerInput(dataPoints) {
                    detectTapGestures { offset ->
                        val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                        val index = (offset.x / stepX).roundToInt().coerceIn(0, dataPoints.size - 1)
                        selectedPointIndex = index
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
                val width = size.width
                val height = size.height

                // Grid background lines (3 horizontal dashed lines)
                val gridYCount = 3
                for (i in 0..gridYCount) {
                    val y = height * (i.toFloat() / gridYCount)
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                fun getY(value: Float): Float {
                    val norm = (value - minVal) / (maxVal - minVal)
                    return height - (norm * height)
                }

                val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

                // Helper to draw smooth bezier line & gradient fill
                fun drawLineSeries(
                    getVal: (ChartDataPoint) -> Float,
                    lineColor: Color,
                    gradientStart: Color
                ) {
                    val path = Path()
                    val fillPath = Path()

                    val points = dataPoints.mapIndexed { idx, pt ->
                        Offset(idx * stepX, getY(getVal(pt)))
                    }

                    if (points.isNotEmpty()) {
                        path.moveTo(points[0].x, points[0].y)
                        fillPath.moveTo(points[0].x, height)
                        fillPath.lineTo(points[0].x, points[0].y)

                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2f, p1.y)
                            val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2f, p2.y)
                            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                            fillPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                        }

                        fillPath.lineTo(points.last().x, height)
                        fillPath.close()

                        // Draw Gradient Fill under curve
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(gradientStart.copy(alpha = 0.25f), Color.Transparent),
                                startY = 0f,
                                endY = height
                            )
                        )

                        // Draw Curve Stroke
                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Draw Data points
                        points.forEachIndexed { idx, pt ->
                            val isSelected = idx == activeIndex
                            drawCircle(
                                color = if (isSelected) Color.White else lineColor,
                                radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                                center = pt
                            )
                            if (isSelected) {
                                drawCircle(
                                    color = lineColor,
                                    radius = 10.dp.toPx(),
                                    center = pt,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }
                }

                if (showRealEstateLine) {
                    drawLineSeries({ it.realEstateValue }, Color(0xFFA855F7), Color(0xFFA855F7))
                }
                if (showCommoditiesLine) {
                    drawLineSeries({ it.commodityValue }, GoldPrimary, GoldLight)
                }
                if (showForexLine) {
                    drawLineSeries({ it.forexValue }, EmeraldSecondary, EmeraldSecondary)
                }

                // Vertical touch crosshair indicator
                val xPos = activeIndex * stepX
                drawLine(
                    color = GoldPrimary.copy(alpha = 0.4f),
                    start = Offset(xPos, 0f),
                    end = Offset(xPos, height),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom X-Axis Time Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataPoints.forEachIndexed { idx, pt ->
                Text(
                    text = pt.timeLabel,
                    color = if (idx == activeIndex) GoldPrimary else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = if (idx == activeIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun LegendPill(
    label: String,
    color: Color,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                if (isActive) color.copy(alpha = 0.15f) else DarkSurfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.5.dp,
                color = if (isActive) color.copy(alpha = 0.6f) else DarkSurfaceBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(if (isActive) color else TextSecondary.copy(alpha = 0.4f), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = if (isActive) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
