package com.michael.easylog

import android.app.Application
import com.michael.easylog.domain.DefaultLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // for Timber integration
//      Timber.plant(Timber.DebugTree())

        // for Bugfender integration
//      Bugfender.init(this, BuildConfig.BUGFENDER_API_KEY, BuildConfig.DEBUG, true)
//      Bugfender.enableUIEventLogging(this)
//      Bugfender.enableLogcatLogging()

        EasyLog.setUp {
            debugMode(BuildConfig.DEBUG)
            defaultLogger(DefaultLogger.DEFAULT_ANDROID)
            filterTag ("Rigel")
            build()
        }
    }
}