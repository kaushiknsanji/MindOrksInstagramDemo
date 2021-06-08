package com.mindorks.kaushiknsanji.instagram.demo.data.repository

import com.mindorks.kaushiknsanji.instagram.demo.data.local.db.DatabaseService
import com.mindorks.kaushiknsanji.instagram.demo.data.model.Post
import com.mindorks.kaushiknsanji.instagram.demo.data.model.User
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.Networking
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.api.NetworkService
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.LikePostRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.PostCreationRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.UnlikePostRequest
import com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.*
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Resource
import com.mindorks.kaushiknsanji.instagram.demo.utils.common.Status
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.DataModelObjectProvider.toMyPostItem
import com.mindorks.kaushiknsanji.instagram.demo.utils.test.TestConstants
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Local Unit Test on Post repository.
 *
 * @author Kaushik N Sanji
 */
@RunWith(MockitoJUnitRunner::class)
class PostRepositoryTest {

    // Mocked Network Service instance
    @Mock
    private lateinit var networkService: NetworkService

    // Mocked Database Service instance
    @Mock
    private lateinit var databaseService: DatabaseService

    // Post Repository instance
    private lateinit var postRepository: PostRepository

    @Before
    fun setUp() {
        // Setup API Key with a Fake for testing
        Networking.API_KEY = TestConstants.API_KEY

        // Initialize PostRepository
        postRepository = PostRepository(networkService, databaseService)
    }

    @After
    fun tearDown() {
        // No-op
    }

