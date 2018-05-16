package it.chrand.quakereport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView


class EarthquakeActivity : AppCompatActivity() {

    val LOG_TAG = EarthquakeActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_activity)

        // Create a fake list of earthquake locations.
        val earthquakes = QueryUtils.extractEarthquakes()

        // Find a reference to the {@link ListView} in the layout
        val earthquakeListView = findViewById<View>(R.id.list) as ListView

        val itemsAdapter = EarthquakeAdapter(this, earthquakes, R.color.colorPrimaryLight)

        earthquakeListView.setAdapter(itemsAdapter)
    }
}
