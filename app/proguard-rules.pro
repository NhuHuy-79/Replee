# --- Dagger Hilt ---
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class hilt_aggregated_deps.** { *; }
-keepclassmembers class * {
    @dagger.hilt.android.AndroidEntryPoint *;
}

# --- Room Database ---
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.Entity { *; }
-keep class * extends androidx.room.Dao { *; }
-keep class androidx.room.RoomDatabase { *; }

# --- Firebase (Firestore, Realtime, FCM) ---
-keep class com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keepattributes Signature, *Annotation*

# --- Retrofit & Gson ---

-keep class com.nhuhuy.replee.data.model.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# --- WorkManager ---
-keep class androidx.work.** { *; }

# --- Kotlin Serialization (Nếu dùng) ---
-keepattributes *Annotation*
-keep class kotlinx.serialization.** { *; }


#--- Kotlin Class  --
-keep class com.nhuhuy.replee.notification.** { *; }
-keep class com.nhuhuy.replee.worker.** { *; }
-keep class com.nhuhuy.replee.MainState

-keep class com.nhuhuy.replee.core.common.base.** { *; }
-keep class com.nhuhuy.replee.core.common.error.** { *; }

-keep class com.nhuhuy.replee.core.data.** { *; }
-keep class com.nhuhuy.replee.core.database.base.BaseDao
-keep class com.nhuhuy.replee.core.database.entity.** { *; }
-keep class com.nhuhuy.replee.core.model.** { *; }
-keep class com.nhuhuy.replee.core.network.model.** { *;}
-keep class com.nhuhuy.replee.core.network.api.** { *; }

-keep class com.nhuhuy.replee.feature_auth.data.model.** { *; }
-keep class com.nhuhuy.replee.feature_auth.presentation.login.** { *; }
-keep class com.nhuhuy.replee.feature_auth.presentation.recover_password.** { *; }
-keep class com.nhuhuy.replee.feature_auth.presentation.sign_up.** { *; }

-keep class com.nhuhuy.replee.feature_chat.data.model.** { *; }
-keep class com.nhuhuy.replee.feature_chat.data.worker.** { *; }
-keep class com.nhuhuy.replee.feature_chat.domain.** { *; }
-keep class com.nhuhuy.replee.feature_chat.presentation.chat.state.**{ *; }
-keep class com.nhuhuy.replee.feature_chat.presentation.conversation.state.**{ *; }
-keep class com.nhuhuy.replee.feature_chat.presentation.search.state.**{ *; }
-keep class com.nhuhuy.replee.feature_chat.presentation.pin.state.**{ *; }
-keep class com.nhuhuy.replee.feature_chat.presentation.option.state.**{ *; }

-keep class com.nhuhuy.replee.feature_profile.data.**{ *; }
-keep class com.nhuhuy.replee.feature_profile.domain.**{ *; }
-keep class com.nhuhuy.replee.feature_profile.presentation.profile.state.** { *; }

-dontwarn org.junit.**
-dontwarn io.mockk.**
-dontwarn org.robolectric.**
-dontwarn com.google.auto.service.**
-dontwarn org.junit.jupiter.api.extension.**

-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.api.** { *; }

-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, Exceptions

-dontwarn com.google.android.gms.**

-printseeds seeds.txt
