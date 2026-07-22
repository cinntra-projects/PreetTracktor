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
-keep class * { *; }
-keep class de.hdodenhof.circleimageview.** { *; }
-keep class com.hbb20.** { *; }
-keep class pub.devrel.easypermissions.** { *; }
-keep class www.sanju.motiontoast.** { *; }
-keep public class * extends com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** { *; }
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**
-keep class com.journeyapps.** { *; }
-dontwarn com.google.zxing.**
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.facebook.shimmer.** { *; }
-keep class com.facebook.shimmer.** { *; }
-keep class androidx.** { *; }
-dontwarn androidx.**


