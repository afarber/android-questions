package de.afarber.simplemapstyle

import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.MapType
import de.afarber.openmapview.OpenMapView

class MainActivity : ComponentActivity() {
    private lateinit var map: OpenMapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)
        lifecycle.addObserver(map)

        map.setCenter(LatLng(47.9, 1.9))
        map.setZoom(16f)

        val styleGroup = findViewById<RadioGroup>(R.id.styleGroup)
        styleGroup.setOnCheckedChangeListener { _, checkedId ->
            map.setMapType(
                when (checkedId) {
                    R.id.humanitarian -> MapType.HUMANITARIAN
                    R.id.transport -> MapType.OPNVKARTE
                    R.id.cyclosm -> MapType.CYCLOSM
                    else -> MapType.STANDARD
                },
            )
        }
        styleGroup.check(R.id.humanitarian)
    }
}
