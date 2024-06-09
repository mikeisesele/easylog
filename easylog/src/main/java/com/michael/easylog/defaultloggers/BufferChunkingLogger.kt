package com.michael.easylog.defaultloggers

import com.michael.easylog.EasyLog
import com.michael.easylog.LogType
import android.util.Log
import com.michael.easylog.domain.Logger

class BufferChunkingLogger : Logger {
    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

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

        val fullMessage = "$logMessage:: $logData"
        val fullLogTag = "${EasyLog.logTag}:: ${logObject::class.java.simpleName} ($fileName:$lineNumber)"

        if (fullMessage.length > MAX_LOG_LENGTH) {
            var i = 0
            while (i < fullMessage.length) {
                logChunk(fullLogTag, level, fullMessage.substring(i, Math.min(fullMessage.length, i + MAX_LOG_LENGTH)))
                i += MAX_LOG_LENGTH
            }
        } else {
            logChunk(fullLogTag, level, fullMessage)
        }
    }

    private fun logChunk(tag: String, level: LogType, chunk: String) {
        when (level) {
            LogType.DEBUG -> Log.d(tag, chunk)
            LogType.INFO -> Log.i(tag, chunk)
            LogType.ERROR -> Log.e(tag, chunk)
            LogType.VERBOSE -> Log.v(tag, chunk)
            LogType.WARNING -> Log.w(tag, chunk)
            LogType.TERRIBLE_FAILURE -> Log.wtf(tag, chunk)
        }
    }
}
