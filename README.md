# Fibonacci
RecyclerView w/PagedList implementation example.

このリポジトリーに付した３つのタグが、以下の各セクションに対応している。

# ListView

![ListView](https://user-images.githubusercontent.com/30690161/66760083-e703d480-ee90-11e9-8967-5afba38d2ace.png)

`ListView` は、その表示するリスト項目の内容を保持する（そしてその内容を表示するためのビューを返す） `ListAdapter` をつなぐことで動作する。

```kotlin
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
```

この例で利用する `Fibonacci` は次のように実装している。

```kotlin
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
```

# RecyclerView

![RecyclerView](https://user-images.githubusercontent.com/30690161/66760086-e79c6b00-ee90-11e9-983b-619841b32d98.png)

`RecyclerView` も同様に、項目を保持してビューに変換する `RecyclerView.Adapter` とつないで利用する。
`RecyclerView.Adapter` はビューの生成と（その再利用、）表示内容の設定が分離された。

```kotlin
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
```

また `ViewHolder` が項目と表示内容の設定の責務を分担するよう設計されている。

```kotlin
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal fun bind(fibonacci: Fibonacci) = with(fibonacci) {
        text1.text = "$value"
        text2.text = "$index"
    }

    private val text1: TextView = itemView.findViewById(android.R.id.text1)

    private val text2: TextView = itemView.findViewById(android.R.id.text2)

}
```

# PagedList

![PagedList](https://user-images.githubusercontent.com/30690161/66760084-e703d480-ee90-11e9-88b9-0da3ad67c3f2.png)

`PagedListAdapter` を実装することで `RecyclerView.Adapter` にデータの遅延読み込み機能を持たせられる。
項目数の管理は `PagedListAdapter` ベースクラスで分担されることになり、アイテムへは `getItem(position: Int)` を介してアクセスする。

動的に変化するリストの表示に対応するため、項目同士を比較する `differ` をコンストラクター引数で渡す必要がある。

```kotlin
class Adapter : PagedListAdapter<Fibonacci, ViewHolder>(Differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        createView(parent, viewType).let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        getItem(position)!!.let { holder.bind(it) }

    override fun getItemViewType(position: Int): Int = position % 2

    private fun createView(parent: ViewGroup, viewType: Int): View =
        LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
            .also {
                if (viewType != 0) {
                    it.setBackgroundColor(getColor(parent.context, R.color.colorPrimaryPale))
                }
            }

}
```

`Differ` の実装はたとえばこのようなものである。（項目 ID が一致するか、その内容まで一致するか、という検査を担当する）

```kotlin
object Differ : DiffUtil.ItemCallback<Fibonacci>() {

    override fun areItemsTheSame(oldItem: Fibonacci, newItem: Fibonacci): Boolean =
        oldItem.index == newItem.index

    override fun areContentsTheSame(oldItem: Fibonacci, newItem: Fibonacci): Boolean =
        oldItem.index == newItem.index

}
```

`PagedList` がそのリスト全体の部分を取得するために `DataSource` を実装する必要がある。

```kotlin
class DataSource : ItemKeyedDataSource<Int, Fibonacci>() {

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
```

この `DataSource` を `PagedList.Builder` を介して `PagedList` にしたてる。
例えば `Activity` で `RecyclerView` に設定する `Adapter` に、次のようにして `PagedList` を作成して（`submitList` で）接続できる。

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = Adapter().also { adapter ->
            PagedList.Builder(DataSource(), 10)
                .setFetchExecutor(Executor.sub)
                .setNotifyExecutor(Executor.main)
                .build()
                .let { adapter.submitList(it) }
        }
    }
```

なお `PagedList.Builder` には `Executor` を渡す必要がある。これは例えば次のように実装できる。

```kotlin
object Executor {

    val main: Executor = with(Handler(Looper.getMainLooper())) {
        Executor {
            while (!post(it)) {
                Thread.sleep(0)
            }
        }
    }

    val sub: Executor = Executors.newCachedThreadPool()

}
```
