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
package urstark.solstice.onboarding

import com.google.common.collect.ImmutableList
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import urstark.solstice.BuildConfig
import urstark.solstice.R

class ObserveOnboardingDataUseCase
    @Inject
    constructor(
        private val repository: OnboardingRepository,
    ) {
        operator fun invoke(refreshSignals: Flow<Int>): Flow<OnboardingData> =
            repository.observeShouldShowOnboarding()
                .combine(refreshSignals) { shouldShowOnboarding, _ ->
                    OnboardingData(
                        shouldShowOnboarding = shouldShowOnboarding,
                        permissions = repository.currentPermissions(),
                    )
                }
                .flowOn(Dispatchers.IO)
    }

class BuildOnboardingUiStateUseCase
    @Inject
    constructor() {
        operator fun invoke(
            data: OnboardingData,
            currentPage: Int,
        ): OnboardingUiState =
            OnboardingUiState(
                shouldShowOnboarding = data.shouldShowOnboarding,
                currentPage = currentPage.coerceIn(0, pages.lastIndex),
                variantLabelResId = variantLabelResId(),
                versionName = BuildConfig.VERSION_NAME,
                pages = pages,
                permissions = ImmutableList.copyOf(data.permissions.map { it.toUiModel() }),
                communityActions = communityActions,
            )

        private fun variantLabelResId(): Int =
            if (BuildConfig.DISTRIBUTION == DISTRIBUTION_GMS) {
                R.string.onboarding_gms_variant
            } else {
                R.string.onboarding_foss_variant
            }

        private fun OnboardingPermissionData.toUiModel(): OnboardingPermissionUiModel =
            OnboardingPermissionUiModel(
                id = id,
                titleResId = id.titleResId(),
                descriptionResId = id.descriptionResId(),
                iconResId = id.iconResId(),
                status = status,
                action = action,
            )

        private fun OnboardingPermissionId.titleResId(): Int =
            when (this) {
                OnboardingPermissionId.NOTIFICATIONS -> R.string.onboarding_permission_notifications_title
                OnboardingPermissionId.LOCAL_AUDIO -> R.string.permission_storage_title
                OnboardingPermissionId.MICROPHONE -> R.string.music_recognition_permission_title
                OnboardingPermissionId.BLUETOOTH_CONNECT -> R.string.onboarding_permission_bluetooth_connect_title
                OnboardingPermissionId.NETWORK -> R.string.onboarding_permission_network_title
                OnboardingPermissionId.PLAYBACK_SERVICE -> R.string.onboarding_permission_playback_service_title
                OnboardingPermissionId.AUDIO_SETTINGS -> R.string.onboarding_permission_audio_settings_title
                OnboardingPermissionId.APP_INSTALLATION -> R.string.onboarding_permission_app_installation_title
                OnboardingPermissionId.BLUETOOTH_SCAN -> R.string.onboarding_permission_bluetooth_scan_title
            }

        private fun OnboardingPermissionId.descriptionResId(): Int =
            when (this) {
                OnboardingPermissionId.NOTIFICATIONS -> R.string.onboarding_permission_notifications_desc
                OnboardingPermissionId.LOCAL_AUDIO -> R.string.permission_storage_desc
                OnboardingPermissionId.MICROPHONE -> R.string.music_recognition_permission_desc
                OnboardingPermissionId.BLUETOOTH_CONNECT -> R.string.onboarding_permission_bluetooth_connect_desc
                OnboardingPermissionId.NETWORK -> R.string.onboarding_permission_network_desc
                OnboardingPermissionId.PLAYBACK_SERVICE -> R.string.onboarding_permission_playback_service_desc
                OnboardingPermissionId.AUDIO_SETTINGS -> R.string.onboarding_permission_audio_settings_desc
                OnboardingPermissionId.APP_INSTALLATION -> R.string.onboarding_permission_app_installation_desc
                OnboardingPermissionId.BLUETOOTH_SCAN -> R.string.onboarding_permission_bluetooth_scan_desc
            }

        private fun OnboardingPermissionId.iconResId(): Int =
            when (this) {
                OnboardingPermissionId.NOTIFICATIONS -> R.drawable.solar_bell
                OnboardingPermissionId.LOCAL_AUDIO -> R.drawable.solar_folder
                OnboardingPermissionId.MICROPHONE -> R.drawable.solar_microphone_2
                OnboardingPermissionId.BLUETOOTH_CONNECT -> R.drawable.solar_bluetooth
                OnboardingPermissionId.NETWORK -> R.drawable.solar_wi_fi_router
                OnboardingPermissionId.PLAYBACK_SERVICE -> R.drawable.solar_music_library_2
                OnboardingPermissionId.AUDIO_SETTINGS -> R.drawable.solar_settings
                OnboardingPermissionId.APP_INSTALLATION -> R.drawable.solar_download
                OnboardingPermissionId.BLUETOOTH_SCAN -> R.drawable.solar_bluetooth
            }

        private companion object {
            const val DISTRIBUTION_GMS = "gms"

            val pages =
                ImmutableList.of(
                    OnboardingPageUiModel(
                        id = OnboardingPageId.WELCOME,
                        titleResId = R.string.onboarding_welcome_title,
                        subtitleResId = R.string.onboarding_welcome_subtitle,
                        iconResId = R.drawable.app_icon_small,
                    ),
                    OnboardingPageUiModel(
                        id = OnboardingPageId.PERMISSIONS,
                        titleResId = R.string.onboarding_permissions_title,
                        subtitleResId = R.string.onboarding_permissions_subtitle,
                        iconResId = R.drawable.solar_folder_security,
                    ),
                    OnboardingPageUiModel(
                        id = OnboardingPageId.COMMUNITY,
                        titleResId = R.string.onboarding_community_title,
                        subtitleResId = R.string.onboarding_community_subtitle,
                        iconResId = R.drawable.solar_star,
                    ),
                )

            val communityActions =
                ImmutableList.of(
                    OnboardingCommunityActionUiModel(
                        id = "github",
                        titleResId = R.string.support_development_star,
                        descriptionResId = R.string.onboarding_community_github_desc,
                        iconResId = R.drawable.github,
                        url = "https://github.com/SolsticeIO/Solstice",
                    ),
                    OnboardingCommunityActionUiModel(
                        id = "discord",
                        titleResId = R.string.onboarding_community_discord_title,
                        descriptionResId = R.string.onboarding_community_telegram_desc,
                        iconResId = R.drawable.discord,
                        url = "https://discord.gg/XF2fpb9rTq",
                    ),
                    OnboardingCommunityActionUiModel(
                        id = "telegram",
                        titleResId = R.string.onboarding_community_telegram_title,
                        descriptionResId = R.string.onboarding_community_telegram_desc,
                        iconResId = R.drawable.telegram,
                        url = "https://t.me/SolsticeIO",
                    ),
                    OnboardingCommunityActionUiModel(
                        id = "donate",
                        titleResId = R.string.about_content_desc_donate,
                        descriptionResId = R.string.onboarding_community_donate_desc,
                        iconResId = R.drawable.solar_hand_money,
                        url = "https://solsticeio.vercel.app/donate",
                    ),
                )
        }
    }

class CompleteOnboardingUseCase
    @Inject
    constructor(
        private val repository: OnboardingRepository,
    ) {
        suspend operator fun invoke() {
            repository.markCompleted()
        }
    }
