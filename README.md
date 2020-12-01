# MovieGuide

* A Movie Application uses [TheMovie API](https://www.themoviedb.org) based on Kotlin MVVM architecture and material design.
* A single-activity pattern, using the Navigation component to manage fragment operations.
* Reactive UIs using LiveData observables and Data Binding.
* Handles background tasks using Executors for the [master branch](https://github.com/Mustafashahoud/MoviesApp/tree/master), coroutines + Flow for the [2nd](https://github.com/Mustafashahoud/MoviesApp/tree/coroutines-flow) and [3th](https://github.com/Mustafashahoud/MoviesApp/tree/paging3-network) branch and RxJava for [4th branch](https://github.com/Mustafashahoud/MoviesApp/tree/paging3-rxjava).
* It consists of 16 fragments which are fully tested by Espresso.


## Libraries
- 100% Kotlin + [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- MVVM Architecture
- Architecture Components (Lifecycle, LiveData, ViewModel, DataBinding, Navigation, Room with FTS4)
- [TheMovie API](https://www.themoviedb.org)
- [Dagger2](https://github.com/google/dagger) for dependency injection
- [Retrofit2 & Gson](https://github.com/square/retrofit) for REST API
- [Glide](https://github.com/bumptech/glide) for loading images
- [RxJava](https://github.com/ReactiveX/RxJava/tree/2.x) for the [4th branch](https://github.com/Mustafashahoud/MoviesApp/tree/paging3-rxjava)
- [LeakCanary](https://square.github.io/leakcanary/) for detecting memory leak
- [Mockito-kotlin](https://github.com/nhaarman/mockito-kotlin) for Junit mock test
- [Espresso](https://developer.android.com/training/testing/espresso) for UI testing
- [Timber](https://github.com/JakeWharton/timber) for logging

### Stable samples - Kotlin
|     Sample     | Description |
| ------------- | ------------- |
| [master](https://github.com/Mustafashahoud/MoviesApp/tree/master) | The base for the rest of the other branch. <br/>Uses Kotlin, Architecture Components, AppExecutors, Dagger, Retrofit Data Binding, etc. and uses Room as source of truth, with a reactive UI. |
| [coroutines-flow](https://github.com/Mustafashahoud/MoviesApp/tree/coroutines-flow)| Same like the master branch but much better as it uses coroutines Flow (single source of truth with Flow). For testing coroutines Flow check [Tandem Repository](https://github.com/Mustafashahoud/Tandem). |
| [paging3-network](https://github.com/Mustafashahoud/MoviesApp/tree/paging3-network)| This branch uses Paging3 library to handle paging, it is wayyy easier. |
| [paging3-rxjava](https://github.com/Mustafashahoud/MoviesApp/tree/paging3-rxjava)| This branch uses RxJava2 with the Paging3 library as well as ViewBinding instead of kotlin synthetic. |

## App Demo

<p float="left">
  <img src="https://user-images.githubusercontent.com/33812602/80283982-c9ef6e80-871b-11ea-9de2-6b3299922a58.jpg" height="400" />
  <img src="https://user-images.githubusercontent.com/33812602/80283992-cf4cb900-871b-11ea-8cc4-9e82bbd3e880.jpg" height="400" />
  <img src="https://user-images.githubusercontent.com/33812602/80283993-d1af1300-871b-11ea-961c-bba911462ac6.jpg" height="400" />
  <img src="https://user-images.githubusercontent.com/33812602/80283997-d4116d00-871b-11ea-867b-e8394c39bc9b.jpg" height="400" />
  <img src="https://user-images.githubusercontent.com/33812602/80284001-d5db3080-871b-11ea-9c14-201e1ca5df12.jpg" height="400" />
  <img src="https://user-images.githubusercontent.com/33812602/80284006-da9fe480-871b-11ea-8de4-c69530027f27.jpg" height="400" />
  <img src="https://user-images.githubusercontent.com/33812602/80284008-dd023e80-871b-11ea-96c0-848548a306af.jpg" height="400" />
  <img src="https://user-images.githubusercontent.com/33812602/80284337-3c614e00-871e-11ea-82b6-5b8bcddb92db.gif" height="400" />
</p>

## License
```xml
Copyright 2020 The Android Open Source Project, Inc.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```