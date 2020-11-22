//package com.mustafa.movieguideapp.repository.tvs
//
//import androidx.paging.PagingSource
//import com.mustafa.movieguideapp.api.TheDiscoverService
//import com.mustafa.movieguideapp.models.Tv
//import com.mustafa.movieguideapp.models.FilterData
//import com.mustafa.movieguideapp.testing.OpenForTesting
//import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
//import retrofit2.HttpException
//import java.io.IOException
//import javax.inject.Inject
//
//@OpenForTesting
//class FilteredTvsPagingSource @Inject constructor(
//    private val service: TheDiscoverService,
//    private val filterData: FilterData,
//    private val totalCount: (Int) -> Unit
//) : PagingSource<Int, Tv>() {
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tv> {
//        return try {
//            val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
//            val response = service.searchTvFilters(
//                page = currentLoadingPageKey,
//                rating = filterData.rating,
//                sort = filterData.sort,
//                with_genres = filterData.genres,
//                with_keywords = filterData.keywords,
//                with_original_language = filterData.language,
//                with_runtime = filterData.runtime,
//                year = filterData.year
//            )
//
//            totalCount(response.total_results)
//
//            val tvs = response.results
//
//
//            LoadResult.Page(
//                data = tvs,
//                prevKey = if (currentLoadingPageKey == TMDB_STARTING_PAGE_INDEX) null else currentLoadingPageKey - 1,
//                nextKey = if (tvs.isEmpty() || response.page >= response.total_pages) null else currentLoadingPageKey.plus(
//                    1
//                )
//            )
//
//        } catch (exception: IOException) {
//            return LoadResult.Error(exception)
//        } catch (exception: HttpException) {
//            return LoadResult.Error(exception)
//        } catch (exception: Exception) {
//            LoadResult.Error(exception)
//        }
//    }
//}