
package com.mustafa.movieapp.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mustafa.movieapp.room.AppDatabase
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class DbTest {
  lateinit var db: AppDatabase

  @Before
  fun initDB() {
    db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
        AppDatabase::class.java).build()
  }

  @After
  fun closeDB() {
    db.close()
  }
}
