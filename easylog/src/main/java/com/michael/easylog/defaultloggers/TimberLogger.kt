package com.michael.easylog.defaultloggers

import com.michael.easylog.EasyLog
import com.michael.easylog.LogType
import com.michael.easylog.domain.Logger
import timber.log.Timber

class TimberLogger : Logger {
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

        val fullMessage = "$logMessage (at $fileName:$lineNumber):: $logData"

        when (level) {
            LogType.DEBUG -> Timber.tag(EasyLog.logTag).d(fullMessage)
            LogType.INFO -> Timber.tag(EasyLog.logTag).i(EasyLog.logTag, fullMessage)
            LogType.ERROR -> Timber.tag(EasyLog.logTag).e(fullMessage)
            LogType.VERBOSE -> Timber.tag(EasyLog.logTag).v(fullMessage)
            LogType.WARNING -> Timber.tag(EasyLog.logTag).w(fullMessage)
            LogType.TERRIBLE_FAILURE -> Timber.tag(EasyLog.logTag).wtf(fullMessage)
        }
    }
}
