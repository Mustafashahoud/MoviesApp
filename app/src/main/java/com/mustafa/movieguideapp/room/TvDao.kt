package com.mustafa.movieguideapp.room

import androidx.room.*
import com.mustafa.movieguideapp.models.entity.FilteredTvResult
import com.mustafa.movieguideapp.models.entity.SearchTvResult
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.models.entity.TvRecentQueries

@Dao
abstract class TvDao {
    @Query("SELECT * FROM Tv WHERE page in (:pages)  AND search = 0 order by page")
    abstract suspend fun loadDiscoveryTvListByPage(pages: List<Int>): List<Tv>

    @Query("SELECT * FROM Tv WHERE id in (:ids) AND search = 1 AND poster_path <> '' order by page ")
    abstract suspend fun loadSearchTvsList(ids: List<Int>): List<Tv>

    @Query("SELECT * FROM SearchTvResult WHERE `query` = :query AND pageNumber = :page")
    abstract suspend fun searchTvResult(query: String, page: Int): SearchTvResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTvList(tvs: List<Tv>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTvRecentQuery(insertTvRecentQuery: TvRecentQueries)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSearchTvResult(result: SearchTvResult)

    @Query("SELECT * FROM FilteredTvResult WHERE page = :pageNumber")
    abstract suspend fun getFilteredTvResultByPage(pageNumber: Int): FilteredTvResult?

    @Query("SELECT * FROM Tv WHERE id in (:tvIds) AND search = 0 AND filter = 1 AND poster_path <> '' ORDER BY page")
    abstract suspend fun loadFilteredTvList(tvIds: List<Int>): List<Tv>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFilteredTvResult(result: FilteredTvResult)

    @Update
    abstract suspend fun updateTv(tv: Tv)

    @Query("SELECT * FROM Tv WHERE id = :id_")
    abstract suspend fun getTv(id_: Int): Tv

    @Query("SELECT * FROM TvRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30 ")
    abstract suspend fun loadTvRecentQueries(): List<TvRecentQueries>

    @Query("DELETE FROM TvRecentQueries")
    abstract suspend fun deleteAllTvRecentQueries()

    @Query("SELECT * FROM Tv JOIN tvSuggestionsFts ON Tv.id == tvSuggestionsFts.id WHERE tvSuggestionsFts.name MATCH '%' || :text || '%' LIMIT 20")
    abstract suspend fun loadTvSuggestions(text: String): List<Tv>

//    fun loadDiscoveryTvListOrdered(tvIds: List<Int>): LiveData<List<Tv>> {
//        val order = SparseArrayCompat<Int>() // SparseIntArray can be used
//        tvIds.withIndex().forEach {
//            order.put(it.value, it.index)
//        }
//        return Transformations.map(
//            loadDiscoveryTvList(tvIds)
//        ) { tvs ->
//            tvs.sortedWith(compareBy { order.get(it.id) })
//        }
//    }

//  fun loadSearchTvListOrdered(tvIds: List<Int>): LiveData<List<Tv>> {
//    val order = SparseIntArray() // SparseArrayCompat can be used
//    tvIds.withIndex().forEach {
//      order.put(it.value, it.index)
//    }
//    return Transformations.map(
//      loadSearchTvList(tvIds)) { tvs ->
//      tvs.sortedWith(compareBy { order.get(it.id) })
//    }
//  }
}
