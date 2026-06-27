import os

file_path = "app/src/main/kotlin/urstark/solstice/viewmodels/AboutViewModel.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace leadDeveloper with urstark
old_lead = """                leadDeveloper =
                    TeamMember(
                        avatarUrl = "https://avatars.githubusercontent.com/u/107134739?v=4",
                        name = "morie",
                        positionResId = R.string.about_position_lead_dev,
                        profileUrl = "https://github.com/rukamori",
                        links =
                            AboutLinkCollection.of(
                                AboutLinkUiModel(
                                    id = "github",
                                    iconResId = R.drawable.github,
                                    labelResId = R.string.about_content_desc_github,
                                    url = "https://github.com/rukamori",
                                ),
                                AboutLinkUiModel(
                                    id = "website",
                                    iconResId = R.drawable.website,
                                    labelResId = R.string.about_content_desc_website,
                                    url = "https://koiiverse.cloud",
                                ),
                                AboutLinkUiModel(
                                    id = "discord",
                                    iconResId = R.drawable.alternate_email,
                                    labelResId = R.string.about_content_desc_discord,
                                    url = "https://discord.com/users/886971572668219392",
                                ),
                            ),
                    ),"""

new_lead = """                leadDeveloper =
                    TeamMember(
                        avatarUrl = "https://github.com/urstark.png",
                        name = "urstark",
                        positionResId = R.string.about_position_lead_dev,
                        profileUrl = "https://github.com/urstark",
                        links =
                            AboutLinkCollection.of(
                                AboutLinkUiModel(
                                    id = "github",
                                    iconResId = R.drawable.github,
                                    labelResId = R.string.about_content_desc_github,
                                    url = "https://github.com/urstark",
                                ),
                            ),
                    ),"""

content = content.replace(old_lead, new_lead)

# Add rukamori to respecters
old_respecters = """                respecters =
                    TeamMemberCollection.of("""

new_respecters = """                respecters =
                    TeamMemberCollection.of(
                        TeamMember(
                            avatarUrl = "https://avatars.githubusercontent.com/u/107134739?v=4",
                            name = "rukamori",
                            positionResId = R.string.about_position_rukamori,
                            profileUrl = "https://github.com/rukamori",
                            links =
                                AboutLinkCollection.of(
                                    AboutLinkUiModel(
                                        id = "github",
                                        iconResId = R.drawable.github,
                                        labelResId = R.string.about_content_desc_github,
                                        url = "https://github.com/rukamori",
                                    ),
                                ),
                        ),"""

content = content.replace(old_respecters, new_respecters)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated AboutViewModel.kt")
