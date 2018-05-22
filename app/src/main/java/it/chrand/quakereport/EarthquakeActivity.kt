package it.chrand.quakereport

import android.app.LoaderManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.content.Intent
import android.content.Loader
import android.net.Uri
import android.util.Log
import android.widget.TextView
import java.security.AccessController.getContext

class EarthquakeActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Earthquake>> {

    val LOG_TAG = EarthquakeActivity::class.java.name
    private val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10"
    lateinit var itemsAdapter: EarthquakeAdapter


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Earthquake>> {
        return EarthquakeLoader(this,USGS_REQUEST_URL)
    }

    override fun onLoadFinished(loader: Loader<List<Earthquake>>?, data: List<Earthquake>?) {
        itemsAdapter.setEarthquakes(data)
        (findViewById<View>(R.id.emptyView) as TextView).text = this.getString(R.string.no_earthquakes_found)
    }

    override fun onLoaderReset(loader: Loader<List<Earthquake>>?) {
        itemsAdapter.clear()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_activity)

        // Find a reference to the {@link ListView} in the layout
        val earthquakeListView = findViewById<View>(R.id.list) as ListView

        itemsAdapter = EarthquakeAdapter(this, ArrayList<Earthquake>(), R.color.colorPrimaryLight)

        earthquakeListView.setAdapter(itemsAdapter)
        earthquakeListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val i = Intent(Intent.ACTION_VIEW,
                    Uri.parse(itemsAdapter.getItem(position).url))
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(i)
        }

        earthquakeListView.emptyView = findViewById<View>(R.id.emptyView) as TextView
        loaderManager.initLoader(0, null, this)
    }
}
