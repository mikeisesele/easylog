package com.michael.easylog.defaultloggers

import com.michael.easylog.LogType
import android.content.Context
import com.michael.easylog.domain.Logger
import com.michael.easylog.logE
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileLogger(private val context: Context?) : Logger {
    override fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType,
        fileName: String?,
        lineNumber: Int
    ) {
        // Check if the context is null
        if (context == null) {
            "Unable to write log message to file. Ensure you pass a valid context to .context() in your EasyLog setup".logE("Null Context")
            return
        }

        try {
            val logData = try {
                logObject.toString()
            } catch (e: Exception) {
                "Error converting data to string: ${e.message}"
            }

            val fullMessage = "$logMessage (at $fileName:$lineNumber):: $logData\n"
            val logFile = File(context.filesDir, "logs.txt")

            // Append the log message to the file
            FileWriter(logFile, true).use { writer ->
                writer.append(fullMessage)
            }
        } catch (e: IOException) {
            // Handle the error appropriately
            "Failed to write log message to file: ${e.message}".logE("File Write Error")
        }
    }
}
