# EasyLog

EasyLog is a lightweight logging utility for Android applications designed to simplify logging tasks with a concise syntax.

## Features

- Easy-to-use API for logging messages
- Supports logging at different levels of severity
- Lightweight and efficient
- Supports filtering log messages in logcat
- Logs only in development environments, preserving production performance.

**Advanced Features:**
- Automatically captures the class and line number of the log call site.
- Displays the data type of the logged object.
- Log messages are formatted for easy readability in Logcat.

## Installation

1. To use EasyLog in your Android project, add the following dependency to your `build.gradle` file:

```groovy
# Groovy
implementation 'com.github.mikeisesele:easylog:latestVersion'
```

```kotlin
// Kotlin dsl
implementation("com.github.mikeisesele:easylog:latestVersion")
```

2. Ensure you have JitPack configured in your settings.gradle file for dependency resolution:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Add this line  <==
    }
}
```

## Usage

1. **Enable Debugging**: Ensure your application is in debug mode to view logs in the logcat.

2. **Initialization**: Initialize the logger during your application's initialization phase with optional configurations:

    ```kotlin

    // This setup can be done in your Application class or BaseAvtivty if you have one. 
    // Otherwise, the MainActivity works just fine.

    EasyLog.setup(
        filterTag = "CustomTag", // Optional filter tag for log messages. Defaults to EASY-LOG
        debugMode = BuildConfig.DEBUG // Ensure logs performs in debug mode only. [ BuildConfig.DEBUG returns false in release environment ]
    )

    // NOTE: Internally, debugMode is true by default. to prevent logging on production, do not manually set this to true.
    // rather use BuildConfig.DEBUG as the parameter, 
    // or better still use environment variables with BuildVariants.

    ```

3. **Logging**:

   Log messages using concise syntax directly on objects:

   ```kotlin
   "Hello".logD("This is a debug message") // DEBUG - Hello: This is a debug message
   "World".logI("This is an info message") // INFO - World: This is an info message
   42.logE("This is an error message") // ERROR - 42: This is an error message
   SomeObject().logV("This is a verbose message") // VERBOSE - SomeObject: This is a verbose message
   "Warning".logW("This is a warning message") // WARNING - Warning: This is a warning message
   "WTF".logWtf("This should not happen") // TERRIBLE_FAILURE - WTF: This should not happen
   ```

4. **Default Logging**:

   Optionally, you can use default log messages:

   ```kotlin
   "Another Message".log() // EASY-LOG: Another Message

   val myNullableObject: MyClass? = getNullableObject()
   myNullableObject.logInline()

   // logInline the values of savedInstanceState [nullable]
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState.logInline()) Logs Inline  <==
   }

   val myObject: MyClass = getNonNullableObject()
    myObject.logInline() //  log message is optional to pass

    // log the value of contact screen state [non-nullable]
   ExampleScreen(
        viewModelState = state.logInline(),  Logs Inline  <==
    )
   ```

### More Logging Examples

EasyLog provides concise methods for logging messages at different levels of severity. 
Log messages are automatically formatted for easy readability in Logcat, 
including the class and line number of the log call site and the data type of the logged object.

```kotlin
// Log integer value
123.logD("Integer: ")

// Log double value
123.0.logD("Double: ")

// Log string value
"John".logD("String: ")

// Log boolean value
true.logD("Boolean: ")

// Log without any arguments
"Default Log".log()
```

```kotlin
2024-06-01 12:03:53.325 27193 CustomTag: Integer: 123 (MainActivity.kt:77) // assuming the log was called from MainActivity line 77
2024-06-01 12:03:53.325 27193 CustomTag: Double: 123.0 (MainViewModel.kt:78) // assuming the log was called from MainViewModel line 78
2024-06-01 12:03:53.325 27193 CustomTag: String: John (MainRepository.kt:79) // assuming the log was called from MainRepository line 79
2024-06-01 12:03:53.326 27193 CustomTag: Boolean: true (HomeScreenComposable.kt:80) // assuming the log was called from HomeScreenComposable line 80
2024-06-01 12:03:53.326 27193 CustomTag: String: Default Log (HomeScreenComposable.kt:12) // assuming the log was called from HomeScreenComposable line 12

// each source i.e. (MainActivity.kt:77) for example... are clickable.
```

## Viewing Logs with Logcat

To view logs generated by EasyLog in Logcat, filter the log messages using the custom log tag specified during initialization. For example, if you initialized EasyLog with the tag "CustomTag", you can filter log messages in Logcat using the following command:

```
adb logcat -s CustomTag
```
This command will display only the log messages generated by EasyLog with the specified tag (CustomTag) in this case for example.


## Notes

- Log messages are only displayed in debug mode.

## API Reference

### `EasyLog`

#### `setUp(filterTag: String, debugMode: Boolean)`

Sets up the EasyLog utility with the specified filter tag and debug mode.

- `filterTag`: Custom, optional tag to be used in log messages for easy filtering. Default value is "EASY-LOG".
- `debugMode`: If set to true, logging will be enabled; if set to false, logging will be disabled. Default value is false.

### Log Methods

- `logD(logMessage: String?)`: Logs a DEBUG message with an optional custom log message.
- `logI(logMessage: String?)`: Logs an INFO message with an optional custom log message.
- `logE(logMessage: String?)`: Logs an ERROR message with an optional custom log message.
- `logV(logMessage: String?)`: Logs a VERBOSE message with an optional custom log message.
- `logW(logMessage: String?)`: Logs a WARNING message with an optional custom log message.
- `logWtf(logMessage: String?)`: Logs a TERRIBLE FAILURE (WTF) message with an optional custom log message.
- `log()`: Logs a DEBUG message without a custom message and does not return the object.
- `logInline()`: Logs a DEBUG message if the calling object is not null and returns the object. if its null then a notifying message is logged

### `LogType` Enum

Specifies the log level:

- `DEBUG`
- `INFO`
- `ERROR`
- `VERBOSE`
- `WARNING`
- `TERRIBLE_FAILURE`


## License

EasyLog is released under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! If you encounter any issues or have suggestions for improvements, please open an issue or submit a pull request on GitHub.

## Acknowledgements

This library is built with ❤️ by [Michael Isesele].
