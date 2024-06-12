# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Retain public API methods
-keepclassmembers class com.michael.easylog.** {
    public void logD(java.lang.String);
    public void logI(java.lang.String);
    public void logE(java.lang.String);
    public void logV(java.lang.String);
    public void log();
    public static *** logW(...);
    public static *** logWtf(...);
    public static void setup(java.lang.String);
}

# Retain LogType enum and its members
-keepclassmembers enum com.michael.easylog.LogType {
    *;
}

# Keep specific methods and fields in LogType enum
-keepclassmembers class com.michael.easylog.LogType {
    public *;
}

## Keep internal implementation classes
#-keep class com.michael.easylog.internal.** { *; }

# Keep Parcelable implementation with CREATOR field
-keepclasseswithmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Retain all public methods and classes
-keep class ** {
    public protected *;
}

# Remove logging statements in release build
-assumenosideeffects class com.michael.easylog.EasyLogKt {
    public static *** logD(...);
    public static *** logI(...);
    public static *** logE(...);
    public static *** logV(...);
    public static *** log(...);
    public static *** logW(...);
    public static *** logWtf(...);
}
