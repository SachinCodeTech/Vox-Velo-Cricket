package com.example.voxvelo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voxvelo.model.CoachInsights
import com.example.voxvelo.model.Confidence
import com.example.voxvelo.model.Delivery
import com.example.voxvelo.model.FormatUtils
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvAmber
import com.example.voxvelo.ui.theme.VvBgRaised
import com.example.voxvelo.ui.theme.VvLine
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvTeal
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim
import com.example.voxvelo.ui.theme.VvTextFaint
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = VvTextDim,
        modifier = modifier.padding(top = 18.dp, bottom = 8.dp)
    )
}

@Composable
fun StatStrip(
    rows: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
    ) {
        rows.forEachIndexed { index, (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = key,
                    style = MaterialTheme.typography.bodyMedium,
                    color = VvTextDim
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = VvText,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (index < rows.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(VvLineStrong)
                )
            }
        }
    }
}

@Composable
fun CardListItem(
    title: String,
    sub: String,
    value: String,
    valueLabel: String? = null,
    badgeText: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "card_list_item"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = VvText,
                    fontWeight = FontWeight.SemiBold
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VvTeal.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = VvTeal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = sub,
                style = MaterialTheme.typography.bodyMedium,
                color = VvTextDim
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VvAccent
            )
            if (valueLabel != null) {
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = VvTextDim
                )
            }
        }
    }
}

