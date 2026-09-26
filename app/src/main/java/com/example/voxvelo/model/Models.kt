package com.example.voxvelo.model

import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

enum class Screen {
    HOME,
    SESSIONS,
    SESSION_DETAIL,
    ANALYZE,
    PLAYERS,
    PLAYER_DETAIL,
    TEAM_DASHBOARD,
    COMPARE,
    MORE
}

enum class Confidence(val label: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

enum class Handedness(val label: String) {
    RIGHT_HANDED("Right-handed"),
    LEFT_HANDED("Left-handed")
}

enum class BowlStyle(val label: String) {
    FAST("Fast"),
    MEDIUM("Medium"),
    SPIN("Spin")
}

enum class CameraAngle(val label: String) {
    SIDE("Side-on (recommended)"),
    BEHIND("Behind bowler"),
    OTHER("Other")
}

data class Player(
    val id: String,
    val name: String,
    val hand: Handedness,
    val style: BowlStyle,
    val team: String,
    val createdAt: Long
)

data class BounceAnalysis(
    val releaseToBounceTime: Double,
    val bounceToArrivalTime: Double,
    val bounceDistance: Double,
    val stumpsDistance: Double
)

data class Delivery(
    val kmh: Double,
    val mph: Double,
    val time: Double,
    val distance: Double,
    val fps: Double,
    val confidence: Confidence,
    val angle: CameraAngle,
    val bounce: BounceAnalysis? = null,
    val ts: Long = System.currentTimeMillis()
)

data class Session(
    val id: String,
    val name: String,
    val playerId: String,
    val createdAt: Long,
    val deliveries: List<Delivery> = emptyList()
)

data class SessionStats(
    val fastest: Double?,
    val slowest: Double?,
    val avg: Double?,
    val median: Double?,
    val valid: Int,
    val low: Int,
    val consistency: Double?
)

data class CoachInsights(
    val summary: String,
    val observations: List<String>,
    val drills: List<String>
)

object FormatUtils {
    fun fmt(v: Double?, decimals: Int = 1): String {
        if (v == null || v.isNaN()) return "—"
        return String.format(Locale.US, "%.${decimals}f", v)
    }

    fun kmhToMph(kmh: Double): Double = kmh * 0.621371

    fun timeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour
        return when {
            diff < minute -> "just now"
            diff < hour -> "${diff / minute}m ago"
            diff < day -> "${diff / hour}h ago"
            diff < 7 * day -> "${diff / day}d ago"
            else -> "${diff / (7 * day)}w ago"
        }
    }
}

