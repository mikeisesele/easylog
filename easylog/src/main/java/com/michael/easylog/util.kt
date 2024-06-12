package com.michael.easylog

import android.os.Build
import androidx.annotation.RequiresApi
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

fun LocalDateTime.toReadable(): String {
    return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")
        return this.format(formatter)
    } else {
        "This function requires API level 26 or higher".logInline()
    }
}
