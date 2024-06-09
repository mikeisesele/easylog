# EasyLog


EasyLog is a lightweight, simple, and flexible logging utility for Android applications. It provides a concise syntax for logging messages directly from objects. It supports various default and custom loggers such as Timber, BugFender, File logging, and Custom Logging Implementations based on your needs.


## Features

EasyLog offers:

1. **Custom Configuration**: Tailor logging behavior with ease during setup.
  
2. **Multiple Logging Levels**: Log messages at various levels of severity, including DEBUG, INFO, ERROR, VERBOSE, WARNING, and TERRIBLE_FAILURE.

3. **Simplified Syntax**: Log messages directly from objects with concise methods, enhancing readability.

4. **Default Log Messages**: Quickly log messages without specifying a custom message.

5. **Automatic Contextual Information**: Capture class and line number information automatically for each log call, aiding in debugging.

6. **Debug Mode Logging**: Log messages are displayed only in debug mode, ensuring optimal performance in production environments.

7. **Seamless Integration**: Easily integrate with popular logging libraries like Timber and Bugfender.

8. **Efficient Performance**: Lightweight and efficient design minimizes impact on application performance.

9. **Advanced Logging Capabilities**: Log on nullable and non-nullable objects and return the logged data for continued system processing.

10. **Integration with Custom Logging Implementations**: Set up custom logging implementations to suit your specific requirements.

11. **Support for Logging to File**: Log messages to a file for storage and future reference.

12. **Support for Buffer Chunking**: Log messages in chunks for improved performance and management.

13. **Integration with Bugfender**: Seamlessly integrate with Bugfender for remote logging and centralized log management.

14. **Integration with Timber**: Integrate with Timber, a logging library for Android, for additional logging features and functionality.

15. **Integration with Other Logging Libraries**: Easily integrate with other logging libraries as per project requirements.



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
    // EasyLog set up uses the builder pattern and this setup can be done in your Application class or BaseAvtivty if you have one. 
    // Otherwise, the MainActivity works just fine.

   ```kotlin
    EasyLog.setUp {
        // Optional. Filter tag for log messages. Defaults to EASY-LOG
        filterTag("MyAppLogTag")
        
        // Required. Specify debug mode
        debugMode(BuildConfig.DEBUG) 
        
        // Optional. needed when you have a custom logging implementation extending the Logger Interface
        customLogger()
        
        // Optional. You can only use this when you need to set other DefaultLogger enums as default. 
        defaultLogger(DefaultLogger.DEFAULT_ANDROID)
        
        // Optional. Provide the application context only when DefaultLogger.FILE_LOGGER is used
        context()
        
        // Required. Build and finalize the logger setup
        build()
    }
        
    // or use the explicit builder
        
    EasyLog.Builder()
        .filterTag("MyAppLogTag)
        .debugMode(BuildConfig.DEBUG)
        .customLogger() 
        .defaultLogger(DefaultLogger.DEFAULT_ANDROID) 
        .context()
        .build()


    // NOTE: Internally, debugMode is true by default. to prevent logging on production, do not manually set this to true. 
    // rather use BuildConfig.DEBUG as the parameter, [ BuildConfig.DEBUG returns false in release environment ]
    // or better still use environment variables with BuildVariants.

    ```

3. **Logging**:

   Log messages using concise syntax directly on objects:

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

EasyLog provides concise methods for logging messages at different levels of severity. 
Log messages are automatically formatted for easy readability in Logcat, 
including the class and line number of the log call site and the data type of the logged object.


