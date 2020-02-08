package com.mustafa.movieapp.di

import com.mustafa.movieapp.view.ui.movies.search.SearchFragment
import com.mustafa.movieapp.view.ui.movies.moviedetail.MovieDetailFragment
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListFragment
import com.mustafa.movieapp.view.ui.movies.search.filter.SearchResultFilterFragment
import com.mustafa.movieapp.view.ui.movies.search.result.SearchResultFragment
import com.mustafa.movieapp.view.ui.person.CelebritiesListFragment
import com.mustafa.movieapp.view.ui.person.CelebrityDetailFragment
import com.mustafa.movieapp.view.ui.tv.TvDetailFragment
import com.mustafa.movieapp.view.ui.tv.TvListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMovieListFragment(): MovieListFragment

    @ContributesAndroidInjector
    abstract fun contributeMovieDetailFragment(): MovieDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeTvDetailFragment(): TvDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeTvListFragment(): TvListFragment

    @ContributesAndroidInjector
    abstract fun contributeCelebritiesListFragment(): CelebritiesListFragment

    @ContributesAndroidInjector
    abstract fun contributeCelebrityDetailFragment(): CelebrityDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeMoviesSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchResultFragment(): SearchResultFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchResultFilterFragment(): SearchResultFilterFragment





}