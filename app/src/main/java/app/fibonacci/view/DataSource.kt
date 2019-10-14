package app.fibonacci.view

import app.fibonacci.model.Fibonacci
import kotlin.math.max
import androidx.paging.ItemKeyedDataSource as DataSource

class DataSource : DataSource<Int, Fibonacci>() {

    override fun getKey(item: Fibonacci): Int = item.index

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Fibonacci>
    ) = with(params) {
        values.drop(requestedInitialKey ?: 0).take(requestedLoadSize)
            .let { callback.onResult(it, it.firstOrNull()?.index ?: 0, it.size) }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Fibonacci>) =
        with(params) {
            values.drop(key + 1).take(requestedLoadSize).let(callback::onResult)
        }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Fibonacci>) =
        with(params) {
            values.take(max(0, key - 1)).takeLast(requestedLoadSize).let(callback::onResult)
        }

    private val values: List<Fibonacci> = (0 until 1000).map { Fibonacci.of(it) }

}
