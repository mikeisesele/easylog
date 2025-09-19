package com.michael.easylog

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.michael.easylog.defaultloggers.DefaultAndroidLogger
import com.michael.easylog.defaultloggers.FileLogger
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * EasyLog v2 - Enhanced logging utility for Android applications with improved formatting.
 */
object EasyLog {
    private const val IDENTIFIER = "EASY-LOG"

    @Volatile
    var logTag: String = IDENTIFIER
        private set

    @Volatile
    private var logLevel: LogType = LogType.DEBUG

    @Volatile
    private var minimumLogLevel: LogType = LogType.DEBUG

    @Volatile
    private var isDebugMode: Boolean = true

    private val loggers = mutableListOf<Logger>()

    class Builder {
        private var filterTag: String = IDENTIFIER
        private var debugMode: Boolean = true
        private var minLogLevel: LogType = LogType.DEBUG
        private val defaultLoggers = mutableListOf<DefaultLogger>()
        private val customLoggers = mutableListOf<Logger>()
        private var context: Context? = null

        fun filterTag(filterTag: String) = apply { this.filterTag = filterTag }
        fun debugMode(debugMode: Boolean) = apply { this.debugMode = debugMode }
        fun minimumLogLevel(level: LogType) = apply { this.minLogLevel = level }
        fun addDefaultLogger(defaultLogger: DefaultLogger) = apply {
            this.defaultLoggers.add(defaultLogger)
        }

        @Deprecated("Use addDefaultLogger instead", ReplaceWith("addDefaultLogger(defaultLogger)"))
        fun defaultLogger(defaultLogger: DefaultLogger) = addDefaultLogger(defaultLogger)

        fun context(context: Context?) = apply { this.context = context }

        fun build() {
            synchronized(EasyLog) {
                logTag = filterTag
                isDebugMode = debugMode
                minimumLogLevel = minLogLevel
                loggers.clear()
                loggers.addAll(customLoggers)

                defaultLoggers.forEach { defaultLogger ->
                    loggers.add(createLogger(defaultLogger, context))
                }

                if (loggers.isEmpty()) {
                    loggers.add(DefaultAndroidLogger())
                }
            }
        }
    }

    @JvmStatic
    fun setUp(action: BuilderAction) {
        val builder = Builder()
        action.apply(builder)
        builder.build()
    }

    fun setUp(builder: Builder.() -> Unit) {
        Builder().apply(builder).build()
    }

    fun setMinimumLogLevel(level: LogType) {
        synchronized(this) {
            minimumLogLevel = level
        }
    }

    fun getMinimumLogLevel(): LogType = minimumLogLevel

    private fun shouldLog(level: LogType): Boolean {
        return level.ordinal >= minimumLogLevel.ordinal
    }

    private fun createLogger(defaultLogger: DefaultLogger, context: Context?): Logger {
        return when (defaultLogger) {
            DefaultLogger.DEFAULT_ANDROID -> DefaultAndroidLogger()
            DefaultLogger.FILE_LOGGER -> FileLogger(context!!)
            DefaultLogger.BUFFER_CHUNKING -> {
                Log.w(
                    logTag,
                    "BUFFER_CHUNKING logger is deprecated. Falling back to DEFAULT_ANDROID."
                )
                DefaultAndroidLogger()
            }

            DefaultLogger.BUG_FENDER -> {
                Log.w(logTag, "BUG_FENDER logger is deprecated. Falling back to DEFAULT_ANDROID.")
                DefaultAndroidLogger()
            }

            DefaultLogger.TIMBER -> {
                Log.w(logTag, "TIMBER logger is deprecated. Falling back to DEFAULT_ANDROID.")
                DefaultAndroidLogger()
            }
        }
    }

