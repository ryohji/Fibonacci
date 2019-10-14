package app.fibonacci

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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
