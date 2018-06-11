package it.chrand.quakereport

import android.Manifest
import android.app.LoaderManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.widget.*


class EarthquakeActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Earthquake>>, SwipeRefreshLayout.OnRefreshListener {

    private val LOG_TAG = EarthquakeActivity::class.java.name
    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var permissionGranted_access_fine_location = false
    private val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query"
    //private val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10"
    lateinit var itemsAdapter: EarthquakeAdapter
    lateinit var mLocationManager: LocationManager
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Earthquake>> {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default))
        val orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default))
        val searchMode = sharedPrefs.getString(
                getString(R.string.settings_search_mode_key),
                getString(R.string.settings_search_mode_default))
        val searchRadius = sharedPrefs.getString(
                getString(R.string.settings_search_radius_key),
                getString(R.string.settings_search_radius_default))

        val baseUri = Uri.parse(USGS_REQUEST_URL)
        val uriBuilder = baseUri.buildUpon()

        uriBuilder.appendQueryParameter("format", "geojson")
        uriBuilder.appendQueryParameter("eventtype", "earthquake")
        uriBuilder.appendQueryParameter("limit", "10")
        uriBuilder.appendQueryParameter("minmag", minMagnitude)
        uriBuilder.appendQueryParameter("orderby", orderBy)
        if (searchMode != "worldwide") {
            val (valid, latitude, longitude) = getLocation()
            if (valid) {
                uriBuilder.appendQueryParameter("latitude", latitude.toString())
                uriBuilder.appendQueryParameter("longitude", longitude.toString())
                uriBuilder.appendQueryParameter("maxradiuskm", searchRadius)
            } else {
                val toast = Toast.makeText(applicationContext, R.string.no_location_found, Toast.LENGTH_LONG)
                toast.show()
            }
        }

        return EarthquakeLoader(this, uriBuilder.toString())
    }

    private fun getLocation(): Triple<Boolean, Double, Double> {

        val location = checkPermissionsGetLocation()
        if (location != null)
            return Triple(true, location.getLatitude(), location.getLongitude())
        else
            return Triple(false, 0.0, 0.0)
    }

    private fun checkPermissionsGetLocation(): Location? {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                TODO()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else
            permissionGranted_access_fine_location = true

        if (permissionGranted_access_fine_location) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
            return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            // mLocationManager.removeUpdates(locationListener)
        }

        return null
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // val thetext = "" + location.longitude + ":" + location.latitude
            // val toast = Toast.makeText(applicationContext, thetext, Toast.LENGTH_SHORT)
            // toast.show()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permissionGranted_access_fine_location = true
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    permissionGranted_access_fine_location = false
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onLoadFinished(loader: Loader<List<Earthquake>>?, data: List<Earthquake>?) {
        (findViewById<View>(R.id.progress_bar) as ProgressBar).visibility = View.GONE
        (findViewById<View>(R.id.emptyView) as TextView).text = this.getString(R.string.no_earthquakes_found)
        itemsAdapter.setEarthquakes(data)
    }

    override fun onLoaderReset(loader: Loader<List<Earthquake>>?) {
        itemsAdapter.clear()
    }

    /*
     * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
     * performs a swipe-to-refresh gesture.
     */
    override fun onRefresh() {
        // This method performs the actual data-refresh operation.
        // The method calls setRefreshing(false) when it's finished.
        loaderManager.restartLoader(0, null, this)
        swipeRefreshLayout.setRefreshing(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_activity)

        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Find a reference to the {@link ListView} in the layout
        val earthquakeListView = findViewById<View>(R.id.list) as ListView
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener(this)

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
        else if (id == R.id.menu_refresh){
            swipeRefreshLayout!!.setRefreshing(true)
            loaderManager.restartLoader(0, null, this)
            swipeRefreshLayout!!.setRefreshing(false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}