//package com.mustafa.movieguideapp.repository
//
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//import androidx.room.withTransaction
//import com.mustafa.movieguideapp.api.TheDiscoverService
//import com.mustafa.movieguideapp.models.Movie
//import com.mustafa.movieguideapp.models.entity.MovieRemoteKeys
//import com.mustafa.movieguideapp.room.AppDatabase
//import retrofit2.HttpException
//import java.io.IOException
//import java.io.InvalidObjectException
//
//
//@OptIn(ExperimentalPagingApi::class)
///**
// * @param service  so we can make network requests.
// * @param database so we can save data we got from the network request.
// */
//class MoviesRemoteMediator(
//    private val service: TheDiscoverService,
//    private val database: AppDatabase
//) : RemoteMediator<Int, Movie>() {
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, Movie>
//    ): MediatorResult {
//        val page = when (loadType) {
//            LoadType.REFRESH -> {
//                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
//                remoteKeys?.nextKey?.minus(1) ?: NEWS_API_STARTING_PAGE_INDEX
//            }
//            LoadType.PREPEND -> {
//                val remoteKeys = getRemoteKeyForFirstItem(state)
//                    ?: throw InvalidObjectException("Remote key and the prevKey should not be null")
//                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
//            }
//            LoadType.APPEND -> {
//                val remoteKeys = getRemoteKeyForLastItem(state)
//                if (remoteKeys?.nextKey == null) {
//                    throw InvalidObjectException("Remote key should not be null for $loadType")
//                }
//                remoteKeys.nextKey
//            }
//        }
//        try {
//            val response = service.fetchMovies(page)
//
//            val movies = response.results
//
//            val endOfPaginationReached = movies.isEmpty() || response.page >= response.total_pages
//            database.withTransaction {
//                // clear all tables in the database
//                if (loadType == LoadType.REFRESH) {
//                    database.remoteMovieKeysDao().clearMovieRemoteKeys()
//                    database.movieDao().clearMovies()
//                }
//
//                val prevKey = if (page == NEWS_API_STARTING_PAGE_INDEX) null else page - 1
//                val nextKey = if (endOfPaginationReached) null else page + 1
//                val keys = movies.map {
//                    MovieRemoteKeys(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
//                }
//                database.remoteMovieKeysDao().insertAll(keys)
//                database.movieDao().insertAll(movies)
//            }
//            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
//        } catch (exception: IOException) {
//            return MediatorResult.Error(exception)
//        } catch (exception: HttpException) {
//            return MediatorResult.Error(exception)
//        }
//    }
//
//    companion object {
//        private const val NEWS_API_STARTING_PAGE_INDEX = 1
//    }
//
//    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): MovieRemoteKeys? {
//        // Get the last page that was retrieved, that contained items.
//        // From that last page, get the last item
//        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
//            ?.let { movie ->
//                // Get the remote keys of the last item retrieved
//                database.remoteMovieKeysDao().remoteKeysMovieId(movie.id)
//            }
//    }
//
//    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): MovieRemoteKeys? {
//        // Get the first page that was retrieved, that contained items.
//        // From that first page, get the first item
//        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
//            ?.let { movie ->
//                // Get the remote keys of the first items retrieved
//                database.remoteMovieKeysDao().remoteKeysMovieId(movie.id)
//            }
//    }
//
//    private suspend fun getRemoteKeyClosestToCurrentPosition(
//        state: PagingState<Int, Movie>
//    ): MovieRemoteKeys? {
//        // The paging library is trying to load data after the anchor position
//        // Get the item closest to the anchor position
//        return state.anchorPosition?.let { position ->
//            state.closestItemToPosition(position)?.id?.let { movieId ->
//                database.remoteMovieKeysDao().remoteKeysMovieId(movieId)
//            }
//        }
//    }
//
//}