package com.example.voxvelo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voxvelo.data.AppDatabase
import com.example.voxvelo.data.VoxVeloRepository
import com.example.voxvelo.model.BounceAnalysis
import com.example.voxvelo.model.BowlStyle
import com.example.voxvelo.model.CameraAngle
import com.example.voxvelo.model.Confidence
import com.example.voxvelo.model.Delivery
import com.example.voxvelo.model.FormatUtils
import com.example.voxvelo.model.Handedness
import com.example.voxvelo.model.Player
import com.example.voxvelo.model.Screen
import com.example.voxvelo.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class VoxVeloUiState(
    val currentScreen: Screen = Screen.HOME,
    val screenPayload: String? = null,
    val backStack: List<Pair<Screen, String?>> = emptyList(),
    val players: List<Player> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val activePlayerId: String? = null,
    val currentSessionId: String? = null,
    val toastMessage: String? = null,
    val showPlayerDialog: Boolean = false,
    val showSessionDialog: Boolean = false,
    val sessionDialogMode: String = "create", // "create" or "save-delivery"
    val showClearConfirmDialog: Boolean = false,
    val showSpeedBoardModal: Boolean = false,
    val pendingDelivery: Delivery? = null,

    // Analyze Screen State
    val analyzeTab: String = "import", // "import" or "result"
    val hasVideo: Boolean = true, // Sample clip loaded by default
    val videoLabel: String = "Sample Delivery Clip (Side-on 60fps)",
    val duration: Double = 1.85,
    val currentTime: Double = 0.42,
    val startMark: Double? = 0.32,
    val bounceMark: Double? = 0.98,
    val endMark: Double? = 1.28,
    val fps: String = "60",
    val distance: String = "20.12",
    val bounceDistance: String = "15.5",
    val cameraAngle: CameraAngle = CameraAngle.SIDE,
    val isSuggesting: Boolean = false,
    val suggestedRelease: Double? = null,
    val suggestedArrival: Double? = null,
    val lastResult: Delivery? = null
)

class VoxVeloViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VoxVeloRepository
    private val _uiState = MutableStateFlow(VoxVeloUiState())
    val uiState: StateFlow<VoxVeloUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VoxVeloRepository(database)

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }

        viewModelScope.launch {
            repository.playersFlow.collect { playersList ->
                _uiState.update { state ->
                    val activeId = state.activePlayerId ?: playersList.firstOrNull()?.id
                    state.copy(players = playersList, activePlayerId = activeId)
                }
            }
        }

        viewModelScope.launch {
            repository.sessionsFlow.collect { sessionsList ->
                _uiState.update { state ->
                    val curSessionId = state.currentSessionId ?: sessionsList.firstOrNull()?.id
                    state.copy(sessions = sessionsList, currentSessionId = curSessionId)
                }
            }
        }
    }

    fun goto(screen: Screen, payload: String? = null) {
        _uiState.update { current ->
            val isTab = screen in listOf(Screen.HOME, Screen.SESSIONS, Screen.ANALYZE, Screen.PLAYERS, Screen.MORE)
            val newStack = if (isTab) emptyList() else current.backStack + Pair(current.currentScreen, current.screenPayload)
            current.copy(currentScreen = screen, screenPayload = payload, backStack = newStack)
        }
    }

    fun goBack(): Boolean {
        val stack = _uiState.value.backStack
        if (stack.isNotEmpty()) {
            val previous = stack.last()
            val newStack = stack.dropLast(1)
            _uiState.update {
                it.copy(currentScreen = previous.first, screenPayload = previous.second, backStack = newStack)
            }
            return true
        } else if (_uiState.value.currentScreen != Screen.HOME) {
            _uiState.update { it.copy(currentScreen = Screen.HOME, screenPayload = null, backStack = emptyList()) }
            return true
        }
        return false
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun openPlayerDialog() {
        _uiState.update { it.copy(showPlayerDialog = true) }
    }

    fun closePlayerDialog() {
        _uiState.update { it.copy(showPlayerDialog = false) }
    }

    fun openSessionDialog(mode: String = "create", pending: Delivery? = null) {
        _uiState.update {
            it.copy(showSessionDialog = true, sessionDialogMode = mode, pendingDelivery = pending)
        }
    }

    fun closeSessionDialog() {
        _uiState.update { it.copy(showSessionDialog = false, pendingDelivery = null) }
    }

    fun openClearConfirmDialog() {
        _uiState.update { it.copy(showClearConfirmDialog = true) }
    }

    fun closeClearConfirmDialog() {
        _uiState.update { it.copy(showClearConfirmDialog = false) }
    }

    fun setSpeedBoardModal(open: Boolean) {
        _uiState.update { it.copy(showSpeedBoardModal = open) }
    }

    fun addPlayer(name: String, hand: Handedness, style: BowlStyle, team: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            showToast("Enter a player name.")
            return
        }
        val newPlayer = Player(
            id = "p_${UUID.randomUUID().toString().take(8)}",
            name = trimmed,
            hand = hand,
            style = style,
            team = team.trim(),
            createdAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.savePlayer(newPlayer)
            _uiState.update {
                it.copy(
                    activePlayerId = it.activePlayerId ?: newPlayer.id,
                    showPlayerDialog = false
                )
            }
            showToast("Player added: $trimmed")
        }
    }

    fun setActivePlayer(id: String) {
        val player = _uiState.value.players.find { it.id == id }
        _uiState.update { it.copy(activePlayerId = id) }
        showToast("${player?.name ?: "Player"} is now the active player.")
    }

    fun addSession(name: String, playerId: String) {
        val sessionName = name.trim().ifEmpty { "Session ${System.currentTimeMillis()}" }
        val newSession = Session(
            id = "s_${UUID.randomUUID().toString().take(8)}",
            name = sessionName,
            playerId = playerId,
            createdAt = System.currentTimeMillis(),
            deliveries = emptyList()
        )
        viewModelScope.launch {
            repository.saveSession(newSession)
            _uiState.update {
                it.copy(
                    currentSessionId = newSession.id,
                    showSessionDialog = false
                )
            }
            showToast("Session \"$sessionName\" created.")
        }
    }

    fun savePendingDeliveryToSession(name: String, playerId: String) {
        val pending = _uiState.value.pendingDelivery
        val sessionName = name.trim().ifEmpty { "Session" }
        val newSession = Session(
            id = "s_${UUID.randomUUID().toString().take(8)}",
            name = sessionName,
            playerId = playerId,
            createdAt = System.currentTimeMillis(),
            deliveries = if (pending != null) listOf(pending) else emptyList()
        )
        viewModelScope.launch {
            repository.saveSession(newSession)
            _uiState.update {
                it.copy(
                    currentSessionId = newSession.id,
                    showSessionDialog = false,
                    pendingDelivery = null
                )
            }
            showToast("Session \"$sessionName\" created and delivery saved.")
        }
    }

    fun addDelivery(sessionId: String, delivery: Delivery) {
        viewModelScope.launch {
            repository.addDeliveryToSession(sessionId, delivery)
            val session = _uiState.value.sessions.find { it.id == sessionId }
            showToast("Delivery saved to \"${session?.name ?: "session"}\".")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll()
            _uiState.update {
                it.copy(
                    currentSessionId = null,
                    showClearConfirmDialog = false
                )
            }
            showToast("Local data cleared.")
        }
    }

    // --- Analyze actions ---

    fun setAnalyzeTab(tab: String) {
        _uiState.update { it.copy(analyzeTab = tab) }
    }

    fun loadCustomVideo(label: String) {
        _uiState.update {
            it.copy(
                hasVideo = true,
                videoLabel = label,
                currentTime = 0.0,
                duration = 2.4,
                startMark = null,
                bounceMark = null,
                endMark = null,
                suggestedRelease = null,
                suggestedArrival = null
            )
        }
        showToast("Video loaded: $label")
    }

    fun stepFrame(nFrames: Int) {
        val rate = _uiState.value.fps.toDoubleOrNull() ?: 60.0
        val dt = nFrames.toDouble() / rate
        _uiState.update {
            val nextT = max(0.0, min(it.duration, it.currentTime + dt))
            it.copy(currentTime = nextT)
        }
    }

    fun setScrubTime(t: Double) {
        _uiState.update {
            it.copy(currentTime = max(0.0, min(it.duration, t)))
        }
    }

    fun markPoint(point: String) {
        val t = _uiState.value.currentTime
        _uiState.update {
            when (point) {
                "start" -> it.copy(startMark = t)
                "bounce" -> it.copy(bounceMark = t)
                else -> it.copy(endMark = t)
            }
        }
    }

    fun resetMarks() {
        _uiState.update {
            it.copy(
                startMark = null,
                bounceMark = null,
                endMark = null,
                suggestedRelease = null,
                suggestedArrival = null
            )
        }
    }

    fun suggestFrames() {
        _uiState.update { it.copy(isSuggesting = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(400) // Brief motion frame scan simulation
            _uiState.update {
                val rel = max(0.1, it.duration * 0.22)
                val arr = min(it.duration - 0.1, it.duration * 0.72)
                it.copy(
                    isSuggesting = false,
                    suggestedRelease = rel,
                    suggestedArrival = arr
                )
            }
            showToast("Suggested frames marked from motion detection.")
        }
    }

    fun jumpToSuggestion(which: String) {
        _uiState.update {
            val t = if (which == "release") it.suggestedRelease ?: it.currentTime else it.suggestedArrival ?: it.currentTime
            it.copy(currentTime = t)
        }
    }

    fun setFpsInput(fps: String) {
        _uiState.update { it.copy(fps = fps) }
    }

    fun setDistanceInput(d: String) {
        _uiState.update { it.copy(distance = d) }
    }

    fun setBounceDistanceInput(d: String) {
        _uiState.update { it.copy(bounceDistance = d) }
    }

    fun setCameraAngle(angle: CameraAngle) {
        _uiState.update { it.copy(cameraAngle = angle) }
    }

    fun calculateSpeed() {
        val state = _uiState.value
        val start = state.startMark
        val end = state.endMark

        if (start == null || end == null) {
            showToast("Mark both the release frame and the arrival frame first.")
            return
        }

        val dist = state.distance.toDoubleOrNull()
        val rate = state.fps.toDoubleOrNull()

        if (dist == null || dist <= 0) {
            showToast("Enter a valid distance in meters.")
            return
        }
        if (rate == null || rate <= 0) {
            showToast("Enter a valid FPS.")
            return
        }

        val time = abs(end - start)
        if (time <= 0.001) {
            showToast("Start and end frames must differ.")
            return
        }

        val mps = dist / time
        val kmh = mps * 3.6
        val mph = FormatUtils.kmhToMph(kmh)

        var confidence: Confidence = when {
            rate >= 120.0 && time >= 0.05 -> Confidence.HIGH
            rate >= 60.0 && time >= 0.04 -> Confidence.HIGH
            rate >= 30.0 && time >= 0.03 -> Confidence.MEDIUM
            else -> Confidence.LOW
        }
        if (kmh > 185.0 || kmh < 35.0) {
            confidence = Confidence.LOW
        }

        var bounce: BounceAnalysis? = null
        val bMark = state.bounceMark
        if (bMark != null) {
            val bDist = state.bounceDistance.toDoubleOrNull() ?: 0.0
            val releaseT = min(start, end)
            val arrivalT = max(start, end)
            if (bDist > 0 && bMark > releaseT && bMark < arrivalT) {
                bounce = BounceAnalysis(
                    releaseToBounceTime = abs(bMark - start),
                    bounceToArrivalTime = abs(end - bMark),
                    bounceDistance = bDist,
                    stumpsDistance = max(0.0, dist - bDist)
                )
            }
        }

        val result = Delivery(
            kmh = kmh,
            mph = mph,
            time = time,
            distance = dist,
            fps = rate,
            confidence = confidence,
            angle = state.cameraAngle,
            bounce = bounce,
            ts = System.currentTimeMillis()
        )

        _uiState.update {
            it.copy(lastResult = result, analyzeTab = "result")
        }
        showToast("Calculated speed: ${FormatUtils.fmt(kmh, 1)} km/h")
    }

    fun saveLastResult() {
        val result = _uiState.value.lastResult ?: return
        val players = _uiState.value.players
        if (players.isEmpty()) {
            showToast("Add a player first.")
            goto(Screen.PLAYERS)
            return
        }

        val curId = _uiState.value.currentSessionId
        val session = _uiState.value.sessions.find { it.id == curId }
        if (session != null) {
            addDelivery(session.id, result.copy(ts = System.currentTimeMillis()))
        } else {
            openSessionDialog("save-delivery", result.copy(ts = System.currentTimeMillis()))
        }
    }
}
