package com.gmail.rewheldev.app

import android.app.Application
import com.gmail.rewheldev.BuildConfig
import com.gmail.rewheldev.helpers.logging.ReleaseTree
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}