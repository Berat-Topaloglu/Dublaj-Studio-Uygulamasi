package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.AppDatabase
import com.example.data.Repository
import com.example.data.Video
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary

enum class AppTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Anasayfa", Icons.Default.Dashboard, "tab_home"),
    VIDEOS("Katalog", Icons.Default.Search, "tab_videos"),
    CREATE("Atölye", Icons.Default.Mic, "tab_create"),
    PROJECTS("Projelerim", Icons.Default.Tune, "tab_projects"),
    PROFILE("Hesabım", Icons.Default.Person, "tab_profile")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Veritabanını ve Depoyu başlat (Safe and pristine lifecycle scope)
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = Repository(database, lifecycleScope)

        // 2. ViewModel tanımlaması
        val viewModel: DubStudioViewModel by viewModels {
            DubStudioViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                val videos by viewModel.videos.collectAsStateWithLifecycle()
                val jobs by viewModel.jobs.collectAsStateWithLifecycle()
                val notifications by viewModel.notifications.collectAsStateWithLifecycle()
                val activeMedia by viewModel.activeMedia.collectAsStateWithLifecycle()

                var currentTab by remember { mutableStateOf(AppTab.HOME) }
                var showNotificationsPanel by remember { mutableStateOf(false) }

                // Keşif sayfasından seçilen ve direkt dublaja aktarılan video
                var preselectedVideoForCreation by remember { mutableStateOf<Video?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // Ortak Özel Stüdyo Başlık Çubuğu
                        Column(
                            modifier = Modifier
                                .background(StudioSecondary)
                                .windowInsetsPadding(WindowInsets.statusBars)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(StudioPrimary)
                                    )
                                    Text(
                                        text = "DubStudio AI",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 19.sp,
                                        color = Color.White
                                    )
                                }

                                // Bildirim Butonuna unread rozeti eklenmesi
                                val unreadCount = notifications.count { !it.read }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .clickable { showNotificationsPanel = true }
                                        .testTag("notification_bell_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Bildirimler",
                                        tint = if (unreadCount > 0) StudioAccent else Color.White
                                    )
                                    if (unreadCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(StudioPrimary)
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        }
                    },
                    bottomBar = {
                        // Edge-to-Edge window insets standard M3 NavigationBar
                        NavigationBar(
                            containerColor = StudioSecondary,
                            contentColor = StudioPrimary,
                            tonalElevation = 8.dp,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            AppTab.values().forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (tab != AppTab.CREATE) {
                                            preselectedVideoForCreation = null
                                        }
                                        currentTab = tab
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            tint = if (isSelected) StudioPrimary else Color.White.copy(alpha = 0.6f)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            color = if (isSelected) StudioPrimary else Color.White.copy(alpha = 0.6f)
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = StudioPrimary.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.testTag(tab.tag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(StudioSecondary)
                    ) {
                        // Aktif Panel Geçişleri (Tab Controller Layouts)
                        when (currentTab) {
                            AppTab.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onCreateDubNavigate = {
                                        currentTab = AppTab.CREATE
                                    },
                                    onWatchMedia = { playable ->
                                        viewModel.selectMedia(playable)
                                    }
                                )
                            }
                            AppTab.VIDEOS -> {
                                VideosScreen(
                                    viewModel = viewModel,
                                    onStartDubbing = { video ->
                                        preselectedVideoForCreation = video
                                        currentTab = AppTab.CREATE
                                    },
                                    onWatchVideo = { video ->
                                        val media = PlayableMedia(
                                            title = video.title,
                                            url = video.originalUrl,
                                            originalTranscript = video.description,
                                            translatedTranscript = "Oynatma başlatılıyor...",
                                            srcLang = video.language,
                                            destLang = "English"
                                        )
                                        viewModel.selectMedia(media)
                                    }
                                )
                            }
                            AppTab.CREATE -> {
                                DubCreateScreen(
                                    viewModel = viewModel,
                                    preselectedVideo = preselectedVideoForCreation,
                                    onJobSubmitted = {
                                        preselectedVideoForCreation = null
                                        currentTab = AppTab.PROJECTS
                                    }
                                )
                            }
                            AppTab.PROJECTS -> {
                                MyDubsScreen(
                                    viewModel = viewModel,
                                    onWatchJobMedia = { playable ->
                                        viewModel.selectMedia(playable)
                                    }
                                )
                            }
                            AppTab.PROFILE -> {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    onWatchVideo = { video ->
                                        val media = PlayableMedia(
                                            title = video.title,
                                            url = video.originalUrl,
                                            originalTranscript = video.description,
                                            translatedTranscript = "Oynatma başlatılıyor...",
                                            srcLang = video.language,
                                            destLang = "English"
                                        )
                                        viewModel.selectMedia(media)
                                    }
                                )
                            }
                        }

                        // ÜST PANEL 1: Özel Gelişmiş Video Oynatıcı Katmanı (Active Media overlay)
                        AnimatedVisibility(
                            visible = activeMedia != null,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            activeMedia?.let { media ->
                                VideoPlayerScreen(
                                    activeMedia = media,
                                    onClose = {
                                        viewModel.selectMedia(null)
                                    }
                                )
                            }
                        }

                        // ÜST PANEL 2: Bildirim Akışı Katmanı (Notifications overlay)
                        AnimatedVisibility(
                            visible = showNotificationsPanel,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                        ) {
                            NotificationsOverlay(
                                notifications = notifications,
                                onMarkRead = { id -> viewModel.markNotificationAsRead(id) },
                                onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                                onClearAll = { viewModel.clearNotifications() },
                                onClose = { showNotificationsPanel = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
