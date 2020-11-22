
package com.mustafa.movieguideapp.room

import android.util.SparseIntArray
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mustafa.movieguideapp.models.entity.*

@Dao
abstract class TvDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertTv(tvs: List<Tv>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertTvList(tvs: List<Tv>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertTvRecentQuery(insertTvRecentQuery: TvRecentQueries)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertSearchTvResult(result: SearchTvResult)

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

  @Query("SELECT * FROM FilteredTvResult WHERE page = :pageNumber")
  abstract fun getFilteredTvResultByPageLiveData(pageNumber: Int): LiveData<FilteredTvResult>


  @Query("SELECT * FROM FilteredTvResult WHERE page = :pageNumber")
  abstract fun getFilteredTvResultByPage(pageNumber: Int): FilteredTvResult

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertFilteredTvResult(result: FilteredTvResult)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertDiscoveryTvResult(result: DiscoveryTvResult)

  @Query("SELECT * FROM SearchTvResult WHERE `query` = :query AND pageNumber = :pageNumber ")
  abstract fun searchTvResultLiveData(
    query: String,
    pageNumber: Int
  ): LiveData<SearchTvResult>

  @Query("SELECT * FROM Tv WHERE id in (:tvIds) AND search = 0 ")
  abstract fun loadDiscoveryTvList(tvIds: List<Int>): LiveData<List<Tv>>

  fun loadDiscoveryTvListOrdered(tvIds: List<Int>): LiveData<List<Tv>> {
    val order = SparseArrayCompat<Int>() // SparseIntArray can be used
    tvIds.withIndex().forEach {
      order.put(it.value, it.index)
    }
    return Transformations.map(
      loadDiscoveryTvList(tvIds)) { tvs ->
      tvs.sortedWith(compareBy { order.get(it.id) })
    }
  }

  @Query(
    "SELECT * FROM TvRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30 ")
  abstract fun loadTvRecentQueries(): LiveData<List<TvRecentQueries>>

  @Query("DELETE FROM TvRecentQueries")
  abstract fun deleteAllTvRecentQueries()



  @Query("SELECT * FROM SearchTvResult WHERE `query` = :query AND pageNumber = :pageNumber ")
  abstract fun searchTvResult(query: String, pageNumber: Int): SearchTvResult

  @Query("SELECT * FROM Tv WHERE id in (:tvIds) AND search = 1 AND poster_path <> '' ")
  abstract fun loadSearchTvList(tvIds: List<Int>): LiveData<List<Tv>>

  @Query("SELECT * FROM Tv JOIN tvSuggestionsFts ON Tv.id == tvSuggestionsFts.id WHERE tvSuggestionsFts.name MATCH '%' || :text || '%' LIMIT 20" )
  abstract fun loadTvSuggestions(text: String): LiveData<List<Tv>>

  fun loadSearchTvListOrdered(tvIds: List<Int>): LiveData<List<Tv>> {
    val order = SparseIntArray() // SparseArrayCompat can be used
    tvIds.withIndex().forEach {
      order.put(it.value, it.index)
    }
    return Transformations.map(
      loadSearchTvList(tvIds)) { tvs ->
      tvs.sortedWith(compareBy { order.get(it.id) })
    }
  }
}
