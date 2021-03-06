# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/fmatos/bin/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.* { *;}

# okio
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# retrofit
-keep class java.lang.invoke.** {*;}


# dagger
-dontwarn com.google.errorprone.annotations.*

# lambas for java 8
-dontwarn java.lang.invoke**

# project's network models
-keep class com.fmatos.samples.hud.service.model.amazingwallpapers.** {*; }