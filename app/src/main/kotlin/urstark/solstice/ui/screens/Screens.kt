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
package urstark.solstice.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import urstark.solstice.R

@Immutable
sealed class Screens(
    @StringRes val titleId: Int,
    @DrawableRes val iconIdInactive: Int,
    @DrawableRes val iconIdActive: Int,
    val route: String,
) {
    object Home : Screens(
        titleId = R.string.home,
        iconIdInactive = R.drawable.solar_home,
        iconIdActive = R.drawable.solar_home,
        route = "home",
    )

    object Search : Screens(
        titleId = R.string.search,
        iconIdInactive = R.drawable.solar_magnifer,
        iconIdActive = R.drawable.solar_magnifer,
        route = "search",
    )

    object Library : Screens(
        titleId = R.string.filter_library,
        iconIdInactive = R.drawable.solar_library,
        iconIdActive = R.drawable.solar_library,
        route = "library",
    )

    object Social : Screens(
        titleId = R.string.social,
        iconIdInactive = R.drawable.solar_users_group_rounded,
        iconIdActive = R.drawable.solar_users_group_rounded,
        route = "social",
    )

    object MoodAndGenres : Screens(
        titleId = R.string.mood_and_genres,
        iconIdInactive = R.drawable.solar_sale,
        iconIdActive = R.drawable.solar_sale,
        route = "mood_and_genres",
    )

    companion object {
        val MainScreens = listOf(Home, Search, Social, Library)
        val TvMainScreens = listOf(Home, Search, Social, Library)
    }
}
