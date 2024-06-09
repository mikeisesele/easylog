package com.michael.easylog.defaultloggers

import android.util.Log
import com.michael.easylog.EasyLog.logTag
import com.michael.easylog.LogType
import com.michael.easylog.domain.Logger

class DefaultAndroidLogger: Logger {
    override fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType,
        fileName: String?,
        lineNumber: Int
    ) {
        val logData = try {
            logObject.toString()
        } catch (e: Exception) {
            "Error converting data to string: ${e.message}"
        }


        val tag = "$logTag (${logObject::class.java.simpleName} object at $fileName:$lineNumber)"
        val fullMessage = "$logMessage:: $logData"

        when (level) {
            LogType.DEBUG -> Log.d(tag, fullMessage)
            LogType.INFO -> Log.i(tag, fullMessage)
            LogType.ERROR -> Log.e(tag, fullMessage)
            LogType.VERBOSE -> Log.v(tag, fullMessage)
            LogType.WARNING -> Log.w(tag, fullMessage)
            LogType.TERRIBLE_FAILURE -> Log.wtf(tag, fullMessage)
        }
    }
}
