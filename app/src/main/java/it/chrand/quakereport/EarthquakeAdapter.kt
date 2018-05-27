package it.chrand.quakereport

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.LinearLayout
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*
import android.graphics.drawable.GradientDrawable
import android.util.Log

private val LOCATION_SEPARATOR = " of "

class EarthquakeAdapter(val getContext: Context, val list: ArrayList<Earthquake>, val backgroundColorId: Int) :
        ArrayAdapter<Earthquake>(getContext, 0, list) {
    val LOG_TAG = EarthquakeAdapter::class.java.name

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                    R.layout.list_item, parent, false)

            val textContainer = listItemView!!.findViewById(R.id.text_container) as LinearLayout
            // find the color the ressourceId maps to
            val color = ContextCompat.getColor(getContext, backgroundColorId)
            textContainer.setBackgroundColor(color)
        }

        // Get the object located at this position in the list
        val currentItem = list[position]

        val magTextView = listItemView!!.findViewById(R.id.mag_text) as TextView
        magTextView.setText(DecimalFormat("0.0").format(currentItem.magnitude))
        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        val magnitudeCircle = magTextView.getBackground() as GradientDrawable

        // Get the appropriate background color based on the current earthquake magnitude
        val magnitudeColor = getMagnitudeColor(currentItem.magnitude)
        magnitudeCircle.setColor(magnitudeColor)


        val offsetLocationTextView = listItemView.findViewById(R.id.offsetLocation_text) as TextView
        val primaryLocationTextView = listItemView.findViewById(R.id.primaryLocation_text) as TextView
        if (currentItem.location.contains(LOCATION_SEPARATOR)) {
            offsetLocationTextView.setText(currentItem.location.split(LOCATION_SEPARATOR).get(0)+ LOCATION_SEPARATOR)
            primaryLocationTextView.setText(currentItem.location.split(LOCATION_SEPARATOR).get(1))
        }
        else {
            offsetLocationTextView.setText(getContext().getString(R.string.near_the))
            primaryLocationTextView.setText(currentItem.location)
        }

        val dateTextView = listItemView.findViewById(R.id.date_text) as TextView
        val timeTextView = listItemView.findViewById(R.id.time_text) as TextView
        val date = Date(currentItem.time)
        dateTextView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date).toString())
        timeTextView.setText(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date).toString())

        // Return the whole list item layout (containing 2 TextViews)
        // so that it can be shown in the ListView
        return listItemView
    }

    fun setEarthquakes(earthquakes: List<Earthquake>?) {

        list.clear()
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            list.addAll(earthquakes)
        }
        notifyDataSetChanged()
    }

    fun getMagnitudeColor(mag: Double): Int {
        return ContextCompat.getColor(getContext, when {
            mag < 2.0 -> R.color.magnitude1
            mag < 3.0 -> R.color.magnitude2
            mag < 4.0 -> R.color.magnitude3
            mag < 5.0 -> R.color.magnitude4
            mag < 6.0 -> R.color.magnitude5
            mag < 7.0 -> R.color.magnitude6
            mag < 8.0 -> R.color.magnitude7
            mag < 9.0 -> R.color.magnitude8
            mag < 10.0 -> R.color.magnitude9
            else -> R.color.magnitude10plus
        })
    }
}