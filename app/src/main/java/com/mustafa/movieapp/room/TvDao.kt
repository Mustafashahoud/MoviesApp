
package com.mustafa.movieapp.room

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mustafa.movieapp.models.entity.DiscoveryTvResult
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.Tv

@Dao
abstract class TvDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertTv(tvs: List<Tv>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertTvList(tvs: List<Tv>)

  @Update
  abstract fun updateTv(tv: Tv)

  @Query("SELECT * FROM Tv WHERE id = :id_")
  abstract fun getTv(id_: Int): Tv

  @Query("SELECT * FROM Tv WHERE page = :page_")
  abstract fun getTvList(page_: Int): LiveData<List<Tv>>

  @Query("SELECT * FROM DiscoveryTvResult WHERE page = :pageNumber")
  abstract fun getDiscoveryTvResultByPage(pageNumber: Int): DiscoveryTvResult

  @Query("SELECT * FROM DiscoveryTvResult WHERE page = :pageNumber")
  abstract fun getDiscoveryTvResultByPageLiveData(pageNumber: Int): LiveData<DiscoveryTvResult>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertDiscoveryTvResult(result: DiscoveryTvResult)

  @Query("SELECT * FROM Tv WHERE id in (:tvIds) AND search = 0 ")
  abstract fun loadDiscoveryTvList(tvIds: List<Int>): LiveData<List<Tv>>

  fun loadDiscoveryMovieListOrdered(tvIds: List<Int>): LiveData<List<Tv>> {
    val order = SparseIntArray() // SparseArrayCompat can be used
    tvIds.withIndex().forEach {
      order.put(it.value, it.index)
    }
    return Transformations.map(
      loadDiscoveryTvList(tvIds)) { tvs ->
      tvs.sortedWith(compareBy { order.get(it.id) })
    }
  }


}
