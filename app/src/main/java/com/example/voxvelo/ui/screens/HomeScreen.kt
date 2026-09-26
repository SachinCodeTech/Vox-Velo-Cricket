package com.example.voxvelo.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voxvelo.R
import com.example.voxvelo.model.CricketStatsCalculator
import com.example.voxvelo.model.FormatUtils
import com.example.voxvelo.model.Screen
import com.example.voxvelo.ui.VoxVeloUiState
import com.example.voxvelo.ui.VoxVeloViewModel
import com.example.voxvelo.ui.components.CardListItem
import com.example.voxvelo.ui.components.EmptyState
import com.example.voxvelo.ui.components.SectionLabel
import com.example.voxvelo.ui.components.StatStrip
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvTeal
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim

@Composable
fun HomeScreen(
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    val allDeliveries = uiState.sessions.flatMap { s ->
        s.deliveries.map { d -> Pair(s.name, d) }
    }
    val latest = allDeliveries.maxByOrNull { it.second.ts }
    val speeds = allDeliveries.map { it.second.kmh }
    val fastest = speeds.maxOrNull()
    val avg = if (speeds.isNotEmpty()) speeds.sum() / speeds.size else null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Image Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, VvLineStrong, RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "VoxVelo Speed Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VvSurface.copy(alpha = 0.45f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "VOXVELO CRICKET",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VvTeal,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Measure. Analyze. Improve.",
                    style = MaterialTheme.typography.titleLarge,
                    color = VvText,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Last Delivery Hero Board
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VvSurface)
                .border(1.dp, VvLineStrong, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Text(
                text = "LAST RECORDED DELIVERY",
                style = MaterialTheme.typography.labelSmall,
                color = VvTextDim
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (latest != null) FormatUtils.fmt(latest.second.kmh, 1) else "—",
                    style = MaterialTheme.typography.displayLarge,
                    color = VvText,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "km/h",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    color = VvTextDim,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (latest != null) "${latest.first} · ${FormatUtils.timeAgo(latest.second.ts)}" else "No deliveries yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VvTextDim
                )
                Button(
                    onClick = { viewModel.goto(Screen.ANALYZE) },
                    colors = ButtonDefaults.buttonColors(containerColor = VvAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("analyze_delivery_button")
                ) {
                    Text(
                        text = "Analyze a delivery",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VvText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overall stats strip
        StatStrip(
            rows = listOf(
                Pair("Fastest recorded", "${FormatUtils.fmt(fastest, 1)} km/h"),
                Pair("Average speed", "${FormatUtils.fmt(avg, 1)} km/h"),
                Pair("Total deliveries", "${allDeliveries.size}"),
                Pair("Sessions logged", "${uiState.sessions.size}")
            )
        )

        SectionLabel(text = "Quick actions")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Speed Board Action
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VvSurface)
                    .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
                    .clickable { viewModel.setSpeedBoardModal(true) }
                    .padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = VvAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Speed Board",
                    style = MaterialTheme.typography.titleMedium,
                    color = VvText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Live delivery readout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VvTextDim
                )
            }

            // My Sessions Action
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VvSurface)
                    .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
                    .clickable { viewModel.goto(Screen.SESSIONS) }
                    .padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatListBulleted,
                    contentDescription = null,
                    tint = VvTeal,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "My Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = VvText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Review past bowling",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VvTextDim
                )
            }
        }

        SectionLabel(text = "Recent sessions")

        if (uiState.sessions.isEmpty()) {
            EmptyState(
                title = "No sessions yet",
                message = "Analyze your first delivery to start a session."
            )
        } else {
            uiState.sessions.take(3).forEach { session ->
                val stats = CricketStatsCalculator.sessionStats(session.deliveries)
                CardListItem(
                    title = session.name,
                    sub = "${session.deliveries.size} deliveries · ${FormatUtils.timeAgo(session.createdAt)}",
                    value = FormatUtils.fmt(stats.avg, 1),
                    valueLabel = "avg km/h",
                    onClick = { viewModel.goto(Screen.SESSION_DETAIL, session.id) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
