# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep DTOs
-keep class com.chucklingkoala.stagehand.data.remote.dto.** { *; }
-keep class com.chucklingkoala.stagehand.domain.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Koin
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.androidx.** { *; }
-dontwarn org.koin.**

# Keep all constructors for dependency injection
-keepclassmembers class * {
    public <init>(...);
}

# Keep ViewModels for Koin injection
-keep class com.chucklingkoala.stagehand.presentation.** { *; }
-keep class com.chucklingkoala.stagehand.data.repository.** { *; }

# Keep data classes
-keep class com.chucklingkoala.stagehand.data.** { *; }

# Reflection
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.reflect.** { *; }
-keep interface kotlin.reflect.** { *; }

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep BuildConfig
-keep class com.chucklingkoala.stagehand.BuildConfig { *; }
