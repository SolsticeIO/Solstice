# Implementation Plan: Full Social Overhaul & Element X Settings Parity

This plan details the steps to make the Social (Matrix) tab fully functional, persist user sessions, and completely overhaul the settings UI to match the depth and functionality of Element X Android.

## 1. Authentication & Session Persistence
- **Session Restoration**: Currently, the app loses the Matrix session when closed. I will implement session persistence using `MatrixManager` and the Rust SDK's session restoring capabilities so that the user remains logged in across app restarts.
- **Login UI**: Add a circular progress indicator (loader) to the Login button in `SocialLoginScreen.kt` to provide visual feedback while the authentication request is processing.

## 2. Comprehensive Search Functionality (MatrixSearchScreen)
- **People / Friend Search**: Fully wire the search to query the Matrix user directory.
- **Public Room Search**: Implement querying for the homeserver's public rooms list so users can discover and join spaces.
- *(Note: Message search is excluded from this phase as requested).*

## 3. Modular Settings & Element X UI Parity
I will refactor `SocialSettingsScreen.kt` to act as a navigation hub and move all individual settings pages into `app/src/main/kotlin/urstark/solstice/ui/screens/social/settings/`. I will copy the comprehensive UI and sub-settings modules from Element X, skipping irrelevant ones (Preferences, Keyboard, Appearance, Voice/Video, Labs).

### [NEW] `AccountSettingsScreen.kt`
- Profile picture display and upload functionality.
- Display name modification.
- Matrix ID display and account management links.

### [NEW] `SessionsSettingsScreen.kt`
- Detailed list of the current session and other active sessions.
- Session verification status, IP address, and last seen timestamps.
- Ability to sign out of other sessions.

### [NEW] `NotificationsSettingsScreen.kt`
- Global push rule toggles (Enable/Disable all).
- Mention and keyword notification management.

### [NEW] `SecuritySettingsScreen.kt` & `EncryptionSettingsScreen.kt`
- **Security**: Privacy settings (Email/Phone discovery), Ignored users list.
- **Encryption**: Secure backup enablement, recovery key generation, and session verification flows.

### [MODIFY] `SocialSettingsScreen.kt`
- Serve as the root list routing to these new modular screens, styled beautifully to match Solstice.

## 4. Comprehensive Chat & Messaging Features
The chat experience will be fully built out (no mocks) with the following features:
- **Core Messaging**: Send messages to personal DMs and Groups.
- **Message Actions**: Edit, Delete, Pin, and Unpin messages.
- **Message States**: Read receipts (tick marks) and "Edited" indicators.
- **Reactions**: Send and display emoji reactions.
- **Replies**: Swipe-to-reply gesture, reply UI logic, and clicking a reply to jump/scroll to the original message.
- **Context Menu**: Long-press (hold) on a message to show options (Reply, Copy, Delete, etc.).
- **Chat Management**: Mute and Unmute individual chats and group DMs.
- **Profile Viewing**: Group profile view models and public profile view models.
- **Group Roles**: Support for Member, Moderator, and Admin roles with visible Admin tags in groups.
- **Typing Indicators**: Show typing animations when a user is composing a message.
- **Room List Previews**: Format last messages in the room list intelligently:
  - If sent by the current user (DM or Group): `"You: [message]"`
  - If sent by another user in a DM: `"[message]"`
  - If sent by another user in a Group: `"[User Name]: [message]"`

## Verification
- App compiles successfully (`./gradlew assembleGmsMobileUniversalDebug`).
- Closing and reopening the app keeps the user logged in.
- Settings screens mirror the structure and functionality of Element X.
- All listed chat features (swipe-to-reply, long-press menus, reactions, roles) function correctly against a real Matrix server.
