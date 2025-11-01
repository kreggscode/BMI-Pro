# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ====================================
# PRODUCTION-READY PROGUARD RULES
# BMI Health App - Complete Protection
# ====================================

# Keep line numbers for debugging crashes
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all annotations
-keepattributes *Annotation*

# Keep generic signature for reflection
-keepattributes Signature

# Keep exceptions
-keepattributes Exceptions

# ====================================
# KOTLIN & COROUTINES
# ====================================
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Kotlin Serialization
-keepattributes InnerClasses
-keep class kotlin.Metadata { *; }

# ====================================
# RETROFIT & OKHTTP (API CALLS)
# ====================================
# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ====================================
# GSON (JSON PARSING)
# ====================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep all model classes (data classes)
-keep class com.kreggscode.bmi.data.model.** { *; }
-keep class com.kreggscode.bmi.data.api.** { *; }

# ====================================
# ROOM DATABASE
# ====================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep class com.kreggscode.bmi.data.database.** { *; }
-keep interface com.kreggscode.bmi.data.database.** { *; }

# ====================================
# COMPOSE & ANDROID
# ====================================
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.datastore.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class com.kreggscode.bmi.viewmodel.** { *; }

# ====================================
# CAMERAX
# ====================================
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ====================================
# COIL (IMAGE LOADING)
# ====================================
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ====================================
# VICO CHARTS
# ====================================
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# ====================================
# ACCOMPANIST
# ====================================
-keep class com.google.accompanist.** { *; }
-dontwarn com.google.accompanist.**

# ====================================
# POLLINATIONS AI API
# ====================================
# Keep all API service interfaces and models
-keep interface com.kreggscode.bmi.data.api.PollinationsService { *; }
-keep class com.kreggscode.bmi.data.api.PollinationsService$* { *; }

# ====================================
# GENERAL ANDROID
# ====================================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Parcelables
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializables
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ====================================
# REMOVE LOGGING IN PRODUCTION
# ====================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ====================================
# OPTIMIZATION
# ====================================
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

