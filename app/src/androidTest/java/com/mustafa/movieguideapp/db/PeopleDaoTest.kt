package com.mustafa.movieguideapp.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.movieguideapp.utils.LiveDataTestUtil.getValue
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockPerson
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PeopleDaoTest : DbTest() {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndRead() {
        //Given - insert
        val people = listOf(mockPerson())
        db.peopleDao().insertPeople(people)

        // When
        val loadFromDB = getValue(db.peopleDao().loadPeopleList(listOf(123)))[0]

        //// THEN - The loaded data contains the expected values
        assertThat(loadFromDB.page, `is`(1))
        assertThat(loadFromDB.id, `is`(123))
    }

    @Test
    fun updateAndRead() {

        val mockedPerson = mockPerson()
        val people = listOf(mockedPerson)
        db.peopleDao().insertPeople(people)

        val loadFromDB = db.peopleDao().getPerson(mockedPerson.id)
        assertThat(loadFromDB.page, `is`(1))

        mockedPerson.page = 10
        db.peopleDao().updatePerson(mockedPerson)

        val updated = db.peopleDao().getPerson(mockedPerson.id)
        assertThat(updated.page, `is`(10))
    }
}