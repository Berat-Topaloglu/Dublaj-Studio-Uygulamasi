package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<Video>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<Video>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Video)

    @Update
    suspend fun updateVideo(video: Video)
}

@Dao
interface DubJobDao {
    @Query("SELECT * FROM dub_jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<DubJob>>

    @Query("SELECT * FROM dub_jobs WHERE id = :id")
    fun getJobById(id: Int): Flow<DubJob?>

    @Query("SELECT * FROM dub_jobs WHERE id = :id")
    suspend fun getJobByIdSync(id: Int): DubJob?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: DubJob): Long

    @Update
    suspend fun updateJob(job: DubJob)

    @Delete
    suspend fun deleteJob(job: DubJob)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE notifications SET read = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_preference WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_preference WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)
}
