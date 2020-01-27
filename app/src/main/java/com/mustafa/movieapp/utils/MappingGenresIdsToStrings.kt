package com.mustafa.movieapp.utils


/**
 * Mapping Genres' Ids To Strings according to the TheMovie Services
 * try out this : https://api.themoviedb.org/3/genre/movie/list?api_key=<<api_key>>&language=en-US
 *
 */

class StringUtils {
    companion object {
        @JvmStatic
        fun getMovieGenresById(ids: List<Int>): String {
            val listString = arrayListOf<String>()
            for (id in ids){
                when (id) {
                    12 ->  listString.add("Adventure")
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
            for (id in ids){
                when (id) {
                    10759 ->  listString.add("Action & Adventure")
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
        fun formatReleaseDate(date: String?): String {
            if (!date.isNullOrEmpty()) {
                return "Release: $date"
            }
            return ""
        }
    }
}