# ============================================================
# Weather App - ProGuard Rules (CORRECTED VERSION)
# ============================================================

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ============================================================
# 🔹 YOUR DATA MODELS - CRITICAL!
# ============================================================

# Keep ALL data model classes (for JSON parsing)
-keep class com.st10140587.prog7314_poe.data.model.** { *; }
-keepclassmembers class com.st10140587.prog7314_poe.data.model.** { *; }

# Keep the WeatherRepository
-keep class com.st10140587.prog7314_poe.data.WeatherRepository { *; }
-keepclassmembers class com.st10140587.prog7314_poe.data.WeatherRepository {
    public *;
}

# Keep ForecastResponse and nested classes
-keep class com.st10140587.prog7314_poe.data.model.ForecastResponse { *; }
-keep class com.st10140587.prog7314_poe.data.model.ForecastResponse$** { *; }

# Keep PlaceSuggestion (used in MainActivity)
-keep class com.st10140587.prog7314_poe.PlaceSuggestion { *; }

# Keep UI data classes
-keep class com.st10140587.prog7314_poe.ui.HourUi { *; }
-keep class com.st10140587.prog7314_poe.ui.DayUi { *; }
-keep class com.st10140587.prog7314_poe.ui.** { *; }

# Keep adapters and ViewHolders
-keep class com.st10140587.prog7314_poe.SuggestionAdapter { *; }
-keep class com.st10140587.prog7314_poe.SuggestionAdapter$* { *; }
-keep class com.st10140587.prog7314_poe.SuggestionVH { *; }
-keep class com.st10140587.prog7314_poe.**Adapter { *; }
-keep class com.st10140587.prog7314_poe.**Adapter$* { *; }

# ============================================================
# 🔹 GSON - JSON Parsing Library
# ============================================================

-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep fields with @SerializedName
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Prevent stripping of generic signatures (needed for Gson)
-keepattributes Signature

# ============================================================
# 🔹 RETROFIT - REST API Client
# ============================================================

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Keep generic signature of Call, Response (R8 full mode)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# ============================================================
# 🔹 OKHTTP - HTTP Client
# ============================================================

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# OkHttp platform used only when on JVM and when Conscrypt and other security providers are available.
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ============================================================
# 🔹 KOTLIN COROUTINES
# ============================================================

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

-dontwarn kotlinx.coroutines.**

# ============================================================
# 🔹 ROOM DATABASE
# ============================================================

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** getDatabase(android.content.Context);
}

-dontwarn androidx.room.paging.**

# Keep Room entity classes and their fields
-keep class com.st10140587.prog7314_poe.data.local.** { *; }

# ============================================================
# 🔹 FIREBASE
# ============================================================

-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ============================================================
# 🔹 ANDROIDX & MATERIAL
# ============================================================

-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding {
    public static * inflate(android.view.LayoutInflater);
    public static * bind(android.view.View);
}

# RecyclerView
-keep class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder {
    <init>(...);
}

-keepclassmembers class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder {
    <init>(...);
}

# ============================================================
# 🔹 KOTLIN SPECIFIC
# ============================================================

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep data classes and their members
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** { *; }

# Preserve data class copy() methods
-keepclassmembers class * {
    *** copy(...);
}

# ============================================================
# 🔹 GENERAL ANDROID
# ============================================================

# Keep all native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
}

# Keep Parcelables
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================================
# 🔹 DEBUGGING (Comment out for production)
# ============================================================

# Uncomment to print what ProGuard is removing (check build output)
#-printusage build/outputs/mapping/release/usage.txt

# Uncomment to see what's being kept
#-printseeds build/outputs/mapping/release/seeds.txt

# Uncomment to see obfuscation mapping
#-printmapping build/outputs/mapping/release/mapping.txt

# ============================================================
# END OF PROGUARD RULES
# ============================================================