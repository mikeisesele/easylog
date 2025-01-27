package com.michael.easylog.defaultloggers

import com.michael.easylog.LogType
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.michael.easylog.Logger
import com.michael.easylog.ifNullSetDefault
import com.michael.easylog.logE
import com.michael.easylog.toReadable
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime

class FileLogger(
    private val context: Context?,
    usableFileName: String? = null,
    shouldDeleteExistingFile: Boolean = false
) : Logger {

    private val logFileName: String = usableFileName?.let {
        if (it.contains(".txt")) it else "$it.txt"
    }.ifNullSetDefault { "logs.txt" }

    private val logFile: File by lazy {
        val file = File(context?.filesDir, logFileName)
        if (shouldDeleteExistingFile) {
            if (file.exists()) {
                file.delete()
            }
        }
        file
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType,
        fileName: String?,
        lineNumber: Int,
    ) {
        // Check if the context is null
        if (context == null) {
            "Unable to write log message to file. Ensure you pass a valid context to .context() in your EasyLog setup"
                .logE("Null Context in FileLogger")
            return
        }

        try {
            val logData = try { logObject.toString() }
            catch (e: Exception) { "Error converting data to string: ${e.message}" }

            val fullMessage = "${LocalDateTime.now().toReadable()} $logMessage (at $fileName:$lineNumber):: $logData\n"

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

