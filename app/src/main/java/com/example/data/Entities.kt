package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class Video(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val originalUrl: String,
    val category: String,
    val language: String,
    val duration: Int, // saniye cinsinden
    val thumbnailUrl: String,
    val viewCount: Int = 0
)

@Entity(tableName = "dub_jobs")
data class DubJob(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val originalUrl: String,
    var status: String, // PENDING, TRANSCRIBING, TRANSLATING, SYNTHESIZING, COMPLETE, ERROR
    var progress: Int, // 0 - 100
    val srcLang: String,
    val destLang: String,
    val originalTranscript: String,
    val translatedTranscript: String,
    val outputUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val errorMessage: String = ""
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // INFO, SUCCESS, ERROR
    val title: String,
    val message: String,
    val relatedJobId: Int,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_preference")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Tekil satır
    val name: String,
    val email: String,
    val role: String,
    val preferredLanguage: String,
    val notificationEmail: Boolean,
    val notificationInApp: Boolean,
    val favoriteVideoIds: String // Virgülle ayrılmış id'ler: "1,2,5"
)
