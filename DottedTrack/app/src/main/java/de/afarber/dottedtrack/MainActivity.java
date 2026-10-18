package de.afarber.dottedtrack;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.os.Bundle;
import android.util.Xml;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.AndroidPathEffect_androidKt;
import androidx.compose.ui.graphics.PathEffect;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.afarber.openmapview.CameraUpdateFactory;
import de.afarber.openmapview.LatLng;
import de.afarber.openmapview.LatLngBounds;
import de.afarber.openmapview.OpenMapView;
import de.afarber.openmapview.Polyline;
import de.afarber.openmapview.PolylineOptions;

public class MainActivity extends AppCompatActivity {
    private static final String[] STYLES = {"Solid", "Dashed", "Dotted"};

    private final List<Polyline> lines = new ArrayList<>();
    private float density;
    private int style = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        density = getResources().getDisplayMetrics().density;

        OpenMapView map = findViewById(R.id.map);
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        // One polyline per track segment, as in the question
        for (List<LatLng> segment : readTrackSegments("track.gpx")) {
            lines.add(map.addPolyline(new PolylineOptions()
                    .addAll(segment)
                    .color(Color.rgb(0, 191, 255))
                    .width(6 * density)
                    .strokePattern(pattern(style))));
            for (LatLng point : segment) {
                bounds.include(point);
            }
        }
        // Once the map is laid out, show the whole track
        map.post(() -> map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), Math.round(48 * density))));

        Button button = findViewById(R.id.style);
        button.setText(STYLES[style]);
        button.setOnClickListener(v -> {
            style = (style + 1) % STYLES.length;
            button.setText(STYLES[style]);
            for (Polyline line : lines) {
                line.setStrokePattern(pattern(style));
            }
        });
    }

    private PathEffect pattern(int style) {
        if (style == 1) {
            // Dashes of 6 dp with gaps of 6 dp
            return PathEffect.Companion.dashPathEffect(new float[] {6 * density, 6 * density}, 0f);
        }
        if (style == 2) {
            // Round dots: a circle of 6 dp stamped every 12 dp along the line
            Path dot = new Path();
            dot.addCircle(0f, 0f, 3 * density, Path.Direction.CW);
            return AndroidPathEffect_androidKt.toComposePathEffect(
                    new PathDashPathEffect(dot, 12 * density, 0f, PathDashPathEffect.Style.TRANSLATE));
        }
        return null;
    }

    // A minimal GPX reader: the <trkpt> elements of each <trkseg>
    private List<List<LatLng>> readTrackSegments(String asset) {
        List<List<LatLng>> segments = new ArrayList<>();
        try (InputStream in = getAssets().open(asset)) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, null);
            List<LatLng> segment = null;
            for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
                if (event == XmlPullParser.START_TAG && "trkseg".equals(parser.getName())) {
                    segment = new ArrayList<>();
                } else if (event == XmlPullParser.START_TAG && "trkpt".equals(parser.getName()) && segment != null) {
                    segment.add(new LatLng(
                            Double.parseDouble(parser.getAttributeValue(null, "lat")),
                            Double.parseDouble(parser.getAttributeValue(null, "lon"))));
                } else if (event == XmlPullParser.END_TAG && "trkseg".equals(parser.getName()) && segment != null) {
                    segments.add(segment);
                    segment = null;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read " + asset, e);
        }
        return segments;
    }
}
