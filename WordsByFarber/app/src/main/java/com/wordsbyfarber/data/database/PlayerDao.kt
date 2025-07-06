package com.wordsbyfarber.data.database

// Room DAO for accessing and manipulating player data in the database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Query("SELECT * FROM players ORDER BY elo DESC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE given LIKE '%' || :searchQuery || '%' ORDER BY elo DESC")
    fun searchPlayers(searchQuery: String): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE uid = :uid LIMIT 1")
    suspend fun getPlayerById(uid: Int): PlayerEntity?

    @Query("SELECT COUNT(*) FROM players")
    suspend fun getPlayerCount(): Int

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}