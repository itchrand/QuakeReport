package it.chrand.quakereport

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.LinearLayout


class EarthquakeAdapter(val getContext: Context, val list: ArrayList<Earthquake>, val backgroundColorId: Int) :
        ArrayAdapter<Earthquake>(getContext, 0, list) {

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

        val miwokTextView = listItemView!!.findViewById(R.id.mag_text) as TextView
        miwokTextView.setText(currentItem.magnitude.toString())

        val locationTextView = listItemView.findViewById(R.id.location_text) as TextView
        locationTextView.setText(currentItem.location)

        val dateTextView = listItemView.findViewById(R.id.date_text) as TextView
        dateTextView.setText(currentItem.date)

        // Return the whole list item layout (containing 2 TextViews)
        // so that it can be shown in the ListView
        return listItemView
    }
}