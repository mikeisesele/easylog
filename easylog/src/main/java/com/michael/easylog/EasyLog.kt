package com.michael.easylog

import android.util.Log

/**
 * EasyLog - A simple logging utility for Android applications.
 *
 * Usage:
 * 1. To see logs in the logcat, add the following to gradle.properties:
 *
 *
 * 2. Initialize the logger in your application's initialization phase:
 *    Logger.setup(filterTag = "CustomTag") // EASY-LOG is set by default
 *    - This filterTag would be what you can use to filter in your logcat.
 *
 * 3. Log messages using concise syntax directly on objects:
 *    - "Hello".logD("This is a debug message") // This is a debug message - Hello
 *    - "World".logI("This is an info message") // This is an info message - World
 *    - 42.logE("This is an error message") // This is an error message - 42
 *    - SomeObject().logV("This is a verbose message") // This is a verbose message - SomeObject
 *
 * 4. Optionally, you can use default log messages:
 *    - "Another Message".log() // default message - Another Message
 *
 * Note:
 * - Log messages are only displayed in debug mode.
 * - Default log level is DEBUG, which can be changed during setup.
 */
object EasyLog {
    private const val IDENTIFIER = "EASY-LOG"
    private var logTag: String = IDENTIFIER
    private var logLevel: LogType = LogType.DEBUG

    fun setup(filterTag: String) {
        logTag = filterTag
    }

    internal fun log(
        logMessage: String? = null,
        logObject: Any,
        level: LogType = logLevel,
        fileName: String?,
        lineNumber: Int
    ) {
        val logData = try {
            logObject.toString()
        } catch (e: Exception) {
            "Error converting data to string: ${e.message}"
        }
        logInternal(logMessage ?: "default message", logObject::class.java.simpleName, logData, level, fileName, lineNumber)
    }

    private fun logInternal(logMessage: String, clazz: String, logObject: Any, level: LogType, fileName: String?, lineNumber: Int) {
        val logInfo = "$clazz: $logObject"
        val logTag = "$logTag: $logMessage ($fileName:$lineNumber)"
        when (level) {
            LogType.DEBUG -> Log.d(logTag, logInfo)
            LogType.INFO -> Log.i(logTag, logInfo)
            LogType.ERROR -> Log.e(logTag, logInfo)
            LogType.VERBOSE -> Log.v(logTag, logInfo)
        }
    }
}

fun Any.logD(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.DEBUG,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
}

fun Any.logI(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.INFO,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber,
    )
}

fun Any.logE(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.ERROR,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber,
    )
}

fun Any.logV(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.VERBOSE,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
}

fun Any.log() {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = null,
        logObject = this,
        level = LogType.DEBUG,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
}

enum class LogType {
    DEBUG,
    INFO,
    ERROR,
    VERBOSE
}

