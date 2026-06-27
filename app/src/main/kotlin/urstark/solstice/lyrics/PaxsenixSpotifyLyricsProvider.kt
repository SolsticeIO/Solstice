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
package urstark.solstice.lyrics

import android.content.Context
import urstark.solstice.constants.EnablePaxsenixSpotifyLyricsKey
import urstark.solstice.paxsenix.PaxsenixLyrics
import urstark.solstice.utils.dataStore
import urstark.solstice.utils.get

object PaxsenixSpotifyLyricsProvider : LyricsProvider {
    override val name = "Paxsenix: Spotify"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnablePaxsenixSpotifyLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
    ): Result<String> = PaxsenixLyrics.getSpotifyLyrics(title, artist, duration)

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        getLyrics(id, title, artist, album, duration).onSuccess(callback)
    }
}
