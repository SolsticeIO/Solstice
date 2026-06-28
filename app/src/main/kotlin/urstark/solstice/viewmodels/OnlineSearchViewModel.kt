/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 * 
 * Based on ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * 
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */
package urstark.solstice.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import urstark.solstice.constants.HideExplicitKey
import urstark.solstice.constants.HideVideoKey
import urstark.solstice.innertube.YouTube
import urstark.solstice.innertube.YouTube.SearchFilter.Companion.FILTER_ALBUM
import urstark.solstice.innertube.YouTube.SearchFilter.Companion.FILTER_ARTIST
import urstark.solstice.innertube.YouTube.SearchFilter.Companion.FILTER_COMMUNITY_PLAYLIST
import urstark.solstice.innertube.YouTube.SearchFilter.Companion.FILTER_FEATURED_PLAYLIST
import urstark.solstice.innertube.YouTube.SearchFilter.Companion.FILTER_SONG
import urstark.solstice.innertube.YouTube.SearchFilter.Companion.FILTER_VIDEO
import urstark.solstice.innertube.models.SongItem
import urstark.solstice.innertube.models.YTItem
import urstark.solstice.innertube.models.filterExplicit
import urstark.solstice.innertube.models.filterVideo
import urstark.solstice.innertube.pages.SearchSummaryPage
import urstark.solstice.models.ItemsPage
import urstark.solstice.ui.screens.search.OnlineSearchResultArgument
import urstark.solstice.ui.screens.search.decodeOnlineSearchQuery
import urstark.solstice.utils.dataStore
import urstark.solstice.utils.get
import urstark.solstice.utils.reportException
import javax.inject.Inject

enum class OnlineSearchSort {
    DEFAULT,
    VIEWS,
}

@HiltViewModel
class OnlineSearchViewModel
    @Inject
    constructor(
        @ApplicationContext val context: Context,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val query =
            decodeOnlineSearchQuery(
                savedStateHandle.get<String>(OnlineSearchResultArgument).orEmpty(),
            )
        val filter = MutableStateFlow<YouTube.SearchFilter?>(null)
        val sort = MutableStateFlow(OnlineSearchSort.DEFAULT)
        var summaryPage by mutableStateOf<SearchSummaryPage?>(null)
        val viewStateMap = mutableStateMapOf<String, ItemsPage?>()

        private val allModeFilters =
            listOf(
                FILTER_SONG,
                FILTER_VIDEO,
                FILTER_ALBUM,
                FILTER_ARTIST,
                FILTER_COMMUNITY_PLAYLIST,
                FILTER_FEATURED_PLAYLIST,
            )
        private var isSummaryLoading = false
        private val loadingFilters = mutableSetOf<String>()

        init {
            viewModelScope.launch {
                filter.collect { selectedFilter ->
                    if (selectedFilter == null) {
                        viewModelScope.launch {
                            loadSummaryIfNeeded()
                        }
                        allModeFilters.forEach { allModeFilter ->
                            viewModelScope.launch {
                                loadFilterIfNeeded(allModeFilter)
                            }
                        }
                    } else {
                        loadFilterIfNeeded(selectedFilter)
                    }
                }
            }
        }

        private suspend fun loadSummaryIfNeeded() {
            if (summaryPage != null || isSummaryLoading) return

            isSummaryLoading = true
            try {
                YouTube
                    .searchSummary(query)
                    .onSuccess {
                        summaryPage =
                            it
                                .filterExplicit(context.dataStore.get(HideExplicitKey, false))
                                .filterVideo(context.dataStore.get(HideVideoKey, false))
                    }.onFailure {
                        reportException(it)
                    }
            } finally {
                isSummaryLoading = false
            }
        }

        private suspend fun loadFilterIfNeeded(filter: YouTube.SearchFilter) {
            val filterKey = filter.value
            if (viewStateMap.containsKey(filterKey) || !loadingFilters.add(filterKey)) return

            try {
                YouTube
                    .search(query, filter)
                    .onSuccess { result ->
                        viewStateMap[filterKey] =
                            ItemsPage(
                                result.items
                                    .distinctBy { it.id }
                                    .filterExplicit(
                                        context.dataStore.get(
                                            HideExplicitKey,
                                            false,
                                        ),
                                    ).filterVideo(context.dataStore.get(HideVideoKey, false)),
                                result.continuation,
                            )
                    }.onFailure {
                        reportException(it)
                    }
            } finally {
                loadingFilters.remove(filterKey)
            }
        }

        fun loadMore() {
            val filter = filter.value?.value
            viewModelScope.launch {
                if (filter == null) return@launch
                val viewState = viewStateMap[filter] ?: return@launch
                val continuation = viewState.continuation
                if (continuation != null) {
                    val searchResult =
                        YouTube.searchContinuation(continuation).getOrNull() ?: return@launch
                    viewStateMap[filter] =
                        ItemsPage(
                            (viewState.items + searchResult.items).distinctBy { it.id },
                            searchResult.continuation,
                        )
                }
            }
        }

        fun updateSort(sort: OnlineSearchSort) {
            this.sort.value = sort
        }

        fun sortedItems(
            items: List<YTItem>,
            sort: OnlineSearchSort = this.sort.value,
        ): List<YTItem> =
            when (sort) {
                OnlineSearchSort.DEFAULT -> {
                    items
                }

                OnlineSearchSort.VIEWS -> {
                    items
                        .withIndex()
                        .sortedWith(
                            compareByDescending<IndexedValue<YTItem>> {
                                (it.value as? SongItem)?.viewCount ?: Long.MIN_VALUE
                            }.thenBy { it.index },
                        ).map { it.value }
                }
            }
    }
