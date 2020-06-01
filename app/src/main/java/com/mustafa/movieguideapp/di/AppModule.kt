package com.mustafa.movieguideapp.di

import android.app.Application
import androidx.annotation.NonNull
import androidx.room.Room
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mustafa.movieguideapp.api.*
import com.mustafa.movieguideapp.room.AppDatabase
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.room.TvDao
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@NonNull application: Application): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, "Movie.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(@NonNull database: AppDatabase): MovieDao {
        return database.movieDao()
    }

    @Provides
    @Singleton
    fun provideTvDao(@NonNull database: AppDatabase): TvDao {
        return database.tvDao()
    }

    @Provides
    @Singleton
    fun providePeopleDao(@NonNull database: AppDatabase): PeopleDao {
        return database.peopleDao()
    }


    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(@NonNull okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.themoviedb.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideDiscoverService(@NonNull retrofit: Retrofit): TheDiscoverService {
        return retrofit.create(TheDiscoverService::class.java)
    }

    @Provides
    @Singleton
    fun providePeopleService(@NonNull retrofit: Retrofit): PeopleService {
        return retrofit.create(PeopleService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieService(@NonNull retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)
    }

    @Provides
    @Singleton
    fun provideTvService(@NonNull retrofit: Retrofit): TvService {
        return retrofit.create(TvService::class.java)
    }
}