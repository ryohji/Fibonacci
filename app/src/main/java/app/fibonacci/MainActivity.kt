package app.fibonacci

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.fibonacci.view.Adapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.adapter = Adapter()
    }

}
