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
package urstark.solstice.lyrics

import android.content.Context
import urstark.solstice.constants.EnablePaxsenixNeteaseLyricsKey
import urstark.solstice.paxsenix.PaxsenixLyrics
import urstark.solstice.utils.dataStore
import urstark.solstice.utils.get

object PaxsenixNeteaseLyricsProvider : LyricsProvider {
    override val name = "Paxsenix: NetEase"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnablePaxsenixNeteaseLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
    ): Result<String> = PaxsenixLyrics.getNeteaseLyrics(title, artist, duration)

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
