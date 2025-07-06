package com.wordsbyfarber.data.database

// Room entity representing a player with statistics and profile information
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val uid: Int,
    val elo: Int,
    val motto: String?,
    val given: String,
    val photo: String,
    val lat: Double,
    val lng: Double,
    val avgTime: String,
    val avgScore: Double
)