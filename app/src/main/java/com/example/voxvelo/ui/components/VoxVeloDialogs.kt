package com.example.voxvelo.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voxvelo.model.BowlStyle
import com.example.voxvelo.model.Confidence
import com.example.voxvelo.model.Handedness
import com.example.voxvelo.ui.VoxVeloUiState
import com.example.voxvelo.ui.VoxVeloViewModel
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvTeal
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim

@Composable
fun AddPlayerDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, hand: Handedness, style: BowlStyle, team: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var hand by remember { mutableStateOf(Handedness.RIGHT_HANDED) }
    var style by remember { mutableStateOf(BowlStyle.FAST) }
    var team by remember { mutableStateOf("") }

    var handExpanded by remember { mutableStateOf(false) }
    var styleExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VvSurface,
        title = {
            Text(text = "Add player", style = MaterialTheme.typography.titleLarge, color = VvText)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = VvTextDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VvTeal,
                        unfocusedBorderColor = VvLineStrong,
                        focusedTextColor = VvText,
                        unfocusedTextColor = VvText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("player_name_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = hand.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hand", color = VvTextDim, fontSize = 11.sp) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = VvTextDim) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VvTeal,
                                unfocusedBorderColor = VvLineStrong,
                                focusedTextColor = VvText,
                                unfocusedTextColor = VvText
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().clickable { handExpanded = true }
                        )
                        DropdownMenu(
                            expanded = handExpanded,
                            onDismissRequest = { handExpanded = false },
                            modifier = Modifier.background(VvSurface2)
                        ) {
                            Handedness.entries.forEach { h ->
                                DropdownMenuItem(
                                    text = { Text(h.label, color = VvText) },
                                    onClick = {
                                        hand = h
                                        handExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = style.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Style", color = VvTextDim, fontSize = 11.sp) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = VvTextDim) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VvTeal,
                                unfocusedBorderColor = VvLineStrong,
                                focusedTextColor = VvText,
                                unfocusedTextColor = VvText
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().clickable { styleExpanded = true }
                        )
                        DropdownMenu(
                            expanded = styleExpanded,
                            onDismissRequest = { styleExpanded = false },
                            modifier = Modifier.background(VvSurface2)
                        ) {
                            BowlStyle.entries.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.label, color = VvText) },
                                    onClick = {
                                        style = s
                                        styleExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = team,
                    onValueChange = { team = it },
                    label = { Text("Team / Academy (optional)", color = VvTextDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VvTeal,
                        unfocusedBorderColor = VvLineStrong,
                        focusedTextColor = VvText,
                        unfocusedTextColor = VvText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("player_team_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, hand, style, team) },
                colors = ButtonDefaults.buttonColors(containerColor = VvAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_player_confirm_button")
            ) {
                Text(text = "Save player", color = VvText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Cancel", color = VvTextDim)
            }
        }
    )
}

@Composable
fun AddSessionDialog(
    uiState: VoxVeloUiState,
    onDismiss: () -> Unit,
    onConfirm: (name: String, playerId: String) -> Unit
) {
    var name by remember { mutableStateOf("Session ${java.text.SimpleDateFormat("MMM d", java.util.Locale.US).format(java.util.Date())}") }
    var selectedPlayerId by remember {
        mutableStateOf(uiState.activePlayerId ?: uiState.players.firstOrNull()?.id ?: "")
    }
    var playerExpanded by remember { mutableStateOf(false) }

    val playerName = uiState.players.find { it.id == selectedPlayerId }?.name ?: "Select Player"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VvSurface,
        title = {
            Text(text = "Session details", style = MaterialTheme.typography.titleLarge, color = VvText)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Session name", color = VvTextDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VvTeal,
                        unfocusedBorderColor = VvLineStrong,
                        focusedTextColor = VvText,
                        unfocusedTextColor = VvText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("session_name_input")
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Player", color = VvTextDim) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = VvTextDim) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VvTeal,
                            unfocusedBorderColor = VvLineStrong,
                            focusedTextColor = VvText,
                            unfocusedTextColor = VvText
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().clickable { playerExpanded = true }
                    )
                    DropdownMenu(
                        expanded = playerExpanded,
                        onDismissRequest = { playerExpanded = false },
                        modifier = Modifier.background(VvSurface2)
                    ) {
                        uiState.players.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name, color = VvText) },
                                onClick = {
                                    selectedPlayerId = p.id
                                    playerExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedPlayerId) },
                colors = ButtonDefaults.buttonColors(containerColor = VvAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_session_confirm_button")
            ) {
                Text(
                    text = if (uiState.sessionDialogMode == "save-delivery") "Save delivery" else "Create session",
                    color = VvText,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Cancel", color = VvTextDim)
            }
        }
    )
}

@Composable
fun ClearConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VvSurface,
        title = {
            Text(text = "Clear all local data", style = MaterialTheme.typography.titleLarge, color = VvText)
        },
        text = {
            Text(
                text = "This deletes all players, sessions and deliveries stored on this device. Continue?",
                style = MaterialTheme.typography.bodyMedium,
                color = VvTextDim
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = VvAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_clear_button")
            ) {
                Text(text = "Clear data", color = VvText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Cancel", color = VvTextDim)
            }
        }
    )
}

@Composable
fun SpeedBoardModal(
    uiState: VoxVeloUiState,
    onDismiss: () -> Unit
) {
    val allDeliveries = uiState.sessions.flatMap { it.deliveries }
    val latest = allDeliveries.maxByOrNull { it.ts }
    val kmh = latest?.kmh ?: 138.5
    val mph = latest?.mph ?: (kmh * 0.621371)
    val time = latest?.time ?: 0.523
    val distance = latest?.distance ?: 20.12
    val fps = latest?.fps ?: 60.0
    val confidence = latest?.confidence ?: Confidence.HIGH

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VvSurface,
        title = null,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SpeedBoard(
                    kmh = kmh,
                    mph = mph,
                    time = time,
                    distance = distance,
                    fps = fps,
                    confidence = confidence
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Close", color = VvText)
            }
        }
    )
}
