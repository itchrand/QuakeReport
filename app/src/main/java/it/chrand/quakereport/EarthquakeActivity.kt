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
        val earthquakes = ArrayList<String>()
        earthquakes.add("San Francisco")
        earthquakes.add("London")
        earthquakes.add("Tokyo")
        earthquakes.add("Mexico City")
        earthquakes.add("Moscow")
        earthquakes.add("Rio de Janeiro")
        earthquakes.add("Paris")

        // Find a reference to the {@link ListView} in the layout
        val earthquakeListView = findViewById<View>(R.id.list) as ListView

        // Create a new {@link ArrayAdapter} of earthquakes
        val adapter = ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, earthquakes)

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter)
    }
}
