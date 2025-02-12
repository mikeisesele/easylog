package com.michael.easylog

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.michael.easylog.defaultloggers.BufferChunkingLogger
import com.michael.easylog.defaultloggers.BugFenderLogger
import com.michael.easylog.defaultloggers.DefaultAndroidLogger
import com.michael.easylog.defaultloggers.FileLogger
import com.michael.easylog.defaultloggers.TimberLogger

/**
 * EasyLog - A simple logging utility for Android applications.
 *
 * 1. Initialize the logger in your application's initialization phase:
 *          EasyLog.
 *              setUp {
 *                  debugMode(BuildConfig.DEBUG)
 *                  defaultLogger(DefaultLogger.DEFAULT_ANDROID)
 *                  filterTag ("CustomTag")  // EASY-LOG is set by default
 *              }
 *    - This filterTag would be what you can use to filter in your logcat.
 *
 * 2. Log messages using concise syntax directly on objects:
 *    - "Hello".logD("This is a debug message") // This is a debug message - Hello
 *
 * 3. Optionally, you can use default log messages:
 *    - "Another Message".log() // default message - Another Message
 *    -  "Another Message".logInline() // default message - logs Another Message and returns the object itself
 *
 * Note:
 * - Default defaultLogger level is DEFAULT_ANDROID.
 * others internal loggers include: FILE_LOGGER, TIMBER, BUFFER_CHUNKING, BUG_FENDER
 */

object EasyLog {
    private const val IDENTIFIER = "EASY-LOG"
    var logTag: String = IDENTIFIER
    private var logLevel: LogType = LogType.DEBUG
    private var isDebugMode: Boolean = true
    private val loggers = mutableListOf<Logger>()

    // Builder for EasyLog configuration
    class Builder {

        private var filterTag: String = IDENTIFIER
        private var debugMode: Boolean = isDebugMode
        private val defaultLoggers = mutableListOf<DefaultLogger>()
        private val customLoggers = mutableListOf<Logger>()
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
         * Optional.
         * Sets the default logger to be used for logging.
         * The default logger [ DefaultLogger.DEFAULT_ANDROID ] is used if not specified.
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
        fun addDefaultLogger(defaultLogger: DefaultLogger) = apply { this.defaultLoggers.add(defaultLogger) }


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
        fun addCustomLogger(customLogger: Logger) = apply { this.customLoggers.add(customLogger) }

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
            loggers.clear()
            loggers.addAll(customLoggers)
            defaultLoggers.forEach { defaultLogger ->
                loggers.add(createLogger(defaultLogger, context))
            }
            if (loggers.isEmpty()) {
                loggers.add(DefaultAndroidLogger()) // Fallback to default logger
            }
        }
    }

    @JvmStatic // make it callable from Java as a static method
    fun setUp(action: BuilderAction) {
        val builder = Builder()
        action.apply(builder)
        builder.build()
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
            loggers.forEach { logger ->
                logger.log(
                    logMessage = logMessage.ifNullSetDefault { "Logged Data" },
                    logObject = logObject,
                    level = level,
                    fileName = fileName,
                    lineNumber = lineNumber,
                )
            }
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
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
    val stackTraceElement = getStackTraceElement()
    EasyLog.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.DEBUG,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber
    )
    return this
}

@RequiresApi(Build.VERSION_CODES.O)
fun <T : Any> T.logToFile(
    logMessage: String? = null,
    fileName: String? = null,
    context: Context,
    shouldDeleteExistingFile: Boolean = true
): T {
    val stackTraceElement = getStackTraceElement()
    val fileLogger = FileLogger(context, fileName, shouldDeleteExistingFile)
    fileLogger.log(
        logMessage = logMessage,
        logObject = this,
        level = LogType.DEBUG,
        fileName = stackTraceElement.fileName,
        lineNumber = stackTraceElement.lineNumber,
    )
    return this
}

private fun getStackTraceElement(): StackTraceElement {
    val stackTrace = Throwable().stackTrace

    // Filter out irrelevant stack trace elements
    val filteredStackTrace = stackTrace.filter {
        it.fileName != null &&
        !it.className.startsWith("kotlinx.coroutines.") &&
        !it.className.startsWith("androidx.compose.") &&
        !it.className.contains("EasyLog") &&
        !it.methodName.contains("log")
    }

    // Find the first stack frame outside the logging infrastructure
    val fallbackStackFrame = filteredStackTrace.firstOrNull { !it.className.contains("EasyLog") }

    // If no suitable stack frame found, fallback to a known stack frame
    val primaryFrame = fallbackStackFrame ?: filteredStackTrace.firstOrNull() ?: stackTrace[0]

    // If the primary frame is within a coroutine or composable, traverse up the stack to find the outermost frame
    if (primaryFrame.className.startsWith("kotlinx.coroutines.") ||
        primaryFrame.className.startsWith("androidx.compose.")) {
        var outerFrame: StackTraceElement? = primaryFrame
        var currentIndex = filteredStackTrace.indexOf(primaryFrame)

        // Traverse up the stack until a non-coroutine and non-composable frame is found
        while (currentIndex >= 0 && (filteredStackTrace[currentIndex].className.startsWith("kotlinx.coroutines.") || filteredStackTrace[currentIndex].className.startsWith("androidx.compose."))) {
            outerFrame = filteredStackTrace[currentIndex]
            currentIndex--
        }

        // Use the outermost frame if found, otherwise use the primary frame
        return outerFrame ?: primaryFrame
    }

    return primaryFrame
}

enum class LogType {
    DEBUG,
    INFO,
    ERROR,
    VERBOSE,
    WARNING,
    TERRIBLE_FAILURE
}
