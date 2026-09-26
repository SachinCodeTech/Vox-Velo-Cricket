package com.example.voxvelo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.voxvelo.ui.components.SectionLabel
import com.example.voxvelo.ui.components.StatStrip
import com.example.voxvelo.ui.components.TrendChart
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvAmber
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvTeal
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim

@Composable
fun PlayersScreen(
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
            text = "Players",
            style = MaterialTheme.typography.headlineMedium,
            color = VvText
        )
        Text(
            text = "Create a profile for each bowler you track.",
            style = MaterialTheme.typography.bodyMedium,
            color = VvTextDim,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Button(
            onClick = { viewModel.openPlayerDialog() },
            colors = ButtonDefaults.buttonColors(containerColor = VvAccent),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("add_player_button")
        ) {
            Text(
                text = "Add player",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VvText
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.goto(Screen.TEAM_DASHBOARD) },
                colors = ButtonDefaults.buttonColors(containerColor = VvSurface),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).border(1.dp, VvLineStrong, RoundedCornerShape(8.dp))
            ) {
                Text(text = "Team / academy view", color = VvText, fontSize = 12.sp)
            }
            Button(
                onClick = { viewModel.goto(Screen.COMPARE) },
                colors = ButtonDefaults.buttonColors(containerColor = VvSurface),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).border(1.dp, VvLineStrong, RoundedCornerShape(8.dp))
            ) {
                Text(text = "Compare", color = VvText, fontSize = 12.sp)
            }
        }

        SectionLabel(text = "All players")

        if (uiState.players.isEmpty()) {
            EmptyState(
                title = "No players yet",
                message = "Add a player to start tracking their sessions."
            )
        } else {
            uiState.players.forEach { p ->
                val speeds = uiState.sessions
                    .filter { it.playerId == p.id }
                    .flatMap { it.deliveries.map { d -> d.kmh } }
                val best = speeds.maxOrNull()
                val isActive = p.id == uiState.activePlayerId

                CardListItem(
                    title = p.name,
                    sub = "${p.style.label} · ${p.hand.label}${if (p.team.isNotEmpty()) " · ${p.team}" else ""}",
                    value = FormatUtils.fmt(best, 1),
                    valueLabel = "best km/h",
                    badgeText = if (isActive) "ACTIVE" else null,
                    onClick = { viewModel.goto(Screen.PLAYER_DETAIL, p.id) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PlayerDetailScreen(
    playerId: String?,
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    val player = uiState.players.find { it.id == playerId }

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
            Text(text = "Players", color = VvTeal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (player == null) {
            EmptyState(title = "Player not found")
        } else {
            val playerSessions = uiState.sessions.filter { it.playerId == player.id }.sortedBy { it.createdAt }
            val allDeliveries = playerSessions.flatMap { it.deliveries }
            val speeds = allDeliveries.map { it.kmh }
            val best = speeds.maxOrNull()
            val avg = if (speeds.isNotEmpty()) speeds.sum() / speeds.size else null
            val sessionAvgs = playerSessions.mapNotNull { CricketStatsCalculator.sessionStats(it.deliveries).avg }

            Text(
                text = player.name,
                style = MaterialTheme.typography.headlineMedium,
                color = VvText
            )
            Text(
                text = "${player.style.label} · ${player.hand.label}${if (player.team.isNotEmpty()) " · ${player.team}" else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = VvTextDim,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            StatStrip(
                rows = listOf(
                    Pair("Best delivery", "${FormatUtils.fmt(best, 1)} km/h"),
                    Pair("Overall average", "${FormatUtils.fmt(avg, 1)} km/h"),
                    Pair("Sessions", "${playerSessions.size}"),
                    Pair("Total deliveries", "${allDeliveries.size}")
                )
            )

            SectionLabel(text = "Performance trend — average speed by session")
            TrendChart(values = sessionAvgs)

            SectionLabel(text = "AI Cricket Coach — career summary")
            CoachBox(insights = CricketStatsCalculator.generateCoachInsights(allDeliveries))

            SectionLabel(text = "Sessions")
            if (playerSessions.isEmpty()) {
                EmptyState(title = "No sessions for this player yet.")
            } else {
                playerSessions.reversed().forEach { s ->
                    val st = CricketStatsCalculator.sessionStats(s.deliveries)
                    CardListItem(
                        title = s.name,
                        sub = "${s.deliveries.size} deliveries · ${FormatUtils.timeAgo(s.createdAt)}",
                        value = FormatUtils.fmt(st.avg, 1),
                        valueLabel = "avg km/h",
                        onClick = { viewModel.goto(Screen.SESSION_DETAIL, s.id) },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { viewModel.setActivePlayer(player.id) },
                colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("set_active_player_button")
            ) {
                Text(text = "Set as active player", color = VvTeal, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeamDashboardScreen(
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    val groups = uiState.players.groupBy {
        if (it.team.trim().isNotEmpty()) it.team.trim() else "Unassigned"
    }
    val keys = groups.keys.sortedWith { a, b ->
        if (a == "Unassigned") 1 else if (b == "Unassigned") -1 else a.compareTo(b)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
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
            Text(text = "Players", color = VvTeal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Team & academy view",
            style = MaterialTheme.typography.headlineMedium,
            color = VvText
        )
        Text(
            text = "Players grouped by the team/academy on their profile, with squad-level speed stats.",
            style = MaterialTheme.typography.bodyMedium,
            color = VvTextDim,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        if (keys.isEmpty()) {
            EmptyState(
                title = "No players yet",
                message = "Add players with a team/academy to see squad stats here."
            )
        } else {
            keys.forEach { teamName ->
                val roster = groups[teamName] ?: emptyList()
                val speeds = roster.flatMap { p ->
                    uiState.sessions.filter { it.playerId == p.id }.flatMap { it.deliveries.map { d -> d.kmh } }
                }
                val fastest = speeds.maxOrNull()
                val avg = if (speeds.isNotEmpty()) speeds.sum() / speeds.size else null

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VvSurface)
                        .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.titleMedium,
                        color = VvText,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${roster.size} player${if (roster.size == 1) "" else "s"} · fastest ${FormatUtils.fmt(fastest, 1)} km/h · avg ${FormatUtils.fmt(avg, 1)} km/h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VvTextDim
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roster.forEach { p ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(VvSurface2)
                                    .border(1.dp, VvLineStrong, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.goto(Screen.PLAYER_DETAIL, p.id) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = p.name,
                                    fontSize = 12.sp,
                                    color = VvTeal,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CompareScreen(
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    var compareMode by remember { mutableStateOf("players") } // "players" or "sessions"
    var selectedIdA by remember { mutableStateOf(uiState.players.getOrNull(0)?.id ?: "") }
    var selectedIdB by remember { mutableStateOf(uiState.players.getOrNull(1)?.id ?: uiState.players.getOrNull(0)?.id ?: "") }

    val isPlayers = compareMode == "players"
    val items = if (isPlayers) {
        uiState.players.map { Pair(it.id, it.name) }
    } else {
        uiState.sessions.map { Pair(it.id, it.name) }
    }

    val idA = if (items.any { it.first == selectedIdA }) selectedIdA else items.getOrNull(0)?.first ?: ""
    val idB = if (items.any { it.first == selectedIdB }) selectedIdB else items.getOrNull(1)?.first ?: items.getOrNull(0)?.first ?: ""

    val deliveriesA = if (isPlayers) {
        uiState.sessions.filter { it.playerId == idA }.flatMap { it.deliveries }
    } else {
        uiState.sessions.find { it.id == idA }?.deliveries ?: emptyList()
    }

    val deliveriesB = if (isPlayers) {
        uiState.sessions.filter { it.playerId == idB }.flatMap { it.deliveries }
    } else {
        uiState.sessions.find { it.id == idB }?.deliveries ?: emptyList()
    }

    val stA = CricketStatsCalculator.sessionStats(deliveriesA)
    val stB = CricketStatsCalculator.sessionStats(deliveriesB)

    val nameA = items.find { it.first == idA }?.second ?: "—"
    val nameB = items.find { it.first == idB }?.second ?: "—"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
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
            Text(text = "Players", color = VvTeal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Compare",
            style = MaterialTheme.typography.headlineMedium,
            color = VvText
        )
        Text(
            text = "Player vs player, or session vs session.",
            style = MaterialTheme.typography.bodyMedium,
            color = VvTextDim,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Mode Segmented Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(VvSurface)
                .border(1.dp, VvLineStrong, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (compareMode == "players") VvSurface2 else VvSurface)
                    .clickable {
                        compareMode = "players"
                        selectedIdA = uiState.players.getOrNull(0)?.id ?: ""
                        selectedIdB = uiState.players.getOrNull(1)?.id ?: selectedIdA
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Players",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (compareMode == "players") VvTeal else VvTextDim,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (compareMode == "sessions") VvSurface2 else VvSurface)
                    .clickable {
                        compareMode = "sessions"
                        selectedIdA = uiState.sessions.getOrNull(0)?.id ?: ""
                        selectedIdB = uiState.sessions.getOrNull(1)?.id ?: selectedIdA
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (compareMode == "sessions") VvTeal else VvTextDim,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selectors for A and B
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var dropAExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = nameA,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("A", color = VvTextDim, fontSize = 12.sp) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = VvTextDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VvTeal,
                        unfocusedBorderColor = VvLineStrong,
                        focusedTextColor = VvText,
                        unfocusedTextColor = VvText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().clickable { dropAExpanded = true }
                )
                DropdownMenu(
                    expanded = dropAExpanded,
                    onDismissRequest = { dropAExpanded = false },
                    modifier = Modifier.background(VvSurface2)
                ) {
                    items.forEach { itm ->
                        DropdownMenuItem(
                            text = { Text(text = itm.second, color = VvText) },
                            onClick = {
                                selectedIdA = itm.first
                                dropAExpanded = false
                            }
                        )
                    }
                }
            }

            var dropBExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = nameB,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("B", color = VvTextDim, fontSize = 12.sp) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = VvTextDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VvTeal,
                        unfocusedBorderColor = VvLineStrong,
                        focusedTextColor = VvText,
                        unfocusedTextColor = VvText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().clickable { dropBExpanded = true }
                )
                DropdownMenu(
                    expanded = dropBExpanded,
                    onDismissRequest = { dropBExpanded = false },
                    modifier = Modifier.background(VvSurface2)
                ) {
                    items.forEach { itm ->
                        DropdownMenuItem(
                            text = { Text(text = itm.second, color = VvText) },
                            onClick = {
                                selectedIdB = itm.first
                                dropBExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (items.size < 2) {
            EmptyState(title = "Add at least two $compareMode with saved deliveries to compare.")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VvSurface)
                    .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                // Table header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = nameA, style = MaterialTheme.typography.titleMedium, color = VvText, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(text = "METRIC", style = MaterialTheme.typography.labelSmall, color = VvTextDim)
                    Text(text = nameB, style = MaterialTheme.typography.titleMedium, color = VvText, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }

                val comparisonRows = listOf(
                    Triple("Fastest", Pair(stA.fastest, stB.fastest), "km/h"),
                    Triple("Average", Pair(stA.avg, stB.avg), "km/h"),
                    Triple("Consistency", Pair(stA.consistency, stB.consistency), "%"),
                    Triple("Deliveries", Pair(stA.valid.toDouble(), stB.valid.toDouble()), "")
                )

                comparisonRows.forEachIndexed { idx, (label, values, unit) ->
                    val (valA, valB) = values
                    val aWin = valA != null && valB != null && valA > valB
                    val bWin = valA != null && valB != null && valB > valA

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (valA != null) "${FormatUtils.fmt(valA, if (label == "Deliveries") 0 else 1)}${if (unit.isNotEmpty()) " $unit" else ""}" else "—",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = if (aWin) FontWeight.Bold else FontWeight.Normal,
                            color = if (aWin) VvTeal else VvTextDim,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = label,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = VvTextDim
                        )
                        Text(
                            text = if (valB != null) "${FormatUtils.fmt(valB, if (label == "Deliveries") 0 else 1)}${if (unit.isNotEmpty()) " $unit" else ""}" else "—",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = if (bWin) FontWeight.Bold else FontWeight.Normal,
                            color = if (bWin) VvTeal else VvTextDim,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                    if (idx < comparisonRows.size - 1) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(VvLineStrong))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
