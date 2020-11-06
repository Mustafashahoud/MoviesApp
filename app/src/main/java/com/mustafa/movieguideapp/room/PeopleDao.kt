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

    @Query("SELECT * FROM people WHERE id = :id_")
    abstract suspend fun getPerson(id_: Int): Person?

    @Query("SELECT * FROM People  WHERE page in (:pages) AND search = 0 AND profile_path <> '' order by page ")
    abstract suspend fun loadPeopleList(pages: List<Int>): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPeople(people: List<Person>)

    @Update
    abstract suspend fun updatePerson(person: Person?)

    @Query("SELECT * FROM SearchPeopleResult WHERE `query` = :query AND page = :pageNumber ")
    abstract suspend fun searchPeopleResult(query: String, pageNumber: Int): SearchPeopleResult?

    @Query("SELECT * FROM People WHERE id in (:peopleIds) AND search = 1 AND profile_path <> '' order by page")
    abstract suspend fun loadSearchPeopleList(peopleIds: List<Int>): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPeopleRecentQuery(insertPeopleRecentQuery: PeopleRecentQueries)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSearchPeopleResult(result: SearchPeopleResult)

    // Movie Person
    @Query("SELECT * FROM MoviePersonResult WHERE personId = :personId")
    abstract suspend fun getMoviePersonResultByPersonId(personId: Int): MoviePersonResult?

    @Query("SELECT * FROM MoviePerson WHERE id in (:movieIds)  AND poster_path <> ''")
    abstract suspend fun loadMoviesForPerson(movieIds: List<Int>): List<MoviePerson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMovieForPerson(movies: List<MoviePerson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMoviePersonResult(result: MoviePersonResult)

    //  Tv person
    @Query("SELECT * FROM TvPerson WHERE id in (:tvIds)  AND poster_path <> ''")
    abstract suspend fun loadTvsForPerson(tvIds: List<Int>): List<TvPerson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTvPersonResult(result: TvPersonResult)

    @Query("SELECT * FROM TvPersonResult WHERE personId = :personId")
    abstract suspend fun getTvPersonResultByPersonId(personId: Int): TvPersonResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTvForPerson(tvs: List<TvPerson>)

    @Query("SELECT * FROM PeopleRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30 ")
    abstract suspend fun loadPeopleRecentQueries(): List<PeopleRecentQueries>

    @Query("DELETE FROM PeopleRecentQueries")
    abstract suspend fun deleteAllPeopleRecentQueries()

    // Search people Suggestion
    @Query("SELECT * FROM People JOIN peopleSuggestionsFts ON People.id == peopleSuggestionsFts.id WHERE peopleSuggestionsFts.name MATCH '%' || :text || '%' AND profile_path <> '' LIMIT 20")
    abstract suspend fun loadPeopleSuggestions(text: String): List<Person>

//    fun loadPeopleListOrdered(peopleIds: List<Int>): LiveData<List<Person>> {
//        val order = SparseArrayCompat<Int>() // SparseArrayCompat can be used
//        peopleIds.withIndex().forEach {
//            order.put(it.value, it.index)
//        }
//        return Transformations.map(
//            loadPeopleList(peopleIds)
//        ) { people ->
//            people.sortedWith(compareBy { order.get(it.id) })
//        }
//    }



//    fun loadSearchPeopleListOrdered(peopleIds: List<Int>): LiveData<List<Person>> {
//        val order = SparseIntArray() // SparseArrayCompat can be used .. but it would need mocking
//        peopleIds.withIndex().forEach {
//            order.put(it.value, it.index)
//        }
//        return Transformations.map(loadSearchPeopleList(peopleIds)) { people ->
//            people.sortedWith(compareBy { order.get(it.id) })
//        }
//    }

}
