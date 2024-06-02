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
 *    - "Warning".logW("This is a warning message") // This is a warning message - Warning
 *    - "WTF".logWtf("This should not happen") // This should not happen - WTF
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
    private var isDebugMode: Boolean = true


    /**
     * Sets up the EasyLog utility with the specified filter tag and debug mode.
     *
     * @param filterTag The filter tag is a custom, optional tag to be used in log messages for easy filtering.
     *                  Default value is "EASY-LOG".
     *
     * @param debugMode If set to true, logging will be enabled; if set to false, logging will be disabled.
     *                  Debug mode is typically enabled in development or debug builds. [BuildConfig.DEBUG]
     *                  Default value is true.
     *
     * Example setup:
     *
     * ```
     * EasyLog.setup(
     *     filterTag = "CUSTOM TAG",
     *     debugMode = BuildConfig.DEBUG
     * )
     * ```
     */
    fun setUp(filterTag: String, debugMode: Boolean) {
        logTag = filterTag
        isDebugMode = debugMode
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

        if (isDebugMode) {
            logInternal(
                logMessage ?: "EasyLog Default",
                logObject::class.java.simpleName,
                logData,
                level,
                fileName,
                lineNumber
            )
        }
    }

    private fun logInternal(logMessage: String, clazz: String, logObject: Any, level: LogType, fileName: String?, lineNumber: Int) {
        val logInfo = "$logMessage:: $logObject"
        val logTag = "$logTag:: $clazz ($fileName:$lineNumber)"
        when (level) {
            LogType.DEBUG -> Log.d(logTag, logInfo)
            LogType.INFO -> Log.i(logTag, logInfo)
            LogType.ERROR -> Log.e(logTag, logInfo)
            LogType.VERBOSE -> Log.v(logTag, logInfo)
            LogType.WARNING -> Log.w(logTag, logInfo)
            LogType.TERRIBLE_FAILURE -> Log.wtf(logTag, logInfo)
        }
    }
}

/**
 * Logs a DEBUG message with optional custom log message.
 * Uses stack trace to capture the file name and line number where the log was called.
 *
 * @param logMessage Optional message to log.
 */
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

/**
 * Logs an INFO message with optional custom log message.
 * Uses stack trace to capture the file name and line number where the log was called.
 *
 * @param logMessage Optional message to log.
 */
fun Any.logI(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.INFO,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
}

/**
 * Logs an ERROR message with optional custom log message.
 * Uses stack trace to capture the file name and line number where the log was called.
 *
 * @param logMessage Optional message to log.
 */
fun Any.logE(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.ERROR,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
}

/**
 * Logs a VERBOSE message with optional custom log message.
 * Uses stack trace to capture the file name and line number where the log was called.
 *
 * @param logMessage Optional message to log.
 */
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

/**
 * Logs a WARNING message with optional custom log message.
 * Uses stack trace to capture the file name and line number where the log was called.
 *
 * @param logMessage Optional message to log.
 */
fun Any.logW(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.WARNING,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
}

/**
 * Logs a TERRIBLE FAILURE (WTF) message with optional custom log message.
 * Uses stack trace to capture the file name and line number where the log was called.
 *
 * @param logMessage Optional message to log.
 */
fun Any.logWtf(logMessage: String? = null) {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.TERRIBLE_FAILURE,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
}

/**
 * Logs a DEBUG message without a custom message.
 * Uses stack trace to capture the file name and line number where the log was called.
 */
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
    VERBOSE,
    WARNING,
    TERRIBLE_FAILURE
}
