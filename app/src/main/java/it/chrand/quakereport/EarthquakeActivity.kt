package it.chrand.quakereport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask

class EarthquakeActivity : AppCompatActivity() {

    val LOG_TAG = EarthquakeActivity::class.java.name
    private val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10"
    lateinit var itemsAdapter: EarthquakeAdapter

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

        // Kick off an {@link AsyncTask} to perform the network request
        val task = EarthquakeAsyncTask()
        task.execute(USGS_REQUEST_URL)
    }

    /**
     * Update the UI with the given earthquake information.
     */
    private fun updateUi(earthquakes: ArrayList<Earthquake>) {
        itemsAdapter.clear()
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            itemsAdapter.addAll(earthquakes)
        }
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    inner class EarthquakeAsyncTask : AsyncTask<String, Void, ArrayList<Earthquake>>() {

        override fun doInBackground(vararg urls: String): ArrayList<Earthquake>? {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.size < 1 || urls[0] == null) {
                return null
            }

            return QueryUtils.fetchEarthquakeData(urls[0])
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         *
         * It IS okay to modify the UI within this method. We take the [Event] object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */
        override fun onPostExecute(result: ArrayList<Earthquake>?) {
            // If there is no result, do nothing.
            if (result == null) {
                return
            }

            updateUi(result)
        }
    }
}