@Composable
fun ConfidChip(
    level: Confidence,
    modifier: Modifier = Modifier
) {
    val (dotColor, textColor, bgColor) = when (level) {
        Confidence.HIGH -> Triple(VvTeal, VvTeal, VvTeal.copy(alpha = 0.15f))
        Confidence.MEDIUM -> Triple(VvAmber, VvAmber, VvAmber.copy(alpha = 0.15f))
        Confidence.LOW -> Triple(VvAccent, VvAccent, VvAccent.copy(alpha = 0.15f))
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, dotColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "CONFIDENCE: ${level.label.uppercase()}",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun SpeedBoard(
    kmh: Double,
    mph: Double,
    time: Double,
    distance: Double,
    fps: Double,
    confidence: Confidence,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(16.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConfidChip(level = confidence)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = FormatUtils.fmt(kmh, 1),
            style = MaterialTheme.typography.displayLarge,
            color = VvText,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "km/h  ·  ${FormatUtils.fmt(mph, 1)} mph",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            color = VvTextDim
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "ESTIMATED BALL SPEED",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = VvAccent,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(VvSurface2)
                .padding(vertical = 10.dp, horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "FLIGHT TIME", style = MaterialTheme.typography.labelSmall, color = VvTextDim)
                Text(
                    text = "${FormatUtils.fmt(time, 3)}s",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = VvText,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "DISTANCE", style = MaterialTheme.typography.labelSmall, color = VvTextDim)
                Text(
                    text = "${FormatUtils.fmt(distance, 2)}m",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = VvText,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "FPS", style = MaterialTheme.typography.labelSmall, color = VvTextDim)
                Text(
                    text = "${fps.toInt()}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = VvText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PitchOverlay(
    bounceDist: Double?,
    totalDist: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface2)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "PITCH OVERLAY (RELEASE / BOUNCE / ARRIVAL TO SCALE)",
            style = MaterialTheme.typography.labelSmall,
            color = VvTextDim
        )
        Spacer(modifier = Modifier.height(10.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val pad = 36f
            val groundY = size.height - 20f
            val releaseX = pad
            val arrivalX = size.width - pad

            val frac = if (bounceDist != null && totalDist > 0) {
                max(0.0, min(1.0, bounceDist / totalDist)).toFloat()
            } else null
            val bounceX = if (frac != null) releaseX + frac * (arrivalX - releaseX) else null

            // Pitch turf ground line
            drawLine(
                color = VvLineStrong,
                start = Offset(releaseX, groundY),
                end = Offset(arrivalX, groundY),
                strokeWidth = 3f
            )

            // Release marker
            drawCircle(color = VvTeal, radius = 9f, center = Offset(releaseX, groundY))

            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            if (bounceX != null) {
                // Arc 1: Release to Bounce
                val path1 = Path().apply {
                    moveTo(releaseX, groundY)
                    quadraticTo(
                        (releaseX + bounceX) / 2f,
                        groundY - 50f,
                        bounceX,
                        groundY
                    )
                }
                drawPath(
                    path = path1,
                    color = VvAmber,
                    style = Stroke(width = 3f, pathEffect = dashEffect)
                )

                // Bounce marker
                drawCircle(color = VvAmber, radius = 9f, center = Offset(bounceX, groundY))

                // Arc 2: Bounce to Arrival
                val path2 = Path().apply {
                    moveTo(bounceX, groundY)
                    quadraticTo(
                        (bounceX + arrivalX) / 2f,
                        groundY - 32f,
                        arrivalX,
                        groundY
                    )
                }
                drawPath(
                    path = path2,
                    color = VvAccent,
                    style = Stroke(width = 3f, pathEffect = dashEffect)
                )
            } else {
                // Single arc from Release to Arrival
                val directPath = Path().apply {
                    moveTo(releaseX, groundY)
                    quadraticTo(
                        (releaseX + arrivalX) / 2f,
                        groundY - 45f,
                        arrivalX,
                        groundY
                    )
                }
                drawPath(
                    path = directPath,
                    color = VvAccent,
                    style = Stroke(width = 3f, pathEffect = dashEffect)
                )
            }

            // Arrival marker
            drawCircle(color = VvAccent, radius = 9f, center = Offset(arrivalX, groundY))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Release", color = VvTeal, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            if (bounceDist != null) {
                Text(text = "Bounce (${FormatUtils.fmt(bounceDist, 1)}m)", color = VvAmber, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Text(text = "Stumps", color = VvAccent, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun SpeedChart(
    deliveries: List<Delivery>,
    modifier: Modifier = Modifier
) {
    val speeds = deliveries.map { it.kmh }.filter { !it.isNaN() }
    if (speeds.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VvSurface)
                .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No deliveries in this session yet", color = VvTextDim, fontSize = 12.sp)
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(116.dp)) {
            val pad = 16f
            val w = size.width
            val h = size.height
            val minSpeed = speeds.minOrNull() ?: 0.0
            val maxSpeed = speeds.maxOrNull() ?: 0.0
            val range = if (maxSpeed > minSpeed) maxSpeed - minSpeed else 1.0

            val pts = speeds.mapIndexed { idx, s ->
                val x = if (speeds.size > 1) {
                    pad + (idx.toFloat() / (speeds.size - 1)) * (w - pad * 2)
                } else {
                    w / 2f
                }
                val y = (h - pad) - (((s - minSpeed) / range).toFloat() * (h - pad * 2))
                Offset(x, y)
            }

            if (pts.size > 1) {
                val path = Path().apply {
                    moveTo(pts[0].x, pts[0].y)
                    for (i in 1 until pts.size) {
                        lineTo(pts[i].x, pts[i].y)
                    }
                }
                drawPath(path = path, color = VvTeal, style = Stroke(width = 3.5f))
            }

            pts.forEach { pt ->
                drawCircle(color = VvAccent, radius = 5f, center = pt)
                drawCircle(color = VvBgRaised, radius = 2.5f, center = pt)
            }
        }
    }
}

@Composable
fun HistogramChart(
    deliveries: List<Delivery>,
    modifier: Modifier = Modifier
) {
    val speeds = deliveries.map { it.kmh }.filter { !it.isNaN() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        if (speeds.size < 2) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                Text(text = "Needs 2+ deliveries", color = VvTextDim, fontSize = 11.sp)
            }
        } else {
            val minSpeed = speeds.minOrNull() ?: 0.0
            val maxSpeed = speeds.maxOrNull() ?: 0.0
            val range = if (maxSpeed > minSpeed) maxSpeed - minSpeed else 1.0
            val bucketCount = 5
            val buckets = IntArray(bucketCount)
            speeds.forEach { v ->
                var idx = floor(((v - minSpeed) / range) * bucketCount).toInt()
                if (idx >= bucketCount) idx = bucketCount - 1
                if (idx < 0) idx = 0
                buckets[idx]++
            }
            val maxCount = buckets.maxOrNull() ?: 1

            Canvas(modifier = Modifier.fillMaxWidth().height(95.dp)) {
                val pad = 12f
                val w = size.width
                val h = size.height
                val bw = (w - pad * 2) / bucketCount

                drawLine(
                    color = VvLineStrong,
                    start = Offset(pad, h - pad),
                    end = Offset(w - pad, h - pad),
                    strokeWidth = 2f
                )

                buckets.forEachIndexed { i, count ->
                    val bh = if (maxCount > 0) (count.toFloat() / maxCount) * (h - pad * 2 - 10f) else 0f
                    val x = pad + i * bw
                    val y = h - pad - bh
                    drawRect(
                        color = VvTeal,
                        topLeft = Offset(x + 3f, y),
                        size = androidx.compose.ui.geometry.Size(max(4f, bw - 6f), bh)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressChart(
    deliveries: List<Delivery>,
    modifier: Modifier = Modifier
) {
    val speeds = deliveries.map { it.kmh }.filter { !it.isNaN() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        if (speeds.isEmpty()) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                Text(text = "No data yet", color = VvTextDim, fontSize = 11.sp)
            }
        } else {
            val cum = mutableListOf<Double>()
            var sum = 0.0
            speeds.forEachIndexed { idx, s ->
                sum += s
                cum.add(sum / (idx + 1))
            }
            val minCum = cum.minOrNull() ?: 0.0
            val maxCum = cum.maxOrNull() ?: 0.0
            val range = if (maxCum > minCum) maxCum - minCum else 1.0

            Canvas(modifier = Modifier.fillMaxWidth().height(95.dp)) {
                val pad = 12f
                val w = size.width
                val h = size.height

                val pts = cum.mapIndexed { idx, avg ->
                    val x = if (cum.size > 1) {
                        pad + (idx.toFloat() / (cum.size - 1)) * (w - pad * 2)
                    } else {
                        w / 2f
                    }
                    val y = (h - pad) - (((avg - minCum) / range).toFloat() * (h - pad * 2))
                    Offset(x, y)
                }

                if (pts.size > 1) {
                    val path = Path().apply {
                        moveTo(pts[0].x, pts[0].y)
                        for (i in 1 until pts.size) {
                            lineTo(pts[i].x, pts[i].y)
                        }
                    }
                    drawPath(path = path, color = VvAmber, style = Stroke(width = 3.5f))
                }
                pts.forEach { pt ->
                    drawCircle(color = VvAmber, radius = 4f, center = pt)
                }
            }
        }
    }
}

@Composable
fun TrendChart(
    values: List<Double>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        if (values.size < 2) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                Text(text = "Needs 2+ sessions with deliveries", color = VvTextDim, fontSize = 12.sp)
            }
        } else {
            Canvas(modifier = Modifier.fillMaxWidth().height(116.dp)) {
                val pad = 16f
                val w = size.width
                val h = size.height
                val minVal = values.minOrNull() ?: 0.0
                val maxVal = values.maxOrNull() ?: 0.0
                val range = if (maxVal > minVal) maxVal - minVal else 1.0

                val pts = values.mapIndexed { idx, v ->
                    val x = pad + (idx.toFloat() / (values.size - 1)) * (w - pad * 2)
                    val y = (h - pad) - (((v - minVal) / range).toFloat() * (h - pad * 2))
                    Offset(x, y)
                }

                val path = Path().apply {
                    moveTo(pts[0].x, pts[0].y)
                    for (i in 1 until pts.size) {
                        lineTo(pts[i].x, pts[i].y)
                    }
                }
                drawPath(path = path, color = VvAmber, style = Stroke(width = 3.5f))

                pts.forEach { pt ->
                    drawCircle(color = VvAmber, radius = 5f, center = pt)
                    drawCircle(color = VvBgRaised, radius = 2.5f, center = pt)
                }
            }
        }
    }
}

@Composable
fun CoachBox(
    insights: CoachInsights,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = insights.summary,
            style = MaterialTheme.typography.bodyLarge,
            color = VvText,
            fontWeight = FontWeight.Medium
        )
        if (insights.observations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            insights.observations.forEach { obs ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(VvTeal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = obs,
                        style = MaterialTheme.typography.bodyMedium,
                        color = VvTextDim
                    )
                }
            }
        }
        if (insights.drills.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "SUGGESTED DRILLS",
                style = MaterialTheme.typography.labelSmall,
                color = VvAmber
            )
            Spacer(modifier = Modifier.height(6.dp))
            insights.drills.forEach { drill ->
                Text(
                    text = "• $drill",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VvText,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VvSurface)
            .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = VvText,
            fontWeight = FontWeight.SemiBold
        )
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = VvTextDim
            )
        }
    }
}
