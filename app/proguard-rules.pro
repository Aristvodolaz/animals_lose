# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Сохраняем информацию о строках для отладки стека вызовов
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Общие правила
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

# Сохраняем аннотации
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses

# Сохраняем R классы и их поля
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Правила для Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Правила для Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Правила для Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keepclassmembers class androidx.compose.** { *; }
-keep class androidx.compose.ui.text.** { *; }
-keep class androidx.compose.ui.graphics.** { *; }

# Правила для Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Правила для Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Правила для ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.vision.** { *; }

# Правила для Google Maps
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.maps.**

# Правила для Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**

# Правила для Coil
-keep class io.coil.** { *; }
-dontwarn io.coil.**

# Правила для моделей данных
-keep class com.application.lose_animals.data.model.** { *; }

# Правила для сериализации/десериализации
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Правила для сохранения имен полей для Firebase
-keepclassmembers class com.application.lose_animals.** {
  @com.google.firebase.firestore.* *;
}

# Правила для Navigation Component
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# Правила для сохранения имен полей для Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Правила для сохранения имен полей для Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Правила для WebView
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

# Правила для сохранения имен полей для Enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Правила для сохранения имен полей для JavaScript Interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Правила для сохранения имен полей для Native методов
-keepclasseswithmembernames class * {
    native <methods>;
}

# Правила для сохранения имен полей для View конструкторов
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Правила для сохранения имен полей для Service и BroadcastReceiver
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver