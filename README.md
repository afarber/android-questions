# android-questions

The files are public domain - unless there is a different note (by their respective authors) on the top of some files.

- [Maps](#maps)
- [Word](#word)
- [QR Codes and NFC](#qr-codes-and-nfc)
- [UI](#ui)
- [Authentication and Messaging](#authentication-and-messaging)
- [Misc](#misc)

## Maps

[ComposeMap](ComposeMap/)

Kotlin/Compose example wrapping OpenMapView in a reusable OpenStreetMap composable and moving its camera when the selected place changes.

![ComposeMap screenshot](ComposeMap/screenshot.gif)

[ComposeOnlyMap](ComposeOnlyMap/)

Kotlin/Compose example displaying OpenMapView without an XML layout or explicit User-Agent configuration.

![ComposeOnlyMap screenshot](ComposeOnlyMap/screenshot.gif)

[CurvedLine](CurvedLine/)

Compose map comparing a straight line, a geodesic, and a Bezier arc between two draggable markers.

![CurvedLine screenshot](CurvedLine/screenshot.gif)

[CustomMarkerIcon](CustomMarkerIcon/)

Java map example displaying bus-stop markers with a custom round drawable anchored at its center.

![CustomMarkerIcon screenshot](CustomMarkerIcon/screenshot.gif)

[CustomZoom](CustomZoom/)

Java map example with custom buttons that animate zoom while built-in zoom controls and zoom gestures are disabled.

![CustomZoom screenshot](CustomZoom/screenshot.gif)

[DarkModeMap](DarkModeMap/)

Compose map preserving its camera and a user-drawn polygon when switching between light and dark mode.

![DarkModeMap screenshot](DarkModeMap/screenshot.gif)

[DottedTrack](DottedTrack/)

Java map example drawing the same GPX track as a solid, dashed, or dotted polyline.

![DottedTrack screenshot](DottedTrack/screenshot.gif)

[DraggableMarker](DraggableMarker/)

Kotlin map example with a draggable marker using a custom icon.

![DraggableMarker screenshot](DraggableMarker/screenshot.gif)

[DragPolyline](DragPolyline/)

Java map example reshaping a polyline by dragging markers at its vertices.

![DragPolyline screenshot](DragPolyline/screenshot.gif)

[DrivingRoute](DrivingRoute/)

Android Automotive route planner using Compose, OpenMapView, and the public OSRM backend to route between two tapped locations.

![DrivingRoute screenshot](DrivingRoute/screenshot.gif)

[FitAllMarkers](FitAllMarkers/)

Java map example fitting the camera to a collection of markers with newLatLngBounds.

![FitAllMarkers screenshot](FitAllMarkers/screenshot.gif)

[FitRoute](FitRoute/)

Java map example fitting a route with newLatLngBounds before the map has completed layout.

![FitRoute screenshot](FitRoute/screenshot.gif)

[InfoWindowClick](InfoWindowClick/)

Java map example opening a dialog when a marker's info window is tapped.

![InfoWindowClick screenshot](InfoWindowClick/screenshot.gif)

[LabelMarker](LabelMarker/)

Compose map example drawing a hollow pin with a rounded two-line label that can change at runtime.

![LabelMarker screenshot](LabelMarker/screenshot.gif)

[LaunchMaps](LaunchMaps/)

Java example launching an installed map application with a geo URI containing a labeled coordinate.

![LaunchMaps screenshot](LaunchMaps/screenshot.png)

[LiveMarker](LiveMarker/)

Java map example moving one drawable marker to the device's live location and centering the camera on the first fix.

![LiveMarker screenshot](LiveMarker/screenshot.gif)

[MapInScroll](MapInScroll/)

Java form with an OpenMapView inside a ScrollView, allowing map drags to pan while form drags scroll the page.

![MapInScroll screenshot](MapInScroll/screenshot.gif)

[MapInTabs](MapInTabs/)

Compose navigation-tab example preserving a map's camera when switching to another tab and back.

![MapInTabs screenshot](MapInTabs/screenshot.gif)

[MapSnapshot](MapSnapshot/)

Compose map example saving a map image containing a running track as run.png.

![MapSnapshot screenshot](MapSnapshot/screenshot.gif)

[MovingMarker](MovingMarker/)

Compose map example animating a navigation arrow along a predefined route while the camera follows it.

![MovingMarker screenshot](MovingMarker/screenshot.gif)

[MyMarker](MyMarker/)

Legacy Google Maps example adding markers for Hamburg and Kiel, including a custom icon.

![MyMarker screenshot](MyMarker/screenshot.png)

[NavigationOffset](NavigationOffset/)

Java map example placing the current-location marker near the bottom of a heading-up map using camera bearing and map padding.

![NavigationOffset screenshot](NavigationOffset/screenshot.gif)

[NoOverlayClick](NoOverlayClick/)

Java map example comparing normal overlay clicks with shapes that pass taps through and a marker that consumes its clicks.

![NoOverlayClick screenshot](NoOverlayClick/screenshot.gif)

[OverlayAlpha](OverlayAlpha/)

Java map example adjusting the transparency of a topographic tile overlay.

![OverlayAlpha screenshot](OverlayAlpha/screenshot.gif)

[PickLocation](PickLocation/)

Java map form filling longitude and latitude fields from a tap on OpenMapView.

![PickLocation screenshot](PickLocation/screenshot.gif)

[RemoveMarkers](RemoveMarkers/)

Java map example removing a single marker by tapping it or clearing all markers with a button.

![RemoveMarkers screenshot](RemoveMarkers/screenshot.gif)

[ScalingMarker](ScalingMarker/)

Java map example showing a clickable ground overlay and circle whose sizes are specified in meters.

![ScalingMarker screenshot](ScalingMarker/screenshot.gif)

[SimpleMapStyle](SimpleMapStyle/)

Kotlin map example switching between OpenMapView map styles without an API key.

![SimpleMapStyle screenshot](SimpleMapStyle/screenshot.gif)

[TabRowMap](TabRowMap/)

Compose example keeping an OpenMapView and its camera state beneath a TabRow when switching tabs.

![TabRowMap screenshot](TabRowMap/screenshot.gif)

[TwoPointLine](TwoPointLine/)

Java/XML map example drawing a red polyline between two fixed Berlin coordinates and fitting both endpoints in view.

![TwoPointLine screenshot](TwoPointLine/screenshot.gif)

[Vehicles](Vehicles/)

Droidcon 2019 challenge sample fetching vehicle positions, storing them in Room, and showing them on Google Maps.

![Vehicles screenshot](Vehicles/screenshot.png)

[WorldWrap](WorldWrap/)

Java map example repeating the world horizontally while preventing vertical repetition.

![WorldWrap screenshot](WorldWrap/screenshot.gif)

[ZeroSetupMap](ZeroSetupMap/)

Kotlin map example displaying OpenStreetMap tiles without storage permissions or explicit cache initialization.

![ZeroSetupMap screenshot](ZeroSetupMap/screenshot.gif)

[ZoomToLocation](ZoomToLocation/)

Java map example moving the camera to the device's location.

![ZoomToLocation screenshot](ZoomToLocation/screenshot.gif)

## Word

[MyBars](MyBars/)

Custom word-game board with a separate tile rack, draggable letter tiles, and board panning and zooming.

![MyBars screenshot](MyBars/screenshot.png)

[MyDecoder](MyDecoder/)

Word-game board experiment decoding individual letter images from sprite sheets with BitmapRegionDecoder.

![MyDecoder screenshot](MyDecoder/screenshot.png)

[MyDecoder2](MyDecoder2/)

Variant of the sprite-sheet word board that caches scaled letter bitmaps for small tiles and enlarged drag previews.

![MyDecoder2 screenshot](MyDecoder2/screenshot.png)

[MyMask](MyMask/)

Canvas experiment drawing a bitmap overlay on a word-game board.

![MyMask screenshot](MyMask/screenshot.png)

[MyTiles](MyTiles/)

Custom word board with draggable letter tiles, grid snapping, and corner masks that respond to neighboring tiles.

![MyTiles screenshot](MyTiles/screenshot.png)

[MyTiles2](MyTiles2/)

Word-board variant combining grid-snapped tile dragging with constrained scrolling, flinging, and zooming.

![MyTiles2 screenshot](MyTiles2/screenshot.png)

[MyViewPager](MyViewPager/)

Navigation-drawer example selecting games with separate Moves, Game, and Chat pages.

![MyViewPager screenshot](MyViewPager/screenshot.gif)

[PinchZoom](PinchZoom/)

Custom board view demonstrating focal-point zooming, panning, and flinging with a transformation matrix.

![PinchZoom screenshot](PinchZoom/screenshot.gif)

[TopPlayers](TopPlayers/)

Word-game leaderboard loading player rankings and statistics into a list backed by Room and LiveData.

![TopPlayers screenshot](TopPlayers/screenshot.png)

[WordGame](WordGame/)

Dialog examples for choosing a letter, selecting tiles to swap, and entering a word.

![WordGame screenshot](WordGame/screenshot.png)

## QR Codes and NFC

[NFC2BT](NFC2BT/)

Unfinished experiment exchanging Bluetooth addresses and ECDH public keys over NFC for out-of-band pairing.

![NFC2BT screenshot](NFC2BT/screenshot.png)

[NFCStudentTracking](NFCStudentTracking/)

NFC student-transport example reading tag data to maintain a group of students and display it in a list.

![NFCStudentTracking screenshot](NFCStudentTracking/screenshot.png)

[QR_BT_Pairing](QR_BT_Pairing/)

Bluetooth device-list example scanning a QR code for an address and asking to pair with the matching device.

![QR_BT_Pairing screenshot](QR_BT_Pairing/screenshot.png)

[QREncoder](QREncoder/)

Java example generating a fixed QR code with ZXing and displaying it in an ImageView.

![QREncoder screenshot](QREncoder/screenshot.png)

[QRScanner](QRScanner/)

Java example launching ZXing Barcode Scanner and displaying a scanned code or cancellation result.

![QRScanner screenshot](QRScanner/screenshot.png)

[RoundedQRCode](RoundedQRCode/)

Java example generating a rounded QR code from entered text using drawing code adapted from Telegram.

![RoundedQRCode screenshot](RoundedQRCode/screenshot.gif)

[ZXingTestActivity](ZXingTestActivity/)

ZXing test application exercising Barcode Scanner intents, result handling, and barcode benchmarks.

![ZXingTestActivity screenshot](ZXingTestActivity/screenshot.png)

## UI

[BottomSheet1](BottomSheet1/)

Material example showing persistent and modal bottom sheets with state-change callbacks.

![BottomSheet1 screenshot](BottomSheet1/screenshot.gif)

[DialogCountDown](DialogCountDown/)

Dialog example updating its Cancel button with a countdown before automatically dismissing.

![DialogCountDown screenshot](DialogCountDown/screenshot.gif)

[DragAndDraw](DragAndDraw/)

Custom drawing-view example creating translucent rectangles from touch drags alongside sample letter tiles.

![DragAndDraw screenshot](DragAndDraw/screenshot.png)

[FastAdapterScrollingIssue](FastAdapterScrollingIssue/)

Reproduction project combining a FastAdapter game list with filtering, timestamp sorting, and DiffUtil updates.

![FastAdapterScrollingIssue screenshot](FastAdapterScrollingIssue/screenshot.png)

[InteractiveChart](InteractiveChart/)

Custom chart-view example demonstrating animated zoom, scrolling, flinging, and edge effects.

![InteractiveChart screenshot](InteractiveChart/screenshot.gif)

[MyActionBar](MyActionBar/)

Legacy framework ActionBar example showing an indeterminate progress indicator while the Activity is resumed.

![MyActionBar screenshot](MyActionBar/screenshot.png)

[MyCoordinator](MyCoordinator/)

Collapsing-toolbar example with a palette-colored header, RecyclerView, and animated floating action button.

![MyCoordinator screenshot](MyCoordinator/screenshot.png)

[MyList](MyList/)

Fragment-based list picker preserving the selected item index in SharedPreferences.

![MyList screenshot](MyList/screenshot.png)

[MyPrefs](MyPrefs/)

Preference-screen example with checkboxes, text fields, sliders, and a custom number-picker dialog.

![MyPrefs screenshot](MyPrefs/screenshot.png)

[MyScroll](MyScroll/)

ListView experiment appending one item per second and clearing the list with a button or double-tap.

![MyScroll screenshot](MyScroll/screenshot.png)

[MyStripe](MyStripe/)

Word-board experiment drawing draggable letter tiles from regions of horizontal sprite strips.

![MyStripe screenshot](MyStripe/screenshot.png)

[NaviFab](NaviFab/)

Navigation-drawer example combining a circular profile image, game entries, and account fragments.

![NaviFab screenshot](NaviFab/screenshot.png)

[NavigationDrawerExample](NavigationDrawerExample/)

Two-sided navigation-drawer example with a planet list on the left and music actions on the right.

![NavigationDrawerExample screenshot](NavigationDrawerExample/screenshot.png)

[Planets](Planets/)

RecyclerView example displaying planets with star icons and click feedback.

![Planets screenshot](Planets/screenshot.png)

[RotatedList](RotatedList/)

Fragment-navigation example opening an account-provider list with a circular profile image and click feedback.

![RotatedList screenshot](RotatedList/screenshot.png)

[Test](Test/)

Minimal custom View drawing an eight-by-eight checkerboard.

![Test screenshot](Test/screenshot.png)

[TestAbs](TestAbs/)

Legacy AbsoluteLayout experiment positioning and dragging custom letter-tile child views.

![TestAbs screenshot](TestAbs/screenshot.png)

[TestScroll](TestScroll/)

Custom board-view experiment using canvas translation and scaling for scrolling, flinging, and zooming.

![TestScroll screenshot](TestScroll/screenshot.png)

[TestScroll2](TestScroll2/)

Board-view experiment combining a transformation matrix, tile dragging, and an enlarged drag preview.

![TestScroll2 screenshot](TestScroll2/screenshot.png)

[TestScroll3](TestScroll3/)

Custom board-view experiment combining View scrolling, inertial flinging, and focal-point scaling.

![TestScroll3 screenshot](TestScroll3/screenshot.png)

[ValidatePrefs](ValidatePrefs/)

Preference-validation example checking an address, username, and password before saving changes.

![ValidatePrefs screenshot](ValidatePrefs/screenshot.png)

## Authentication and Messaging

[GoogleAuth](GoogleAuth/)

Multi-provider sign-in example combining Google, Facebook, VK, and Odnoklassniki account flows with local user data.

![GoogleAuth screenshot](GoogleAuth/screenshot.png)

[HmsSample](HmsSample/)

Huawei Mobile Services sample with screens for Huawei ID, push messaging, social features, purchases, and games.

![HmsSample screenshot](HmsSample/screenshot.png)

[HuaweiPush](HuaweiPush/)

Huawei Push Kit example retrieving a push token and displaying it in the app.

![HuaweiPush screenshot](HuaweiPush/screenshot.jpg)

[MyLogin](MyLogin/)

Fragment-based social-login experiment with provider screens and an animated login button.

![MyLogin screenshot](MyLogin/screenshot.png)

[MyNotification](MyNotification/)

Notification-action example simulating commands to open a nearby car or flash its lights.

![MyNotification screenshot](MyNotification/screenshot.png)

[PingMe](PingMe/)

Notification sample scheduling a reminder with editable text and snooze and dismiss actions.

![PingMe screenshot](PingMe/screenshot.png)

[SampleLoginWithAmazonAppForAndroid](SampleLoginWithAmazonAppForAndroid/)

Login with Amazon sample requesting profile access and displaying the signed-in user's details.

![SampleLoginWithAmazonAppForAndroid screenshot](SampleLoginWithAmazonAppForAndroid/screenshot.png)

## Misc

[AndroidBooks](AndroidBooks/)

Book-list and description example adapting fragment navigation to the available screen layout.

![AndroidBooks screenshot](AndroidBooks/screenshot.png)

[CriminalIntent](CriminalIntent/)

Fragment-based crime-log exercise with a list, editable crime details, dates, and solved flags.

![CriminalIntent screenshot](CriminalIntent/screenshot.png)

[flutter_app](flutter_app/)

Flutter starter application incrementing an on-screen counter with a floating action button.

![flutter_app screenshot](flutter_app/screenshot.png)

[GeoQuiz](GeoQuiz/)

True-or-false geography quiz with next-question navigation and a separate answer-reveal screen.

![GeoQuiz screenshot](GeoQuiz/screenshot.png)

[HelloMoon](HelloMoon/)

Fragment layout example showing the moon-landing image with Play and Stop controls.

![HelloMoon screenshot](HelloMoon/screenshot.png)

[MagicApp](MagicApp/)

Compose engineering-menu demo with live connectivity information and configurable HTTP, WebSocket, and MQTT diagnostics.

![MagicApp screenshot](MagicApp/screenshot.png)

[SwipeBooks](SwipeBooks/)

ViewPager example displaying three swipeable placeholder sections using fragments.

![SwipeBooks screenshot](SwipeBooks/screenshot.png)

---

![StackOverflow](http://stackoverflow.com/users/flair/165071.png)

[My Android-related questions at StackOverflow](http://stackoverflow.com/search?q=user:165071+[android])
