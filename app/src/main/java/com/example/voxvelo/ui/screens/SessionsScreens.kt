package com.example.voxvelo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voxvelo.model.CricketStatsCalculator
import com.example.voxvelo.model.FormatUtils
import com.example.voxvelo.model.Screen
import com.example.voxvelo.ui.VoxVeloUiState
import com.example.voxvelo.ui.VoxVeloViewModel
import com.example.voxvelo.ui.components.CardListItem
import com.example.voxvelo.ui.components.CoachBox
import com.example.voxvelo.ui.components.EmptyState
import com.example.voxvelo.ui.components.HistogramChart
import com.example.voxvelo.ui.components.ProgressChart
import com.example.voxvelo.ui.components.SectionLabel
import com.example.voxvelo.ui.components.SpeedChart
import com.example.voxvelo.ui.components.StatStrip
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvTeal
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionsScreen(
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Sessions",
            style = MaterialTheme.typography.headlineMedium,
            color = VvText
        )
        Text(
            text = "Every bowling session you've recorded, with speed and consistency at a glance.",
            style = MaterialTheme.typography.bodyMedium,
            color = VvTextDim,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Button(
            onClick = {
                if (uiState.players.isEmpty()) {
                    viewModel.showToast("Add a player first, then start a session.")
                    viewModel.goto(Screen.PLAYERS)
                } else {
                    viewModel.openSessionDialog("create")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = VvAccent),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("start_new_session_button")
        ) {
            Text(
                text = "Start new session",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VvText
            )
        }

        SectionLabel(text = "History")

        if (uiState.sessions.isEmpty()) {
            EmptyState(
                title = "No sessions yet",
                message = "Start a session and save a few deliveries to see them here."
            )
        } else {
            uiState.sessions.forEach { s ->
                val st = CricketStatsCalculator.sessionStats(s.deliveries)
                val player = uiState.players.find { it.id == s.playerId }
                CardListItem(
                    title = s.name,
                    sub = "${player?.name ?: "—"} · ${s.deliveries.size} deliveries · ${FormatUtils.timeAgo(s.createdAt)}",
                    value = FormatUtils.fmt(st.avg, 1),
                    valueLabel = "avg km/h",
                    onClick = { viewModel.goto(Screen.SESSION_DETAIL, s.id) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SessionDetailScreen(
    sessionId: String?,
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    val session = uiState.sessions.find { it.id == sessionId }
    val player = uiState.players.find { it.id == session?.playerId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back Button
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(VvSurface)
                .border(1.dp, VvLineStrong, RoundedCornerShape(8.dp))
                .clickable { viewModel.goBack() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = VvTeal,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Sessions", color = VvTeal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (session == null) {
            EmptyState(
                title = "Session not found",
                message = "It may have been cleared from this device."
            )
        } else {
            val st = CricketStatsCalculator.sessionStats(session.deliveries)
            val dateFormat = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.US)

            Text(
                text = session.name,
                style = MaterialTheme.typography.headlineMedium,
                color = VvText
            )
            Text(
                text = "${player?.name ?: "—"} · ${dateFormat.format(Date(session.createdAt))}",
                style = MaterialTheme.typography.bodyMedium,
                color = VvTextDim,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            StatStrip(
                rows = listOf(
                    Pair("Fastest", "${FormatUtils.fmt(st.fastest, 1)} km/h"),
                    Pair("Slowest", "${FormatUtils.fmt(st.slowest, 1)} km/h"),
                    Pair("Average", "${FormatUtils.fmt(st.avg, 1)} km/h"),
                    Pair("Median", "${FormatUtils.fmt(st.median, 1)} km/h"),
                    Pair("Consistency", if (st.consistency != null) "${FormatUtils.fmt(st.consistency, 0)}%" else "—"),
                    Pair("Valid / low-confidence", "${st.valid} / ${st.low}")
                )
            )

            SectionLabel(text = "Speed by delivery")
            SpeedChart(deliveries = session.deliveries)

            SectionLabel(text = "Distribution & session progress")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "SPEED DISTRIBUTION", style = MaterialTheme.typography.labelSmall, color = VvTextDim)
                    Spacer(modifier = Modifier.height(4.dp))
                    HistogramChart(deliveries = session.deliveries)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "PROGRESS (FATIGUE TREND)", style = MaterialTheme.typography.labelSmall, color = VvTextDim)
                    Spacer(modifier = Modifier.height(4.dp))
                    ProgressChart(deliveries = session.deliveries)
                }
            }

            SectionLabel(text = "Deliveries")
            if (session.deliveries.isEmpty()) {
                EmptyState(title = "No deliveries saved yet")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VvSurface)
                        .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
                ) {
                    val timeFormat = SimpleDateFormat("h:mm:ss a", Locale.US)
                    session.deliveries.forEachIndexed { i, d ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${i + 1}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VvTeal
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "${timeFormat.format(Date(d.ts))} · ${d.confidence.label}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VvTextDim
                                )
                            }
                            Text(
                                text = "${FormatUtils.fmt(d.kmh, 1)} km/h",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = VvAccent
                            )
                        }
                        if (i < session.deliveries.size - 1) {
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

            SectionLabel(text = "AI Cricket Coach")
            CoachBox(insights = CricketStatsCalculator.generateCoachInsights(session.deliveries))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
