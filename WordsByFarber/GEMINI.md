# Gemini Code Review and TODO List

This document provides a summary of the current state of the "Words by Farber" Android app, based on a review of the source code against the requirements outlined in `CLAUDE.md`. It also includes a TODO list of items that need to be implemented to complete the app.

## Current State

The project is well-structured and follows the architecture described in `CLAUDE.md`. The data, domain, and UI layers are clearly separated, and the use of repositories, use cases, and ViewModels is consistent.

### What's Implemented:

*   **Core App Structure:** The basic application structure, including the main activity, application class, and package structure, is in place.
*   **Data Layer:**
    *   Room database with entities and DAOs for words and players.
    *   Repositories for managing preferences and dictionary data.
    *   Network components for downloading and parsing dictionary files.
*   **Domain Layer:**
    *   Use cases for core business logic, such as language selection, dictionary download, and word search.
*   **UI Layer:**
    *   Most of the screens (1-6) are implemented with Jetpack Compose.
    *   Navigation between screens is handled by Jetpack Navigation.
    *   ViewModels are used to manage UI state and business logic.
    *   Many reusable UI components have been created.
*   **Utils:**
    *   Utility functions for string manipulation, network checks, and constants are available.

### What's Missing:

*   **Screens 7-17:** The UI for most of the screens from the "Home" screen onwards (Top Players, Your Profile, etc.) is not yet implemented.
*   **Gameplay Logic:** The core gameplay logic for "Game 1" and "Game 2" is missing. The screens currently display static grids.
*   **Top Players Functionality:** The downloading and parsing of top players data is not implemented.
*   **"Find a Word" Hashing:** The hashing of the search query in the "Find a Word" screen needs to be implemented.
*   **Localization:** While the app is designed to support multiple languages, the actual localization of strings is not fully implemented.
*   **Error Handling:** While some error handling is in place, it could be improved to provide more specific feedback to the user.
*   **Unit Tests:** As stated in `CLAUDE.md`, unit tests have not been implemented yet.

## TODO List

### High Priority

*   [ ] Implement the UI for the remaining screens (7-17), starting with the "Top Players" screen.
*   [ ] Implement the downloading and parsing of top players data from the `top_url`.
*   [ ] Implement the hashing of the search query in the "Find a Word" screen.
*   [ ] Implement the core gameplay logic for "Game 1" and "Game 2".

### Medium Priority

*   [ ] Fully implement localization for all strings in the app.
*   [ ] Improve error handling to provide more specific and user-friendly messages.
*   [ ] Implement the "Your Profile" screen, displaying the user's details.
*   [ ] Implement the word list screens (2-letter, 3-letter, rare letters).

### Low Priority

*   [ ] Implement the "Preferences", "Help", "Privacy Policy", and "Terms of Service" screens.
*   [ ] Write unit tests for the data, domain, and UI layers.
*   [ ] Add animations and transitions to improve the user experience.

- The myUid field in the Language data class is a temporary placeholder for the user's unique identifier until a proper authentication system is in place. It is used to select a user to be displayed in the "My profile" screen.
- The only allowed values in shared prefs are the string values "language" and (in future) "login"


