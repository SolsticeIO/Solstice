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

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import urstark.solstice.BuildConfig
import urstark.solstice.R
import urstark.solstice.constants.DarkModeKey
import urstark.solstice.constants.PureBlackKey
import urstark.solstice.constants.UpdateChannel
import urstark.solstice.defaultUpdateChannel
import urstark.solstice.musicrecognition.MusicRecognitionRoute
import urstark.solstice.ui.component.BottomSheet
import urstark.solstice.ui.component.BottomSheetMenu
import urstark.solstice.ui.component.LocalMenuState
import urstark.solstice.ui.component.rememberBottomSheetState
import urstark.solstice.ui.screens.BrowseScreen
import urstark.solstice.ui.screens.artist.ArtistAlbumsScreen
import urstark.solstice.ui.screens.artist.ArtistItemsScreen
import urstark.solstice.ui.screens.artist.ArtistScreen
import urstark.solstice.ui.screens.artist.ArtistSongsScreen
import urstark.solstice.ui.screens.library.LibraryScreen
import urstark.solstice.ui.screens.library.LocalSongScreen
import urstark.solstice.ui.screens.musicrecognition.MusicRecognitionScreen
import urstark.solstice.ui.screens.playlist.AutoPlaylistScreen
import urstark.solstice.ui.screens.playlist.CachePlaylistScreen
import urstark.solstice.ui.screens.playlist.LocalPlaylistScreen
import urstark.solstice.ui.screens.playlist.OnlinePlaylistScreen
import urstark.solstice.ui.screens.playlist.SpotifyPlaylistScreen
import urstark.solstice.ui.screens.playlist.TopPlaylistScreen
import urstark.solstice.ui.screens.search.OnlineSearchResult
import urstark.solstice.ui.screens.search.OnlineSearchResultArgument
import urstark.solstice.ui.screens.search.OnlineSearchResultRoute
import urstark.solstice.ui.screens.search.OnlineSearchResultRoutePrefix
import urstark.solstice.ui.screens.search.SearchScreen
import urstark.solstice.ui.screens.settings.AboutScreen
import urstark.solstice.ui.screens.settings.AccountSettings
import urstark.solstice.ui.screens.settings.AiIntegrationSettings
import urstark.solstice.ui.screens.settings.AodCustomizedScreen
import urstark.solstice.ui.screens.settings.AppearanceSettings
import urstark.solstice.ui.screens.settings.BackupAndRestore
import urstark.solstice.ui.screens.settings.ChangelogScreen
import urstark.solstice.ui.screens.settings.ContentSettings
import urstark.solstice.ui.screens.settings.CustomizeBackground
import urstark.solstice.ui.screens.settings.DarkMode
import urstark.solstice.ui.screens.settings.DebugSettings
import urstark.solstice.ui.screens.settings.DiscordSettings
import urstark.solstice.ui.screens.settings.HiddenPlaylistsScreen
import urstark.solstice.ui.screens.settings.IntegrationScreen
import urstark.solstice.ui.screens.settings.InternetSettings
import urstark.solstice.ui.screens.settings.LastFMSettings
import urstark.solstice.ui.screens.settings.LyricsAnimationSettings
import urstark.solstice.ui.screens.settings.LyricsSettings
import urstark.solstice.ui.screens.settings.MusicTogetherScreen
import urstark.solstice.ui.screens.settings.PalettePickerScreen
import urstark.solstice.ui.screens.settings.PlayerSettings
import urstark.solstice.ui.screens.settings.PoTokenScreen
import urstark.solstice.ui.screens.settings.PrivacySettings
import urstark.solstice.ui.screens.settings.SettingsScreen
import urstark.solstice.ui.screens.settings.StorageSettings
import urstark.solstice.ui.screens.settings.ThemeCreatorScreen
import urstark.solstice.ui.screens.settings.UpdateScreen
import urstark.solstice.ui.utils.ShowMediaInfo
import urstark.solstice.utils.rememberEnumPreference
import urstark.solstice.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    latestVersionName: () -> String,
    disableAnimations: Boolean = false,
    onClearUpdateBadge: () -> Unit = {},
) {
    composable(Screens.Home.route) {
        HomeScreen(navController)
    }
    composable(
        Screens.Library.route,
    ) {
        LibraryScreen(navController)
    }
    composable(Screens.Social.route) {
        SocialScreen(navController)
    }
    composable(Screens.Search.route) {
        SearchScreen(
            navController = navController,
            onSearchClick = { query ->
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("openSearch", true)
                if (query != null) {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("voiceSearchQuery", query)
                }
            },
        )
    }
    composable("local_songs") {
        LocalSongScreen(navController)
    }
    composable("history") {
        HistoryScreen(navController)
    }
    composable("stats") {
        StatsScreen(navController)
    }
    composable("news") {
        NewsScreen(navController)
    }
    composable(
        route = "view_news/{newsId}",
        arguments =
            listOf(
                navArgument("newsId") { type = NavType.StringType },
            ),
    ) {
        ViewNewsScreen(navController)
    }
    composable(
        route = "year_in_music?year={year}",
        arguments =
            listOf(
                navArgument("year") {
                    type = NavType.IntType
                    defaultValue = -1
                },
            ),
    ) { backStackEntry ->
        val selectedYear = backStackEntry.arguments?.getInt("year")?.takeIf { it > 0 }
        YearInMusicScreen(
            navController = navController,
            initialYear = selectedYear,
        )
    }
    composable(MusicRecognitionRoute) {
        MusicRecognitionScreen(navController)
    }
    composable(Screens.MoodAndGenres.route) {
        MoodAndGenresScreen(navController)
    }
    composable("account") {
        AccountScreen(navController, scrollBehavior)
    }
    composable("new_release") {
        NewReleaseScreen(navController, scrollBehavior)
    }
    composable("charts_screen") {
        ChartsScreen(navController)
    }
    composable(
        route = "browse/{browseId}",
        arguments =
            listOf(
                navArgument("browseId") {
                    type = NavType.StringType
                },
            ),
    ) {
        BrowseScreen(
            navController,
            scrollBehavior,
            it.arguments?.getString("browseId"),
        )
    }
    composable(
        route = OnlineSearchResultRoute,
        arguments =
            listOf(
                navArgument(OnlineSearchResultArgument) {
                    type = NavType.StringType
                },
            ),
        enterTransition = {
            if (disableAnimations) {
                fadeIn(tween(0))
            } else {
                fadeIn(tween(250))
            }
        },
        exitTransition = {
            if (disableAnimations) {
                fadeOut(tween(0))
            } else if (targetState.destination.route?.startsWith(OnlineSearchResultRoutePrefix) == true) {
                fadeOut(tween(200))
            } else {
                fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
            }
        },
        popEnterTransition = {
            if (disableAnimations) {
                fadeIn(tween(0))
            } else if (initialState.destination.route?.startsWith(OnlineSearchResultRoutePrefix) == true) {
                fadeIn(tween(250))
            } else {
                fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
            }
        },
        popExitTransition = {
            if (disableAnimations) {
                fadeOut(tween(0))
            } else {
                fadeOut(tween(200))
            }
        },
    ) {
        OnlineSearchResult(navController)
    }
    composable(
        route = "album/{albumId}",
        arguments =
            listOf(
                navArgument("albumId") {
                    type = NavType.StringType
                },
            ),
    ) {
        AlbumScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}",
        arguments =
            listOf(
                navArgument("artistId") {
                    type = NavType.StringType
                },
            ),
    ) {
        ArtistScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}/songs",
        arguments =
            listOf(
                navArgument("artistId") {
                    type = NavType.StringType
                },
            ),
    ) {
        ArtistSongsScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}/albums",
        arguments =
            listOf(
                navArgument("artistId") {
                    type = NavType.StringType
                },
            ),
    ) {
        ArtistAlbumsScreen(navController, scrollBehavior)
    }
    composable(
        route = "artist/{artistId}/items?browseId={browseId}&params={params}",
        arguments =
            listOf(
                navArgument("artistId") {
                    type = NavType.StringType
                },
                navArgument("browseId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("params") {
                    type = NavType.StringType
                    nullable = true
                },
            ),
    ) {
        ArtistItemsScreen(navController, scrollBehavior)
    }
    composable(
        route = "online_playlist/{playlistId}",
        arguments =
            listOf(
                navArgument("playlistId") {
                    type = NavType.StringType
                },
            ),
    ) {
        OnlinePlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "local_playlist/{playlistId}",
        arguments =
            listOf(
                navArgument("playlistId") {
                    type = NavType.StringType
                },
            ),
    ) {
        LocalPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "spotify_playlist/{playlistId}",
        arguments =
            listOf(
                navArgument("playlistId") {
                    type = NavType.StringType
                },
            ),
    ) {
        SpotifyPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "auto_playlist/{playlist}",
        arguments =
            listOf(
                navArgument("playlist") {
                    type = NavType.StringType
                },
            ),
    ) {
        AutoPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "cache_playlist/{playlist}",
        arguments =
            listOf(
                navArgument("playlist") {
                    type = NavType.StringType
                },
            ),
    ) {
        CachePlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "top_playlist/{top}",
        arguments =
            listOf(
                navArgument("top") {
                    type = NavType.StringType
                },
            ),
    ) {
        TopPlaylistScreen(navController, scrollBehavior)
    }
    composable(
        route = "youtube_browse/{browseId}?params={params}",
        arguments =
            listOf(
                navArgument("browseId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("params") {
                    type = NavType.StringType
                    nullable = true
                },
            ),
    ) {
        YouTubeBrowseScreen(navController)
    }
    composable("settings") {
        SettingsScreen(navController, scrollBehavior, latestVersionName())
    }
    composable("settings/account") {
        AccountSettings(navController, scrollBehavior, latestVersionName())
    }
    composable("settings/hidden_playlists") {
        HiddenPlaylistsScreen(navController, scrollBehavior)
    }
    composable("settings/appearance") {
        AppearanceSettings(navController, scrollBehavior)
    }
    composable("settings/appearance/aod_customized") {
        AodCustomizedScreen(navController, scrollBehavior)
    }
    composable("settings/appearance/palette_picker") {
        PalettePickerScreen(navController)
    }
    composable("settings/appearance/lyrics_animations") {
        LyricsAnimationSettings(navController, scrollBehavior)
    }
    composable("settings/appearance/theme_creator") {
        ThemeCreatorScreen(navController)
    }
    composable("settings/content") {
        ContentSettings(navController)
    }
    composable("settings/lyrics") {
        LyricsSettings(navController)
    }
    composable("settings/internet") {
        InternetSettings(navController, scrollBehavior)
    }
    composable("settings/player") {
        PlayerSettings(navController, scrollBehavior)
    }
    composable("settings/storage") {
        StorageSettings(navController, scrollBehavior)
    }
    composable("settings/privacy") {
        PrivacySettings(navController, scrollBehavior)
    }
    composable("settings/backup_restore") {
        BackupAndRestore(navController, scrollBehavior)
    }
    composable("settings/discord") {
        DiscordSettings(navController, scrollBehavior)
    }
    composable("settings/integration") {
        IntegrationScreen(navController, scrollBehavior)
    }
    composable("settings/ai_integration") {
        AiIntegrationSettings(navController)
    }
    composable("settings/music_together") {
        MusicTogetherScreen(navController, scrollBehavior)
    }
    composable("settings/lastfm") {
        LastFMSettings(navController, scrollBehavior)
    }
    composable("settings/discord/experimental") {
        urstark.solstice.ui.screens.settings
            .DiscordExperimental(navController)
    }
    composable("settings/misc") {
        DebugSettings(navController)
    }
    if (BuildConfig.UPDATER_AVAILABLE) {
        composable("settings/update") {
            UpdateScreen(navController, scrollBehavior, onUpToDate = onClearUpdateBadge)
        }
    }
    composable(
        route = "settings/changelog?channel={channel}",
        arguments =
            listOf(
                navArgument("channel") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
    ) { backStackEntry ->
        val channelName = backStackEntry.arguments?.getString("channel")
        val channel =
            channelName?.let {
                runCatching { UpdateChannel.valueOf(it) }.getOrNull()
            } ?: defaultUpdateChannel
        ChangelogScreen(navController, scrollBehavior, channel = channel)
    }
    composable("settings/about") {
        AboutScreen(navController, scrollBehavior)
    }
    composable("settings/po_token") {
        PoTokenScreen(navController, scrollBehavior)
    }
    composable("customize_background") {
        CustomizeBackground(navController)
    }
    composable(
        route = "$LOGIN_ROUTE?$LOGIN_URL_ARGUMENT={$LOGIN_URL_ARGUMENT}",
        arguments =
            listOf(
                navArgument(LOGIN_URL_ARGUMENT) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
    ) { backStackEntry ->
        LoginScreen(
            navController,
            startUrl = backStackEntry.arguments?.getString(LOGIN_URL_ARGUMENT)?.let(Uri::decode),
        )
    }
}
