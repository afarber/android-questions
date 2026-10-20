package de.afarber.labelmarker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.MarkerComposable
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

private val BERLIN = LatLng(52.5200, 13.4050)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                LabelMarkerScreen()
            }
        }
    }
}

@Composable
private fun LabelMarkerScreen() {
    val camera = rememberCameraPositionState { position = CameraPosition(BERLIN, 16f) }
    val marker = rememberMarkerState(BERLIN)
    var changed by rememberSaveable { mutableStateOf(false) }
    val label = if (changed) "Berlin\nCity center" else "Text\nText"

    Box(Modifier.fillMaxSize()) {
        OpenMap(modifier = Modifier.fillMaxSize(), cameraPositionState = camera) {
            // The content is a bitmap snapshot. Changing this key redraws the label.
            MarkerComposable(
                label,
                state = marker,
                anchor = Pair(38f / 180f, 102f / 128f),
                onClick = { true },
            ) {
                LabelIcon(label)
            }
        }
        Surface(
            modifier = Modifier.align(Alignment.TopStart).safeDrawingPadding().padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp,
        ) {
            Text("Label: ${label.replace("\n", " / ")}", Modifier.padding(16.dp))
        }
        Button(
            onClick = { changed = !changed },
            modifier = Modifier.align(Alignment.TopEnd).safeDrawingPadding().padding(16.dp),
        ) {
            Text("Change label")
        }
    }
}

@Composable
private fun LabelIcon(label: String) {
    // The anchor points to (38, 102), the orange tip, inside this complete icon.
    Box(Modifier.size(180.dp, 128.dp)) {
        Canvas(Modifier.size(76.dp, 102.dp)) {
            val u = size.width / 76f
            val pin = Path().apply {
                fillType = PathFillType.EvenOdd
                moveTo(38f * u, 102f * u)
                cubicTo(20f * u, 84f * u, 0f, 60f * u, 0f, 38f * u)
                cubicTo(0f, 17f * u, 17f * u, 0f, 38f * u, 0f)
                cubicTo(59f * u, 0f, 76f * u, 17f * u, 76f * u, 38f * u)
                cubicTo(76f * u, 60f * u, 56f * u, 84f * u, 38f * u, 102f * u)
                close()
                // A real transparent hole: the map remains visible through it.
                addOval(Rect(13f * u, 13f * u, 63f * u, 63f * u))
            }
            drawPath(pin, Color(0xFFE54300))
        }
        Box(
            modifier = Modifier.offset(60.dp, 92.dp).size(120.dp, 36.dp)
                .background(Color.White, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.Black,
                maxLines = 2,
                softWrap = false,
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            )
        }
    }
}
