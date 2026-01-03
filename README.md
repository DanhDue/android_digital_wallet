JetCleanArch - A modern base code.
=============================================

This project uses many of the popular libraries, plugins, and tools of the Android ecosystem.
It is based on [Android Jetpack][0], [Kotlin][1], [Clean Architecture][2] & [Dependency Injection][3] using [Hilt][4].

I. Introduction
------------

This base code implements the Clean Architecture, follows the SOLID principle and uses Android Jetpack. It presents a modern approach to [Android](https://www.android.com/) application development using [Kotlin](https://kotlinlang.org/) and the latest tech stacks.

The goal of the project is to demonstrate best practices, provide a set of guidelines, and present modern Android application architecture that is modular, scalable, maintainable and testable.

II. Project Structure:
------------------
<p align="center"><img src="screenshots/project_structure.png" alt="Clean Architecture Diagram"></p>

#### 1. buildSrc
The ```buildSrc``` layer is responsible for dependency management. It control and manage all dependencies in one place with Kotlin.

#### 2. Common
The ```common``` layer is responsible for common properties. It contains the implementations of the resources files, themes and components declared in the common layer.

- __component__: This is responsible for common view components that using app.

- __provider__: Defines navigation, resource and theme provider.

- __theme__: Defines themes, colors, fonts and resource files.

#### 3. Libraries
The ```libraries``` layer is responsible for base structure.

- __framework__: Contains different utilities that can be used by the different modules and base structures.

- __jetframework__: Contains different utilities that can be used by the different modules and base structures only for Jet Compose.

- __testing__: Contains the definitions of the test utilities.

#### 4. Data
The ```data``` layer is responsible for selecting the proper data source for the domain layer. It contains the implementations of the repositories declared in the domain layer.

Components of data layer includes:
* __model__

  * __dto__: Defines dto of ui model, also perform data transformation between ```domain```, ```response``` and ```entity``` models.

  * __local__: Defines the schema of SQLite database.

  * __remote__: Defines POJO of network responses.

* __local__: This is responsible for performing caching operations using [Room](https://developer.android.com/training/data-storage/room).

* __remote__: This is responsible for performing network operations eg. defining API endpoints using [Retrofit](https://square.github.io/retrofit/).

* __repository__: Responsible for exposing data to the domain layer.

#### 5. Domain
This is the core layer of the application. The ```domain``` layer is independent of any other layers thus ] domain business logic can be independent from other layers.This means that changes in other layers will have no effect on domain layer eg.  screen UI (presentation layer) or changing database (data layer) will not result in any code change withing domain layer.

Components of domain layer include:
- __usecase__: They enclose a single action, like getting data from a database or posting to a service. They use the repositories to resolve the action they are supposed to do. They usually override the operator ```invoke``` , so they can be called as a function.

#### 6. Presentation
The ```presentation``` layer contains features involved in showing information to the user. The main part of this layer are the views(activity, compose) and ViewModels.


III. Technical Information used in this project:
-------------------------------------------

1. Programming Language: [Android Kotlin][1].
2. Architecture: [Clean Architecture][2] based on [Android Jetpack][0].  For more resources on learning Android development, visit the [Developer Guides](https://developer.android.com/guide/) at [developer.android.com](https://developer.android.com).
3. Multiple Threads: [Kotlin Coroutine][14].
4. Third Party Libraries: [Retrofit][25], [Hilt][22], [Firebase][24], [MockK][21], [etc][15],...
5. Unit Test: [Android testing framework][3], [MockK][21], and [Robolectric][26]; Unit Test coverage Analysis using [JaCoCo][22]; [etc][15],....

Moreover, you can read more detail about some project components by clicking to links contained at [References][17] part.

IV. Libraries Used
--------------

* [Android Jetpack][0] - Components for core system capabilities, Kotlin extensions and support for
  multidex and automated testing.
  * [AppCompat][1] - Degrade gracefully on older versions of Android.
  * [Android KTX][2] - Write more concise, idiomatic Kotlin code.
  * [Test][3] - An Android testing framework for unit and runtime UI tests.
  * [Lifecycle][38] - Perform actions in response to a change in the lifecycle status of another component, such as activities and fragments.
  * [ViewModel][39] - Designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations.
  * [Room][40] - Provides an abstraction layer over SQLite used for offline data caching.
  * [Paging3][41] - The Paging Library makes it easier for you to load data gradually and gracefully.
  * ....

* [Compose][4]
  * [Material][7] - Build Jetpack Compose UIs with ready to use Material Design Components.
  * [Foundation][8] - Write Jetpack Compose applications with ready to use building blocks and extend foundation to build your own design system pieces.
  * [UI][43] - Fundamental components of compose UI needed to interact with the device, including layout, drawing, and input.
  * [ConstraintLayout][44] - ConstraintLayout-compose 1.0 provides ConstraintLayout functionalities in Jetpack Compose.
  * [Lottie][45] - Lottie is a mobile library for Android and iOS that parses Adobe After Effects animations exported as json with Bodymovin and renders them natively on mobile!
  * [Coil][46] - An image loading library for Android backed by Kotlin Coroutines.
  * [Material Design][55] - Build awesome beautiful UIs.
  * ....

* [Accompanist][5] - a group of libraries that aim to supplement Jetpack Compose with features that are commonly required by developers but not yet available.
  * [SwipeRefresh][9] - A library which provides a layout which provides the swipe-to-refresh UX pattern, similar to Android's SwipeRefreshLayout.
  * [Systemuicontroller][47] - System UI Controller provides easy-to-use utilities for updating the System UI bar colors within Jetpack Compose.
  * [Insets][48] - Insets for Jetpack Compose takes a lot of the ideas which drove Insetter for views, and applies them for use in composables.
  * [Placeholder][49] - A library which provides a modifier for display 'placeholder' UI while content is loading.
  * [Navigation][50] - A library which provides Compose Material support for Jetpack Navigation Compose. This features composable bottom sheet destinations.
  * ....

* Third party
  * [Dagger Hilt][51] - Dependency Injection library.
  * [Gradle Kotlin DSL][59] - makes it easy to manage dependency all module that we have
  * [Google-KSP][52] - Kotlin Symbol Processing API
  * [Retrofit][25] for Networking.
  * [OkHttp-Logging-Interceptor][53] - Logs HTTP request and response data.
  * [Moshi][57] - A modern JSON library for Kotlin and Java.
  * [Chucker][58] - An HTTP inspector for Android & OkHTTP (like Charles but on device).
  * [JaCoCo][22] for Unit Test Coverage Analysis.
  * [Firebase][24] for Crashlytics, Analytics, Push Notification,....
  * [Timber][56] - A logger with a small, extensible API which provides utility on top of Android's normal Log class.
  * And a few other libraries ....

* Testing
  * [MockK][21] for unit test mocking. That provides DSL to mock behavior. Built from zero to fit Kotlin language.
    Supports named parameters, object mocks, coroutines and extension function mocking.
  * [Coroutine-Test][60] - Provides testing utilities for effectively testing coroutines.
  * [Robolectric][61] - Running tests on an Android emulator or device is slow! Building, deploying, and launching the app often takes a minute or more. That’s no way to do TDD. There must be a better way.
  * [Truth][62] - Truth makes your test assertions and failure messages more readable.
  * [Turbine][63] - Turbine is a small testing library for kotlinx.coroutines Flow.
  * .....

#### Plugin
* [Check-Dependency-Versions][10] - make easy to determine which dependencies have updates.

#### Code Analyze Tools
* [Ktlint][11] - A ktlint gradle plugin. Provides a convenient wrapper plugin over the ktlint project.
* [Spotless][12] - It’s pretty useful in automating fixes for pretty simple (and common) formatting mistakes as in spaces, newlines, removing unnecessary imports, etc.
* [Detekt][13] - Static code analysis for Kotlin.

V. References
----------
* [setup Jacoco for an Android Multiple Module Project.][29]
* [Change Android Retrofit's Base Url at runtime.][28]
* [Change Android Brightness.][18]
* [Play Youtube videos on Androids.][19]
* [ Setup Jacoco for Android Project.][20]
* [Callbacks in Android Application.][27]

[0]: https://developer.android.com/jetpack/components
[1]: https://kotlinlang.org/
[2]: http://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
[3]: https://en.wikipedia.org/wiki/Dependency_injection
[4]: https://developer.android.com/training/dependency-injection/hilt-android
[4]: https://developer.android.com/jetpack/compose
[5]: https://google.github.io/accompanist
[6]: https://developer.android.com/jetpack
[7]: https://developer.android.com/jetpack/androidx/releases/compose-material
[8]: https://developer.android.com/jetpack/androidx/releases/compose-foundation
[9]: https://google.github.io/accompanist/swiperefresh
[10]: https://github.com/ben-manes/gradle-versions-plugin
[11]: https://github.com/jlleitschuh/ktlint-gradle
[12]: https://github.com/diffplug/spotless
[13]: https://github.com/detekt/detekt
[14]: https://kotlinlang.org/docs/reference/coroutines-overview.html
[15]: https://github.com/DanhDue/JetCleanArch#libraries-used
[16]: https://github.com/DanhDue/AndroidGenericAdapter#screenshots
[17]: https://github.com/DanhDue/AndroidGenericAdapter#references
[18]: https://viblo.asia/p/change-android-application-brightness-like-a-boss-djeZ1ok85Wz
[19]: https://viblo.asia/p/how-to-play-youtube-videos-in-an-android-webview-with-just-a-few-lines-of-code-RQqKL9mbZ7z
[20]: https://viblo.asia/p/setup-jacoco-for-android-project-gGJ59zB9KX2
[21]: https://mockk.io
[22]: https://www.jacoco.org/jacoco/trunk/index.html
[23]: https://insert-koin.io
[24]: https://firebase.google.com
[25]: https://square.github.io/retrofit
[26]: http://robolectric.org
[27]: https://viblo.asia/p/calbacks-trong-ung-dung-android-RnB5pk87lPG
[28]: https://viblo.asia/p/change-retrofits-base-url-at-runtime-ORNZqDLMK0n
[29]: https://viblo.asia/p/setup-jacoco-for-an-android-multiple-module-projectclean-architect-project-4dbZNNoqZYM
[30]: https://en.wikipedia.org/wiki/Single-responsibility_principle
[31]: https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle#:~:text=In%20object%2Doriented%20programming%2C%20the,without%20modifying%20its%20source%20code
[32]: https://en.wikipedia.org/wiki/Liskov_substitution_principle
[33]: https://en.wikipedia.org/wiki/Interface_segregation_principle
[34]: https://en.wikipedia.org/wiki/Dependency_inversion_principle
[35]: https://en.wikipedia.org/wiki/Robert_C._Martin
[36]: https://en.wikipedia.org/wiki/Abstraction_principle_(computer_programming)
[37]: https://en.wikipedia.org/wiki/Dependency_inversion_principle
[38]: https://developer.android.com/topic/libraries/architecture/lifecycle
[39]: https://developer.android.com/topic/libraries/architecture/viewmodel
[40]: https://developer.android.com/training/data-storage/room
[41]: https://developer.android.com/topic/libraries/architecture/paging/v3-overview
[42]: https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView
[43]: https://developer.android.com/jetpack/androidx/releases/compose-ui
[44]: https://developer.android.com/jetpack/androidx/releases/constraintlayout
[45]: https://github.com/airbnb/lottie/blob/master/android-compose.md
[46]: https://coil-kt.github.io/coil/compose
[47]: https://google.github.io/accompanist/systemuicontroller
[48]: https://google.github.io/accompanist/insets
[49]: https://google.github.io/accompanist/placeholder
[50]: https://google.github.io/accompanist/navigation-material
[51]: https://dagger.dev/hilt
[52]: https://github.com/google/ksp
[53]: https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md
[54]: https://developer.android.com/kotlin/flow
[55]: https://material.io/develop/android/docs/getting-started
[56]: https://github.com/JakeWharton/timber
[57]: https://github.com/square/moshi
[58]: https://github.com/ChuckerTeam/chucker
[59]: https://gradle.org/kotlin
[60]: https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test
[61]: http://robolectric.org
[62]: https://github.com/google/truth
[63]: https://github.com/cashapp/turbine

License
-------

Copyright 2023 DanhDue ExOICTIF, danhdue@gmail.com

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
