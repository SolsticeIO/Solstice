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
import android.util.Log
import urstark.solstice.constants.EnableUnisonLyricsKey
import urstark.solstice.unison.Unison
import urstark.solstice.utils.GlobalLog
import urstark.solstice.utils.dataStore
import urstark.solstice.utils.get

object UnisonLyricsProvider : LyricsProvider {
    init {
        Unison.logger = { message ->
            GlobalLog.append(Log.INFO, "Unison", message)
        }
    }

    override val name = "Unison"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnableUnisonLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
    ): Result<String> =
        Unison.getLyrics(
            videoId = id,
            title = title,
            artist = artist,
            album = album,
            durationSeconds = duration,
        )

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        Unison.getAllLyrics(
            videoId = id,
            title = title,
            artist = artist,
            album = album,
            durationSeconds = duration,
            callback = callback,
        )
    }
}
