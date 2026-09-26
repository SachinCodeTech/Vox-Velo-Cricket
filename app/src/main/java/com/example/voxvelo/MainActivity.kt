package com.example.voxvelo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voxvelo.model.Screen
import com.example.voxvelo.ui.VoxVeloUiState
import com.example.voxvelo.ui.VoxVeloViewModel
import com.example.voxvelo.ui.components.AddPlayerDialog
import com.example.voxvelo.ui.components.AddSessionDialog
import com.example.voxvelo.ui.components.ClearConfirmDialog
import com.example.voxvelo.ui.components.SpeedBoardModal
import com.example.voxvelo.ui.screens.AnalyzeScreen
import com.example.voxvelo.ui.screens.CompareScreen
import com.example.voxvelo.ui.screens.HomeScreen
import com.example.voxvelo.ui.screens.MoreScreen
import com.example.voxvelo.ui.screens.PlayerDetailScreen
import com.example.voxvelo.ui.screens.PlayersScreen
import com.example.voxvelo.ui.screens.SessionDetailScreen
import com.example.voxvelo.ui.screens.SessionsScreen
import com.example.voxvelo.ui.screens.TeamDashboardScreen
import com.example.voxvelo.ui.theme.VoxVeloTheme
import com.example.voxvelo.ui.theme.VvAccent
import com.example.voxvelo.ui.theme.VvBg
import com.example.voxvelo.ui.theme.VvLineStrong
import com.example.voxvelo.ui.theme.VvSurface
import com.example.voxvelo.ui.theme.VvSurface2
import com.example.voxvelo.ui.theme.VvTeal
import com.example.voxvelo.ui.theme.VvText
import com.example.voxvelo.ui.theme.VvTextDim
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: VoxVeloViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VoxVeloTheme {
                val uiState by viewModel.uiState.collectAsState()

                BackHandler(enabled = uiState.currentScreen != Screen.HOME || uiState.backStack.isNotEmpty()) {
                    viewModel.goBack()
                }

                VoxVeloApp(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoxVeloApp(
    uiState: VoxVeloUiState,
    viewModel: VoxVeloViewModel
) {
    val activePlayer = uiState.players.find { it.id == uiState.activePlayerId }

    // Auto-clear toast after 2.5 seconds
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            delay(2500)
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VOXVELO",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            color = VvAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CRICKET",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = VvTextDim,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    if (activePlayer != null) {
                        Row(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VvSurface2)
                                .border(1.dp, VvLineStrong, RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(VvTeal)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activePlayer.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = VvText,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VvBg,
                    titleContentColor = VvText
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = VvSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(1.dp, VvLineStrong, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val currentTab = when (uiState.currentScreen) {
                    Screen.HOME -> Screen.HOME
                    Screen.SESSIONS, Screen.SESSION_DETAIL -> Screen.SESSIONS
                    Screen.ANALYZE -> Screen.ANALYZE
                    Screen.PLAYERS, Screen.PLAYER_DETAIL, Screen.TEAM_DASHBOARD, Screen.COMPARE -> Screen.PLAYERS
                    Screen.MORE -> Screen.MORE
                }

                NavigationBarItem(
                    selected = currentTab == Screen.HOME,
                    onClick = { viewModel.goto(Screen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VvAccent,
                        selectedTextColor = VvAccent,
                        indicatorColor = VvAccent.copy(alpha = 0.15f),
                        unselectedIconColor = VvTextDim,
                        unselectedTextColor = VvTextDim
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentTab == Screen.SESSIONS,
                    onClick = { viewModel.goto(Screen.SESSIONS) },
                    icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = "Sessions") },
                    label = { Text("Sessions", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VvTeal,
                        selectedTextColor = VvTeal,
                        indicatorColor = VvTeal.copy(alpha = 0.15f),
                        unselectedIconColor = VvTextDim,
                        unselectedTextColor = VvTextDim
                    ),
                    modifier = Modifier.testTag("nav_sessions")
                )

                NavigationBarItem(
                    selected = currentTab == Screen.ANALYZE,
                    onClick = { viewModel.goto(Screen.ANALYZE) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = "Analyze") },
                    label = { Text("Analyze", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VvAccent,
                        selectedTextColor = VvAccent,
                        indicatorColor = VvAccent.copy(alpha = 0.15f),
                        unselectedIconColor = VvTextDim,
                        unselectedTextColor = VvTextDim
                    ),
                    modifier = Modifier.testTag("nav_analyze")
                )

                NavigationBarItem(
                    selected = currentTab == Screen.PLAYERS,
                    onClick = { viewModel.goto(Screen.PLAYERS) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Players") },
                    label = { Text("Players", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VvTeal,
                        selectedTextColor = VvTeal,
                        indicatorColor = VvTeal.copy(alpha = 0.15f),
                        unselectedIconColor = VvTextDim,
                        unselectedTextColor = VvTextDim
                    ),
                    modifier = Modifier.testTag("nav_players")
                )

                NavigationBarItem(
                    selected = currentTab == Screen.MORE,
                    onClick = { viewModel.goto(Screen.MORE) },
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                    label = { Text("More", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VvText,
                        selectedTextColor = VvText,
                        indicatorColor = VvText.copy(alpha = 0.15f),
                        unselectedIconColor = VvTextDim,
                        unselectedTextColor = VvTextDim
                    ),
                    modifier = Modifier.testTag("nav_more")
                )
            }
        },
        containerColor = VvBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentScreen) {
                Screen.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                Screen.SESSIONS -> SessionsScreen(uiState = uiState, viewModel = viewModel)
                Screen.SESSION_DETAIL -> SessionDetailScreen(sessionId = uiState.screenPayload, uiState = uiState, viewModel = viewModel)
                Screen.ANALYZE -> AnalyzeScreen(uiState = uiState, viewModel = viewModel)
                Screen.PLAYERS -> PlayersScreen(uiState = uiState, viewModel = viewModel)
                Screen.PLAYER_DETAIL -> PlayerDetailScreen(playerId = uiState.screenPayload, uiState = uiState, viewModel = viewModel)
                Screen.TEAM_DASHBOARD -> TeamDashboardScreen(uiState = uiState, viewModel = viewModel)
                Screen.COMPARE -> CompareScreen(uiState = uiState, viewModel = viewModel)
                Screen.MORE -> MoreScreen(uiState = uiState, viewModel = viewModel)
            }

            // Toast Floating Notification
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                if (uiState.toastMessage != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(VvSurface2)
                            .border(1.dp, VvTeal.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = uiState.toastMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = VvText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (uiState.showPlayerDialog) {
        AddPlayerDialog(
            onDismiss = { viewModel.closePlayerDialog() },
            onSave = { name, hand, style, team ->
                viewModel.addPlayer(name, hand, style, team)
            }
        )
    }

    if (uiState.showSessionDialog) {
        AddSessionDialog(
            uiState = uiState,
            onDismiss = { viewModel.closeSessionDialog() },
            onConfirm = { name, playerId ->
                if (uiState.sessionDialogMode == "save-delivery") {
                    viewModel.savePendingDeliveryToSession(name, playerId)
                } else {
                    viewModel.addSession(name, playerId)
                }
            }
        )
    }

    if (uiState.showClearConfirmDialog) {
        ClearConfirmDialog(
            onDismiss = { viewModel.closeClearConfirmDialog() },
            onConfirm = { viewModel.clearAllData() }
        )
    }

    if (uiState.showSpeedBoardModal) {
        SpeedBoardModal(
            uiState = uiState,
            onDismiss = { viewModel.setSpeedBoardModal(false) }
        )
    }
}
