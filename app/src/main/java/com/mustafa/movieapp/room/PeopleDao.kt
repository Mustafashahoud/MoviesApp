
package com.mustafa.movieapp.room

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mustafa.movieapp.models.entity.*

@Dao
abstract class PeopleDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertPeople(people: List<Person>)

  @Update
  abstract fun updatePerson(person: Person)

  @Query("SELECT * FROM people WHERE id = :id_")
  abstract fun getPerson(id_: Int): Person

  @Query("SELECT * FROM People WHERE page = :page_")
  abstract fun getPeople(page_: Int): LiveData<List<Person>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertPeopleResult(result: PeopleResult)

  @Query("SELECT * FROM PeopleResult WHERE page = :pageNumber")
  abstract fun getPeopleResultByPage(pageNumber: Int): PeopleResult

  @Query("SELECT * FROM PeopleResult WHERE page = :pageNumber")
  abstract fun getPeopleResultByPageLiveData(pageNumber: Int): LiveData<PeopleResult>

  @Query("SELECT * FROM People WHERE id in (:personIds)")
  abstract fun loadPeopleList(personIds: List<Int>): LiveData<List<Person>>


  fun loadFilteredTvListOrdered(tvIds: List<Int>): LiveData<List<Person>> {
    val order = SparseIntArray() // SparseArrayCompat can be used
    tvIds.withIndex().forEach {
      order.put(it.value, it.index)
    }
    return Transformations.map(
      loadPeopleList(tvIds)) { people ->
      people.sortedWith(compareBy { order.get(it.id) })
    }
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertMovieForPerson(movies: List<MoviePerson>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertMoviePersonResult(result: MoviePersonResult)

  @Query("SELECT * FROM MoviePersonResult WHERE personId = :personId")
  abstract fun getMoviePersonResultByPersonIdLiveData(personId: Int): LiveData<MoviePersonResult>

  @Query("SELECT * FROM MoviePerson WHERE id in (:movieIds)")
  abstract fun loadMoviesForPerson(movieIds: List<Int>): LiveData<List<MoviePerson>>


  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertTvForPerson(tvs: List<TvPerson>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertTvPersonResult(result: TvPersonResult)

  @Query("SELECT * FROM TvPersonResult WHERE personId = :personId")
  abstract fun getTvPersonResultByPersonIdLiveData(personId: Int): LiveData<TvPersonResult>

  @Query("SELECT * FROM TvPerson WHERE id in (:tvIds)")
  abstract fun loadTvsForPerson(tvIds: List<Int>): LiveData<List<TvPerson>>
}
