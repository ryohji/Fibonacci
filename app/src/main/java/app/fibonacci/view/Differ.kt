package app.fibonacci.view

import androidx.recyclerview.widget.DiffUtil
import app.fibonacci.model.Fibonacci

object Differ : DiffUtil.ItemCallback<Fibonacci>() {

    override fun areItemsTheSame(oldItem: Fibonacci, newItem: Fibonacci): Boolean =
        oldItem.index == newItem.index

    override fun areContentsTheSame(oldItem: Fibonacci, newItem: Fibonacci): Boolean =
        oldItem.index == newItem.index

}
