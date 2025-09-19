package com.michael.easylog

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.michael.easylog.defaultloggers.FileLogger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Extension function to set a default value if the object is null.
 *
 * @param defaultValue A lambda function providing the default value.
 * @return The original object if not null, otherwise the result of defaultValue().
 */
inline fun <T> T?.ifNullSetDefault(defaultValue: () -> T): T {
    return this ?: defaultValue()
}


// Extension functions
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


fun logMany(
    header: String? = null,
    vararg items: Any
) {
    EasyLog.logMany(
        header,
        *items
    )
}

enum class LogType {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    TERRIBLE_FAILURE
}


fun LocalDateTime.toReadable(): String {
    return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")
        return this.format(formatter)
    } else {
        "This function requires API level 26 or higher".logInline()
    }
}
