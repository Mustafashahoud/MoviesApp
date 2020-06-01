//package com.mustafa.movieapp.view.ui.common
//
//import android.content.Context
//import android.database.Cursor
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.cursoradapter.widget.SimpleCursorAdapter
//import androidx.databinding.DataBindingComponent
//import androidx.databinding.DataBindingUtil
//import com.mustafa.movieapp.R
//import com.mustafa.movieapp.databinding.SuggestionSearchItemBinding
//import com.mustafa.movieapp.models.entity.Movie
//
//
//class SuggestionsAdapter(
//    contextIn: Context?,
//    private val dataBindingComponent: DataBindingComponent,
//    layoutIn: Int,
//    cursorIn : Cursor?,
//    fromArray: Array<String>?,
//    toArray: IntArray?,
//    flags: Int)
//    : SimpleCursorAdapter(contextIn, layoutIn, cursorIn, fromArray, toArray, flags) {
//
//    lateinit  var binding : SuggestionSearchItemBinding
//
//    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
//        val id = cursor?.getString(0)
//        val title = cursor?.getString(1)
//        val posterPath = cursor?.getString(2)
//        val voteAverage = cursor?.getString(3)
//        val genres = ""
//        val releaseDate = cursor?.getString(5)
//        val movie = Movie(id?.toInt()!!, title!!, posterPath, voteAverage?.toFloat()!!, releaseDate)
////        movie.genre_ids = genres!!
//
//        val binding = DataBindingUtil.getBinding<SuggestionSearchItemBinding>(view!!)
//        binding?.movie = movie
//    }
//
//
//    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
//        binding = DataBindingUtil.inflate<SuggestionSearchItemBinding>(
//            LayoutInflater.from(parent?.context),
//            R.layout.suggestion_search_item,
//            parent,
//            false,
//            dataBindingComponent
//        )
//        return binding.root
//    }
//
//
//
//
////    private fun convertCommaSeparatedStringToList(commaSeparatedString: String?): List<Int>? {
////        if (commaSeparatedString?.length == 1) {
////            val list = ArrayList<Int>()
////            list.add(commaSeparatedString.toInt())
////            return list
////        }
////        return commaSeparatedString?.split(",")?.map { it.trim().toInt() }
////    }
//
//
//}