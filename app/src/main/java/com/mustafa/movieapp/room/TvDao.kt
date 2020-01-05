
package com.mustafa.movieapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mustafa.movieapp.models.entity.Tv

@Dao
interface TvDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTv(tvs: List<Tv>)

  @Update
  fun updateTv(tv: Tv)

  @Query("SELECT * FROM Tv WHERE id = :id_")
  fun getTv(id_: Int): Tv

  @Query("SELECT * FROM Tv WHERE page = :page_")
  fun getTvList(page_: Int): LiveData<List<Tv>>
}
