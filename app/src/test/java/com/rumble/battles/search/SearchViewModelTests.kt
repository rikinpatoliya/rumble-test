package com.rumble.battles.search

import androidx.lifecycle.SavedStateHandle
import com.rumble.battles.navigation.RumblePath
import com.rumble.battles.search.presentation.searchScreen.SearchViewModel
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.domain.useCases.DeleteAllQueriesUseCase
import com.rumble.domain.search.domain.useCases.DeleteQueryUseCase
import com.rumble.domain.search.domain.useCases.GetAutoCompleteQueriesUseCase
import com.rumble.domain.search.domain.useCases.GetFilteredQueriesUseCase
import com.rumble.domain.search.domain.useCases.GetRecentQueriesUseCase
import com.rumble.domain.search.domain.useCases.SaveQueryUseCase
import com.rumble.domain.search.domain.useCases.UpdateQueryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class SearchViewModelTests {
    private val stateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val mockSaveQueryUseCase = mockk<SaveQueryUseCase>(relaxed = true)
    private val mockUpdateQueryUseCase = mockk<UpdateQueryUseCase>(relaxed = true)
    private val mockGetRecentQueriesUseCase = mockk<GetRecentQueriesUseCase>(relaxed = true)
    private val mockGetFilteredQueriesUseCase = mockk<GetFilteredQueriesUseCase>(relaxed = true)
    private val mockDeleteQueriesUseCase = mockk<DeleteQueryUseCase>(relaxed = true)
    private val deleteAllQueriesUseCase = mockk<DeleteAllQueriesUseCase>(relaxed = true)
    private val getAutoCompleteQueriesUseCase = mockk<GetAutoCompleteQueriesUseCase>(relaxed = true)
    private val testQuery = "TEST"
    private lateinit var viewModel: SearchViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        every { stateHandle.get<String>(RumblePath.QUERY.path) } returns ""
        every { stateHandle.get<String>(RumblePath.NAVIGATION.path) } returns ""
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testEmptyQueryList() {
        coEvery { mockGetRecentQueriesUseCase.invoke() } returns emptyList()
        viewModel = SearchViewModel(
            stateHandle,
            mockSaveQueryUseCase,
            mockUpdateQueryUseCase,
            mockDeleteQueriesUseCase,
            deleteAllQueriesUseCase,
            mockGetRecentQueriesUseCase,
            mockGetFilteredQueriesUseCase,
            getAutoCompleteQueriesUseCase
        )
        assert(viewModel.state.value.recentQueryList.isEmpty())
    }

    @Test
    fun testQueryListWithValues() {
        coEvery { mockGetRecentQueriesUseCase.invoke() } returns listOf(RecentQuery(query = testQuery))
        viewModel = SearchViewModel(
            stateHandle,
            mockSaveQueryUseCase,
            mockUpdateQueryUseCase,
            mockDeleteQueriesUseCase,
            deleteAllQueriesUseCase,
            mockGetRecentQueriesUseCase,
            mockGetFilteredQueriesUseCase,
            getAutoCompleteQueriesUseCase
        )
        assert(viewModel.state.value.recentQueryList.isNotEmpty())
    }

    @Test
    fun testSaveQuery() {
        viewModel = SearchViewModel(
            stateHandle,
            mockSaveQueryUseCase,
            mockUpdateQueryUseCase,
            mockDeleteQueriesUseCase,
            deleteAllQueriesUseCase,
            mockGetRecentQueriesUseCase,
            mockGetFilteredQueriesUseCase,
            getAutoCompleteQueriesUseCase
        )
        viewModel.saveQuery(testQuery)
        coVerify { mockSaveQueryUseCase.invoke(testQuery) }
    }

    @Test
    fun testUpdateQuery() {
        val recentQuery = RecentQuery(query = testQuery)
        viewModel = SearchViewModel(
            stateHandle,
            mockSaveQueryUseCase,
            mockUpdateQueryUseCase,
            mockDeleteQueriesUseCase,
            deleteAllQueriesUseCase,
            mockGetRecentQueriesUseCase,
            mockGetFilteredQueriesUseCase,
            getAutoCompleteQueriesUseCase
        )
        viewModel.updateQuery(recentQuery)
        coVerify { mockUpdateQueryUseCase.invoke(recentQuery) }
    }
}