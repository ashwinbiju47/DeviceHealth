package com.example.devicehealth.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TipDao {

    @Query("SELECT * FROM tips ORDER BY id DESC")
    fun getTips(): Flow<List<Tip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<Tip>)
}
