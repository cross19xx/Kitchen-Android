package com.cross19xx.filmnest.ui.search

import app.cash.turbine.test
import com.cross19xx.filmnest.data.model.SearchResult
import com.cross19xx.filmnest.data.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var repository: SearchRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() = runTest(testDispatcher) {
        val viewModel = SearchViewModel(repository)

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `blank query stays Idle and never hits the repository`() = runTest(testDispatcher) {
        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("   ")
            advanceTimeBy(DEBOUNCE_DURATION + 100)
            runCurrent()

            // A blank query re-emits Idle, but StateFlow conflates equal values,
            // so no new item is produced — the state simply stays Idle.
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { repository.searchMulti(any()) }
    }

    @Test
    fun `a query emits Success with results after the debounce window`() =
        runTest(testDispatcher) {
            val results = listOf(SearchResult.Person(id = 1, name = "Tom Cruise", profilePath = null))
            coEvery { repository.searchMulti("tom") } returns results

            val viewModel = SearchViewModel(repository)

            viewModel.uiState.test {
                assertEquals(SearchUiState.Idle, awaitItem())

                viewModel.onQueryChange("tom")
                advanceTimeBy(DEBOUNCE_DURATION + 100)
                runCurrent()

                val state = expectMostRecentItem()
                assertTrue(state is SearchUiState.Success)
                assertEquals(1, (state as SearchUiState.Success).results.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `a query with no matches emits Empty`() = runTest(testDispatcher) {
        coEvery { repository.searchMulti("zzz") } returns emptyList()

        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("zzz")
            advanceTimeBy(DEBOUNCE_DURATION + 100)
            runCurrent()

            assertEquals(SearchUiState.Empty, expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a repository error emits Error with the message`() = runTest(testDispatcher) {
        coEvery { repository.searchMulti("boom") } throws RuntimeException("Network down")

        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("boom")
            advanceTimeBy(DEBOUNCE_DURATION + 100)
            runCurrent()

            val state = expectMostRecentItem() as SearchUiState.Error
            assertEquals("Network down", state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `debounce only searches for the final query in a rapid sequence`() =
        runTest(testDispatcher) {
            coEvery { repository.searchMulti(any()) } returns emptyList()

            val viewModel = SearchViewModel(repository)

            viewModel.uiState.test {
                assertEquals(SearchUiState.Idle, awaitItem())

                // Type rapidly, each keystroke faster than the debounce window
                viewModel.onQueryChange("s")
                advanceTimeBy(100)
                viewModel.onQueryChange("st")
                advanceTimeBy(100)
                viewModel.onQueryChange("sta")
                advanceTimeBy(100)
                viewModel.onQueryChange("star")
                advanceTimeBy(DEBOUNCE_DURATION + 100)
                runCurrent()

                cancelAndIgnoreRemainingEvents()
            }

            // Only the final, settled query triggers a network call
            coVerify(exactly = 1) { repository.searchMulti("star") }
            coVerify(exactly = 0) { repository.searchMulti("s") }
            coVerify(exactly = 0) { repository.searchMulti("st") }
            coVerify(exactly = 0) { repository.searchMulti("sta") }
        }
}
