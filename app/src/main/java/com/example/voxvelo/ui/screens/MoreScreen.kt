package com.example.voxvelo.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voxvelo.ui.VoxVeloUiState
import com.example.voxvelo.ui.VoxVeloViewModel
import com.example.voxvelo.ui.components.SectionLabel
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvAmber
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MoreScreen(
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "More",
            style = MaterialTheme.typography.headlineMedium,
            color = VvText
        )
        Text(
            text = "VoxVelo Cricket — your cricket performance lab.",
            style = MaterialTheme.typography.bodyMedium,
            color = VvTextDim,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        SectionLabel(text = "Coming soon")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VvSurface)
                .border(1.dp, VvLineStrong, RoundedCornerShape(12.dp))
        ) {
            val roadmap = listOf(
                Pair("Automatic ball tracking (needs a CV model)", "Phase 3"),
                Pair("Advanced biomechanics (needs a pose model)", "Phase 3"),
                Pair("Cloud backup & team sync", "Future")
            )

            roadmap.forEachIndexed { i, (feature, phase) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = VvText,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VvSurface2)
                            .border(1.dp, VvLineStrong, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = phase,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VvAmber
                        )
                    }
                }
                if (i < roadmap.size - 1) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(VvLineStrong))
                }
            }
        }

        SectionLabel(text = "Data management")

        Button(
            onClick = {
                val root = JSONObject()
                val pArray = JSONArray()
                uiState.players.forEach { p ->
                    val obj = JSONObject()
                    obj.put("id", p.id)
                    obj.put("name", p.name)
                    obj.put("style", p.style.name)
                    obj.put("hand", p.hand.name)
                    obj.put("team", p.team)
                    pArray.put(obj)
                }
                root.put("players", pArray)

                val sArray = JSONArray()
                uiState.sessions.forEach { s ->
                    val sObj = JSONObject()
                    sObj.put("id", s.id)
                    sObj.put("name", s.name)
                    sObj.put("playerId", s.playerId)
                    sObj.put("createdAt", s.createdAt)
                    sObj.put("deliveryCount", s.deliveries.size)
                    sArray.put(sObj)
                }
                root.put("sessions", sArray)

                val jsonString = root.toString(2)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_SUBJECT, "VoxVelo Export")
                    putExtra(Intent.EXTRA_TEXT, jsonString)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Export VoxVelo Data"))
                viewModel.showToast("Export data ready to share.")
            },
            colors = ButtonDefaults.buttonColors(containerColor = VvSurface),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VvLineStrong, RoundedCornerShape(8.dp))
                .testTag("export_data_button")
        ) {
            Text(text = "Export data (JSON)", color = VvText)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { viewModel.openClearConfirmDialog() },
            colors = ButtonDefaults.buttonColors(containerColor = VvSurface),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VvAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .testTag("clear_all_data_button")
        ) {
            Text(text = "Clear all local data", color = VvAccent)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "All videos and measurements stay on this device by default. Nothing is uploaded to external servers. The AI Coach runs entirely on your recorded measurements, on-device — it never invents numbers.",
            style = MaterialTheme.typography.bodyMedium,
            color = VvTextDim,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
