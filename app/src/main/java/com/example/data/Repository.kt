package com.example.data

import com.example.api.GeminiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private val videoDao = database.videoDao()
    private val dubJobDao = database.dubJobDao()
    private val notificationDao = database.notificationDao()
    private val userProfileDao = database.userProfileDao()

    // 1. VIDEOS
    val allVideos: Flow<List<Video>> = videoDao.getAllVideos()

    suspend fun addVideo(video: Video) = withContext(Dispatchers.IO) {
        videoDao.insertVideo(video)
    }

    suspend fun incrementViewCount(video: Video) = withContext(Dispatchers.IO) {
        videoDao.updateVideo(video.copy(viewCount = video.viewCount + 1))
    }

    // 2. DUB JOBS
    val allJobs: Flow<List<DubJob>> = dubJobDao.getAllJobs()

    fun getJobById(id: Int): Flow<DubJob?> {
        return dubJobDao.getJobById(id)
    }

    suspend fun deleteJob(job: DubJob) = withContext(Dispatchers.IO) {
        dubJobDao.deleteJob(job)
    }

    // 3. NOTIFICATIONS
    val allNotifications: Flow<List<Notification>> = notificationDao.getAllNotifications()

    suspend fun markNotificationAsRead(id: Int) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        notificationDao.clearAllNotifications()
    }

    // 4. USER PROFILE
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfileFlow()

    suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        userProfileDao.insertUserProfile(profile)
    }

    suspend fun toggleFavoriteVideo(videoId: Int) = withContext(Dispatchers.IO) {
        val currentProfile = userProfileDao.getUserProfileSync() ?: UserProfile(
            id = 1,
            name = "Eda Sessiz",
            email = "silentedas@gmail.com",
            role = "Yapay Zeka Seslendirme Uzmanı",
            preferredLanguage = "Türkçe",
            notificationEmail = false,
            notificationInApp = true,
            favoriteVideoIds = ""
        )

        val favoritesList = currentProfile.favoriteVideoIds
            .split(",")
            .filter { it.isNotEmpty() }
            .toMutableList()

        val idStr = videoId.toString()
        if (favoritesList.contains(idStr)) {
            favoritesList.remove(idStr)
        } else {
            favoritesList.add(idStr)
        }

        val updatedProfile = currentProfile.copy(
            favoriteVideoIds = favoritesList.joinToString(",")
        )
        userProfileDao.insertUserProfile(updatedProfile)
    }

    // 5. JOB PIPELINE ORCHESTRATION (The Studio AI Worker Engine)
    suspend fun startDubbingJob(
        title: String,
        originalUrl: String,
        srcLang: String,
        destLang: String
    ): Long = withContext(Dispatchers.IO) {
        // 1. İlk kaydı oluştur (PENDING)
        val initialJob = DubJob(
            title = title,
            originalUrl = originalUrl,
            status = "PENDING",
            progress = 5,
            srcLang = srcLang,
            destLang = destLang,
            originalTranscript = "Analiz bekleniyor...",
            translatedTranscript = "Çeviri bekleniyor..."
        )
        val jobId = dubJobDao.insertJob(initialJob).toInt()

        // 2. Arka planda çalışma sürecini başlat
        scope.launch(Dispatchers.IO) {
            try {
                // Aşama 1: Ses Analizi ve Deşifre Etme (Original Transcript)
                delay(2500)
                updateJobStatus(jobId, "TRANSCRIBING", 25)

                // Gemini API ile çeviri talep et
                val aiResult = GeminiClient.translateVideoContent(title, srcLang, destLang)

                val (originalTranscriptText, translatedTranscriptText) = if (aiResult != null) {
                    Pair(aiResult.transcript, aiResult.translation)
                } else {
                    // Gemini anahtarı yoksa veya hata alındıysa, zengin içerikli yerel simülatör
                    generateSimulatorTranscripts(title, srcLang, destLang)
                }

                // Aşama 2: Tercüme Filtreleme & Altyazı Hizalama
                delay(3000)
                updateJobStatus(
                    jobId,
                    "TRANSLATING",
                    60,
                    originalTranscript = originalTranscriptText,
                    translatedTranscript = "Hizalanıyor: $translatedTranscriptText"
                )

                // Aşama 3: Ses Klonlama ve Sentezleme (Voice synthesis)
                delay(2500)
                updateJobStatus(
                    jobId,
                    "SYNTHESIZING",
                    85,
                    originalTranscript = originalTranscriptText,
                    translatedTranscript = translatedTranscriptText
                )

                // Aşama 4: Tamamlandı
                delay(2000)
                val completedJob = dubJobDao.getJobByIdSync(jobId)
                if (completedJob != null) {
                    // Kullanabileceğimiz kararlı sample video url'i
                    val demoVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    
                    dubJobDao.updateJob(
                        completedJob.copy(
                            status = "COMPLETE",
                            progress = 100,
                            originalTranscript = originalTranscriptText,
                            translatedTranscript = translatedTranscriptText,
                            outputUrl = demoVideoUrl
                        )
                    )

                    // Başarı bildirimi gönder
                    notificationDao.insertNotification(
                        Notification(
                            type = "SUCCESS",
                            title = "Dublaj Sentezi Başarılı!",
                            message = "\"$title\" başlıklı video $srcLang dilinden $destLang diline başarıyla seslendirildi ve altyazılandırıldı.",
                            relatedJobId = jobId
                        )
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                val erredJob = dubJobDao.getJobByIdSync(jobId)
                if (erredJob != null) {
                    dubJobDao.updateJob(
                        erredJob.copy(
                            status = "ERROR",
                            progress = 100,
                            errorMessage = e.message ?: "Beklenmeyen donanımsal sentez hatası."
                        )
                    )

                    notificationDao.insertNotification(
                        Notification(
                            type = "ERROR",
                            title = "Dublaj Sentez Hatası!",
                            message = "\"$title\" projesinin sentezlenmesi sırasında bir hata oluştu: ${e.message}",
                            relatedJobId = jobId
                        )
                    )
                }
            }
        }

        return@withContext jobId.toLong()
    }

    private suspend fun updateJobStatus(
        id: Int,
        status: String,
        progress: Int,
        originalTranscript: String? = null,
        translatedTranscript: String? = null
    ) {
        val job = dubJobDao.getJobByIdSync(id)
        if (job != null) {
            val updated = job.copy(
                status = status,
                progress = progress,
                originalTranscript = originalTranscript ?: job.originalTranscript,
                translatedTranscript = translatedTranscript ?: job.translatedTranscript
            )
            dubJobDao.updateJob(updated)
        }
    }

    private fun generateSimulatorTranscripts(
        title: String,
        src: String,
        dest: String
    ): Pair<String, String> {
        val srcText: String
        val destText: String

        when {
            src == "İngilizce" && dest == "Türkçe" -> {
                srcText = "Welcome to this episode. Today, we are deep diving into the revolutionary techniques shown in '$title'. We hope you enjoy the spectacular details and insights we gathered."
                destText = "Bu bölüme hoş geldiniz. Bugün, '$title' başlığında gösterilen devrim niteliğindeki teknikleri derinlemesine inceliyoruz. Topladığımız göz alıcı detayları ve içgörüleri beğeneceğinizi umuyoruz."
            }
            src == "Türkçe" && dest == "İngilizce" -> {
                srcText = "Herkese merhaba. Bu çalışmamızda, '$title' konusunu tüm çarpıcı detaylarıyla ele alıyoruz. Bu alandaki teknolojik yenilikleri keyifle seyretmenizi dileriz."
                destText = "Hello everyone. In this study, we cover the topic of '$title' with all its stunning details. We hope you enjoy watching the technological innovations in this field."
            }
            else -> {
                srcText = "Analyzing sequence for video file regarding '$title'. The audio track contains high fidelity signals and clear linguistic semantics in $src."
                destText = "'$title' başlıklı video dosyası sekansı analiz ediliyor. Ses kanalı, $dest dilinde yüksek kaliteli sinyaller ve net dilsel anlamlar içeriyor."
            }
        }
        return Pair(srcText, destText)
    }
}
