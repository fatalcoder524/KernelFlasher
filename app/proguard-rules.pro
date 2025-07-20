# GENERAL
-dontobfuscate
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations

# ANDROID INTERFACES / AIDL
-keep class com.github.capntrips.kernelflasher.FilesystemService
-keep class * implements android.os.IInterface
-keepclassmembers class * {
    public static ** asInterface(android.os.IBinder);
}

# Prevent R8 from stripping native methods used via JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep RootShell library (libsu)
-keep class com.topjohnwu.superuser.** { *; }

# RETROFIT2
-keep interface com.github.capntrips.kernelflasher.GitHubApi { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# ============ GSON ============
-keep class com.google.gson.** { *; }
-keep class com.github.capntrips.kernelflasher.AppUpdater$* { *; }

# Keep all fields/methods annotated with Gson's @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============ KOTLIN ============
-keep class kotlin.Metadata { *; }
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,Exceptions,InnerClasses,EnclosingMethod,Signature,SourceFile,LineNumberTable,*Annotation*,Deprecated,SourceDir,CompilationID,LocalVariableTable,LocalVariableTypeTable,Module*
-keepclassmembers class **.AppUpdater$GitHubRelease {
    <fields>;
}
-keepclassmembers class **.AppUpdater$GitHubAsset {
    <fields>;
}

# ============ COROUTINES ============
-keepclassmembers class kotlinx.coroutines.BuildConfig { public static final boolean DEBUG; }
-keep class kotlinx.** { *; }
-dontwarn kotlinx.**

# ============ DOWNLOAD MANAGER & BROADCAST RECEIVER ============
-keep class com.github.capntrips.kernelflasher.AppUpdater { *; }
-keepclassmembers class com.github.capntrips.kernelflasher.AppUpdater { *; }

# Keep VectorDrawableCompat to avoid crashes or inflation errors
-keep class androidx.vectordrawable.graphics.drawable.VectorDrawableCompat { *; }