```kotlin
2024-06-01 12:03:53.325 27193 CustomTag: Integer: 123 (MainActivity.kt:77) // assuming the log was called from MainActivity line 77
2024-06-01 12:03:53.325 27193 CustomTag: Double: 123.0 (MainViewModel.kt:78) // assuming the log was called from MainViewModel line 78
2024-06-01 12:03:53.325 27193 CustomTag: String: John (MainRepository.kt:79) // assuming the log was called from MainRepository line 79
2024-06-01 12:03:53.326 27193 CustomTag: Boolean: true (HomeScreenComposable.kt:80) // assuming the log was called from HomeScreenComposable line 80
2024-06-01 12:03:53.326 27193 CustomTag: String: Default Log (HomeScreenComposable.kt:12) // assuming the log was called from HomeScreenComposable line 12

// each source i.e. (MainActivity.kt:77) for example... are clickable.
```

5. **Default Logging**:

   Optionally, you can use default log messages:

   ```kotlin
   "Another Message".log() // EASY-LOG: Another Message
   ```

6. **Advanced Logging**:

    log on nullable and non nullable objects and return the logged data for continued system processing

    ```kotlin

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

### Configuration

- Default Loggers

EasyLog supports several default loggers. Choose one as an argument in defaultLogger() during setup:

    BUFFER_CHUNKING - Logs messages in chunks.
    BUG_FENDER - Logs messages to BugFender.
    DEFAULT_ANDROID - Logs messages using the default Android logger.
    FILE_LOGGER - Logs messages to a file.
    TIMBER - Logs messages using Timber.
    
## Set up Logging with Timber

```kotlin
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // Set up Timber in your Application class [ or BaseActivity ]
        Timber.plant(Timber.DebugTree())

        // set up easylog to use Timber
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .defaultLogger(DefaultLogger.TIMBER) 
            .build()
    }
}
// Note: Timber dependency is not needed within your app as it is contained within the library
```

## Set up Remote Logging with Bugfender

```kotlin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // Set up Timber in your Application class [ or BaseActivity ]
        // Get your BUGFENDER_API_KEY. at https://bugfender.com/
        Bugfender.init(this, BUGFENDER_API_KEY, BuildConfig.DEBUG, true)
        Bugfender.enableUIEventLogging(this)
        Bugfender.enableLogcatLogging()


        // set up easylog to use Timber
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .defaultLogger(DefaultLogger.BUG_FENDER) 
            .build()
    }
}
// Just so you know, bugfender dependency is not needed within your app because it is in the library.
```


## Set up Logging to File

```kotlin

1. // add the following to manifest
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

// then proceed to set up

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // set up easylog to use Timber
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .defaultLogger(DefaultLogger.FILE) 
            .context(applicataionContext) // only required during file logging
            .build()
    }
}
```

## Set up Custom Logging 

```kotlin

// 1. set up your custom logger implementation 

class MyCustomLogger: Logger {
     override fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType,
        fileName: String?,
        lineNumber: Int
    ) {
        // your custom log implementation here...
        
        // Use the parameters passed into the log override to format your logcat message

        // Example

        "Hello World".logD("Android")
        -  "Android" is the logMessage
        - The log object is "Hello World". // (you can derive it's type from logObject::class.java.simpleName)
        - LogLevel is an enum. how you'd want to handle various logs.
            example
                when (level) {
                    LogType.DEBUG -> Log.d(tag, fullMessage)
                    LogType.INFO -> Log.i(tag, fullMessage)
                    LogType.ERROR -> Log.e(tag, fullMessage)
                    LogType.VERBOSE -> Log.v(tag, fullMessage)
                    LogType.WARNING -> Log.w(tag, fullMessage)
                    LogType.TERRIBLE_FAILURE -> Log.wtf(tag, fullMessage)
                }
        - fileName is the class name from where the object was logged.
        - lineNumber is the line from where the object was logged.
    }
}


// 2. add this class to EasyLog Set up

```kotlin
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // Add Timber, Bugfender or any custom logging mechanism based on your project needs


        // set up easylog to use Custom logger
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .customLogger(MyCustomLogger()) 
            .build()
    }
}

```


## License

EasyLog is released under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! If you encounter any issues or have suggestions for improvements, please open an issue or submit a pull request on GitHub.

## Acknowledgements

This library is built with ❤️ by [Michael Isesele].
