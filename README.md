# MindOrks Instagram Demo

<image align="right" src="https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="25%"/>

![GitHub](https://img.shields.io/github/license/kaushiknsanji/MindOrksInstagramDemo)  ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kaushiknsanji/MindOrksInstagramDemo)  ![GitHub repo size](https://img.shields.io/github/repo-size/kaushiknsanji/MindOrksInstagramDemo)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/kaushiknsanji/MindOrksInstagramDemo)  ![GitHub All Releases downloads count](https://img.shields.io/github/downloads/kaushiknsanji/MindOrksInstagramDemo/total) ![GitHub search hit counter](https://img.shields.io/github/search/kaushiknsanji/MindOrksInstagramDemo/MindOrks%20Instagram%20App) ![Minimum API level](https://img.shields.io/badge/API-21+-yellow)

This App has been developed as part of the **[MindOrks Android Online Course for Professionals][MindOrks course]**. This is a mini demo version of the real Instagram app built with Android Jetpack and MVVM Architecture.

_**Note:** Exercises that were also part of this course can be found [here](https://github.com/kaushiknsanji/MindOrks_Android_Professional_BootCamp_Assignments)._

## Features
* User registration and management.
* Auto renewal of User access token.
* Tabs for viewing All User Posts, to create & publish a Post and to view "My Posts".
* Posts contain only a Photo which can be picked from System Gallery or captured through System Camera while creating a Post.
* Posts created by the logged-in User can be deleted by that logged-in User only.
* Allows to edit user profile information which includes-
	* User profile picture
	* Profile Name
	* Profile Bio
* Post Likes screen to show who liked the Post, which gets launched on click of the likes count on a Post.
* Post Details screen to show the details of the Post, including the Post Photo and those who have liked the Post.
* Immersive Photo screen to view the Post Photo in detail, which gets launched on click of Post Photo in Post Details screen.
* User can like Posts by either- 
	* clicking on the Heart icon 
	* double tapping on Post Photo
	* navigating to Post Likes or Post Details screen and then clicking on the Heart icon
	
---

## App Compatibility

Android device running with Android OS 5.0 (API Level 21) or above. Designed for Phones and NOT for Tablets.

---

## Getting Started

* Register for the "MindOrks Instagram API" Key by enrolling to the [MindOrks course][]. You will be given an **API Key** and a **Base URL** your application needs to point to.
* Create a Properties file named **instagram_api.properties** in the Project's root folder.
* Define a property named **API_KEY_VAL** and assign it the value of the _API Key_ obtained from the registration process.
* Define another property named **BASE_URL_VAL** and assign it the value of the _Base URL_ obtained from the registration process.

_**Note:** This server is solely **built and managed by MindOrks**. So, please refrain from uploading any sensitive or copyrighted photo, while testing the application. Also, after signing up through this mobile application, do not forget your own login credentials since there is no way to recover if forgotten._

---

## Sample Screenshots

|SignUp Screen|Login Screen|Home Screen / All Posts Tab|
|---|---|---|
|![sign_up_screen](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/sign_up_screen.png)|![login_screen](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/login_screen.png)|![all_posts_tab](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/all_posts_tab.png)|

|Upload Photo Post Tab|User Posts Tab|Edit Profile Screen|
|---|---|---|
|![upload_photo_post_tab](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/upload_photo_post_tab.png)|![user_posts_tab](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/user_posts_tab.png)|![edit_profile_screen](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/edit_profile_screen.png)|

|Likes Screen|Post Details Screen|
|---|---|
|**Without Likes**<br/>![likes_screen_without_likes](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/likes_screen_without_likes.png)<br/><br/>**With Likes**<br/>![likes_screen_with_likes](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/likes_screen_with_likes.png)|**Without Likes**<br/>![post_details_screen_without_likes](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/post_details_screen_without_likes.png)<br/><br/>**With Likes**<br/>![post_details_screen_with_likes](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/post_details_screen_with_likes.png)|

|Fullscreen Photo Immersive Screen (Portrait)|Fullscreen Photo Immersive Screen (Landscape)|
|---|---|
|![fullscreen_photo_immersion_port](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/fullscreen_photo_immersion_port.png)|![fullscreen_photo_immersion_land](https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/screenshots/fullscreen_photo_immersion_land.png)|

## Video Preview

[![Video of Complete App Flow](https://i.ytimg.com/vi/tbzwxqQFvxY/maxresdefault.jpg)](https://youtu.be/tbzwxqQFvxY)

---

## Built With

* [Kotlin](https://kotlinlang.org/)
* [Constraint Layout library](https://developer.android.com/training/constraint-layout/index.html) to make layouts.
* [Material Design library](https://material.io/develop/android/components/) for Material UI.
* Android JetPack components-
	* [ViewBinding](https://developer.android.com/topic/libraries/view-binding) via Kotlin Property delegation, for binding UI elements with its objects.
	* [Room](https://developer.android.com/training/data-storage/room) for caching data in local database and offline operation. _Offline feature is not yet implemented in this application._
	* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) and [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) to maintain the Activity's state, handle user actions and communicate updates to the Repositories.
	* [Android KTX](https://developer.android.com/kotlin/ktx) for simplified and idiomatic Kotlin.
* [Dagger2](https://github.com/google/dagger) for dependency injection of required services to the clients.
* [Glide](https://bumptech.github.io/glide/) to load Images.
* [Retrofit](https://square.github.io/retrofit/) and [OkHttp](https://square.github.io/okhttp/) to communicate with the "MindOrks Instagram API" via REST calls.
* [GSON](https://github.com/google/gson) to deserialize JSON to Java Objects.
* [RxJava3](https://github.com/ReactiveX/RxJava) for Reactive programming.
* [Timber](https://github.com/JakeWharton/timber) for logging and debugging.
* [Junit](https://junit.org/junit4/index.html), [Mockito](https://github.com/mockito/mockito), [Hamcrest](http://hamcrest.org/JavaHamcrest/), [Truth](https://truth.dev/) and [Espresso](https://developer.android.com/training/testing/espresso) for Testing.

### Architecture

<p align="center">
    <a href="https://developer.android.com/jetpack/guide#recommended-app-arch">
        <img alt="Recommended MVVM Architecture" src="https://developer.android.com/topic/libraries/architecture/images/final-architecture.png" width="75%">
    </a>
</p>

App follows the recommended **[MVVM architecture with Repository pattern](https://developer.android.com/jetpack/guide#recommended-app-arch)** as advocated by Google, with certain tweaks as advocated by MindOrks in this course. Tweaks mainly consists of Base classes abstractions like-
* [BaseActivity][] 
	* Facilitates the setup and abstraction to common tasks of an _Activity_ like injecting dependencies, setting up the Views and `LiveData` observers, displaying Toasts, providing back key event for fragments and popping back stack accordingly, etc. 
	* Each _BaseActivity_ comes with a [BaseViewModel][] which represents the Primary `ViewModel` of the _Activity_, providing abstraction for checking & establishing Network Connectivity, and to clear any Rx Disposables & other initializations when the `ViewModel` gets cleared. 
	* _BaseActivity_ also signals its `ViewModel` when it enters the CREATED state for the ViewModels to do required initializations.
* [BaseFragment][] 
	* Facilitates the setup and abstraction to common tasks of a _Fragment_ like injecting dependencies, setting up the Views and `LiveData` observers, displaying Toasts, delegating back key event to its _Activity_, etc. 
	* Each _BaseFragment_ comes with a [BaseViewModel][] which represents the Primary `ViewModel` of the _Fragment_, providing abstraction for checking & establishing Network Connectivity, and to clear any Rx Disposables & other initializations when the `ViewModel` gets cleared. 
	* _BaseFragment_ also signals its `ViewModel` when it enters the CREATED state for the ViewModels to do required initializations.
* Base Classes for `RecyclerView`'s components-
	* [BaseItemViewHolder][] -
		* This _ViewHolder_ is made Lifecycle aware in order to update its Item View in a lifecycle conscious way, i.e., it does NOT update the ItemView when it goes into background.
		* Each Item's _ViewHolder_ communicates with its own `ViewModel`, i.e., **BaseItemViewModel** for fetching information from any data source repository.
	* [BaseItemViewModel][] -
		* This is the Base `ViewModel` for the Lifecycle aware **BaseItemViewHolder**.
		* Each Item's _ViewModel_ updates its ItemView only when the **BaseItemViewHolder**, i.e., its ItemView is visible, by making use of `LiveData`.
	* [BaseAdapter][] -
		* This is the _Adapter_ of `RecyclerView` hosted either by an `Activity` or a `Fragment`.
		* ViewHolder's shown by this _Adapter_ are of type **BaseItemViewHolder**.
		* In order to make the **BaseItemViewHolder** Lifecycle aware, it observes the Lifecycle of the `Activity` / `Fragment` hosting the `RecyclerView` and triggers the Lifecycle state changes accordingly to each of the **BaseItemViewHolders** of RecyclerView Items.

##### Behavior of the Architecture with Lifecycle aware RecyclerView components
* When the hosting Activity/Fragment's `ViewModel` gets a new/updated data list to be shown, it is emitted to the RecyclerView's Adapter via a `LiveData`.
* RecyclerView's Adapter checks the new data with the current data, and notifies the data change accordingly using `AsyncListDiffer` with `AsyncDifferConfig`.
* This triggers calls to `onBindViewHolder()` of the Adapter for the Item ViewHolders at respective positions where the data has changed in order to be rebound.
* The `onBindViewHolder()` of the Adapter delegates to the custom `bind()` method of **BaseItemViewHolder**, which in turn passes the item data provided by the Adapter to its **BaseItemViewModel**'s `LiveData`. Transformations on this `LiveData` will get applied to fetch and keep whatever data needs to be shown on the ItemView when it becomes visible. So, if the ItemViews contain any images, they are downloaded and kept in the Item's **BaseItemViewModel** ready to be shown when the ItemView becomes visible. This maintains the state of the ItemView preventing unnecessary interactions with the data source repositories triggered by events such as configuration/orientation change.
* When the ItemView becomes visible, all the transformed data events will be emitted to **BaseItemViewHolder**, which finally initializes the ItemView to show the data.
* In case of Item level interactions, events are only dispatched to the Item's **BaseItemViewModel**. If this requires a synchronization update in order to keep the corresponding item data in the main list in sync, then this event will be dispatched from the Item's **BaseItemViewModel** to the `ViewModel` of the hosting Activity/Fragment via a registered listener. 
* In case of Navigation based interactions, like navigating to some details screen on click of an Item, events are first dispatched to the Item's **BaseItemViewModel** and then from there to the `ViewModel` of the hosting Activity/Fragment via a registered listener. 

##### Additional tweaks built by me on the same lines of Base classes abstractions
* Listener
	* [BaseListenerObservable][] 
		* Meant for managing any kind of listeners used in the app, which registers listeners in a Thread-safe manner and dispatches callback events to registered listeners. 
		* Used for managing all listeners on **BaseAdapter**.
* Dialogs
	* [BaseDialogFragment][] 
		* Facilitates the setup and abstraction to common tasks of a _DialogFragment_ like injecting dependencies, setting up `LiveData` observers, constructing a templated `AlertDialog` using a `MaterialAlertDialogBuilder` or a complete `AlertDialog` with the provided custom View, displaying Toasts, etc. 
		* Templated `AlertDialog` is constructed with UI fields that may be required for a particular category of Dialogs like say "Confirmation" or "Progress". Once templated, corresponding UI fields can be set with actual data at the time of displaying the Dialog and any unset fields in this case will be automatically hidden. 
	* [BaseDialogViewModel][]
		* Each **BaseDialogFragment** comes with a **BaseDialogViewModel** which represents the Primary `ViewModel` of the _DialogFragment_, providing abstraction to common tasks of _DialogFragment_ like setting the `LiveData` of UI fields and managing its state.
* New Activity Result using `ActivityResultContract`
	* [BaseActivityResultContracts][]
		* Facilitates the setup of required Activity call contracts of the app. 
		* These contracts can also be used by the Fragments of the app.
	* [BaseActivityResultObserver][]
		* Facilitates the setup of Lifecycle observers needed for receiving and handling the activity result in a separate class for the calls being registered via `ActivityResultRegistry`.
		* **BaseActivity** registers this Observer for Activity results.
	* [BaseFragmentResultObserver][]
		* Facilitates the setup of Lifecycle observers needed for receiving and handling the activity result in a separate class for the calls being registered via `ActivityResultRegistry`.
		* **BaseFragment** registers this Observer for Activity results.
* Fully Immersive Activity
	* [BaseImmersiveActivity][]
		* An abstract **BaseActivity** to provide Fullscreen Immersion to activities that subclass this. 
		* Takes care of-
			* publishing System UI visibility state change events to the Primary `ViewModel` of the activity.
			* observing Fullscreen Toggle request events on a registered UI element. This registration of UI element is required to be done by the subclasses.
	* [BaseImmersiveViewModel][]
		* This is the Primary `ViewModel` of **BaseImmersiveActivity** which is a subclass of **BaseViewModel** dedicated to managing common tasks of Fullscreen Immersion.
		* Manages the state of System UI visibility change and handles Fullscreen Toggle requests.

#### Special Utilities

##### App Utilities
* [Event][]
	* A wrapper to the content emitted as Single Live Events by a `LiveData`.
* [Resource][]
	* A Sealed class wrapper to Single Live Events associated with Status metadata information.
	* [Status][] metadata is an `Enum` of values - "SUCCESS", "ERROR", "LOADING" and "UNKNOWN". 
* [LiveDataExt][]
	* Kotlin extension functions on `LiveData` to promote idiomatic code while observing `LiveData` members.
	* This has extensions on `LiveData` of **Event** and also on **Resource**.
* [ViewBindingUtils][]
	* A set of Kotlin Lazy delegates which provides `ViewBinding` instance of an `Activity` or `Fragment` or [BaseItemViewHolder][].
	* Use of Lazy delegates enables to get `ViewBinding` instance in an idiomatic way using a single statement in case of `Activity` and `Fragment` or using simple configuration in case of [BaseItemViewHolder][].
* [BitStateTracker][]
	* Tracks changes to a _"State"_ using bit logic.
	* Useful for merging state changes or updates to a Boolean value when working with `MediatorLiveData`.
* [DialogManager][]
	* Manages all dialogs shown by the subclasses of **BaseActivity** and **BaseFragment** of the app.
	* Maintains a reference to the current active instance of `Dialog` shown. 
	* Restores the active `Dialog` in case of configuration/orientation changes. This prevents a new instance of the same `Dialog` from being shown in the event where an active instance of the `Dialog` was shown prior to such configuration/orientation change.
	* Provides methods for dismissing an active `Dialog` and also to show a new `Dialog` requested.
	* Takes care of dismissing any active Dialogs prior to displaying the requested one.
* [AlertDialogExt][]
	* Provides Kotlin extension functions on `AlertDialog`.
	* Mainly used for templating `AlertDialog` when built with `MaterialAlertDialogBuilder` in **BaseDialogFragment**.
* [ImmersiveModeCompatExt][]
	* Kotlin extension functions on `ComponentActivity` and `Window` to facilitate Fullscreen Immersive mode setup for any `ComponentActivity`.
	* Provides extension method on- 
		* `Window` to register a listener on its `DecorView` to track and capture System UI visibility change events.
		* `ComponentActivity` to ensure app content is always shown below the device display cutouts if any in order to prevent content jump and overlap when System UI visibility changes.
		* `ComponentActivity` to toggle window immersion which internally alters the System UI visibility accordingly.
* [RecyclerViewExt][]
	* Kotlin extension functions on `RecyclerView` to promote idiomatic code while working with its properties.

##### Test Utilities
* [BottomNavigationViewActions][]
	* Provides Espresso `ViewAction`s to perform Navigation action to Menu Items on `BottomNavigationView`.
* [RecyclerViewItemActions][]
	* Provides Espresso `ViewAction`s to perform interactions on any `View` in `RecyclerView` Items.
* [TestActivityResultRegistry][]
	* `ActivityResultRegistry` subclass to stub the Activity Result of the Activity launched for testing.
	* Additionally, records whether the requested Activity was successfully launched or not.
* [ImageMatchers][]
	* Provides Hamcrest `Matcher`s for matching the Drawables in Views.
	* Used for matching-
		* A `Drawable` set as background image of any `View`.
		* Any of the `CompoundDrawable` set on a `TextView`.
		* A `Drawable` set as the source image of `ImageView`.
* [RecyclerViewMatchers][]
	* Provides Hamcrest `Matcher`s for matching any `View` in `RecyclerView` Items.
* [TextInputLayoutMatchers][]
	* Provides Hamcrest `Matcher`s for matching a `TextInputLayout`.
	* Used for matching a `TextInputLayout` based on the Error set on it.
* [DataModelObjectProvider][]
	* Provides easy to use dummy data model instances and functions to manipulate/transform data models.
	* Used extensively for testing in both Instrumented and Local Unit tests.

### Project Structure

#### Project "main" source set structure

```
main
└───java
    └───com.mindorks.kaushiknsanji.instagram.demo
        │   InstagramApplication.kt                  # Root Package contains the Application subclass
        │   
        ├───data                                     # For the data sources and repositories of the app (Remote and Local)
        │   ├───local                                # For Local database of the app and other local resources like Shared Preferences
        │   │   ├───db                               # Local Room database
        │   │   │   │   Converter.kt                 # This package contains the abstract Room database and any Converters it needs
        │   │   │   │   DatabaseService.kt
        │   │   │   │   
        │   │   │   ├───dao                          # Database access objects
        │   │   │   │       
        │   │   │   └───entity                       # Database entities
        │   │   │           
        │   │   └───prefs                            # Classes that communicate with Shared Preferences
        │   │               
        │   ├───model                                # Application wide Data Models required by the App which may also be required by the Remote Request/Response Models
        │   │           
        │   ├───remote                               # For Remote Request/Response Models and API related classes
        │   │   │   Networking.kt                    # Utility class for configuring the Retrofit and providing the instance of required API Service
        │   │   │       
        │   │   ├───api                              # API Endpoints and API Service interfaces
        │   │   │           
        │   │   ├───auth                             # Authentication Token management and renewal
        │   │   │       
        │   │   ├───model                            # Commonly embedded Remote Response Models
        │   │   │       
        │   │   ├───request                          # Remote Request Models
        │   │   │       
        │   │   └───response                         # Remote Response Models
        │   │           
        │   └───repository                           # Classes that provide seamless access to data through the Repository pattern
        │           
        ├───di                                       # For Dagger dependency injection
        │   │   qualifiers.kt                        # Dagger Qualifiers
        │   │   scopes.kt                            # Dagger Scopes
        │   │   
        │   ├───component                            # Dagger Component Interfaces
        │   │       
        │   └───module                               # Dagger Modules
        │           
        ├───ui                                       # For everything related to UI Screens where each screen has Activity/Fragment and their ViewModels
        │   ├───base                                 # Base class abstractions of Activity, Fragment, DialogFragment, ViewModel, ActivityResult, etc.
        │   │   │   
        │   │   └───listeners                        # Abstraction for the Listener entities of the app
        │   │           
        │   ├───common                               # Any UI component common to or shared across the app
        │   │   ├───dialogs                          # Common Dialogs of the app that has DialogFragments, ViewModels and Metadata classes if any pertaining to it
        │   │   │   ├───option                       # Confirmation Alert Dialogs
        │   │   │   │       
        │   │   │   ├───picture                      # Custom Dialog for Photo selection/capture
        │   │   │   │       
        │   │   │   └───progress                     # Custom Dialog for Progress
        │   │   │           
        │   │   └───likes                            # Post Likes RecyclerView item component which is common across multiple screens of the app
        │   │           
        │   ├───detail                               # Post Details screen
        │   │   │   
        │   │   └───photo                            # Post Photo screen for viewing Photo, launched from Post Details screen
        │   │           
        │   ├───home                                 # Home screen for all Posts
        │   │   │   
        │   │   ├───posts                            # Post RecyclerView Item component displayed on Home screen
        │   │   │       
        │   │   └───util                             # Utility classes if any for promoting Test Driven Development. Will be usually used by the ViewModels in MVVM.
        │   │           
        │   ├───like                                 # Post Likes screen
        │   │       
        │   ├───login                                # Login screen
        │   │       
        │   ├───main                                 # Main screen
        │   │       
        │   ├───photo                                # Post Photo upload screen
        │   │       
        │   ├───profile                              # User Profile screen
        │   │   │   
        │   │   ├───edit                             # Edit Profile screen, launched from User Profile screen
        │   │   │       
        │   │   └───posts                            # Post RecyclerView Item component displayed on User Profile screen
        │   │           
        │   ├───signup                               # SignUp screen
        │   │       
        │   └───splash                               # Splash screen
        │           
        └───utils                                    # For all utilities required by the app
            ├───common                               # Utilities common to many functionalities within the app
            │       
            ├───display                              # Utilities pertaining to display like Dialogs, Menus, Themes, Toasts, Views, Text Appearance, etc.
            │       
            ├───factory                              # Factory utilities for supplying an instance of some entity like ViewModel
            │       
            ├───log                                  # Utilities related to logs
            │       
            ├───network                              # Networking utilities
            │       
            ├───rx                                   # Utilities for Reactive Rx Streams
            │       
            └───widget                               # Utilities for View Widgets like RecyclerView, TextInputEditText, TextInputLayout, etc.
```

#### "test" source set structure for Local Unit Tests

* The "test" source set will have the same structure as the "main" source set. 
* If there are some utilities needed to facilitate testing, then it will be placed in `"<applicationId>/utils/test"` package.

```
test
└───java
    └───com.mindorks.kaushiknsanji.instagram.demo
        ├───data                                     # Local Unit Tests for data sources and repositories                    
        │   └───repository
        │           
        ├───ui                                       # Local Unit Tests for UI ViewModels and their utilities if any
        │   ├───home
        │   │   │   
        │   │   └───util
        │   │           
        │   ├───login
        │   │       
        │   ├───main
        │   │       
        │   ├───signup
        │   │       
        │   └───splash
        │           
        └───utils                                    # For testing "main" source set utilities and also for making utilities needed for facilitating tests 
            ├───common                               # Local Unit Tests for common utilities
            │       
            └───rx
                    TestSchedulerProvider.kt         # SchedulerProvider implementation which provides the Reactive TestScheduler instance needed for Local Unit Tests
```

#### "androidTest" source set structure for Instrumented Tests

* The "androidTest" source set will have the same structure as the "main" source set.
* If there are some utilities needed to facilitate testing, then it will be placed in `"<applicationId>/utils/test"` package.
* Fake implementations needed for Instrumented testing will be placed in the respective packages of original interfaces/implementations.
* TestRules needed for Instrumented Test setup will be placed in the `"rule"` package under the respective packages of the component being initialized for testing.

```
androidTest
└───java
    └───com.mindorks.kaushiknsanji.instagram.demo
        ├───data                                           # Fake implementations and TestRules for data sources
        │   ├───local
        │   │   └───rule                                   # TestRules that need to communicate with the local database or resources for test setup
        │   │           UserSessionRule.kt                 # TestRule for the setup of Signed-in User session
        │   │           
        │   └───remote
        │       └───api                                    # Stubbed API Service interfaces with fake implementation for Instrumented Tests
        │               FakeNetworkService.kt              # Fake implementation of NetworkService 
        │               
        ├───di                                             # Dagger dependency injection needed for Instrumented Tests
        │   ├───component                                  # Dagger Test Component interfaces
        │   │       TestComponent.kt                       # Test Component for exposing dependencies from the Test Module for Application
        │   │       
        │   ├───module                                     # Dagger Test Modules
        │   │       ApplicationTestModule.kt               # Test Module for Application
        │   │       
        │   └───rule                                       # Dagger TestRules for test setup
        │           TestComponentRule.kt                   # TestRule for the setup of Dagger Test Component
        │           
        ├───ui                                             # Instrumented Tests for UI screens (Activity or Fragment)
        │   ├───home
        │   │       
        │   ├───login
        │   │       
        │   ├───main
        │   │       
        │   ├───signup
        │   │       
        │   └───splash
        │           
        └───utils                                          # For stubbing "main" source set utilities and also for making utilities needed for facilitating tests
            ├───network                                    # Stubbed networking utilities with fake implementation for Instrumented Tests
            │       FakeNetworkHelperImpl.kt               # Fake implementation of NetworkHelper for testing Network related tasks
            │       
            └───test                                       # Utilities needed for facilitating Instrumented tests
                │   InstrumentedTestUtils.kt               # Utility functions commonly needed for many Instrumented tests and its utilities
                │   
                ├───action                                 # Custom Espresso View Actions
                │       
                ├───activity                               # Utilities needed for working with or testing any Activity
                │       TestActivityResultRegistry.kt      # ActivityResultRegistry subclass to stub the result of Activity launched for tests
                │       
                └───matcher                                # Custom Hamcrest Matchers
```

#### "sharedTest" source set structure for Shared Tests

* The "sharedTest" source set will have the same structure as the "main" source set.
* Contains codes shared between "test" source set and "androidTest" source set.
* Mainly used for sharing Test utilities for Instrumented and/or Local Unit Tests which will be placed in `"<applicationId>/utils/test"` package.

```
sharedTest
└───java
    └───com.mindorks.kaushiknsanji.instagram.demo
        └───utils                                          # For testing "main" source set utilities and also for making utilities needed for facilitating tests 
            └───test                                       # Utilities needed for facilitating tests (Instrumented and/or Local Unit Tests)
                   DataModelObjectProvider.kt              
                   TestConstants.kt                        
```

---

## Changes planned in the future

- [ ] Orientation based scaling of Images
- [ ] New Navigation component for App navigation
- [ ] Migration from Dagger to Hilt
- [ ] Offline functionality
- [ ] More Test cases

---

## Certificate of Completion

<a href="https://mindorks.com/certificate/69081f8f1b">
    <img alt="Completion Certificate" src="https://github.com/kaushiknsanji/MindOrksInstagramDemo/blob/master/art/certificate/completion_certificate.png" width="50%">
</a>

---

## License

```
Copyright 2019 Kaushik N. Sanji

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

<!-- Reference Style Links are to be placed after this -->
[MindOrks course]: https://mindorks.com/android-app-development-online-course-for-professionals
[BaseActivity]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseActivity.kt
[BaseViewModel]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseViewModel.kt
[BaseFragment]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseFragment.kt
[BaseItemViewHolder]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseItemViewHolder.kt
[BaseItemViewModel]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseItemViewModel.kt
[BaseAdapter]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseAdapter.kt
[BaseListenerObservable]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/listeners/BaseListenerObservable.kt
[BaseDialogFragment]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseDialogFragment.kt
[BaseDialogViewModel]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseDialogViewModel.kt
[BaseActivityResultContracts]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseActivityResultContracts.kt
[BaseActivityResultObserver]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseActivityResultObserver.kt
[BaseFragmentResultObserver]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseFragmentResultObserver.kt
[BaseImmersiveActivity]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseImmersiveActivity.kt
[BaseImmersiveViewModel]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/ui/base/BaseImmersiveViewModel.kt
[Event]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/common/Event.kt
[Resource]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/common/Resource.kt
[Status]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/common/Status.kt
[LiveDataExt]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/common/LiveDataExt.kt
[ViewBindingUtils]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/common/ViewBindingUtils.kt
[BitStateTracker]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/common/BitStateTracker.kt
[DialogManager]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/display/DialogManager.kt
[AlertDialogExt]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/display/AlertDialogExt.kt
[ImmersiveModeCompatExt]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/display/ImmersiveModeCompatExt.kt
[RecyclerViewExt]: app/src/main/java/com/mindorks/kaushiknsanji/instagram/demo/utils/widget/RecyclerViewExt.kt
[BottomNavigationViewActions]: app/src/androidTest/java/com/mindorks/kaushiknsanji/instagram/demo/utils/test/action/BottomNavigationViewActions.kt
[RecyclerViewItemActions]: app/src/androidTest/java/com/mindorks/kaushiknsanji/instagram/demo/utils/test/action/RecyclerViewItemActions.kt
[TestActivityResultRegistry]: app/src/androidTest/java/com/mindorks/kaushiknsanji/instagram/demo/utils/test/activity/TestActivityResultRegistry.kt
[ImageMatchers]: app/src/androidTest/java/com/mindorks/kaushiknsanji/instagram/demo/utils/test/matcher/ImageMatchers.kt
[RecyclerViewMatchers]: app/src/androidTest/java/com/mindorks/kaushiknsanji/instagram/demo/utils/test/matcher/RecyclerViewMatchers.kt
[TextInputLayoutMatchers]: app/src/androidTest/java/com/mindorks/kaushiknsanji/instagram/demo/utils/test/matcher/TextInputLayoutMatchers.kt
[DataModelObjectProvider]: app/src/sharedTest/java/com/mindorks/kaushiknsanji/instagram/demo/utils/test/DataModelObjectProvider.kt
