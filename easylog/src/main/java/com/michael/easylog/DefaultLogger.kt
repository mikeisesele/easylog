package com.michael.easylog

/**
 * Default logger enumeration
 */
enum class DefaultLogger {
    DEFAULT_ANDROID,
    FILE_LOGGER,

    // Deprecated - kept for backward compatibility
    @Deprecated("Use DEFAULT_ANDROID instead")
    TIMBER,

    @Deprecated("Use DEFAULT_ANDROID instead")
    BUFFER_CHUNKING,

    @Deprecated("Use DEFAULT_ANDROID instead")
    BUG_FENDER
}