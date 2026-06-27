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
package urstark.solstice.ui.menu

import urstark.solstice.db.entities.PlaylistSongMap
import urstark.solstice.innertube.YouTube

suspend fun removeSongFromRemotePlaylist(
    playlistBrowseId: String,
    playlistSongMap: PlaylistSongMap,
): Result<Unit> =
    runCatching {
        val setVideoIds =
            playlistSongMap.setVideoId?.let(::listOf)
                ?: YouTube.playlistEntrySetVideoIds(playlistBrowseId, playlistSongMap.songId).getOrThrow()

        setVideoIds
            .distinct()
            .forEach { setVideoId ->
                YouTube.removeFromPlaylist(playlistBrowseId, playlistSongMap.songId, setVideoId).getOrThrow()
            }
    }
