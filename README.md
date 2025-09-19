# EasyLog v4

A lightweight, intelligent logging library for Android with beautiful tree-structured output and smart object detection.

## ✨ What's New in v4

- **🌳 Tree-structured output** for complex objects and collections
- **🔍 Smart object detection** with automatic formatting
- **⚡ Runtime log level filtering** 
- **🛡️ Enhanced reflection safety** 
- **📊 Grouped logging** with `logMany()`
- **🎯 Zero breaking changes** - drop-in upgrade

## 📱 Quick Start

### 1. Add Dependency

```kotlin
// build.gradle.kts (app)
implementation("com.github.mikeisesele:easylog:4.0.0")
```

```kotlin
// settings.gradle.kts
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

### 2. Initialize (Application class)

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        EasyLog.setUp {
            debugMode(BuildConfig.DEBUG)
            filterTag("MyApp")  // Optional: custom tag
            minimumLogLevel(LogType.DEBUG)  // New: runtime filtering
        }
    }
}
```

### 3. Enable BuildConfig

```kotlin
// build.gradle.kts (app)
android {
    buildFeatures {
        buildConfig = true
    }
}
```

## 🚀 Usage Examples

### Basic Logging
```kotlin
"Hello World".logD("Greeting")
42.logI("Answer")
true.logE("Boolean value")
```

### Smart Object Formatting
```kotlin
data class User(val name: String, val age: Int, val emails: List<String>)

val user = User("John", 30, listOf("john@work.com", "john@personal.com"))
user.logD("User profile")
```

**Output:**
```
🔍 USER PROFILE: at MainActivity.kt:45
╭─ User (com.example.model)
├─ name: "John"
├─ age: 30
╰─ emails: List[2]
   ├─ [0]: "john@work.com"
   ╰─ [1]: "john@personal.com"
```

### Collections & Arrays
```kotlin
listOf("apple", "banana", "cherry").logD("Fruits")
arrayOf(1, 2, 3, 4, 5).logI("Numbers")
```

**Output:**
```
🔍 FRUITS: at MainActivity.kt:50
╭─ List[3]
├─ [0]: "apple"
├─ [1]: "banana"
╰─ [2]: "cherry"
```

### Grouped Logging
```kotlin
logMany(
    header = "App Configuration",
    BuildConfig.DEBUG,
    BuildConfig.VERSION_NAME,
    "Environment: Production",
    42
)
```

**Output:**
```
🔍 ╭─ APP CONFIGURATION at MainActivity.kt:55
🔍 ├─ [1] true
🔍 ├─ [2] "1.0.0"
🔍 ├─ [3] "Environment: Production"
🔍 ╰─ [4] 42
```

### Inline Logging
```kotlin
val result = "Processing data"
    .logInline("Step 1")
    .uppercase()
    .logInline("Step 2")

// Logs each step and returns the value for chaining
```

### Nullable Objects
```kotlin
val nullableUser: User? = getUser()
nullableUser.logInlineNullable("User lookup result")
```

## ⚙️ Advanced Configuration

### Runtime Log Filtering
```kotlin
// Only show warnings and errors
EasyLog.setMinimumLogLevel(LogType.WARNING)

// Check current level
val currentLevel = EasyLog.getMinimumLogLevel()
```

### File Logging
```kotlin
EasyLog.setUp {
    debugMode(BuildConfig.DEBUG)
    addDefaultLogger(DefaultLogger.FILE_LOGGER)
    context(applicationContext)  // Required for file logging
}
```

### Custom Logger
```kotlin
class MyCustomLogger : Logger {
    override fun log(
        logMessage: String,
        logObject: Any,
        level: LogType,
        fileName: String?,
        lineNumber: Int
    ) {
        // Your custom implementation
    }
}

EasyLog.setUp {
    addCustomLogger(MyCustomLogger())
}
```

## 📊 Log Levels

- `logV()` - Verbose 📝
- `logD()` - Debug 🔍  
- `logI()` - Info ℹ️
- `logW()` - Warning ⚠️
- `logE()` - Error ❌
- `logWtf()` - What a Terrible Failure 💥

## 🔧 Migration from v1

**Zero code changes required!** EasyLog v2 is 100% backward compatible.

### New Features Available:
- Enhanced object formatting (automatic)
- `minimumLogLevel()` configuration
- `logMany()` function
- Better performance and safety

### Deprecated (still works):
```kotlin
// Old way (still works)
.defaultLogger(DefaultLogger.DEFAULT_ANDROID)

// New way (recommended)
.addDefaultLogger(DefaultLogger.DEFAULT_ANDROID)
```

## 📋 Requirements

- **Minimum SDK:** 24
- **Kotlin reflection:** Auto-included
- **ProGuard:** No configuration needed

## 🎯 Best Practices

1. **Use meaningful messages:**
   ```kotlin
   user.logD("After API call")  // Good
   user.logD()  // Works, but less descriptive
   ```

2. **Filter by tag in Logcat:**
   ```
   tag:MyApp
   ```

3. **Use appropriate log levels:**
   ```kotlin
   result.logD("Debug info")      // Development
   error.logE("API failed")       // Production issues
   config.logI("App started")     // Important events
   ```

4. **Leverage inline logging:**
   ```kotlin
   val processed = rawData
       .logInline("Raw data")
       .processStep1()
       .logInline("After step 1")
       .processStep2()
       .logInline("Final result")
   ```

## 📄 License

```
MIT License - see LICENSE file for details
```

## 🙋‍♂️ Support
- **Issues:** [GitHub Issues](https://github.com/mikeisesele/easylog/issues)

⚠️ Important Considerations
Performance & Production Use

Reflection overhead: EasyLog v4 uses Kotlin reflection for enhanced object formatting, which adds computational overhead
Development focus: Designed primarily for development and testing environments where debugging visibility outweighs performance concerns
Production recommendations:

Use minimumLogLevel(LogType.WARNING) or higher in production builds
Consider disabling complex object logging in release builds via debugMode(false)
Monitor app performance when logging large or deeply nested objects



Technical Limitations

Obfuscated code: R8/ProGuard obfuscation may affect property names in formatted output
Sealed classes: Some sealed classes or classes with restricted reflection access may display simplified output
Memory usage: Complex object trees with circular references are handled safely but may use additional memory
Thread safety: While EasyLog is thread-safe, logging very large objects concurrently may impact performance

Best Practices

Development workflow: Use detailed object logging during development and debugging phases
Testing environments: Leverage full EasyLog capabilities in staging/testing environments
Production deployment: Configure appropriate log levels to balance debugging needs with app performance
Large datasets: Consider logging subsets or summaries of large collections rather than complete datasets

Scope & Compatibility

Android focus: Optimized for Android development workflows and Android Studio logcat integration
Minimum SDK: Requires Android API 24+ due to reflection requirements
Kotlin interop: Full compatibility with Kotlin data classes, sealed classes, and standard collections
Java compatibility: Works with Java objects but optimal formatting designed for Kotlin constructs


**Made with ❤️ by [Michael Isesele](https://github.com/mikeisesele)**
