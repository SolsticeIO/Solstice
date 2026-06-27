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
import urstark.solstice.innertube.pages.BrowseResult
import urstark.solstice.utils.dataStore
import urstark.solstice.utils.get
import urstark.solstice.utils.reportException
import javax.inject.Inject

@HiltViewModel
class YouTubeBrowseViewModel
    @Inject
    constructor(
        @ApplicationContext val context: Context,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val browseId = savedStateHandle.get<String>("browseId")!!
        private val params = savedStateHandle.get<String>("params")

        val result = MutableStateFlow<BrowseResult?>(null)

        init {
            viewModelScope.launch {
                YouTube
                    .browse(browseId, params)
                    .onSuccess {
                        val hideVideo = context.dataStore.get(HideVideoKey, false)
                        result.value = it.filterExplicit(context.dataStore.get(HideExplicitKey, false)).filterVideo(hideVideo)
                    }.onFailure {
                        reportException(it)
                    }
            }
        }
    }
