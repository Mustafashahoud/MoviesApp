package com.mustafa.movieguideapp.utils

class FiltersConstants {
    companion object {
        const val RUNTIME = "RUNTIME"
        const val RATINGS = "RATINGS"
        const val KEYWORDS = "KEYWORDS"
        const val LANGUAGES = "LANGUAGES"
        const val YEARS = "YEARS"
        const val GENRES = "GENRES"
        const val COUNTRIES = "COUNTRIES"

        val ratingFilters = listOf("+9", "+8", "+7", "+6", "+5", "+4")

        val runtimeFilters =
            listOf("1 hour or more", "2 hours or more", "3 hours or more", "4 hours or more")

        val languageFilters = listOf(
            "English", "French", "German", "Spanish", "Chinese",
            "Italian", "Russian", "Japanese"
        )

        val genreFilters = listOf(
            "Adventure",
            "Crime",
            "Drama",
            "History",
            "Thriller",
            "Romance",
            "Comedy",
            "Family",
            "War",
            "Horror",
            "Western",
            "Science Fiction",
            "Fantasy",
            "Documentary",
            "Animation"
        )

        val countryFilters = listOf(
            "United States", "Canada", "Germany", "France", "United Kingdom",
            "Spain", "Italy", "India", "Japan"
        )

        val keywordFilters = listOf(
            "Anim", "Superhero", "Bank Robbery", "Based on Novel", "Based on Play",
            "Based on True Story", "Kidnapping", "Cult Film", "High School", "Time Travel", "Zombie"
        )

        val yearFilters = listOf(
            "2020", "2019", "2018", "2017", "2016",
            "2015", "2014", "2013", "2012", "2011", "2010|Before"
        )
    }
}