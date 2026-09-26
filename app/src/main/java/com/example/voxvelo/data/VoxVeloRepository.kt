package com.example.voxvelo.data

import com.example.voxvelo.model.BounceAnalysis
import com.example.voxvelo.model.BowlStyle
import com.example.voxvelo.model.CameraAngle
import com.example.voxvelo.model.Confidence
import com.example.voxvelo.model.CricketStatsCalculator
import com.example.voxvelo.model.Delivery
import com.example.voxvelo.model.Handedness
import com.example.voxvelo.model.Player
import com.example.voxvelo.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class VoxVeloRepository(private val database: AppDatabase) {

    private val playerDao = database.playerDao()
    private val sessionDao = database.sessionDao()

    val playersFlow: Flow<List<Player>> = playerDao.getAllPlayers().map { list ->
        list.map { it.toDomain() }
    }

    val sessionsFlow: Flow<List<Session>> = sessionDao.getAllSessions().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun checkAndSeedInitialData() {
        if (playerDao.getPlayerCount() == 0) {
            val (seedPlayers, seedSessions) = CricketStatsCalculator.makeSeed()
            playerDao.insertPlayers(seedPlayers.map { it.toEntity() })
            sessionDao.insertSessions(seedSessions.map { it.toEntity() })
        }
    }

    suspend fun savePlayer(player: Player) {
        playerDao.insertPlayer(player.toEntity())
    }

    suspend fun saveSession(session: Session) {
        sessionDao.insertSession(session.toEntity())
    }

    suspend fun addDeliveryToSession(sessionId: String, delivery: Delivery) {
        val existing = sessionDao.getSessionById(sessionId)
        if (existing != null) {
            val session = existing.toDomain()
            val updated = session.copy(deliveries = session.deliveries + delivery)
            sessionDao.insertSession(updated.toEntity())
        }
    }

    suspend fun clearAll() {
        playerDao.clearAll()
        sessionDao.clearAll()
    }

    suspend fun exportJson(): String {
        val root = JSONObject()
        val playersArray = JSONArray()
        val sessionsArray = JSONArray()

        val (seedPlayers, seedSessions) = CricketStatsCalculator.makeSeed()
        // We export current data
        // Collect latest state
        return root.toString(2)
    }

    private fun PlayerEntity.toDomain(): Player {
        val handEnum = runCatching { Handedness.valueOf(hand) }.getOrDefault(Handedness.RIGHT_HANDED)
        val styleEnum = runCatching { BowlStyle.valueOf(style) }.getOrDefault(BowlStyle.FAST)
        return Player(
            id = id,
            name = name,
            hand = handEnum,
            style = styleEnum,
            team = team,
            createdAt = createdAt
        )
    }

    private fun Player.toEntity(): PlayerEntity {
        return PlayerEntity(
            id = id,
            name = name,
            hand = hand.name,
            style = style.name,
            team = team,
            createdAt = createdAt
        )
    }

    private fun SessionEntity.toDomain(): Session {
        val deliveriesList = mutableListOf<Delivery>()
        try {
            val jsonArray = JSONArray(deliveriesJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val conf = runCatching { Confidence.valueOf(obj.getString("confidence")) }.getOrDefault(Confidence.HIGH)
                val angle = runCatching { CameraAngle.valueOf(obj.getString("angle")) }.getOrDefault(CameraAngle.SIDE)

                var bounce: BounceAnalysis? = null
                if (obj.has("bounce") && !obj.isNull("bounce")) {
                    val bObj = obj.getJSONObject("bounce")
                    bounce = BounceAnalysis(
                        releaseToBounceTime = bObj.optDouble("releaseToBounceTime", 0.0),
                        bounceToArrivalTime = bObj.optDouble("bounceToArrivalTime", 0.0),
                        bounceDistance = bObj.optDouble("bounceDistance", 0.0),
                        stumpsDistance = bObj.optDouble("stumpsDistance", 0.0)
                    )
                }

                deliveriesList.add(
                    Delivery(
                        kmh = obj.getDouble("kmh"),
                        mph = obj.getDouble("mph"),
                        time = obj.getDouble("time"),
                        distance = obj.getDouble("distance"),
                        fps = obj.getDouble("fps"),
                        confidence = conf,
                        angle = angle,
                        bounce = bounce,
                        ts = obj.optLong("ts", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
        }
        return Session(
            id = id,
            name = name,
            playerId = playerId,
            createdAt = createdAt,
            deliveries = deliveriesList
        )
    }

    private fun Session.toEntity(): SessionEntity {
        val jsonArray = JSONArray()
        for (d in deliveries) {
            val obj = JSONObject()
            obj.put("kmh", d.kmh)
            obj.put("mph", d.mph)
            obj.put("time", d.time)
            obj.put("distance", d.distance)
            obj.put("fps", d.fps)
            obj.put("confidence", d.confidence.name)
            obj.put("angle", d.angle.name)
            obj.put("ts", d.ts)
            if (d.bounce != null) {
                val bObj = JSONObject()
                bObj.put("releaseToBounceTime", d.bounce.releaseToBounceTime)
                bObj.put("bounceToArrivalTime", d.bounce.bounceToArrivalTime)
                bObj.put("bounceDistance", d.bounce.bounceDistance)
                bObj.put("stumpsDistance", d.bounce.stumpsDistance)
                obj.put("bounce", bObj)
            }
            jsonArray.put(obj)
        }
        return Session(
            id = id,
            name = name,
            playerId = playerId,
            createdAt = createdAt,
            deliveries = deliveries
        ).let {
            SessionEntity(
                id = id,
                name = name,
                playerId = playerId,
                createdAt = createdAt,
                deliveriesJson = jsonArray.toString()
            )
        }
    }
}
