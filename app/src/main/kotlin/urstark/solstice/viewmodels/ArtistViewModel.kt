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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import urstark.solstice.constants.HideExplicitKey
import urstark.solstice.db.MusicDatabase
import urstark.solstice.extensions.filterExplicit
import urstark.solstice.extensions.filterExplicitAlbums
import urstark.solstice.innertube.YouTube
import urstark.solstice.innertube.models.filterExplicit
import urstark.solstice.innertube.pages.ArtistPage
import urstark.solstice.utils.dataStore
import urstark.solstice.utils.get
import urstark.solstice.utils.reportException
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ArtistViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val database: MusicDatabase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val artistId = savedStateHandle.get<String>("artistId")!!
        var artistPage by mutableStateOf<ArtistPage?>(null)
        val libraryArtist =
            database
                .artist(artistId)
                .stateIn(viewModelScope, SharingStarted.Lazily, null)
        val librarySongs =
            context.dataStore.data
                .map { it[HideExplicitKey] ?: false }
                .distinctUntilChanged()
                .flatMapLatest { hideExplicit ->
                    database.artistSongsByCreateDateAsc(artistId).map { it.filterExplicit(hideExplicit) } // show all
                    // database.artistSongsPreview(artistId).map { it.filterExplicit(hideExplicit) } // only preview
                }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        val libraryAlbums =
            context.dataStore.data
                .map { it[HideExplicitKey] ?: false }
                .distinctUntilChanged()
                .flatMapLatest { hideExplicit ->
                    database.artistAlbumsPreview(artistId).map { it.filterExplicitAlbums(hideExplicit) }
                }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        init {
            // Load artist page and reload when hide explicit setting changes
            viewModelScope.launch {
                context.dataStore.data
                    .map { it[HideExplicitKey] ?: false }
                    .distinctUntilChanged()
                    .collect {
                        fetchArtistsFromYTM()
                    }
            }
        }

        fun fetchArtistsFromYTM() {
            viewModelScope.launch {
                val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                YouTube
                    .artist(artistId)
                    .onSuccess { page ->
                        val filteredSections =
                            page.sections
                                .map { section ->
                                    section.copy(items = section.items.filterExplicit(hideExplicit))
                                }

                        artistPage = page.copy(sections = filteredSections)

                        withContext(Dispatchers.IO) {
                            database.artist(artistId).firstOrNull()?.artist?.let { artistEntity ->
                                database.update(artistEntity, page)
                            }
                        }
                    }.onFailure {
                        reportException(it)
                    }
            }
        }
    }
