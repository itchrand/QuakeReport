package it.chrand.quakereport

import android.content.AsyncTaskLoader
import android.content.Context

class EarthquakeLoader(context: Context, val url: String)
    : AsyncTaskLoader<List<Earthquake>>(context) {

    val LOG_TAG = EarthquakeLoader::class.java.name

    override fun onStartLoading() {
        forceLoad()
    }

    override fun loadInBackground(): List<Earthquake>? {
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (url.length < 1 || url == null) {
            return null
        }

        return QueryUtils.fetchEarthquakeData(url)
    }
}