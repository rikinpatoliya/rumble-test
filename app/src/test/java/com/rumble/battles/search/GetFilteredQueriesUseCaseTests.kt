package com.rumble.battles.search

import com.rumble.domain.search.domain.useCases.GetFilteredQueriesUseCase
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.model.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetFilteredQueriesUseCaseTests {

    private val filter = "aaa"
    private val mockRepository = mockk<SearchRepository>(relaxed = true)
    private val query1 = RecentQuery(query = "ccc bbb aaa")
    private val query2 = RecentQuery(query = "ccc aaa bbb ")
    private val query3 = RecentQuery(query = "aaa ccc bbb")
    private val useCase = GetFilteredQueriesUseCase(mockRepository)

    @Before
    fun setup() {
        coEvery { mockRepository.filterQueries(filter) } returns listOf(query1, query2, query3)
    }

    @Test
    fun testInvoke() = runBlocking {
        val result = useCase(filter)
        assert(result.first() == query3)
        assert(result[1] == query2)
        assert(result[2] == query1)
    }
}