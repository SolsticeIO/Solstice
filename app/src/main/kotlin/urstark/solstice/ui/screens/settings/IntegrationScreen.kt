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
package urstark.solstice.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import urstark.solstice.LocalPlayerAwareWindowInsets
import urstark.solstice.R
import urstark.solstice.constants.ListenBrainzEnabledKey
import urstark.solstice.constants.ListenBrainzTokenKey
import urstark.solstice.ui.component.IconButton
import urstark.solstice.ui.component.InfoLabel
import urstark.solstice.ui.component.PreferenceEntry
import urstark.solstice.ui.component.PreferenceGroup
import urstark.solstice.ui.component.SwitchPreference
import urstark.solstice.ui.component.TextFieldDialog
import urstark.solstice.ui.utils.backToMain
import urstark.solstice.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrationScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current

    val (listenBrainzEnabled, onListenBrainzEnabledChange) = rememberPreference(ListenBrainzEnabledKey, false)
    val (listenBrainzToken, onListenBrainzTokenChange) = rememberPreference(ListenBrainzTokenKey, "")

    var showListenBrainzTokenEditor = remember { mutableStateOf(false) }

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .verticalScroll(rememberScrollState())
            .padding(bottom = SettingsDimensions.ScreenBottomPadding),
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Top,
                ),
            ),
        )

        PreferenceGroup(title = stringResource(R.string.general)) {
            item {
                PreferenceEntry(
                    title = { Text(stringResource(R.string.discord_integration)) },
                    icon = { Icon(painterResource(R.drawable.solar_record), null) },
                    onClick = {
                        navController.navigate("settings/discord")
                    },
                )
            }
        }

        PreferenceGroup(title = stringResource(R.string.scrobbling)) {
            item {
                PreferenceEntry(
                    title = { Text(stringResource(R.string.lastfm_integration)) },
                    icon = { Icon(painterResource(R.drawable.solar_key), null) },
                    onClick = {
                        navController.navigate("settings/lastfm")
                    },
                )
            }

            item {
                SwitchPreference(
                    title = { Text(stringResource(R.string.listenbrainz_scrobbling)) },
                    description = stringResource(R.string.listenbrainz_scrobbling_description),
                    icon = { Icon(painterResource(R.drawable.solar_key), null) },
                    checked = listenBrainzEnabled,
                    onCheckedChange = onListenBrainzEnabledChange,
                )
            }

            item {
                PreferenceEntry(
                    title = {
                        Text(
                            if (listenBrainzToken.isBlank()) {
                                stringResource(
                                    R.string.set_listenbrainz_token,
                                )
                            } else {
                                stringResource(R.string.edit_listenbrainz_token)
                            },
                        )
                    },
                    icon = { Icon(painterResource(R.drawable.solar_key), null) },
                    onClick = { showListenBrainzTokenEditor.value = true },
                )
            }
        }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.integration)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.solar_arrow_left),
                    contentDescription = null,
                )
            }
        },
    )

    if (showListenBrainzTokenEditor.value) {
        TextFieldDialog(
            initialTextFieldValue =
                androidx.compose.ui.text.input
                    .TextFieldValue(listenBrainzToken),
            onDone = { data ->
                onListenBrainzTokenChange(data)
                showListenBrainzTokenEditor.value = false
            },
            onDismiss = { showListenBrainzTokenEditor.value = false },
            singleLine = true,
            maxLines = 1,
            isInputValid = {
                it.isNotEmpty()
            },
            extraContent = {
                InfoLabel(text = stringResource(R.string.listenbrainz_scrobbling_description))
            },
        )
    }
}
