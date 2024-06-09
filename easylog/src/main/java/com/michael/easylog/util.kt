package com.michael.easylog

/**
 * Extension function to set a default value if the object is null.
 *
 * @param defaultValue A lambda function providing the default value.
 * @return The original object if not null, otherwise the result of defaultValue().
 */
inline fun <T> T?.ifNullSetDefault(defaultValue: () -> T): T {
    return this ?: defaultValue()
}
