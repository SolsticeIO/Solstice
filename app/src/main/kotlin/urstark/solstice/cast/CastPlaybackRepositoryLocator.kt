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
package urstark.solstice.cast

import android.content.Context

object CastPlaybackRepositoryLocator {
    @Volatile private var repository: CastPlaybackRepository? = null

    fun get(context: Context): CastPlaybackRepository =
        repository ?: synchronized(this) {
            repository ?: DefaultCastPlaybackRepository(context.applicationContext).also { repository = it }
        }
}
