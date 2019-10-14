package app.fibonacci.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import app.fibonacci.R
import app.fibonacci.model.Fibonacci

class Adapter : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        (convertView ?: createView(parent, getItemViewType(position))).also {
            with(getItem(position)) {
                it.findViewById<TextView>(android.R.id.text1).text = "$value"
                it.findViewById<TextView>(android.R.id.text2).text = "$index"
            }
        }

    override fun getItem(position: Int): Fibonacci = values[position]

    override fun getItemId(position: Int): Long = values[position].index.toLong()

    override fun getItemViewType(position: Int): Int = position % 2

    override fun getCount(): Int = values.size

    override fun getViewTypeCount(): Int = 2

    private fun createView(parent: ViewGroup, viewType: Int): View =
        LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
            .also {
                if (viewType != 0) {
                    it.setBackgroundColor(getColor(parent.context, R.color.colorPrimaryPale))
                }
            }

    private val values: List<Fibonacci> = (0 until 1000).map { Fibonacci.of(it) }

}
