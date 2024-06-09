package com.michael.easylog.defaultloggers

import com.michael.easylog.LogType

import com.bugfender.sdk.Bugfender
import com.michael.easylog.EasyLog
import com.michael.easylog.Logger

class BugFenderLogger : Logger {
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
            LogType.DEBUG -> Bugfender.d(EasyLog.logTag, fullMessage)
            LogType.INFO -> Bugfender.i(EasyLog.logTag, fullMessage)
            LogType.ERROR -> Bugfender.e(EasyLog.logTag, fullMessage)
            LogType.VERBOSE -> Bugfender.t(EasyLog.logTag, fullMessage)
            LogType.WARNING -> Bugfender.w(EasyLog.logTag, fullMessage)
            LogType.TERRIBLE_FAILURE -> Bugfender.e(EasyLog.logTag, fullMessage)
        }
    }
}