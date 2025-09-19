package com.michael.easylog

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        EasyLog.setUp {
            debugMode(BuildConfig.DEBUG)
            addDefaultLogger(DefaultLogger.DEFAULT_ANDROID)
            filterTag("Rigel")
        }

        logMany(
            header = "Environment info",
            "Debug mode: " + BuildConfig.DEBUG,
             "Log tag: " + EasyLog.logTag,
             "Minimum log level: " + EasyLog.getMinimumLogLevel(),
             "Version name: " + BuildConfig.VERSION_NAME,
             "Version code: " + BuildConfig.VERSION_CODE,
        )
    }
}