object CricketStatsCalculator {
    fun sessionStats(deliveries: List<Delivery>): SessionStats {
        val speeds = deliveries.map { it.kmh }.filter { !it.isNaN() }
        if (speeds.isEmpty()) {
            return SessionStats(
                fastest = null,
                slowest = null,
                avg = null,
                median = null,
                valid = 0,
                low = 0,
                consistency = null
            )
        }

        val sorted = speeds.sorted()
        val sum = speeds.sum()
        val avg = sum / speeds.size
        val median = if (sorted.size % 2 == 1) {
            sorted[sorted.size / 2]
        } else {
            (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
        }

        val variance = speeds.map { (it - avg) * (it - avg) }.sum() / speeds.size
        val sd = sqrt(variance)
        val consistency = if (avg > 0) max(0.0, 100.0 - (sd / avg) * 100.0) else 0.0

        return SessionStats(
            fastest = speeds.maxOrNull(),
            slowest = speeds.minOrNull(),
            avg = avg,
            median = median,
            valid = speeds.size,
            low = deliveries.count { it.confidence == Confidence.LOW },
            consistency = consistency
        )
    }

    fun generateCoachInsights(deliveries: List<Delivery>): CoachInsights {
        val valid = deliveries.filter { !it.kmh.isNaN() }
        if (valid.size < 3) {
            return CoachInsights(
                summary = "Record at least 3 deliveries in this session for the coach to analyze pace and consistency.",
                observations = emptyList(),
                drills = emptyList()
            )
        }

        val speeds = valid.map { it.kmh }
        val minSpeed = speeds.minOrNull() ?: 0.0
        val maxSpeed = speeds.maxOrNull() ?: 0.0
        val avg = speeds.sum() / speeds.size
        val variance = speeds.map { (it - avg) * (it - avg) }.sum() / speeds.size
        val sd = sqrt(variance)
        val consistency = if (avg > 0) max(0.0, 100.0 - (sd / avg) * 100.0) else 0.0
        val lowCount = valid.count { it.confidence == Confidence.LOW }
        val lowRatio = lowCount.toDouble() / valid.size

        val half = max(1, speeds.size / 2)
        val firstHalf = speeds.take(half)
        val secondHalf = speeds.takeLast(half)
        val firstAvg = firstHalf.sum() / firstHalf.size
        val secondAvg = secondHalf.sum() / secondHalf.size
        val drop = firstAvg - secondAvg

        val summary = "Recorded ${valid.size} deliveries ranging from ${FormatUtils.fmt(minSpeed, 1)}–${FormatUtils.fmt(maxSpeed, 1)} km/h, averaging ${FormatUtils.fmt(avg, 1)} km/h at ${FormatUtils.fmt(consistency, 0)}% consistency."

        val observations = mutableListOf<String>()
        val drills = mutableListOf<String>()

        if (abs(drop) >= 2.0) {
            if (drop > 0) {
                observations.add("Your final deliveries averaged ${FormatUtils.fmt(drop, 1)} km/h slower than your first ones — a fatigue pattern across the session.")
                drills.add("Fatigue management — split long spells into shorter high-intensity sets with full recovery, and re-check pace in the back half of practice.")
            } else {
                observations.add("Your later deliveries were ${FormatUtils.fmt(abs(drop), 1)} km/h faster on average than your first ones — pace built through the session.")
            }
        } else {
            observations.add("Pace held steady from the start of the session to the end — no meaningful fatigue drop detected.")
        }

        if (consistency >= 90.0) {
            observations.add("Delivery-to-delivery consistency is excellent, within a tight speed band.")
        } else if (consistency >= 75.0) {
            observations.add("Consistency is solid, with some natural variation delivery to delivery.")
            drills.add("Run-up consistency — bowl to a fixed run-up mark and focus on repeating release timing each delivery.")
        } else {
            observations.add("Consistency is lower than typical (${FormatUtils.fmt(consistency, 0)}%) — speed varied noticeably between deliveries.")
            drills.add("Run-up consistency — bowl to a fixed run-up mark and focus on repeating release timing each delivery.")
            drills.add("Rhythm drill — bowl 6-ball overs at 70% effort focusing purely on a repeatable action before building back to full pace.")
        }

        if (lowRatio > 0.2) {
            observations.add("$lowCount of ${valid.size} deliveries were low-confidence readings — camera setup is limiting measurement reliability.")
            drills.add("Recording setup — use a side-on angle, 60fps or higher, even lighting and a stable phone position for more reliable readings.")
        }

        if (drills.isEmpty()) {
            drills.add("No corrective drills flagged from this data — maintain the current run-up and recording setup.")
        }

        return CoachInsights(summary, observations, drills)
    }

    fun makeSeed(): Pair<List<Player>, List<Session>> {
        val now = System.currentTimeMillis()
        val day = 86_400_000L

        fun del(kmh: Double, ts: Long, confidence: Confidence = Confidence.HIGH): Delivery {
            val time = 20.12 / (kmh / 3.6)
            return Delivery(
                kmh = kmh,
                mph = kmh * 0.621371,
                time = time,
                distance = 20.12,
                fps = 60.0,
                confidence = confidence,
                angle = CameraAngle.SIDE,
                bounce = null,
                ts = ts
            )
        }

        val players = listOf(
            Player(
                id = "p_arjun",
                name = "Arjun Mehta",
                hand = Handedness.RIGHT_HANDED,
                style = BowlStyle.FAST,
                team = "VoxVelo Academy",
                createdAt = now - 20 * day
            ),
            Player(
                id = "p_priya",
                name = "Priya Sharma",
                hand = Handedness.RIGHT_HANDED,
                style = BowlStyle.MEDIUM,
                team = "VoxVelo Academy",
                createdAt = now - 18 * day
            ),
            Player(
                id = "p_kabir",
                name = "Kabir Singh",
                hand = Handedness.LEFT_HANDED,
                style = BowlStyle.SPIN,
                team = "City Club",
                createdAt = now - 12 * day
            )
        )

        val sMorning = Session(
            id = "s_morning",
            name = "Morning Nets",
            playerId = "p_arjun",
            createdAt = now - 2 * day,
            deliveries = listOf(
                del(136.2, now - 2 * day + 8 * 60_000L),
                del(138.5, now - 2 * day + 12 * 60_000L),
                del(141.1, now - 2 * day + 16 * 60_000L),
                del(139.4, now - 2 * day + 20 * 60_000L),
                del(137.8, now - 2 * day + 24 * 60_000L),
                del(135.0, now - 2 * day + 28 * 60_000L, Confidence.MEDIUM),
                del(133.6, now - 2 * day + 32 * 60_000L),
                del(132.1, now - 2 * day + 36 * 60_000L, Confidence.MEDIUM)
            )
        )

        val sEvening = Session(
            id = "s_evening",
            name = "Evening Spell",
            playerId = "p_arjun",
            createdAt = now - 6 * 3600_000L,
            deliveries = listOf(
                del(134.8, now - 6 * 3600_000L + 4 * 60_000L),
                del(137.2, now - 6 * 3600_000L + 8 * 60_000L),
                del(138.9, now - 6 * 3600_000L + 12 * 60_000L),
                del(136.4, now - 6 * 3600_000L + 16 * 60_000L),
                del(133.1, now - 6 * 3600_000L + 20 * 60_000L, Confidence.MEDIUM),
                del(131.8, now - 6 * 3600_000L + 24 * 60_000L)
            )
        )

        val sYorkers = Session(
            id = "s_yorkers",
            name = "Nets — Yorkers",
            playerId = "p_priya",
            createdAt = now - 3 * day,
            deliveries = listOf(
                del(118.4, now - 3 * day + 10 * 60_000L),
                del(121.0, now - 3 * day + 14 * 60_000L),
                del(119.6, now - 3 * day + 18 * 60_000L),
                del(116.2, now - 3 * day + 22 * 60_000L, Confidence.MEDIUM),
                del(114.8, now - 3 * day + 26 * 60_000L),
                del(112.5, now - 3 * day + 30 * 60_000L, Confidence.LOW)
            )
        )

        val sSpin = Session(
            id = "s_spin",
            name = "Off-spin block",
            playerId = "p_kabir",
            createdAt = now - day,
            deliveries = listOf(
                del(84.2, now - day + 5 * 60_000L),
                del(86.1, now - day + 9 * 60_000L),
                del(87.8, now - day + 13 * 60_000L),
                del(85.4, now - day + 17 * 60_000L),
                del(83.0, now - day + 21 * 60_000L, Confidence.MEDIUM),
                del(81.6, now - day + 25 * 60_000L),
                del(80.2, now - day + 29 * 60_000L, Confidence.MEDIUM)
            )
        )

        return Pair(players, listOf(sEvening, sSpin, sMorning, sYorkers))
    }
}