    @Test
    fun getAllPostsList_requestDoAllPostsListCall() {
        // Get a random User
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Set the values for the Ids of First Post and Last Post to be fetched
        val firstPostId = "0"
        val lastPostId = "10"

        // Get some Posts for test
        val mockedPosts = DataModelObjectProvider.getRandomPosts(lastPostId.toInt())

        // Mock the NetworkService "doAllPostsListCall" to return a SingleSource of AllPostsListResponse
        doReturn(Single.just(AllPostsListResponse("statusCode", 200, "message", mockedPosts)))
            .`when`(networkService)
            .doAllPostsListCall(
                firstPostId = firstPostId,
                lastPostId = lastPostId,
                userId = user.id,
                accessToken = user.accessToken,
                apiKey = Networking.API_KEY
            )

        // Call to PostRepository "getAllPostsList"
        val allPostsList =
            postRepository.getAllPostsList(firstPostId, lastPostId, user).blockingGet()

        // Verify that there was a call to "doAllPostsListCall" API method of NetworkService
        verify(networkService).doAllPostsListCall(
            firstPostId = firstPostId,
            lastPostId = lastPostId,
            userId = user.id,
            accessToken = user.accessToken,
            apiKey = Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the list of Posts returned for the PostRepository "getAllPostsList" call
        // is same as the mocked list of Posts
        assertEquals(mockedPosts, allPostsList)
    }

    @Test
    fun givenPostNotYetLikedByUser_onLikePost_requestDoLikePostCallAndUpdateLikedByList() {
        // Get a random User
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Get a random Post
        val post = DataModelObjectProvider.getSingleRandomPost()

        // Mock the NetworkService "doLikePostCall" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doLikePostCall(
                LikePostRequest(post.id),
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "likePost" with a copy of the Post
        val updatedPost = postRepository.likePost(post.shallowCopy(), user).blockingGet()

        // Verify that there was a call to "doLikePostCall" API method of NetworkService
        verify(networkService).doLikePostCall(
            LikePostRequest(post.id),
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the updated Post returned for the PostRepository "likePost" call
        // is different from the original Post passed to the call
        assertNotEquals(post, updatedPost)

        // Assert that the Liked By User list in the updated Post is not empty
        assertTrue(updatedPost.likedBy!!.isNotEmpty())

        // Assert that the Liked By User list in the updated Post contains the User provided to the call
        assertTrue(updatedPost.likedBy!!.any { likedByUser: Post.User -> likedByUser.id == user.id })
    }

    @Test
    fun givenPostAlreadyLikedByUser_onLikePost_requestDoLikePostCallWithLikedByListUnchanged() {
        // Get a random User
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Get a random Post
        val post = DataModelObjectProvider.getSingleRandomPost()

        // Mock the NetworkService "doLikePostCall" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doLikePostCall(
                LikePostRequest(post.id),
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "likePost" to get the given Post to be liked by the given User
        postRepository.likePost(post, user).blockingGet()

        // Call again to PostRepository "likePost", but this time with the copy of
        // the currently updated Post, to check for any further updates has occurred on the same
        // (Not a possible scenario in the UI flow)
        val updatedPost = postRepository.likePost(post.shallowCopy(), user).blockingGet()

        // Verify that there was 2 calls to "doLikePostCall" API method of NetworkService
        verify(networkService, times(2)).doLikePostCall(
            LikePostRequest(post.id),
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the updated Post returned for the second PostRepository "likePost" call
        // is same as the original Post updated by the first call
        assertEquals(post, updatedPost)

        // Assert that the Liked By User list in the updated Post of the second call is not empty
        assertTrue(updatedPost.likedBy!!.isNotEmpty())

        // Assert that the Liked By User list in the updated Post of the second call
        // contains only one User even after the second call to update the same
        assertTrue(updatedPost.likedBy!!.count { likedByUser: Post.User -> likedByUser.id == user.id } == 1)
    }

    @Test
    fun givenPostLikedByUser_onUnLikePost_requestDoUnlikePostCallAndRemoveUserInLikedByList() {
        // Get two random Users
        val (user1: User, user2: User) = DataModelObjectProvider.getRandomUsers(2)

        // Get a random Post
        val post = DataModelObjectProvider.getSingleRandomPost()

        // "user1" and "user2" will like the Post, and then
        // "user1" will unlike the same Post

        // Make the Post to be liked by user "user1" : START
        // Mock the NetworkService "doLikePostCall" with "user1" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doLikePostCall(
                LikePostRequest(post.id),
                user1.id,
                user1.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "likePost" to get the given Post to be liked by "user1"
        postRepository.likePost(post, user1).blockingGet()

        // Verify that there was a call to "doLikePostCall" API method of NetworkService with "user1"
        verify(networkService).doLikePostCall(
            LikePostRequest(post.id),
            user1.id,
            user1.accessToken,
            Networking.API_KEY
        )
        // Make the Post to be liked by user "user1" : END

        // Make the Post to be liked by user "user2" : START
        // Mock the NetworkService "doLikePostCall" with "user2" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doLikePostCall(
                LikePostRequest(post.id),
                user2.id,
                user2.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "likePost" to get the given Post to be liked by "user2"
        postRepository.likePost(post, user2).blockingGet()

        // Verify that there was a call to "doLikePostCall" API method of NetworkService with "user2"
        verify(networkService).doLikePostCall(
            LikePostRequest(post.id),
            user2.id,
            user2.accessToken,
            Networking.API_KEY
        )
        // Make the Post to be liked by user "user2" : END

        // Make the Post to be unliked by user "user1" : START
        // Mock the NetworkService "doUnlikePostCall" with "user1" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doUnlikePostCall(
                UnlikePostRequest(post.id),
                user1.id,
                user1.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "unLikePost" to get the copy of the liked Post
        // to be unliked by "user1"
        val unlikedPost = postRepository.unLikePost(post.shallowCopy(), user1).blockingGet()

        // Verify that there was a call to "doUnlikePostCall" API method of NetworkService with "user1"
        verify(networkService).doUnlikePostCall(
            UnlikePostRequest(post.id),
            user1.id,
            user1.accessToken,
            Networking.API_KEY
        )
        // Make the Post to be unliked by user "user1" : END

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the unliked Post returned for the PostRepository "unLikePost" call
        // is different from the liked Post passed to the call
        assertNotEquals(post, unlikedPost)

        // Assert that the Liked By User list in the unliked Post is not empty
        // since only one of the two users have unliked
        assertTrue(unlikedPost.likedBy!!.isNotEmpty())

        // Assert that the Liked By User list in the unliked Post does not contain "user1"
        // which unliked the Post
        assertTrue(unlikedPost.likedBy!!.none { likedByUser: Post.User -> likedByUser.id == user1.id })
    }

    @Test
    fun givenPostLikedBySomeOtherUser_onUnLikePost_requestDoUnlikePostCallWithLikedByListUnchanged() {
        // Get three random Users
        val (user1: User, user2: User, user3: User) = DataModelObjectProvider.getRandomUsers(3)

        // Get a random Post
        val post = DataModelObjectProvider.getSingleRandomPost()

        // "user1" and "user2" will like the Post, and then
        // "user3" will try to unlike the Post (Not a possible scenario in the UI flow)

        // Make the Post to be liked by user "user1" : START
        // Mock the NetworkService "doLikePostCall" with "user1" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doLikePostCall(
                LikePostRequest(post.id),
                user1.id,
                user1.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "likePost" to get the given Post to be liked by "user1"
        postRepository.likePost(post, user1).blockingGet()

        // Verify that there was a call to "doLikePostCall" API method of NetworkService with "user1"
        verify(networkService).doLikePostCall(
            LikePostRequest(post.id),
            user1.id,
            user1.accessToken,
            Networking.API_KEY
        )
        // Make the Post to be liked by user "user1" : END

        // Make the Post to be liked by user "user2" : START
        // Mock the NetworkService "doLikePostCall" with "user2" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doLikePostCall(
                LikePostRequest(post.id),
                user2.id,
                user2.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "likePost" to get the given Post to be liked by "user2"
        postRepository.likePost(post, user2).blockingGet()

        // Verify that there was a call to "doLikePostCall" API method of NetworkService with "user2"
        verify(networkService).doLikePostCall(
            LikePostRequest(post.id),
            user2.id,
            user2.accessToken,
            Networking.API_KEY
        )
        // Make the Post to be liked by user "user2" : END

        // Make the Post to be unliked by user "user3" : START
        // Mock the NetworkService "doUnlikePostCall" with "user3" to return a SingleSource of GeneralResponse
        doReturn(Single.just(GeneralResponse("statusCode", 200, "Success")))
            .`when`(networkService)
            .doUnlikePostCall(
                UnlikePostRequest(post.id),
                user3.id,
                user3.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "unLikePost" to get the copy of the liked Post
        // to be unliked by "user3"
        val unlikedPost = postRepository.unLikePost(post.shallowCopy(), user3).blockingGet()

        // Verify that there was a call to "doUnlikePostCall" API method of NetworkService with "user3"
        verify(networkService).doUnlikePostCall(
            UnlikePostRequest(post.id),
            user3.id,
            user3.accessToken,
            Networking.API_KEY
        )
        // Make the Post to be unliked by user "user3" : END

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the unliked Post returned for the PostRepository "unLikePost" call
        // is same as the liked Post passed to the call since user "user3"
        // who unliked the Post was not present in that liked list
        assertEquals(post, unlikedPost)

        // Assert that the Liked By User list in the unliked Post is not empty
        // since users "user1" and "user2" have not unliked the Post
        assertTrue(unlikedPost.likedBy!!.isNotEmpty())

        // Assert that the Liked By User list in the unliked Post does not contain "user3"
        assertTrue(unlikedPost.likedBy!!.none { likedByUser: Post.User -> likedByUser.id == user3.id })

        // Assert that the Liked By User list in the unliked Post contains "user1"
        assertTrue(unlikedPost.likedBy!!.any { likedByUser: Post.User -> likedByUser.id == user1.id })

        // Assert that the Liked By User list in the unliked Post contains "user2"
        assertTrue(unlikedPost.likedBy!!.any { likedByUser: Post.User -> likedByUser.id == user2.id })
    }

    @Test
    fun createPost_requestDoPostCreationCallAndReturnPostCreated() {
        // Get a random User to create a Post
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Get a random Post created by the above User
        val mockedPost = DataModelObjectProvider.getSingleRandomPostOfUser(user)

        // Mock the NetworkService "doPostCreationCall" to return a SingleSource of "PostCreationResponse"
        doReturn(
            Single.just(
                PostCreationResponse(
                    statusCode = "statusCode",
                    status = 200,
                    post = mockedPost.toMyPostItem(),
                    message = "Success"
                )
            )
        )
            .`when`(networkService)
            .doPostCreationCall(
                PostCreationRequest(
                    imageUrl = mockedPost.imageUrl,
                    imageWidth = mockedPost.imageWidth!!,
                    imageHeight = mockedPost.imageHeight!!,
                ),
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "createPost" to create a Post for the given user
        val postCreated = postRepository.createPost(
            imageUrl = mockedPost.imageUrl,
            imageWidth = mockedPost.imageWidth!!,
            imageHeight = mockedPost.imageHeight!!,
            user = user
        ).blockingGet()

        // Verify that there was a call to "doPostCreationCall" API method of NetworkService
        verify(networkService).doPostCreationCall(
            PostCreationRequest(
                imageUrl = mockedPost.imageUrl,
                imageWidth = mockedPost.imageWidth!!,
                imageHeight = mockedPost.imageHeight!!,
            ),
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the Post created in response to the PostRepository "createPost" call
        // is same as the Post used for mocking
        assertEquals(mockedPost, postCreated)
    }

    @Test
    fun getUserPostsList_requestDoMyPostsListCallAndReturnListOfPostsCreatedByTheUser() {
        // Get a random User
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Get 10 random Posts created by the above User for mocking
        val mockedPosts = DataModelObjectProvider.getRandomPostsOfUser(user, 10)

        // Mock the NetworkService "doMyPostsListCall" with the above user
        // to return a SingleSource of MyPostsListResponse
        doReturn(Single.just(MyPostsListResponse(
            statusCode = "statusCode",
            status = 200,
            message = "Success",
            posts = mockedPosts.map { post: Post -> post.toMyPostItem() }
        )))
            .`when`(networkService)
            .doMyPostsListCall(
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "getUserPostsList" to get a list of the above user's posts
        val postsReturned = postRepository.getUserPostsList(user).blockingGet()

        // Verify that there was a call to "doMyPostsListCall" API method of NetworkService
        verify(networkService).doMyPostsListCall(
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the Posts of User returned in response to the PostRepository
        // "getUserPostsList" call is same as the Posts of User used for mocking
        assertEquals(mockedPosts, postsReturned)
    }

    @Test
    fun getPostDetail_requestDoPostDetailCallAndReturnTheRequiredPost() {
        // Get a random User
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Get a random Post
        val mockedPost = DataModelObjectProvider.getSingleRandomPost()

        // Mock the NetworkService "doPostDetailCall" for the copy of the above post
        // to return a SingleSource of PostDetailResponse
        doReturn(
            Single.just(
                PostDetailResponse(
                    statusCode = "statusCode",
                    status = 200,
                    message = "Success",
                    post = mockedPost.shallowCopy()
                )
            )
        )
            .`when`(networkService)
            .doPostDetailCall(
                postId = mockedPost.id,
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "getPostDetail" to get the Post details of the given Post Id
        val postReturned = postRepository.getPostDetail(mockedPost.id, user).blockingGet()

        // Verify that there was a call to "doPostDetailCall" API method of NetworkService
        verify(networkService).doPostDetailCall(
            postId = mockedPost.id,
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the Post returned in response to the PostRepository "getPostDetail" call
        // is same as the Post used for mocking
        assertEquals(mockedPost, postReturned)
    }

    @Test
    fun givenGeneralResponseStatus200_onDeletePost_requestDoDeletePostCallAndReturnSuccessResource() {
        // Get a random User
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Get a random Post created by the above User
        val post = DataModelObjectProvider.getSingleRandomPostOfUser(user)

        // Expected message of API response
        val expectedMessage = "Success"

        // Mock the NetworkService "doDeletePostCall" for the above Post
        // to return a SingleSource of GeneralResponse with status as 200 and message as "Success"
        doReturn(Single.just(GeneralResponse("statusCode", 200, expectedMessage)))
            .`when`(networkService)
            .doDeletePostCall(
                postId = post.id,
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "deletePost" to delete the given post and get the response
        val responseResource: Resource<String> = postRepository.deletePost(post, user).blockingGet()

        // Verify that there was a call to "doDeletePostCall" API method of NetworkService
        verify(networkService).doDeletePostCall(
            postId = post.id,
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the Status of the Resource returned in response
        // to the PostRepository "deletePost" call is the Status "SUCCESS"
        assertEquals(Status.SUCCESS, responseResource.status)

        // Assert that the Data of the Resource returned in response
        // to the PostRepository "deletePost" call is the expected message "Success"
        assertEquals(expectedMessage, responseResource.peekData())
    }

    @Test
    fun givenGeneralResponseStatusNot200_onDeletePost_requestDoDeletePostCallAndReturnUnknownResource() {
        // Get a random User
        val user = DataModelObjectProvider.getSingleRandomUser()

        // Get a random Post created by the above User
        val post = DataModelObjectProvider.getSingleRandomPostOfUser(user)

        // Expected message of API response
        val expectedMessage = "Failure"

        // Mock the NetworkService "doDeletePostCall" for the above Post
        // to return a SingleSource of GeneralResponse with status as 500 and message as "Failure"
        doReturn(Single.just(GeneralResponse("statusCode", 500, expectedMessage)))
            .`when`(networkService)
            .doDeletePostCall(
                postId = post.id,
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        // Call to PostRepository "deletePost" to delete the given post and get the response
        val responseResource: Resource<String> = postRepository.deletePost(post, user).blockingGet()

        // Verify that there was a call to "doDeletePostCall" API method of NetworkService
        verify(networkService).doDeletePostCall(
            postId = post.id,
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

        // Verify that there is no more interactions with the NetworkService API
        verifyNoMoreInteractions(networkService)

        // Assert that the Status of the Resource returned in response
        // to the PostRepository "deletePost" call is the Status "UNKNOWN"
        assertEquals(Status.UNKNOWN, responseResource.status)

        // Assert that the Data of the Resource returned in response
        // to the PostRepository "deletePost" call is the expected message "Failure"
        assertEquals(expectedMessage, responseResource.peekData())
    }

}