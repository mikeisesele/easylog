package com.michael.easylog

import android.content.Context
import com.michael.easylog.defaultloggers.BufferChunkingLogger
import com.michael.easylog.defaultloggers.BugFenderLogger
import com.michael.easylog.defaultloggers.DefaultAndroidLogger
import com.michael.easylog.defaultloggers.FileLogger
import com.michael.easylog.defaultloggers.TimberLogger

/**
 * EasyLog - A simple logging utility for Android applications.
 *
 * Usage:
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
    var logTag: String = IDENTIFIER
    private var logLevel: LogType = LogType.DEBUG
    private var isDebugMode: Boolean = true
    private lateinit var logger: Logger

    // Builder for EasyLog configuration
    class Builder {

        private var filterTag: String = IDENTIFIER
        private var debugMode: Boolean = isDebugMode
        private var defaultLogger: DefaultLogger? = null
        private var customLogger: Logger? = null
        private var context: Context? = null

        /**
         * Sets the custom filter tag to be used in log messages.
         * This tag helps in filtering log messages for easier debugging and identification.
         *
         * @param filterTag The custom filter tag to be applied to log messages.
         * default filter tag is "EASY-LOG"
         */
        fun filterTag(filterTag: String) = apply { this.filterTag = filterTag }

        /**
         * Sets the debug mode for logging.
         * When debug mode is enabled, logging is performed; otherwise, logging is disabled.
         *
         * @param debugMode Boolean value indicating whether logging should be enabled in debug mode.
         * set the below in your app build.gradle file, inside the android block
         *   buildFeatures {
         *      buildConfig = true
         *   }
         *
         *   then pass in BuildConfig.DEBUG as a parameter
         */
        fun debugMode(debugMode: Boolean) = apply { this.debugMode = debugMode }

        /**
         * Sets the default logger to be used for logging.
         * The default logger is used if no custom logger is specified.
         *
         * @param defaultLogger The default logger to be used for logging.
         *
         * Possible values for defaultLogger:
         *
         * - `DefaultLogger.DEFAULT_ANDROID`: Logs the log messages using the default Android logger.
         * - `DefaultLogger.FILE_LOGGER`: Logs the log messages to a file in the app's cache directory.
         * - `DefaultLogger.TIMBER`: Logs the log messages using the Timber library.
         * - `DefaultLogger.BUFFER_CHUNKING`: Chunks the log messages into smaller pieces.
         * - `DefaultLogger.BUG_FENDER`: Logs the error messages using BugFender's remote server.
         *
         */
        fun defaultLogger(defaultLogger: DefaultLogger?) = apply { this.defaultLogger = defaultLogger }

        /**
         * Sets the custom logger to be used for logging.
         * If a custom logger is specified, it takes precedence over the default logger.
         *
         * @param customLogger The custom logger to be used for logging.
         *
         * Use a custom logger when you need specific logging behavior tailored to your application's needs.
         * For example, you may want to implement a custom logger that logs messages to a remote server
         * or formats log messages in a specific way.
         *
         * provide a custom logger class extending the Logger interface adn override the log method.
         */
        fun customLogger(customLogger: Logger?) = apply { this.customLogger = customLogger }

        /**
         * Sets the context to be used for logging.
         * This context is only required for File logging.
         *
         * @param context The context to be used for logging, if applicable.
         */
        fun context(context: Context?) = apply { this.context = context }


        fun build() {
            logTag = filterTag
            isDebugMode = debugMode
            logger = when {
                customLogger != null -> customLogger!!
                defaultLogger != null -> createLogger(defaultLogger!!, context)
                else -> DefaultAndroidLogger() // Fallback to default logger
            }
        }
    }

    fun setUp(builder: Builder.() -> Unit) {
        Builder().apply(builder).build()
    }

    private fun createLogger(defaultLogger: DefaultLogger, context: Context?): Logger {
            return when (defaultLogger) {
                DefaultLogger.BUFFER_CHUNKING -> BufferChunkingLogger()
                DefaultLogger.BUG_FENDER -> BugFenderLogger()
                DefaultLogger.DEFAULT_ANDROID -> DefaultAndroidLogger()
                DefaultLogger.FILE_LOGGER -> FileLogger(context!!)
                DefaultLogger.TIMBER -> TimberLogger()
        }
    }


    internal fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType = logLevel,
        fileName: String?,
        lineNumber: Int
    ) {
        if (isDebugMode) {
            logger.log(
                logMessage = logMessage.ifNullSetDefault { "Logged Data" },
                logObject = logObject,
                level = level,
                fileName = fileName,
                lineNumber = lineNumber
            )
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

/**
 * Logs a DEBUG message if the calling object is not null and returns the object itself.
 *
 * @param None
 * @return T? - The original object, which can be null.
 *
 * This extension function can be called on any nullable object.
 * If the object is null, it skips logging.
 * This function is useful for logging and returning nullable objects in a fluent style.
 *
 * Example:
 * ```
 * val myNullableObject: MyClass? = getNullableObject()
 * myNullableObject.logInlineNullable()
 * ```
 */
fun <T : Any> T?.logInlineNullable(logMessage: String? = null): T? {
    val stackTraceElement = Throwable().stackTrace[1]
    this?.let {
        EasyLog.log(
            logMessage = logMessage,
            logObject = it,
            level = LogType.DEBUG,
            fileName = stackTraceElement.fileName,
            lineNumber = stackTraceElement.lineNumber
        )
    } ?: run {
        EasyLog.log(
            logMessage = "Object at ${stackTraceElement.fileName + ":" + stackTraceElement.lineNumber} is null",
            logObject = "Null",
            level = LogType.DEBUG,
            fileName = stackTraceElement.fileName,
            lineNumber = stackTraceElement.lineNumber
        )
    }
    return this
}

/**
 * Logs a DEBUG message and returns the object itself.
 *
 * @param None
 * @return T - The original object, which is guaranteed to be non-null.
 * This function is useful for logging and returning non-nullable objects in a fluent style.
 *
 */
fun <T : Any> T.logInline(logMessage: String? = null): T {
    val stackTraceElement = Throwable().stackTrace[1]
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.DEBUG,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
    return this
}


enum class LogType {
    DEBUG,
    INFO,
    ERROR,
    VERBOSE,
    WARNING,
    TERRIBLE_FAILURE
}
