package it.chrand.quakereport

import android.app.LoaderManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.content.Intent
import android.content.Loader
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import android.preference.PreferenceManager
import android.content.SharedPreferences


class EarthquakeActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Earthquake>> {

    val LOG_TAG = EarthquakeActivity::class.java.name
    private val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query"
    //private val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10"
    lateinit var itemsAdapter: EarthquakeAdapter


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Earthquake>> {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default))
        val orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default))

        val baseUri = Uri.parse(USGS_REQUEST_URL)
        val uriBuilder = baseUri.buildUpon()

        uriBuilder.appendQueryParameter("format", "geojson")
        uriBuilder.appendQueryParameter("eventtype", "earthquake")
        uriBuilder.appendQueryParameter("limit", "10")
        uriBuilder.appendQueryParameter("minmag", minMagnitude)
        uriBuilder.appendQueryParameter("orderby", orderBy)

        return EarthquakeLoader(this, uriBuilder.toString())
    }

    override fun onLoadFinished(loader: Loader<List<Earthquake>>?, data: List<Earthquake>?) {
        (findViewById<View>(R.id.progress_bar) as ProgressBar).visibility = View.GONE
        (findViewById<View>(R.id.emptyView) as TextView).text = this.getString(R.string.no_earthquakes_found)
        itemsAdapter.setEarthquakes(data)
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
        earthquakeListView.onItemClickListener = AdapterView.OnItemClickListener { _adapterView, _view, position, _id ->
            val i = Intent(Intent.ACTION_VIEW,
                    Uri.parse(itemsAdapter.getItem(position).url))
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(i)
        }

        earthquakeListView.emptyView = findViewById<View>(R.id.emptyView) as TextView
        if (!networkConnection()) {
            (findViewById<View>(R.id.progress_bar) as ProgressBar).visibility = View.GONE
            (findViewById<View>(R.id.emptyView) as TextView).text = this.getString(R.string.no_internet_connection)
        } else
            loaderManager.initLoader(0, null, this)
    }

    fun networkConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return !(networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI &&
                        networkInfo.getType() != ConnectivityManager.TYPE_MOBILE))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.action_settings) {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}