package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Video::class, DubJob::class, Notification::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun dubJobDao(): DubJobDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dubstudio_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: AppDatabase) {
            // Varsayılan video kataloğunu yükle (Pristine Turkish UTF-8 strings)
            val initialVideos = listOf(
                Video(
                    id = 1,
                    title = "Yapay Zeka Seslendirme Devrimi",
                    description = "Yapay zekanın ses sentezleme ve doğal dil işleme teknolojilerinde ulaştığı son nokta. Kelimelerin duygularla buluştuğu eşsiz bir vizyon sunumu.",
                    originalUrl = "https://www.youtube.com/watch?v=ai_revolution",
                    category = "Teknoloji",
                    language = "İngilizce",
                    duration = 180,
                    thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&q=80",
                    viewCount = 1420
                ),
                Video(
                    id = 2,
                    title = "Geleneksel Akdeniz Yemekleri",
                    description = "Ege ve Akdeniz'in taze otları, zeytinyağlıları ve eşsiz deniz ürünleriyle harmanlanmış şef eğitim serisi. Sağlıklı yaşamın mutfak kapısı.",
                    originalUrl = "https://www.youtube.com/watch?v=mediterranean_cooking",
                    category = "Mutfak",
                    language = "Türkçe",
                    duration = 320,
                    thumbnailUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=500&q=80",
                    viewCount = 850
                ),
                Video(
                    id = 3,
                    title = "Kuantum Fiziğinin Temelleri",
                    description = "Makro dünyadan atom altı parçacıkların büyüleyici evrenine uzanan bilimsel yolculuk. Süperpozisyon ve kuantum dolanıklığı kavramlarının animasyonlu anlatımı.",
                    originalUrl = "https://www.youtube.com/watch?v=quantum_physics",
                    category = "Bilim",
                    language = "İngilizce",
                    duration = 450,
                    thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=500&q=80",
                    viewCount = 2100
                ),
                Video(
                    id = 4,
                    title = "Gezegenimizin Gizli Ormanları",
                    description = "Yağmur ormanlarının balta girmemiş derinliklerindeki vahşi yaşam ve mikro ekosistemlerin büyüleyici hikayesi.",
                    originalUrl = "https://www.youtube.com/watch?v=secret_forests",
                    category = "Belgesel",
                    language = "Almanca",
                    duration = 600,
                    thumbnailUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=500&q=80",
                    viewCount = 940
                ),
                Video(
                    id = 5,
                    title = "Modern Caz Müziğin Ritmi",
                    description = "New Orleans sokaklarından modern caz kulüplerine; saksafon soloları ve piyano doğaçlamaları eşliğinde cazın tarihi.",
                    originalUrl = "https://www.youtube.com/watch?v=history_of_jazz",
                    category = "Müzik",
                    language = "İngilizce",
                    duration = 270,
                    thumbnailUrl = "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=500&q=80",
                    viewCount = 1105
                ),
                Video(
                    id = 6,
                    title = "Mars Kolonizasyonu Yolculuğu",
                    description = "İnsanlığın kızıl gezegende kurmayı planladığı yaşam habitatları, roket teknolojileri ve gelecek projeksiyonu.",
                    originalUrl = "https://www.youtube.com/watch?v=mars_journey",
                    category = "Uzay",
                    language = "Fransızca",
                    duration = 380,
                    thumbnailUrl = "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?w=500&q=80",
                    viewCount = 1890
                )
            )
            db.videoDao().insertVideos(initialVideos)

            // Varsayılan kullanıcı profilini yükle
            val defaultProfile = UserProfile(
                id = 1,
                name = "Eda Sessiz",
                email = "silentedas@gmail.com",
                role = "Yapay Zeka Seslendirme Uzmanı",
                preferredLanguage = "Türkçe",
                notificationEmail = false,
                notificationInApp = true,
                favoriteVideoIds = "1,3"
            )
            db.userProfileDao().insertUserProfile(defaultProfile)

            // Varsayılan başlangıç bildirimi gönder
            val initialNotification = Notification(
                type = "INFO",
                title = "DubStudio AI Dünyasına Hoş Geldiniz!",
                message = "Yapay zeka seslendirme stüdyosuna hoş geldiniz! Üst menüden videoları inceleyebilir, hemen yeni bir seslendirme projesi oluşturabilirsiniz.",
                relatedJobId = 0
            )
            db.notificationDao().insertNotification(initialNotification)
        }
    }
}
