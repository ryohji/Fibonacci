package app.fibonacci.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.fibonacci.model.Fibonacci

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal fun bind(fibonacci: Fibonacci) = with(fibonacci) {
        text1.text = "$value"
        text2.text = "$index"
    }

    private val text1: TextView = itemView.findViewById(android.R.id.text1)

    private val text2: TextView = itemView.findViewById(android.R.id.text2)

}
