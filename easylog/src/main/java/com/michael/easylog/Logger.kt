package com.michael.easylog

interface Logger {
    fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType = LogType.INFO,
        fileName: String?,
        lineNumber: Int
    )
}
