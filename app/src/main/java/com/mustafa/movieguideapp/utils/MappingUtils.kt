package com.mustafa.movieguideapp.utils


/**
 * Mapping Genres' Ids To Strings according to the TheMovie Services
 * try this out : https://api.themoviedb.org/3/genre/movie/list?api_key=<<api_key>>&language=en-US
 *
 */

class StringUtils {
    companion object {
        @JvmStatic
        fun getMovieGenresById(ids: List<Int>): String {
            val listString = arrayListOf<String>()
            for (id in ids) {
                when (id) {
                    12 -> listString.add("Adventure")
                    28 -> listString.add("Action")
                    16 -> listString.add("Animation")
                    35 -> listString.add("Comedy")
                    80 -> listString.add("Crime")
                    99 -> listString.add("Documentary")
                    18 -> listString.add("Drama")
                    14 -> listString.add("Fantasy")
                    36 -> listString.add("History")
                    27 -> listString.add("Horror")
                    878 -> listString.add("Science Fiction")
                    37 -> listString.add("Western")
                    53 -> listString.add("Thriller")
                    10752 -> listString.add("War")
                    10770 -> listString.add("TV Movie")
                    10402 -> listString.add("Music")
                    9648 -> listString.add("Mystery")
                    10749 -> listString.add("Romance")
                    10751 -> listString.add("Family")
                }
            }

            return listString.joinToString()
        }

        @JvmStatic
        fun getTvGenresById(ids: List<Int>): String {
            val listString = arrayListOf<String>()
            for (id in ids) {
                when (id) {
                    10759 -> listString.add("Action & Adventure")
                    10762 -> listString.add("Kids")
                    16 -> listString.add("Animation")
                    35 -> listString.add("Comedy")
                    80 -> listString.add("Crime")
                    99 -> listString.add("Documentary")
                    18 -> listString.add("Drama")
                    10763 -> listString.add("News")
                    10764 -> listString.add("Reality")
                    10765 -> listString.add("Sci-Fi & Fantasy")
                    37 -> listString.add("Western")
                    10766 -> listString.add("Soap")
                    10767 -> listString.add("Talk")
                    10768 -> listString.add("War & Politics")
                    9648 -> listString.add("Mystery")
                    10751 -> listString.add("Family")
                }
            }

            return listString.joinToString()
        }


        @JvmStatic
        fun getMovieGenresAsSeparatedString(genres: List<String>?): String {
            val listGenresInteger = arrayListOf<Int>()
            if (genres != null) {
                for (genre in genres) {
                    when (genre) {
                        "Adventure" -> listGenresInteger.add(12)
                        "Action" -> listGenresInteger.add(28)
                        "Animation" -> listGenresInteger.add(16)
                        "Comedy" -> listGenresInteger.add(35)
                        "Crime" -> listGenresInteger.add(80)
                        "Documentary" -> listGenresInteger.add(99)
                        "Drama" -> listGenresInteger.add(18)
                        "Fantasy" -> listGenresInteger.add(14)
                        "History" -> listGenresInteger.add(36)
                        "Horror" -> listGenresInteger.add(27)
                        "Science Fiction" -> listGenresInteger.add(878)
                        "Western" -> listGenresInteger.add(37)
                        "Thriller" -> listGenresInteger.add(53)
                        "War" -> listGenresInteger.add(10752)
                        "TV Movie" -> listGenresInteger.add(10770)
                        "Music" -> listGenresInteger.add(10402)
                        "Mystery" -> listGenresInteger.add(9648)
                        "Romance" -> listGenresInteger.add(10749)
                        "Family" -> listGenresInteger.add(10751)
                    }
                }
            }

            val stringGenres = listGenresInteger.map {
                it.toString()
            }

            return stringGenres.joinToString()
        }


        @JvmStatic
        fun formatReleaseDate(date: String?): String {
            if (!date.isNullOrEmpty()) {
                return "Release: $date"
            }
            return ""
        }

        @JvmStatic
        fun getISOLanguage(language: String?): String {
            when (language) {
                "German" -> return "de"
                "French" -> return "fr"
                "English" -> return "en"
                "Spanish" -> return "es"
                "Japanese" -> return "ja"
                "Chinese" -> return "zh"
                "Italian" -> return "it"
                "Russian" -> return "ru"
            }
            return "en"
        }

        @JvmStatic
        fun getISORegion(region: String?): String {
            region?.let {
                return when (region) {
                    "United State" -> "US"
                    "Germany" -> "DE"
                    "United Kingdom" -> "GB"
                    "France" -> "FR"
                    "Canada" -> "CA"
                    "Spain" -> "ES"
                    "Japan" -> "JP"
                    "China" -> "CN"
                    "Italy" -> "IT"
                    "Russia" -> "RU"
                    else -> "US"
                }
            }
            return "US"
        }


        @JvmStatic
        fun mapRunTime(runtime: String?): Int {
            runtime?.let {
                return when (runtime) {
                    "1 hour or more" -> 60
                    "2 hours or more" -> 120
                    "3 hours or more" -> 180
                    "4 hours or more" -> 240
                    else -> 120
                }
            }
            return 120
        }


        @JvmStatic
        fun mapKeywordsToSeparatedIds(languages: List<String>?): String {
            val keywordIds = ArrayList<String>()
            if (languages != null) {
                for (language in languages) {
                    when (language) {
                        "Based on Real Events" -> keywordIds.add("260728")
                        "Based on nNovel" -> keywordIds.add("260723")
                        "Anime" -> keywordIds.add("210024")
                        "Cult Film" -> keywordIds.add("34117")
                        "Superhero" -> keywordIds.add("9715")
                        "Zombie" -> keywordIds.add("12377")
                        "Time Travel" -> keywordIds.add("4379")
                        "Kidnapping" -> keywordIds.add("1930")
                        "High School" -> keywordIds.add("6270")
                        "Haunting" -> keywordIds.add("10224")
                        "Epic" -> keywordIds.add("6917")
                        "Bank Robbery" -> keywordIds.add("15363")
                        "Alien Invasion" -> keywordIds.add("14909")
                        "Action Hero" -> keywordIds.add("219404")
                    }
                }
            }
            return keywordIds.joinToString()
        }
    }
}