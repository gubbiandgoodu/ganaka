package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculations ORDER BY id ASC")
    fun getAllCalculations(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculations WHERE mode = :mode ORDER BY id ASC")
    fun getCalculationsByMode(mode: String): Flow<List<CalculationEntity>>

    @Query("SELECT COUNT(*) FROM calculations")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: CalculationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(calculations: List<CalculationEntity>)

    @androidx.room.Update
    suspend fun updateCalculation(calculation: CalculationEntity)

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calculations WHERE mode = :mode")
    suspend fun clearMode(mode: String)

    @Query("DELETE FROM calculations")
    suspend fun clearAll()

    @Query("DELETE FROM calculations WHERE expression IN ('100', '200', '100+300', '300+3', '86x9')")
    suspend fun removeTestCalculations()
}
