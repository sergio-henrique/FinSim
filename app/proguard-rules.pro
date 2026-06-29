# Regras ProGuard para o FinSim
# Adicione regras específicas conforme bibliotecas forem adicionadas.

# Hilt
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Kotlin
-keepclassmembers class ** {
    @kotlin.Metadata *;
}
