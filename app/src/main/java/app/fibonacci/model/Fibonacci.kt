package app.fibonacci.model

import java.math.BigInteger

data class Fibonacci(val index: Int, val value: BigInteger) {

    companion object {
        internal fun of(index: Int): Fibonacci {
            while (cache.size <= index) {
                cache += cache.takeLast(2).let { it[0] + it[1] }
            }
            return Fibonacci(index, cache[index])
        }

        private val cache = listOf(0, 1)
            .map { BigInteger("$it") }
            .toMutableList()
    }

}
