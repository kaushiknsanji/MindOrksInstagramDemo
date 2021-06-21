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

# Keep Generics information AS-IS
-keepattributes Signature

# Keep Application wide Model classes AS-IS
-keep class com.mindorks.kaushiknsanji.instagram.demo.data.model.** { *; }

# Keep Remote Model classes AS-IS
-keep class com.mindorks.kaushiknsanji.instagram.demo.data.remote.model.** { *; }

# Keep Remote Request Model classes AS-IS
-keep class com.mindorks.kaushiknsanji.instagram.demo.data.remote.request.** { *; }

# Keep Remote Response Model classes AS-IS
-keep class com.mindorks.kaushiknsanji.instagram.demo.data.remote.response.** { *; }

# Keep Local Database Entity classes AS-IS
-keep class com.mindorks.kaushiknsanji.instagram.demo.data.local.db.entity.** { *; }

# Keep AppGlideModule Implementations AS-IS
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

# The Activity Result Contract for ParaCamera Activity Intent is setup using a reflection call
# to the library method, since there is no other way. Hence this class member must be preserved.
-keepclassmembers class com.mindorks.paracamera.Camera { void setUpIntent(android.content.Intent); }