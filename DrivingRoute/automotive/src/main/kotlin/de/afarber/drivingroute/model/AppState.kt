package de.afarber.drivingroute.model

enum class AppState {
    IDLE,               // Both markers and Cancel FAB are hidden, waiting for first user touch
    START_MARKER,       // Start marker has been placed and is shown, Cancel FAB is shown
    FINISH_MARKER,      // Finish marker placed and shown, Start marker is shown, Cancel FAB is shown
    ROUTE_DISPLAYED     // Both markers and Cancel FAB are shown, route calculated and drawn as polyline
}