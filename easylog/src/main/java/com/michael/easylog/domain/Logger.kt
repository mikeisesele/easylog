package com.michael.easylog.domain

import com.michael.easylog.LogType

/**
 * Logger - An interface for logging messages with various levels of severity.
 *
 * This interface defines a method for logging messages, allowing implementations
 * to handle logging in different ways, such as logging to a file, console, or remote server.
 *
 * @interface Logger
 */
interface Logger {

    /**
     * Logs a message with a specified severity level.
     *
     * This method logs a message along with the context of the log, including the object
     * being logged, the severity level, and the location in the source code where the log
     * call was made.
     *
     * the info below can be formatted based on your project requirements
     *
     * @param logMessage Optional custom message to log. If null, a default of Logged Data is used internally.
     * @param logObject The object whose state is being logged. This provides context to the log entry.
     * @param level The severity level of the log. Default of the LogType enum is LogType.INFO.
     *              others include Debug, Warn, Error
     * @param fileName The name of the source file where the log call was made. This is typically
     *                 captured using stack trace information.
     * @param lineNumber The line number in the source file where the log call was made. This is
     *                   also typically captured using stack trace information.
     *
     *
     *
     */
    fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType = LogType.INFO,
        fileName: String?,
        lineNumber: Int
    )
}
