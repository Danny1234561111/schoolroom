package ru.dolbak.roomhomework

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ResultsDao {
    @Query("SELECT * FROM results ORDER BY :order")
    fun getAll(order: String): LiveData<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg result: ResultEntity)

    @Query("DELETE FROM results WHERE name LIKE '%' || :substring || '%'")
    suspend fun deleteByNameContaining(substring: String) //Removed the gettable object

    @Update
    suspend fun update(vararg result: ResultEntity)

    @Query("SELECT * FROM results WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): ResultEntity?
}