    internal fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType = logLevel,
        fileName: String?,
        lineNumber: Int
    ) {
        if (!isDebugMode || !shouldLog(level)) return

        val emoji = getEmojiForLevel(level)
        val formattedObject = formatLogObject(logObject)
        val message = formatLogMessage(logMessage, formattedObject, fileName, lineNumber, emoji)

        loggers.forEach { logger ->
            when (logger) {
                is DefaultAndroidLogger -> {
                    logToAndroid(level, message, logTag)
                }

                else -> {
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

    private fun formatLogObject(logObject: Any): String {
        return when {
            isPrimitiveType(logObject) -> formatPrimitive(logObject)
            logObject is List<*> -> formatList(logObject)
            logObject is Array<*> -> formatArray(logObject)
            isPrimitiveArray(logObject) -> formatPrimitiveArray(logObject)
            else -> formatComplexObject(logObject)
        }
    }

    private fun formatPrimitive(obj: Any): String {
        return when (obj) {
            is String -> "\"$obj\""
            is Boolean -> obj.toString()
            is Number -> obj.toString()
            is Char -> "'$obj'"
            is Enum<*> -> obj.name
            else -> obj.toString()
        }
    }

    private fun formatList(list: List<*>): String {
        if (list.isEmpty()) return "List[0] (empty)"

        val sb = StringBuilder()
        val shouldUseHeaderFooter = list.size > 5

        if (shouldUseHeaderFooter) {
            sb.append("═══ LIST[${list.size}] ═══\n")
        } else {
            sb.append("╭─ List[${list.size}]\n")
        }

        val itemsToShow = minOf(list.size, 10)
        list.take(itemsToShow).forEachIndexed { index, item ->
            val isLast = index == itemsToShow - 1 && list.size <= 10
            val prefix = if (shouldUseHeaderFooter) {
                if (index == itemsToShow - 1 && list.size <= 10) "└─" else "├─"
            } else {
                if (isLast) "╰─" else "├─"
            }

            when {
                item == null -> sb.append("$prefix [$index]: null\n")
                item is String -> sb.append("$prefix [$index]: \"$item\"\n")
                isPrimitiveType(item) -> sb.append("$prefix [$index]: $item\n")
                else -> sb.append("$prefix [$index]: ${item::class.simpleName}\n")
            }
        }

        if (list.size > 10) {
            val prefix = if (shouldUseHeaderFooter) "└─" else "╰─"
            sb.append("$prefix ... and ${list.size - 10} more items\n")
        }

        if (shouldUseHeaderFooter) {
            sb.append("═══════════════════════════════════")
        }

        return sb.toString().trimEnd()
    }

    private fun formatArray(array: Array<*>): String {
        if (array.isEmpty()) return "Array[0] (empty)"

        val sb = StringBuilder()
        val shouldUseHeaderFooter = array.size > 5

        if (shouldUseHeaderFooter) {
            sb.append("═══ ARRAY[${array.size}] ═══\n")
        } else {
            sb.append("╭─ Array[${array.size}]\n")
        }

        val itemsToShow = minOf(array.size, 10)
        array.take(itemsToShow).forEachIndexed { index, item ->
            val isLast = index == itemsToShow - 1 && array.size <= 10
            val prefix = if (shouldUseHeaderFooter) {
                if (index == itemsToShow - 1 && array.size <= 10) "└─" else "├─"
            } else {
                if (isLast) "╰─" else "├─"
            }

            when {
                item == null -> sb.append("$prefix [$index]: null\n")
                item is String -> sb.append("$prefix [$index]: \"$item\"\n")
                isPrimitiveType(item) -> sb.append("$prefix [$index]: $item\n")
                else -> sb.append("$prefix [$index]: ${item::class.simpleName}\n")
            }
        }

        if (array.size > 10) {
            val prefix = if (shouldUseHeaderFooter) "└─" else "╰─"
            sb.append("$prefix ... and ${array.size - 10} more items\n")
        }

        if (shouldUseHeaderFooter) {
            sb.append("═══════════════════════════════════")
        }

        return sb.toString().trimEnd()
    }

    private fun isPrimitiveType(obj: Any): Boolean {
        return when (obj) {
            is String, is Number, is Boolean, is Char -> true
            is Enum<*> -> true
            else -> {
                val className = obj::class.qualifiedName ?: ""
                className.startsWith("kotlinx.coroutines.") ||
                        obj::class.javaPrimitiveType != null
            }
        }
    }

    private fun isPrimitiveArray(obj: Any): Boolean {
        return when (obj) {
            is IntArray, is LongArray, is FloatArray, is DoubleArray,
            is BooleanArray, is ByteArray, is CharArray, is ShortArray -> true

            else -> false
        }
    }

    private fun formatPrimitiveArray(obj: Any): String {
        val (typeName, size, items) = when (obj) {
            is IntArray -> Triple("IntArray", obj.size, obj.take(10).map { it.toString() })
            is LongArray -> Triple("LongArray", obj.size, obj.take(10).map { it.toString() })
            is FloatArray -> Triple("FloatArray", obj.size, obj.take(10).map { it.toString() })
            is DoubleArray -> Triple("DoubleArray", obj.size, obj.take(10).map { it.toString() })
            is BooleanArray -> Triple("BooleanArray", obj.size, obj.take(10).map { it.toString() })
            is ByteArray -> Triple("ByteArray", obj.size, obj.take(10).map { it.toString() })
            is CharArray -> Triple("CharArray", obj.size, obj.take(10).map { "'$it'" })
            is ShortArray -> Triple("ShortArray", obj.size, obj.take(10).map { it.toString() })
            else -> return obj.toString()
        }

        if (size == 0) return "$typeName[0] (empty)"

        val sb = StringBuilder()
        val shouldUseHeaderFooter = size > 5

        if (shouldUseHeaderFooter) {
            sb.append("═══ $typeName[$size] ═══\n")
        } else {
            sb.append("╭─ $typeName[$size]\n")
        }

        val itemsToShow = minOf(size, 10)
        items.take(itemsToShow).forEachIndexed { index, item ->
            val isLast = index == itemsToShow - 1 && size <= 10
            val prefix = if (shouldUseHeaderFooter) {
                if (index == itemsToShow - 1 && size <= 10) "└─" else "├─"
            } else {
                if (isLast) "╰─" else "├─"
            }
            sb.append("$prefix [$index]: $item\n")
        }

        if (size > 10) {
            val prefix = if (shouldUseHeaderFooter) "└─" else "╰─"
            sb.append("$prefix ... and ${size - 10} more items\n")
        }

        if (shouldUseHeaderFooter) {
            sb.append("═══════════════════════════════════")
        }

        return sb.toString().trimEnd()
    }

    private fun formatComplexObject(obj: Any, depth: Int = 0, maxDepth: Int = 3): String {
        return try {
            val sb = StringBuilder()
            val className = obj::class.simpleName ?: "Unknown"
            val qualifiedName = obj::class.qualifiedName ?: "Unknown"
            val packageName = qualifiedName.substringBeforeLast('.', "")

            val properties = obj::class.memberProperties
                .filter { property ->
                    try {
                        property.isAccessible = true
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                .sortedBy { it.name }

            val shouldUseHeaderFooter = depth == 0 && properties.size > 5

            if (depth == 0) {
                if (shouldUseHeaderFooter) {
                    sb.append("═══ $className")
                    if (packageName.isNotEmpty() && packageName != className) {
                        sb.append(" ($packageName)")
                    }
                    sb.append(" ═══\n")
                } else {
                    sb.append("╭─ $className")
                    if (packageName.isNotEmpty() && packageName != className) {
                        sb.append(" ($packageName)")
                    }
                    sb.append("\n")
                }
            }

            if (properties.isEmpty()) {
                sb.append("${getIndent(depth)}├─ No accessible properties\n")
                if (depth == 0) {
                    if (shouldUseHeaderFooter) {
                        sb.append("═══════════════════════════════════")
                    } else {
                        sb.append("╰─────────────────────────")
                    }
                }
                return sb.toString().trimEnd()
            }

            properties.forEachIndexed { index, property ->
                val isLast = index == properties.size - 1
                val prefix = when {
                    shouldUseHeaderFooter && isLast -> "└─"
                    shouldUseHeaderFooter -> "├─"
                    depth == 0 && isLast -> "╰─"
                    depth == 0 -> "├─"
                    isLast -> "└─"
                    else -> "├─"
                }

                try {
                    property.isAccessible = true
                    val value = (property as KProperty1<Any, *>).get(obj)
                    val indent = getIndent(depth)

                    when {
                        value == null -> {
                            sb.append("$indent$prefix ${property.name}: null\n")
                        }

                        value is String -> {
                            sb.append("$indent$prefix ${property.name}: \"$value\"\n")
                        }

                        value is Number -> {
                            sb.append("$indent$prefix ${property.name}: $value\n")
                        }

                        value is Boolean -> {
                            sb.append("$indent$prefix ${property.name}: $value\n")
                        }

                        value is Enum<*> -> {
                            sb.append("$indent$prefix ${property.name}: ${value.name}\n")
                        }

                        value is Collection<*> -> {
                            sb.append("$indent$prefix ${property.name}: ${value::class.simpleName}[${value.size}]")
                            if (value.isNotEmpty() && depth < maxDepth) {
                                sb.append("\n")
                                formatCollectionItems(value, sb, depth + 1, maxDepth)
                            } else {
                                sb.append("\n")
                            }
                        }

                        value is Array<*> -> {
                            sb.append("$indent$prefix ${property.name}: ${value::class.simpleName}[${value.size}]")
                            if (value.isNotEmpty() && depth < maxDepth) {
                                sb.append("\n")
                                formatArrayItems(value, sb, depth + 1, maxDepth)
                            } else {
                                sb.append("\n")
                            }
                        }

                        isPrimitiveArray(value) -> {
                            val formattedArray = formatPrimitiveArray(value)
                            val lines = formattedArray.split('\n')
                            if (lines.size == 1) {
                                sb.append("$indent$prefix ${property.name}: ${lines[0]}\n")
                            } else {
                                sb.append("$indent$prefix ${property.name}:\n")
                                lines.forEach { line ->
                                    if (line.isNotBlank()) {
                                        sb.append("${getIndent(depth + 1)}$line\n")
                                    }
                                }
                            }
                        }

                        isPrimitiveType(value) -> {
                            sb.append("$indent$prefix ${property.name}: $value\n")
                        }

                        depth < maxDepth -> {
                            sb.append("$indent$prefix ${property.name}: ${value::class.simpleName}\n")
                            val nestedFormatted = formatComplexObject(value, depth + 1, maxDepth)
                            val nestedLines = nestedFormatted.split('\n').filter { it.isNotBlank() }
                            nestedLines.forEach { line ->
                                sb.append("${getIndent(depth + 1)}$line\n")
                            }
                        }

                        else -> {
                            sb.append("$indent$prefix ${property.name}: ${value::class.simpleName} {...}\n")
                        }
                    }
                } catch (e: Exception) {
                    val indent = getIndent(depth)
                    sb.append("$indent$prefix ${property.name}: <inaccessible>\n")
                }
            }

            if (shouldUseHeaderFooter) {
                sb.append("═══════════════════════════════════")
            }

            sb.toString().trimEnd()
        } catch (e: Exception) {
            obj.toString()
        }
    }

    private fun formatCollectionItems(
        collection: Collection<*>,
        sb: StringBuilder,
        depth: Int,
        maxDepth: Int
    ) {
        val items = collection.take(5)
        items.forEachIndexed { index, item ->
            val isLast = index == items.size - 1 && collection.size <= 5
            val prefix = if (isLast) "└─" else "├─"
            val indent = getIndent(depth)

            when {
                item == null -> sb.append("$indent$prefix [$index]: null\n")
                isPrimitiveType(item) -> sb.append("$indent$prefix [$index]: $item\n")
                depth < maxDepth -> {
                    sb.append("$indent$prefix [$index]: ${item::class.simpleName}\n")
                    val nestedFormatted = formatComplexObject(item, depth + 1, maxDepth)
                    val nestedLines = nestedFormatted.split('\n').filter { it.isNotBlank() }
                    nestedLines.forEach { line ->
                        sb.append("${getIndent(depth + 1)}$line\n")
                    }
                }

                else -> sb.append("$indent$prefix [$index]: ${item::class.simpleName} {...}\n")
            }
        }

        if (collection.size > 5) {
            val indent = getIndent(depth)
            sb.append("$indent└─ ... and ${collection.size - 5} more items\n")
        }
    }

    private fun formatArrayItems(array: Array<*>, sb: StringBuilder, depth: Int, maxDepth: Int) {
        val items = array.take(5)
        items.forEachIndexed { index, item ->
            val isLast = index == items.size - 1 && array.size <= 5
            val prefix = if (isLast) "└─" else "├─"
            val indent = getIndent(depth)

            when {
                item == null -> sb.append("$indent$prefix [$index]: null\n")
                isPrimitiveType(item) -> sb.append("$indent$prefix [$index]: $item\n")
                depth < maxDepth -> {
                    sb.append("$indent$prefix [$index]: ${item::class.simpleName}\n")
                    val nestedFormatted = formatComplexObject(item, depth + 1, maxDepth)
                    val nestedLines = nestedFormatted.split('\n').filter { it.isNotBlank() }
                    nestedLines.forEach { line ->
                        sb.append("${getIndent(depth + 1)}$line\n")
                    }
                }

                else -> sb.append("$indent$prefix [$index]: ${item::class.simpleName} {...}\n")
            }
        }

        if (array.size > 5) {
            val indent = getIndent(depth)
            sb.append("$indent└─ ... and ${array.size - 5} more items\n")
        }
    }

    private fun getIndent(depth: Int): String {
        return when (depth) {
            0 -> ""
            1 -> "│  "
            2 -> "│  │  "
            3 -> "│  │  │  "
            else -> "│  ".repeat(depth)
        }
    }

    private fun getEmojiForLevel(level: LogType): String = when (level) {
        LogType.DEBUG -> "🔍"
        LogType.INFO -> "ℹ️"
        LogType.ERROR -> "❌"
        LogType.VERBOSE -> "📝"
        LogType.WARNING -> "⚠️"
        LogType.TERRIBLE_FAILURE -> "💥"
    }

    private fun formatLogMessage(
        logMessage: String?,
        logObject: String,
        fileName: String?,
        lineNumber: Int,
        emoji: String
    ): String {
        val location = "at $fileName:$lineNumber"
        val hasMultipleLines = logObject.contains('\n')

        return if (hasMultipleLines) {
            if (logMessage != null) {
                "$emoji ${logMessage.uppercase()}: $location\n$logObject"
            } else {
                "$emoji ${getObjectTypeName(logObject)}: $location\n$logObject"
            }
        } else {
            if (logMessage != null) {
                "$emoji ${logMessage.uppercase()} $location - $logObject"
            } else {
                "$emoji $logObject $location"
            }
        }
    }

    private fun getObjectTypeName(formattedObject: String): String {
        val firstLine = formattedObject.lines().firstOrNull() ?: ""
        return when {
            firstLine.startsWith("╭─ ") -> firstLine.removePrefix("╭─ ").substringBefore(" (")
            else -> "Object"
        }
    }

    /**
     * Logs multiple objects with a single call, each object as a separate log entry.
     *
     * @param level The log level to use
     * @param message Optional message to prepend to each log
     * @param objects Variable number of objects to log
     */
    @Suppress("ComplexMethod")
    fun logMany(
        header: String? = null,
        vararg items: Any
    ) {
        val stackTraceElement = getStackTraceElement()

        if (!isDebugMode || !shouldLog(LogType.DEBUG)) return

        val emoji = getEmojiForLevel(LogType.DEBUG)
        val location = "at ${stackTraceElement.fileName}:${stackTraceElement.lineNumber}"

        // Build tree structure
        val sb = StringBuilder()
        val shouldUseHeaderFooter = items.size > 5

        // Header
        val headerText = header?.uppercase() ?: "LOG GROUP"
        if (shouldUseHeaderFooter) {
            sb.append("$emoji ═══ $headerText ═══ $location\n")
        } else {
            sb.append("$emoji ╭─ $headerText $location\n")
        }

        // Items
        items.forEachIndexed { index, item ->
            val isLast = index == items.size - 1
            val prefix = if (shouldUseHeaderFooter) {
                if (isLast) "└─" else "├─"
            } else {
                if (isLast) "╰─" else "├─"
            }

            val formattedObject = formatLogObject(item)
            val indexIndicator = "[${index + 1}] "

            if (formattedObject.contains('\n')) {
                // Multi-line object - indent all lines
                sb.append("$emoji $prefix $indexIndicator\n")
                formattedObject.split('\n').forEach { line ->
                    if (line.isNotBlank()) {
                        sb.append("$emoji │  $line\n")
                    }
                }
            } else {
                // Single line object
                sb.append("$emoji $prefix $indexIndicator$formattedObject\n")
            }
        }

        // Footer for large groups
        if (shouldUseHeaderFooter) {
            sb.append("$emoji ═══════════════════════════════════")
        }

        // Log the complete tree structure
        EasyLog.logToAndroid(LogType.DEBUG, sb.toString().trimEnd())
    }
    

    private fun logToAndroid(level: LogType, message: String, tag: String = logTag) {
        when (level) {
            LogType.DEBUG -> Log.d(tag, message)
            LogType.INFO -> Log.i(tag, message)
            LogType.ERROR -> Log.e(tag, message)
            LogType.VERBOSE -> Log.v(tag, message)
            LogType.WARNING -> Log.w(tag, message)
            LogType.TERRIBLE_FAILURE -> Log.wtf(tag, message)
        }
    }
}


internal fun getStackTraceElement(): StackTraceElement {
    val stackTrace = Throwable().stackTrace

    val filteredStackTrace = stackTrace.filter {
        it.fileName != null &&
                !it.className.startsWith("kotlinx.coroutines.") &&
                !it.className.startsWith("androidx.compose.") &&
                !it.className.contains("EasyLog") &&
                !it.methodName.contains("log")
    }

    val fallbackStackFrame = filteredStackTrace.firstOrNull { !it.className.contains("EasyLog") }
    val primaryFrame = fallbackStackFrame ?: filteredStackTrace.firstOrNull() ?: stackTrace[0]

    if (primaryFrame.className.startsWith("kotlinx.coroutines.") ||
        primaryFrame.className.startsWith("androidx.compose.")
    ) {
        var outerFrame: StackTraceElement? = primaryFrame
        var currentIndex = filteredStackTrace.indexOf(primaryFrame)

        while (currentIndex >= 0 && (filteredStackTrace[currentIndex].className.startsWith("kotlinx.coroutines.") || filteredStackTrace[currentIndex].className.startsWith(
                "androidx.compose."
            ))
        ) {
            outerFrame = filteredStackTrace[currentIndex]
            currentIndex--
        }

        return outerFrame ?: primaryFrame
    }

    return primaryFrame
}
