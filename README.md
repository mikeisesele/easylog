# EasyLog

EasyLog is a lightweight, simple, and flexible logging utility for Android applications. It provides a concise syntax for logging messages directly from objects and supports various default and custom loggers.

## Features

- **Simplified Syntax**: Log directly from objects for a streamlined debugging experience.
- **Custom Configuration**: Tailor logging behavior to your specific needs using custom loggers.
- **Seamless Integration**: Easily integrate with popular logging libraries like Timber, Bugfender, and more.
- **Automatic Contextual Info**: Gain insights into your code with automatically captured class and line number information. [ with few exceptions. see below ]
- **Inline Logs**: Say goodbye to scattered log statements with support for inline logging, allowing you to embed log messages directly within your code.
- **Multi-Logger Support**: Combine multiple logging solutions for simultaneous logs, enhancing flexibility and functionality.
- **Multiple Logging Levels**: Cover all debugging scenarios with support for DEBUG, INFO, ERROR, VERBOSE, WARNING, and TERRIBLE_FAILURE levels.
- **Debug Mode Logging**: Optimize performance by logging only in debug mode.
- **Advanced Capabilities**: Log nullable/non-nullable objects and return logged data for enhanced debugging.
- **File Logging**: Log messages to a file for long-term storage and analysis.
- **Buffer Chunking**: Improve efficiency by logging messages in chunks for optimized performance.
- **Efficient Performance**: Keep your application running smoothly with minimal impact on performance.


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

2. Configure Jitpack in your settings.gradle file

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

1. **Enable Debugging**: Ensure your application is in debug mode.

2. **Initialization**: Initialization: Initialize in your Application class, BaseActivity or MainActivity:

   ```kotlin
    EasyLog.setUp {
        // Optional. Filter tag for log messages. Defaults to EASY-LOG
        filterTag("MyAppLogTag")
        
        // Required. Specify debug mode
        debugMode(BuildConfig.DEBUG) 
        
        // Optional. needed when you have a custom logging implementation extending the Logger Interface
        addCustomLogger()
        
        // Optional. You can only use this when you need to set other DefaultLogger enums as default.
        // else DefaultLogger.DEFAULT_ANDROID is internally set as default.
        addDefaultLogger(DefaultLogger.DEFAULT_ANDROID)
        
        // Optional. Provide the application context only when DefaultLogger.FILE_LOGGER is used
        context()
    }
        
    // or use the explicit builder
        
    EasyLog.Builder()
        .filterTag("MyAppLogTag)
        .debugMode(BuildConfig.DEBUG)
        .addDefaultLogger(DefaultLogger.DEFAULT_ANDROID) 
        .context()
        .build()


    // NOTE: Internally, debugMode is true by default. to prevent logging on production, do not manually set this to true. 
    // rather use BuildConfig.DEBUG as the parameter, [ BuildConfig.DEBUG returns false in release environment ]
    // or better still use environment variables with BuildVariants.

    ```

3. **Default Logging**: Log messages using concise syntax directly on objects:

 ```kotlin
    123.logD("Integer: ")        
    123.0.logI("Double: ")
    "John".logE("String: ")
    true.logV("Boolean: ")
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

4. **Inline Logging**: Log on nullable and non-nullable objects and return the logged data for continued system processing

  ```kotlin

     val myNullableObject: MyClass? = getNullableObject()
     myNullableObject.logNullableInline()

     // logInline the values of savedInstanceState [nullable]
       override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState.logNullableInline()) Logs Inline  <==
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

EasyLog supports several default loggers. Choose any as an argument in defaultLogger() during setup:

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
        Timber.plant(Timber.DebugTree())
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .addDefaultLogger(DefaultLogger.TIMBER) 
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
        // Get your BUGFENDER_API_KEY. at https://bugfender.com/
        Bugfender.init(this, BUGFENDER_API_KEY, BuildConfig.DEBUG, true)
        Bugfender.enableUIEventLogging(this)
        Bugfender.enableLogcatLogging()

        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .addDefaultLogger(DefaultLogger.BUG_FENDER) 
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
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .addDefaultLogger(DefaultLogger.FILE) 
            .context(applicataionContext) // only required during file logging
            .build()
    }
}
```

## Set up Custom Logging 

```kotlin

class MyCustomLogger: Logger {
     override fun log(
        logMessage: String?,
        logObject: Any,
        level: LogType,
        fileName: String?,
        lineNumber: Int
    ) {
        // your custom log implementation here... //
        
        // Use the parameters passed into the log override to format your logcat message

        "Hello World".logD("Android")
        -  "Android" is the logMessage
        - The log object is "Hello World". // (you can derive it's type from logObject::class.java.simpleName)
        - level is an enum. how you'd want to handle various logs.
            example
                when (level) {
                    LogType.DEBUG -> Log.d(tag, fullMessage)
                    // handle other log types based on your needs
                }
        - fileName is the class name from where the object was logged.
        - lineNumber is the line from where the object was logged.
    }
}
```


```kotlin
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .addCustomLogger(MyCustomLogger()) 
            .build()
    }
}

```

## Set up Multiple Logging

```kotlin
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        EasyLog.Builder()
            .filterTag("MyAppLogTag)
            .debugMode(BuildConfig.DEBUG)
            .addDefaultLogger(DefaultLogger.DEFAULT_ANDROID)
            .addDefaultLogger(DefaultLogger.FILE_LOGGER)
            .context(this@App) // Required for FileLogger
            .build()
    }
}

```

## Automatic Contextual Logging Exceptions

Easylog provides Class and Line numbers for its logs but with some exceptions where you might notice discrepancies.

The below context may provide discrepancies

- Logs called from Init blocks
- Logs called from Some Coroutine scopes or nested coroutine scopes
- Logs called from Composables.


## License

EasyLog is released under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! If you encounter any issues or have suggestions for improvements, please open an issue or submit a pull request on GitHub.

## Acknowledgements

This library is built with ❤️ by [Michael Isesele].
