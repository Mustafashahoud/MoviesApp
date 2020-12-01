package com.mustafa.movieguideapp.repository.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.mustafa.movieguideapp.api.MovieService
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.testing.OpenForTesting
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class MovieRepository @Inject constructor(private val service: MovieService) {

    fun loadKeywords(id: Int): LiveData<Resource<List<Keyword>>> {
        return LiveDataReactiveStreams.fromPublisher(service.fetchKeywords(id = id)
            .subscribeOn(Schedulers.io())
            .map<Resource<List<Keyword>>> { Resource.Success(it.keywords, false) }
            .onErrorReturn { Resource.Error(it.toString()) }
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable()
        )

    }

    fun loadVideos(id: Int): LiveData<Resource<List<Video>>> {
        return LiveDataReactiveStreams.fromPublisher(
            service.fetchVideos(id = id)
                .subscribeOn(Schedulers.io())
                .map<Resource<List<Video>>> { Resource.Success(it.results, false) }
                .onErrorReturn { Resource.Error(it.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .toFlowable()
        )
    }

    fun loadReviews(id: Int): LiveData<Resource<List<Review>>> {
        return LiveDataReactiveStreams.fromPublisher(
            service.fetchReviews(id = id)
                .subscribeOn(Schedulers.io())
                .map<Resource<List<Review>>> { Resource.Success(it.results, false) }
                .onErrorReturn { Resource.Error(it.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .toFlowable()
        )
    }
}
