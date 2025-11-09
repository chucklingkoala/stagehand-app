# Koin-specific ProGuard rules
# These rules ensure Koin's reflection-based dependency injection works in release builds

# Keep Koin core classes
-keep class org.koin.core.** { *; }
-keep class org.koin.androidx.** { *; }
-keep interface org.koin.core.** { *; }

# Keep all Koin module definitions
-keepclassmembers class * {
    public <init>(...);
}

# Preserve generic signatures for Koin
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations

# Keep all ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep app-specific classes for Koin injection
-keep class com.chucklingkoala.stagehand.presentation.** { *; }
-keep class com.chucklingkoala.stagehand.data.repository.** { *; }
-keep class com.chucklingkoala.stagehand.di.** { *; }

# Prevent obfuscation of Kotlin metadata
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

# Keep parameter names for dependency injection
-keepparameternames
