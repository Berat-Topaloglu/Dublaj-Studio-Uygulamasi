package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DubJob
import com.example.data.Notification
import com.example.data.Repository
import com.example.data.UserProfile
import com.example.data.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlayableMedia(
    val title: String,
    val url: String,
    val originalTranscript: String,
    val translatedTranscript: String,
    val srcLang: String,
    val destLang: String
)

class DubStudioViewModel(private val repository: Repository) : ViewModel() {

    // 1. Durumlar (Reactive State Flows)
    val videos: StateFlow<List<Video>> = repository.allVideos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val jobs: StateFlow<List<DubJob>> = repository.allJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val notifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Oynatıcı Durumu (Active Player Media)
    private val _activeMedia = MutableStateFlow<PlayableMedia?>(null)
    val activeMedia: StateFlow<PlayableMedia?> = _activeMedia.asStateFlow()

    // 2. Aksiyonlar (Intent Handler Actions)
    fun startDubJob(title: String, originalUrl: String, srcLang: String, destLang: String) {
        viewModelScope.launch {
            repository.startDubbingJob(title, originalUrl, srcLang, destLang)
        }
    }

    fun toggleFavorite(videoId: Int) {
        viewModelScope.launch {
            repository.toggleFavoriteVideo(videoId)
        }
    }

    fun deleteJob(job: DubJob) {
        viewModelScope.launch {
            repository.deleteJob(job)
        }
    }

    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    fun selectMedia(media: PlayableMedia?) {
        _activeMedia.value = media
    }

    fun updateProfile(
        name: String,
        email: String,
        role: String,
        preferredLanguage: String,
        notificationEmail: Boolean,
        notificationInApp: Boolean
    ) {
        viewModelScope.launch {
            val currentProfile = userProfile.value ?: UserProfile(
                id = 1,
                name = name,
                email = email,
                role = role,
                preferredLanguage = preferredLanguage,
                notificationEmail = notificationEmail,
                notificationInApp = notificationInApp,
                favoriteVideoIds = ""
            )
            repository.saveUserProfile(
                currentProfile.copy(
                    name = name,
                    email = email,
                    role = role,
                    preferredLanguage = preferredLanguage,
                    notificationEmail = notificationEmail,
                    notificationInApp = notificationInApp
                )
            )
        }
    }
}

class DubStudioViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DubStudioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DubStudioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
