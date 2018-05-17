package it.chrand.quakereport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.content.Intent
import android.net.Uri


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
        earthquakeListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            // TODO intent öffnen

            val i = Intent(Intent.ACTION_VIEW,
                    Uri.parse(earthquakes[position].url))
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(i)
        }
    }
}
