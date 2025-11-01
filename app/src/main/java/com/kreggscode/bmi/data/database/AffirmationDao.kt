package com.kreggscode.bmi.data.database

import androidx.room.*
import com.kreggscode.bmi.data.model.Affirmation
import kotlinx.coroutines.flow.Flow

@Dao
interface AffirmationDao {
    @Query("SELECT * FROM affirmations ORDER BY createdAt DESC")
    fun getAllAffirmations(): Flow<List<Affirmation>>

    @Query("SELECT * FROM affirmations WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteAffirmations(): Flow<List<Affirmation>>

    @Query("SELECT * FROM affirmations WHERE category = :category ORDER BY RANDOM()")
    fun getAffirmationsByCategory(category: String): Flow<List<Affirmation>>

    @Query("SELECT * FROM affirmations ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomAffirmation(): Affirmation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAffirmation(affirmation: Affirmation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(affirmations: List<Affirmation>)

    @Update
    suspend fun updateAffirmation(affirmation: Affirmation)

    @Delete
    suspend fun deleteAffirmation(affirmation: Affirmation)

    @Query("UPDATE affirmations SET isFavorite = :isFavorite WHERE id = :affirmationId")
    suspend fun toggleFavorite(affirmationId: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM affirmations")
    suspend fun getCount(): Int
}

