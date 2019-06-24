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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep class com.adyen.ui.DefaultPaymentRequestListener {*;}
#-keep class com.adyen.ui.DefaultPaymentRequestDetailsListener {*;}

-keep class com.oppwa.mobile.connect.** { *; }
-dontwarn com.oppwa.**

-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8adapters.
-dontwarn retrofit2.Platform$Java8

-keepattributes Signature
-keepattributes Exceptions
-dontwarn org.xmlpull.v1.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keepattributes SourceFile,LineNumberTable
# rename the source files to something meaningless, but it must be retained
-renamesourcefileattribute ''

# models classes
-keep class qix.app.qix.models.** { *; }

-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}