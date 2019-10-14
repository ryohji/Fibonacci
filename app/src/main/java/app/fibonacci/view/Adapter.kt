package app.fibonacci.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import app.fibonacci.R
import app.fibonacci.model.Fibonacci

class Adapter : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        createView(parent, viewType).let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(values[position])

    override fun getItemViewType(position: Int): Int = position % 2

    override fun getItemCount(): Int = values.size

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
