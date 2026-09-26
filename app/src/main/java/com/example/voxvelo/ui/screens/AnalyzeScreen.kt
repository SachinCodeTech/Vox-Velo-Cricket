package com.example.voxvelo.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voxvelo.model.CameraAngle
import com.example.voxvelo.model.FormatUtils
import com.example.voxvelo.ui.VoxVeloUiState
import com.example.voxvelo.ui.VoxVeloViewModel
import com.example.voxvelo.ui.components.EmptyState
import com.example.voxvelo.ui.components.PitchOverlay
import com.example.voxvelo.ui.components.SectionLabel
import com.example.voxvelo.ui.components.SpeedBoard
import com.example.voxvelo.ui.components.StatStrip
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvAmber
import com.example.voxvelo.ui.theme.VvLine
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvTeal
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim

@Composable
fun AnalyzeScreen(
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.loadCustomVideo("Imported Video Clip (${uri.lastPathSegment?.takeLast(14) ?: "clip"})")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Analyze delivery",
            style = MaterialTheme.typography.headlineMedium,
            color = VvText
        )
        Text(
            text = "Import or record a bowling delivery, mark release, bounce and arrival, and get an estimated speed with a pitch overlay.",
            style = MaterialTheme.typography.bodyMedium,
            color = VvTextDim,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Sub Tabs: Import / Record vs Result
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
                    .background(if (uiState.analyzeTab == "import") VvSurface2 else VvSurface)
                    .clickable { viewModel.setAnalyzeTab("import") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Import / Record",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (uiState.analyzeTab == "import") VvTeal else VvTextDim,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (uiState.analyzeTab == "result") VvSurface2 else VvSurface)
                    .clickable { viewModel.setAnalyzeTab("result") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Result",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (uiState.analyzeTab == "result") VvAccent else VvTextDim,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.analyzeTab == "import") {
            // Import / Record pane
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.VideoFile, contentDescription = null, tint = VvTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Import video", color = VvText, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        viewModel.loadCustomVideo("Recorded Clip (Camera 60fps)")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = VvAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Record now", color = VvText, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Video monitor / Scrubber viewport
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VvSurface)
                    .border(1.dp, VvLineStrong, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                // Monitor screen simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VvSurface2)
                        .border(1.dp, VvLine, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = VvTeal,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.videoLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = VvText,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Frame Scrubbing & Marker Mode",
                            style = MaterialTheme.typography.labelSmall,
                            color = VvTextDim
                        )
                    }

                    // FPS badge (top left)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(VvSurface.copy(alpha = 0.85f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "FPS ${uiState.fps} (detected)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = VvTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Marker badge (bottom right)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(VvSurface.copy(alpha = 0.85f))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (uiState.startMark != null) "S ${FormatUtils.fmt(uiState.startMark, 2)}s" else "S —",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (uiState.startMark != null) VvTeal else VvTextDim,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (uiState.bounceMark != null) "B ${FormatUtils.fmt(uiState.bounceMark, 2)}s" else "B —",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (uiState.bounceMark != null) VvAmber else VvTextDim,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (uiState.endMark != null) "E ${FormatUtils.fmt(uiState.endMark, 2)}s" else "E —",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (uiState.endMark != null) VvAccent else VvTextDim,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Time scrubber
                Slider(
                    value = uiState.currentTime.toFloat(),
                    onValueChange = { viewModel.setScrubTime(it.toDouble()) },
                    valueRange = 0f..uiState.duration.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = VvTeal,
                        activeTrackColor = VvTeal,
                        inactiveTrackColor = VvLineStrong
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("video_scrub_slider")
                )

                // Frame stepping buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.stepFrame(-5) },
                        colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "-5f", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = VvText)
                    }
                    Button(
                        onClick = { viewModel.stepFrame(-1) },
                        colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "-1f", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = VvText)
                    }
                    Text(
                        text = "${FormatUtils.fmt(uiState.currentTime, 3)}s",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VvTeal
                    )
                    Button(
                        onClick = { viewModel.stepFrame(1) },
                        colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "+1f", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = VvText)
                    }
                    Button(
                        onClick = { viewModel.stepFrame(5) },
                        colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "+5f", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = VvText)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Marker selection buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Release button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VvSurface2)
                            .border(1.dp, if (uiState.startMark != null) VvTeal else VvLineStrong, RoundedCornerShape(8.dp))
                            .clickable { viewModel.markPoint("start") }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "RELEASE (start)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = VvTeal, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (uiState.startMark != null) "${FormatUtils.fmt(uiState.startMark, 3)}s" else "not set",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = VvText
                        )
                    }

                    // Bounce button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VvSurface2)
                            .border(1.dp, if (uiState.bounceMark != null) VvAmber else VvLineStrong, RoundedCornerShape(8.dp))
                            .clickable { viewModel.markPoint("bounce") }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "BOUNCE (opt)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = VvAmber, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (uiState.bounceMark != null) "${FormatUtils.fmt(uiState.bounceMark, 3)}s" else "not set",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = VvText
                        )
                    }

                    // Arrival button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VvSurface2)
                            .border(1.dp, if (uiState.endMark != null) VvAccent else VvLineStrong, RoundedCornerShape(8.dp))
                            .clickable { viewModel.markPoint("end") }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "ARRIVAL (end)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = VvAccent, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (uiState.endMark != null) "${FormatUtils.fmt(uiState.endMark, 3)}s" else "not set",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = VvText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Suggest frames button
                Button(
                    onClick = { viewModel.suggestFrames() },
                    colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("suggest_frames_button")
                ) {
                    Text(
                        text = if (uiState.isSuggesting) "Analyzing motion…" else "Suggest frames from motion",
                        color = VvTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (uiState.suggestedRelease != null || uiState.suggestedArrival != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.jumpToSuggestion("release") },
                            colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Jump to release", color = VvTextDim, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.jumpToSuggestion("arrival") },
                            colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Jump to arrival", color = VvTextDim, fontSize = 11.sp)
                        }
                    }
                }
            }

            SectionLabel(text = "Measurement inputs")

            // Distance input
            OutlinedTextField(
                value = uiState.distance,
                onValueChange = { viewModel.setDistanceInput(it) },
                label = { Text("Distance (m) — release point to stumps", color = VvTextDim, fontSize = 12.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VvTeal,
                    unfocusedBorderColor = VvLineStrong,
                    focusedTextColor = VvText,
                    unfocusedTextColor = VvText
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("distance_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bounce distance input
            OutlinedTextField(
                value = uiState.bounceDistance,
                onValueChange = { viewModel.setBounceDistanceInput(it) },
                label = { Text("Bounce distance (m) — release point to bounce", color = VvTextDim, fontSize = 12.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VvAmber,
                    unfocusedBorderColor = VvLineStrong,
                    focusedTextColor = VvText,
                    unfocusedTextColor = VvText
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("bounce_distance_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Calibration grid: FPS and Camera Angle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.fps,
                    onValueChange = { viewModel.setFpsInput(it) },
                    label = { Text("FPS", color = VvTextDim, fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VvTeal,
                        unfocusedBorderColor = VvLineStrong,
                        focusedTextColor = VvText,
                        unfocusedTextColor = VvText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("fps_input")
                )

                // Camera Angle Dropdown
                var angleExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1.5f)) {
                    OutlinedTextField(
                        value = uiState.cameraAngle.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Camera angle", color = VvTextDim, fontSize = 12.sp) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = VvTextDim
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VvTeal,
                            unfocusedBorderColor = VvLineStrong,
                            focusedTextColor = VvText,
                            unfocusedTextColor = VvText
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { angleExpanded = true }
                    )
                    DropdownMenu(
                        expanded = angleExpanded,
                        onDismissRequest = { angleExpanded = false },
                        modifier = Modifier.background(VvSurface2)
                    ) {
                        CameraAngle.entries.forEach { angle ->
                            DropdownMenuItem(
                                text = { Text(text = angle.label, color = VvText) },
                                onClick = {
                                    viewModel.setCameraAngle(angle)
                                    angleExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate Speed Button
            Button(
                onClick = { viewModel.calculateSpeed() },
                colors = ButtonDefaults.buttonColors(containerColor = VvAccent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("calculate_speed_button")
            ) {
                Text(
                    text = "Calculate speed",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VvText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Estimated Speed — not a certified radar measurement. Accuracy depends on FPS, camera angle, calibration and ball visibility. Bounce marking is manual — the ball is never auto-tracked or fabricated.",
                style = MaterialTheme.typography.bodyMedium,
                color = VvTextDim,
                fontSize = 11.sp
            )
        } else {
            // Result tab
            val res = uiState.lastResult
            if (res == null) {
                EmptyState(
                    title = "No result yet",
                    message = "Calculate a delivery in the Import tab first."
                )
            } else {
                SpeedBoard(
                    kmh = res.kmh,
                    mph = res.mph,
                    time = res.time,
                    distance = res.distance,
                    fps = res.fps,
                    confidence = res.confidence
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (res.confidence == com.example.voxvelo.model.Confidence.LOW) {
                        "Low confidence — reposition camera, improve lighting, or use a higher-FPS recording for a more reliable estimate."
                    } else {
                        "Method: Video Frame Analysis (Distance ÷ Flight Time). Estimated speed, not a certified radar measurement."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (res.confidence == com.example.voxvelo.model.Confidence.LOW) VvAccent else VvTextDim,
                    fontSize = 11.sp
                )

                if (res.bounce != null) {
                    SectionLabel(text = "Bounce / pitch analysis")
                    StatStrip(
                        rows = listOf(
                            Pair("Release → bounce time", "${FormatUtils.fmt(res.bounce.releaseToBounceTime, 3)}s"),
                            Pair("Bounce → arrival time", "${FormatUtils.fmt(res.bounce.bounceToArrivalTime, 3)}s"),
                            Pair("Bounce distance", "${FormatUtils.fmt(res.bounce.bounceDistance, 2)}m"),
                            Pair("Bounce → stumps (est.)", "${FormatUtils.fmt(res.bounce.stumpsDistance, 2)}m")
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PitchOverlay(
                        bounceDist = res.bounce.bounceDistance,
                        totalDist = res.distance
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveLastResult() },
                        colors = ButtonDefaults.buttonColors(containerColor = VvTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("save_to_session_button")
                    ) {
                        Text(text = "Save to session", color = VvSurface, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.setAnalyzeTab("import") },
                        colors = ButtonDefaults.buttonColors(containerColor = VvSurface2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("remark_frames_button")
                    ) {
                        Text(text = "Re-mark frames", color = VvText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
