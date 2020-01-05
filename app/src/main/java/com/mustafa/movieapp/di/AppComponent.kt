package com.mustafa.movieapp.di

import android.app.Application
import com.mustafa.movieapp.MovieApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule ::class,
    MainActivityModule::class,
    AppModule::class])
interface AppComponent {

    fun inject(movieApp: MovieApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }


}
