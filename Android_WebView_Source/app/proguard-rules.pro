# Keep only the application entry point and WebView callbacks needed by the wrapper.
-keep class com.aarvexa.erp.MainActivity { *; }

# Preserve Android WebView JavaScript bridge members if added in future.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
