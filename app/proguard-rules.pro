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
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# https://developer.android.com/build/shrink-code?utm_source=android-studio&hl=zh-cn#retracing
# 对堆栈轨迹进行轨迹还原
-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile

-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**
-dontwarn android.content.res.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

-keepclassmembers class kotlin.io.** { *; }

-keep class org.zeroturnaround.zip.** { *; }
-keep class net.lingala.zip4j.** { *; }

# =========================================================================
# QingNote Proguard / R8 Keep Rules
# =========================================================================

# 1. Keep all model/bean classes to prevent serialization & Room mapping failures
-keep class com.qingguang.qingnote.bean.** { *; }
-keep class com.ldlywt.note.bean.** { *; }
-keep class org.tasks.data.entity.** { *; }
-keep class com.qingguang.qingnote.tasks.data.** { *; }

# 2. Keep Room database structures, DAOs, and generated implementations
-keep class com.qingguang.qingnote.db.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.Dao
-keep class * extends androidx.room.Dao { *; }

# 3. Kotlinx Serialization support
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class *$$serializer { *; }
-keepclassmembers class * {
    *** Companion;
}

# 4. Keep Hilt and Dagger classes and injection entry points
-keep class * {
    @javax.inject.Inject <fields>;
    @javax.inject.Inject <init>(...);
}
-keep class **_HiltModules* { *; }
-keep class **_HiltComponents* { *; }

# 5. Keep third-party UI libraries (Salt UI, Kizitonwose Calendar, Markwon)
-keep class com.moriafly.salt.ui.** { *; }
-keep class io.github.moriafly.salt.ui.** { *; }
-keep class com.kizitonwose.calendar.** { *; }
-keep class dev.jeziellago.compose.markdowntext.** { *; }
-keep class io.noties.markwon.** { *; }

# 6. Suppress warnings for missing optional compile-time dependencies in third-party libraries
-dontwarn groovy.lang.**
-dontwarn groovy.util.**
-dontwarn org.codehaus.groovy.**
-dontwarn javax.cache.**
-dontwarn org.jparsec.**
-dontwarn java.beans.**
-dontwarn org.apache.commons.collections.**
-dontwarn javax.servlet.**
-dontwarn org.apache.avalon.framework.logger.**
-dontwarn org.apache.log.**
-dontwarn org.apache.log4j.**
-dontwarn java.lang.management.**