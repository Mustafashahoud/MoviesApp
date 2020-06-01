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
abstract class PeopleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPeople(people: List<Person>)

    @Update
    abstract fun updatePerson(person: Person)

    @Query("SELECT * FROM people WHERE id = :id_")
    abstract fun getPerson(id_: Int): Person

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPeopleResult(result: PeopleResult)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertSearchPeopleResult(result: SearchPeopleResult)

    @Query("SELECT * FROM PeopleResult WHERE page = :pageNumber")
    abstract fun getPeopleResultByPage(pageNumber: Int): PeopleResult

    @Query("SELECT * FROM PeopleResult WHERE page = :pageNumber")
    abstract fun getPeopleResultByPageLiveData(pageNumber: Int): LiveData<PeopleResult>

    @Query("SELECT * FROM People WHERE id in (:personIds) AND search = 0 AND profile_path <> '' ")
    abstract fun loadPeopleList(personIds: List<Int>): LiveData<List<Person>>


    fun loadPeopleListOrdered(peopleIds: List<Int>): LiveData<List<Person>> {
        val order = SparseArrayCompat<Int>() // SparseArrayCompat can be used
        peopleIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(
            loadPeopleList(peopleIds)
        ) { people ->
            people.sortedWith(compareBy { order.get(it.id) })
        }
    }

    // Get Movies for a Person/Celebrity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovieForPerson(movies: List<MoviePerson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMoviePersonResult(result: MoviePersonResult)

    @Query("SELECT * FROM MoviePersonResult WHERE personId = :personId")
    abstract fun getMoviePersonResultByPersonIdLiveData(personId: Int): LiveData<MoviePersonResult>

    @Query("SELECT * FROM MoviePerson WHERE id in (:movieIds)  AND poster_path <> ''")
    abstract fun loadMoviesForPerson(movieIds: List<Int>): LiveData<List<MoviePerson>>

    // Get tvs for a Person/Celebrity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTvForPerson(tvs: List<TvPerson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTvPersonResult(result: TvPersonResult)

    @Query("SELECT * FROM TvPersonResult WHERE personId = :personId")
    abstract fun getTvPersonResultByPersonIdLiveData(personId: Int): LiveData<TvPersonResult>

    @Query("SELECT * FROM TvPerson WHERE id in (:tvIds)  AND poster_path <> ''")
    abstract fun loadTvsForPerson(tvIds: List<Int>): LiveData<List<TvPerson>>

    // Search People/Celebrities

    @Query("SELECT * FROM SearchPeopleResult WHERE `query` = :query AND page = :pageNumber ")
    abstract fun searchPeopleResultLiveData(
        query: String,
        pageNumber: Int
    ): LiveData<SearchPeopleResult>

    @Query("SELECT * FROM SearchPeopleResult WHERE `query` = :query AND page = :pageNumber ")
    abstract fun searchPeopleResult(query: String, pageNumber: Int): SearchPeopleResult

    @Query("SELECT * FROM People WHERE id in (:peopleIds) AND search = 1 AND profile_path <> ''")
    abstract fun loadSearchPeopleList(peopleIds: List<Int>): LiveData<List<Person>>

    fun loadSearchPeopleListOrdered(peopleIds: List<Int>): LiveData<List<Person>> {
        val order = SparseIntArray() // SparseArrayCompat can be used .. but it would need mocking
        peopleIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(loadSearchPeopleList(peopleIds)) { people ->
            people.sortedWith(compareBy { order.get(it.id) })
        }
    }

    //  Recent Search Query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPeopleRecentQuery(insertPeopleRecentQuery: PeopleRecentQueries)

    @Query("SELECT * FROM PeopleRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30 ")
    abstract fun loadPeopleRecentQueries(): LiveData<List<PeopleRecentQueries>>

    @Query("DELETE FROM PeopleRecentQueries")
    abstract fun deleteAllPeopleRecentQueries()

    // Search people Suggestion
    @Query("SELECT * FROM People JOIN peopleSuggestionsFts ON People.id == peopleSuggestionsFts.id WHERE peopleSuggestionsFts.name MATCH '%' || :text || '%' AND profile_path <> '' LIMIT 20" )
    abstract fun loadPeopleSuggestions(text: String): LiveData<List<Person>>
}
