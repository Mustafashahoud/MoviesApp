package com.mustafa.movieguideapp.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.utils.LiveDataTestUtil
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockTv
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TvDaoTest : DbTest() {

  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndRead() {
        val tvList = ArrayList<Tv>()
        val tv = mockTv()
        tvList.add(tv)

        db.tvDao().insertTv(tvList)
        val loadFromDB = LiveDataTestUtil.getValue(db.tvDao().getTvList(tv.page))[0]
        assertThat(loadFromDB.page, `is`(1))
        assertThat(loadFromDB.id, `is`(123))
    }

    @Test
    fun updateAndReadTest() {
        val tvList = ArrayList<Tv>()
        val tv = mockTv()
        tvList.add(tv)
        db.tvDao().insertTv(tvList)

        val loadFromDB = db.tvDao().getTv(tv.id)
        assertThat(loadFromDB.page, `is`(1))

        tv.page = 10
        db.tvDao().updateTv(tv)

        val updated = db.tvDao().getTv(tv.id)
        assertThat(updated.page, `is`(10))
    }
}
