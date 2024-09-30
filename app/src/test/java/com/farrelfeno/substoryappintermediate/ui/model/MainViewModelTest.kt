package com.farrelfeno.substoryappintermediate.ui.model

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.farrelfeno.substoryappintermediate.adapter.MainAdapter
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.repository.StoryRepository
import com.farrelfeno.substoryappintermediate.response.ListStoryItem
import com.farrelfeno.substoryappintermediate.ui.DataDummy
import com.farrelfeno.substoryappintermediate.ui.MainDispatcherRule
import com.farrelfeno.substoryappintermediate.ui.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userPreference: UserPreference
    private lateinit var viewModel: MainViewModel

    private lateinit var logStaticMock: MockedStatic<Log>

    @Before
    fun setUp() {
        viewModel = MainViewModel(userPreference, storyRepository)
        logStaticMock = Mockito.mockStatic(Log::class.java)
    }

    @After
    fun tearDown() {
        logStaticMock.close()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
       logStaticMock.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }.thenReturn(false)

        val dummyToken = "Bearer 1234"
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStory(dummyToken)).thenReturn(expectedStory)
        val actualStory: PagingData<ListStoryItem> = viewModel.getStory(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {

        logStaticMock.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }.thenReturn(false)

        val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWNDTU1NYjFmS2hzN3k1TGUiLCJpYXQiOjE2OTk0MDE0NzZ9.ELOfNWcQsAIRFju9RKWwiHqynUH90E7LwGCJROShuxQ"
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStory(dummyToken)).thenReturn(expectedStory)
        val actualStory: PagingData<ListStoryItem> = viewModel.getStory(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback =  noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}