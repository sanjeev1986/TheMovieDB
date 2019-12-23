package com.sample.themoviedb.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * Exists to enable Pagination Arch component's callbacks to run on UI thread.
 */
class MainThreadExecutor : Executor {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mHandler.post(command)
    }

}