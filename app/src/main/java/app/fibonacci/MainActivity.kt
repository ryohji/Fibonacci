package app.fibonacci

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import app.fibonacci.view.Adapter
import app.fibonacci.view.DataSource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

}
