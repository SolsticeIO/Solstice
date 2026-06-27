/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 * 
 * @@BASED_ON_SOLSTICE_TOKEN@@
 * © Rukamori — github.com/rukamori
 * 
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */
package urstark.solstice.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import urstark.solstice.constants.HideExplicitKey
import urstark.solstice.constants.HideVideoKey
import urstark.solstice.db.MusicDatabase
import urstark.solstice.innertube.YouTube
import urstark.solstice.innertube.models.filterExplicit
import urstark.solstice.innertube.models.filterVideo
import urstark.solstice.innertube.pages.ExplorePage
import urstark.solstice.utils.dataStore
import urstark.solstice.utils.get
import urstark.solstice.utils.reportException
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel
    @Inject
    constructor(
        @ApplicationContext val context: Context,
        val database: MusicDatabase,
    ) : ViewModel() {
        val explorePage = MutableStateFlow<ExplorePage?>(null)

        private suspend fun load() {
            YouTube
                .explore()
                .onSuccess { page ->
                    val artists: MutableMap<Int, String> = mutableMapOf()
                    val favouriteArtists: MutableMap<Int, String> = mutableMapOf()
                    database.allArtistsByPlayTime().first().let { list ->
                        var favIndex = 0
                        for ((artistsIndex, artist) in list.withIndex()) {
                            artists[artistsIndex] = artist.id
                            if (artist.artist.bookmarkedAt != null) {
                                favouriteArtists[favIndex] = artist.id
                                favIndex++
                            }
                        }
                    }
                    explorePage.value =
                        page.copy(
                            newReleaseAlbums =
                                page.newReleaseAlbums
                                    .sortedBy { album ->
                                        val artistIds = album.artists.orEmpty().mapNotNull { it.id }
                                        val firstArtistKey =
                                            artistIds.firstNotNullOfOrNull { artistId ->
                                                if (artistId in favouriteArtists.values) {
                                                    favouriteArtists.entries.firstOrNull { it.value == artistId }?.key
                                                } else {
                                                    artists.entries.firstOrNull { it.value == artistId }?.key
                                                }
                                            } ?: Int.MAX_VALUE
                                        firstArtistKey
                                    }.filterExplicit(
                                        context.dataStore.get(HideExplicitKey, false),
                                    ).filterVideo(context.dataStore.get(HideVideoKey, false)),
                        )
                }.onFailure {
                    reportException(it)
                }
        }

        init {
            viewModelScope.launch(Dispatchers.IO) {
                load()
            }
        }
    }
