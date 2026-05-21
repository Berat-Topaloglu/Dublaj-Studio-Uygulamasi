package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.DubJob
import com.example.data.Notification
import com.example.data.Video
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// 1. HOME SCREEN (Landing Dashboard)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: DubStudioViewModel,
    onCreateDubNavigate: () -> Unit,
    onWatchMedia: (PlayableMedia) -> Unit
) {
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val jobs by viewModel.jobs.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val unreadNotifsCount = notifications.count { !it.read }
    val activeJobsCount = jobs.count { it.status != "COMPLETE" && it.status != "ERROR" }
    val completedJobsCount = jobs.count { it.status == "COMPLETE" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hoş Geldiniz Başlığı ve Premium Kartı
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(StudioPrimary.copy(alpha = 0.25f), Color.Transparent),
                            radius = 600f
                        )
                    )
                    .border(BorderStroke(1.dp, StudioPrimary.copy(alpha = 0.4f)), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioPrimary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("PRO STÜDYO", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.White)
                        }
                        Text(
                            text = "Gecikme: 40ms",
                            color = StudioAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "DubStudio AI Kontrol Paneli",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gelişmiş ses klonlama, yapay zeka deşifreleme ve diller arası video dublaj platformu. Yerel yapay zeka birimleri çalışıyor.",
                        color = SubText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // İstatistik Grid Bölümü
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Katalog Videosu",
                    value = videos.size.toString(),
                    icon = Icons.Default.VideoLibrary,
                    accentColor = StudioPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Aktif İşlem",
                    value = activeJobsCount.toString(),
                    icon = Icons.Default.Memory,
                    accentColor = StudioAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Başarılı Sentez",
                    value = completedJobsCount.toString(),
                    icon = Icons.Default.Hearing,
                    accentColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Hızlı Aksiyon Ayracı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreateDubNavigate() }
                    .testTag("quick_create_button"),
                colors = CardDefaults.cardColors(containerColor = StudioPrimary.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, StudioPrimary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(StudioPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Yeni AI Seslendirme / Dublaj Başlat",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            "Farklı dildeki videoları saniyeler içinde seslendirin.",
                            color = SubText,
                            fontSize = 12.sp
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = StudioPrimary)
                }
            }
        }

        // Bildirim Özeti Şeridi
        if (unreadNotifsCount > 0) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioAccent.copy(alpha = 0.15f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = StudioAccent)
                    Text(
                        text = "Okunmamış $unreadNotifsCount yeni stüdyo bildiriminiz var.",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Son Dublaj Çalışmalarım Başlığı
        item {
            Text(
                text = "Son Dublaj Çalışmalarım",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Son Çalışmaların Listesi
        if (jobs.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "Henüz Dublaj İşlemi Yok",
                    description = "Üstteki butonu kullanarak ilk video dublaj projenizi saniyeler içinde oluşturabilirsiniz.",
                    icon = Icons.Default.GraphicEq
                )
            }
        } else {
            items(jobs.take(4)) { job ->
                DubJobItemCard(job = job, onDelete = { viewModel.deleteJob(job) }, onPlay = {
                    val media = PlayableMedia(
                        title = job.title,
                        url = job.outputUrl,
                        originalTranscript = job.originalTranscript,
                        translatedTranscript = job.translatedTranscript,
                        srcLang = job.srcLang,
                        destLang = job.destLang
                    )
                    onWatchMedia(media)
                })
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = title,
                color = SubText,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==========================================
// 2. VIDEOS SCREEN (Discovery & Filter Catalog)
// ==========================================
@Composable
fun VideosScreen(
    viewModel: DubStudioViewModel,
    onStartDubbing: (Video) -> Unit,
    onWatchVideo: (Video) -> Unit
) {
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var sortBy by remember { mutableStateOf("Yeni") } // "Yeni", "Popüler"

    val categories = listOf("Tümü", "Teknoloji", "Mutfak", "Bilim", "Belgesel", "Müzik", "Uzay")

    // Filtreleme mantığı
    val filteredVideos = videos.filter { video ->
        val matchesSearch = video.title.contains(searchQuery, ignoreCase = true) || 
                            video.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "Tümü" || video.category == selectedCategory
        matchesSearch && matchesCategory
    }.sortedByDescending {
        if (sortBy == "Popüler") it.viewCount else it.id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("videos_screen")
    ) {
        // Arama ve Filtre Bölümü
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioSecondary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                placeholder = { Text("Video başlığı veya içerik ara...", color = SubText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StudioPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = SubText)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StudioPrimary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                    focusedContainerColor = StudioCardBg,
                    unfocusedContainerColor = StudioCardBg,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Kategoriler Akışı (LazyRow)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (isSelected) StudioPrimary else StudioCardBg
                            )
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (isSelected) StudioPrimary else Color.White.copy(alpha = 0.08f)
                                ),
                                RoundedCornerShape(50.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("category_tag_$category")
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else WhiteText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Sıralama ve Sonuç Sayısı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredVideos.size} video bulundu",
                    color = SubText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Sıralama:",
                        color = SubText,
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (sortBy == "Yeni") "Yeni Eklenenler" else "Popülerlik",
                        color = StudioAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                sortBy = if (sortBy == "Yeni") "Popüler" else "Yeni"
                            }
                            .testTag("sort_toggle")
                    )
                }
            }
        }

        // Video Grid Listesi
        if (filteredVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    title = "Video Bulunamadı",
                    description = "Filtre kriterlerinize veya arama teriminize uyan katalog videosu bulunmuyor. Lütfen başka bir sorgu deneyin.",
                    icon = Icons.Default.SearchOff
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredVideos) { video ->
                    val isFavorite = profile?.favoriteVideoIds?.split(",")?.contains(video.id.toString()) == true

                    VideoListItem(
                        video = video,
                        isFavorite = isFavorite,
                        onFavoriteClick = { viewModel.toggleFavorite(video.id) },
                        onStartDubbing = { onStartDubbing(video) },
                        onWatchClick = { onWatchVideo(video) }
                    )
                }
            }
        }
    }
}

@Composable
fun VideoListItem(
    video: Video,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onStartDubbing: () -> Unit,
    onWatchClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("video_card_${video.id}"),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Görsel / Küçük Resim Paneli
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Kategori ve Süre Rozetleri
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioPrimary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(video.category, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        val min = video.duration / 60
                        val sec = video.duration % 60
                        Text(
                            text = String.format("%02d:%02d", min, sec),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Oynatma / Hızlı İzleme Tuşu
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .clickable { onWatchClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            // Metin ve Detaylar Paneli
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favoriye Ekle",
                        tint = if (isFavorite) Color(0xFFFFD700) else SubText,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onFavoriteClick() }
                            .testTag("favorite_button_${video.id}")
                    )
                }

                Text(
                    text = video.description,
                    color = SubText,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                // İstatistik Satırı & Dublaj Yap Butonu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👀 ${video.viewCount} izlenme",
                            color = SubText,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "🌐 Orj: ${video.language}",
                            color = StudioAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onStartDubbing,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("dub_action_${video.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Transform, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dublaj Başlat", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. DUBCREATE SCREEN (AI Dubbing Job Setup)
// ==========================================
@Composable
fun DubCreateScreen(
    viewModel: DubStudioViewModel,
    preselectedVideo: Video?,
    onJobSubmitted: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var projectTitle by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Link, 1: Dosya Yükle

    // Diller
    val languages = listOf("Türkçe", "İngilizce", "Almanca", "Fransızca", "İspanyolca")
    var srcLanguage by remember { mutableStateOf("İngilizce") }
    var destLanguage by remember { mutableStateOf("Türkçe") }

    // Dropdown States
    var srcExpanded by remember { mutableStateOf(false) }
    var destExpanded by remember { mutableStateOf(false) }

    // Upload Simulation States
    var uploadProgress by remember { mutableStateOf(0) }
    var isUploading by remember { mutableStateOf(false) }

    // Parametreleri preselected video varsa baştan otomatik ata
    LaunchedEffect(preselectedVideo) {
        if (preselectedVideo != null) {
            projectTitle = "${preselectedVideo.title} (Dublaj Çalışması)"
            videoUrl = preselectedVideo.originalUrl
            srcLanguage = preselectedVideo.language
        } else {
            projectTitle = "AI Seslendirme Projesi #${(100..999).random()}"
            videoUrl = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dub_create_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Yapay Zeka Seslendirme Atölyesi",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Burada dilediğiniz video linkini ekleyerek veya cihazınızdan dosya yükleyerek anında tercümeli ses sentezi oluşturabilirsiniz.",
                color = SubText,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        // Link Ekle vs Dosya Yükle sekmesi
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioCardBg,
                contentColor = StudioPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Video Linki Yapıştır", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Presigned Dosya Yükleme", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Girdi bölümleri
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Proje Adı Metin Sahası
                    Text("Proje Başlığı veya Başlığı", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = projectTitle,
                        onValueChange = { projectTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_title_input"),
                        placeholder = { Text("Örn: Kuantum Fiziği Türkçe Sentezi") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (selectedTab == 0) {
                        // Link girişi saha
                        Text("Video Bağlantı Adresi (URL)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("video_url_input"),
                            placeholder = { Text("https://www.youtube.com/watch?v=...") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = StudioPrimary) }
                        )

                        // Hazır şablonlar
                        Text("Hazır Örnek Şablonlar:", color = SubText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Youtube", "Vimeo", "MP4").forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .clickable {
                                            videoUrl = "https://www.$preset-demo-stream.com/sample_media.mp4"
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(preset, color = WhiteText, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // Dosya yükleme simülatör paneli
                        Text("Yükleme Depolama Birimi", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.25f))
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploading) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(progress = { uploadProgress / 100f }, color = StudioAccent)
                                    Text("Simüle Edilen Upload: $uploadProgress%", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Presigned S3 URL aracılığıyla güvenle yükleniyor...", color = SubText, fontSize = 11.sp)
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.clickable {
                                        coroutineScope.launch {
                                            isUploading = true
                                            uploadProgress = 0
                                            while (uploadProgress < 100) {
                                                delay(100)
                                                uploadProgress += 10
                                            }
                                            videoUrl = "https://studio-presigned-bucket.s3.amazonaws.com/uploads/user_voice_video.mp4"
                                            isUploading = false
                                        }
                                    }.testTag("simulate_upload_panel")
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = StudioAccent, modifier = Modifier.size(36.dp))
                                    Text("Dosya Seçmek İçin Dokunun", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Video formats: MP4, MOV, WEBM (Max: 100MB)", color = SubText, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dil Seçimleri (Dropdowns)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Kaynak Dil
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Orjinal Dil (Kaynak)", color = SubText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(8.dp))
                                .clickable { srcExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("src_lang_dropdown")
                        ) {
                            Text(srcLanguage, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            DropdownMenu(
                                expanded = srcExpanded,
                                onDismissRequest = { srcExpanded = false }
                            ) {
                                languages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang) },
                                        onClick = {
                                            srcLanguage = lang
                                            srcExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Hedef Dil
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Arzulanan Dil (Hedef)", color = SubText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(8.dp))
                                .clickable { destExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("dest_lang_dropdown")
                        ) {
                            Text(destLanguage, color = StudioAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            DropdownMenu(
                                expanded = destExpanded,
                                onDismissRequest = { destExpanded = false }
                            ) {
                                languages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang) },
                                        onClick = {
                                            destLanguage = lang
                                            destExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Gönder butonu
        item {
            Button(
                onClick = {
                    if (projectTitle.isNotEmpty() && videoUrl.isNotEmpty()) {
                        viewModel.startDubJob(projectTitle, videoUrl, srcLanguage, destLanguage)
                        onJobSubmitted()
                    }
                },
                enabled = projectTitle.isNotEmpty() && videoUrl.isNotEmpty() && !isUploading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_job_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioPrimary,
                    disabledContainerColor = StudioPrimary.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.SettingsVoice, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Dublaj ve Sentez Başlat", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 4. MY DUBS SCREEN (Job Monitor & Details)
// ==========================================
@Composable
fun MyDubsScreen(
    viewModel: DubStudioViewModel,
    onWatchJobMedia: (PlayableMedia) -> Unit
) {
    val jobs by viewModel.jobs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mydubs_screen")
    ) {
        // Üst Bilgi Satırı
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Dublaj Sentez Kuyruğunuz", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${jobs.size} toplam seslendirme projesi kayıtlı", color = SubText, fontSize = 12.sp)
            }
            if (jobs.isNotEmpty()) {
                Text(
                    text = "Yapay Zeka Sentez Ünitesi Aktif",
                    color = StudioAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (jobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    title = "Proje Bulunmamaktadır",
                    description = "Henüz sentez oluşturmadınız. Yapay Zeka Atölyesi sayfasından dilediğiniz videodan seslendirme talep edebilirsiniz.",
                    icon = Icons.Default.LibraryMusic
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(jobs) { job ->
                    DubJobItemCard(
                        job = job,
                        onDelete = { viewModel.deleteJob(job) },
                        onPlay = {
                            val media = PlayableMedia(
                                title = job.title,
                                url = job.outputUrl,
                                originalTranscript = job.originalTranscript,
                                translatedTranscript = job.translatedTranscript,
                                srcLang = job.srcLang,
                                destLang = job.destLang
                            )
                            onWatchJobMedia(media)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DubJobItemCard(
    job: DubJob,
    onDelete: () -> Unit,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("job_card_${job.id}"),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Başlık satırı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Orjinal: ${job.originalUrl}",
                        color = SubText,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("delete_job_${job.id}")
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Proje Sil", tint = Color.Red.copy(alpha = 0.8f))
                }
            }

            // Diller Arası Dönüşüm Rozetleri
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageBadge(lang = job.srcLang, isTarget = false)
                Icon(Icons.Default.TrendingFlat, contentDescription = null, tint = SubText, modifier = Modifier.size(16.dp))
                LanguageBadge(lang = job.destLang, isTarget = true)
            }

            // Durum / Yükleme Çubuğu Alanı
            val statusColor = when (job.status) {
                "COMPLETE" -> Color(0xFF4CAF50)
                "ERROR" -> Color.Red
                "SYNTHESIZING" -> StudioAccent
                else -> StudioPrimary
            }

            val statusText = when (job.status) {
                "PENDING" -> "Gecikmeli Sentez Başlatılıyor..."
                "TRANSCRIBING" -> "Yapay Zeka Ses Deşifre Ediliyor (Original Audio)..."
                "TRANSLATING" -> "Metin Yapay Zeka Tarafından Çevriliyor..."
                "SYNTHESIZING" -> "Yapay Kaynak Ses Klonlanıyor ve Sentezleniyor..."
                "COMPLETE" -> "Sentez Başarılı! Oynatılmaya Hazır."
                "ERROR" -> "Hata: ${job.errorMessage}"
                else -> job.status
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${job.progress}%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { job.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = statusColor,
                    trackColor = Color.White.copy(alpha = 0.08f)
                )
            }

            // Transkript Önizleme Bölmesi (Toggle / Detaylar)
            if (job.status == "COMPLETE" || job.status == "SYNTHESIZING" || job.status == "TRANSLATING") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.15f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🤖 AI Deşifre & Çeviri Havuzu", color = SubText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Orjinal: ${job.originalTranscript}",
                        color = WhiteText,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = Color.White.copy(alpha = 0.05f))
                    Text(
                        text = "Çeviri: ${job.translatedTranscript}",
                        color = StudioAccent,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Eğer tamamsa Oynatıcı Butonunu göster
            if (job.status == "COMPLETE") {
                Button(
                    onClick = onPlay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("play_on_active_player_${job.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Video Oynatıcıda Aç ve Kulak Ver", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LanguageBadge(lang: String, isTarget: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isTarget) StudioAccent.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f))
            .border(
                BorderStroke(0.5.dp, if (isTarget) StudioAccent.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.15f)),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = lang,
            color = if (isTarget) StudioAccent else WhiteText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==========================================
// 5. VIDEOPLAYER SCREEN (Studio Player Deck)
// ==========================================
@Composable
fun VideoPlayerScreen(
    activeMedia: PlayableMedia,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var playPercent by remember { mutableStateOf(0.15f) }
    var soundTone by remember { mutableStateOf(1.0f) } // Ses klonlama tonu
    var noiseSuppression by remember { mutableStateOf(true) }

    // Altyazı takip simülasyonu
    var displayedSubtitle by remember { mutableStateOf("") }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (playPercent < 1.0f) {
                delay(300)
                playPercent += 0.01f
                if (playPercent >= 1.0f) {
                    playPercent = 0.0f
                }
            }
        }
    }

    // Subtitle trigger simülasyon
    LaunchedEffect(playPercent) {
        val originalWords = activeMedia.originalTranscript.split(" ")
        val translatedWords = activeMedia.translatedTranscript.split(" ")

        val index = (playPercent * translatedWords.size).toInt().coerceIn(0, translatedWords.size - 1)
        displayedSubtitle = translatedWords.subList(0, (index + 1).coerceAtMost(translatedWords.size)).joinToString(" ")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioSecondary)
            .testTag("videoplayer_screen")
    ) {
        // Üst Kapatma ve İkincil Ekran Başlığı
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(StudioPrimary)
                )
                Text(
                    "Stüdyo Sentezi Oynatıcı",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_player_deck")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
            }
        }

        // 1. Ekran / Simüle Edilen Görüntü Paneli
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Color.Black)
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))),
            contentAlignment = Alignment.Center
        ) {
            // Şık bir arkadaki görsel dalgaları simülatörü
            AnimatedSoundWaveform(isPlaying = isPlaying)

            // Altyazı Bölgesi (Ekran Üstü bindirme)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Video Bilgi Rozeti
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${activeMedia.srcLang} ➜ ${activeMedia.destLang}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioAccent
                    )
                }

                // Canlı Subtitle render
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = displayedSubtitle,
                        color = StudioAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Gecikmeli Oynatma Kontrolleri (Zaman Çizelgesi)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Slider(
                value = playPercent,
                onValueChange = { playPercent = it },
                modifier = Modifier.fillMaxWidth().testTag("player_timeline_slider"),
                colors = SliderDefaults.colors(
                    activeTrackColor = StudioPrimary,
                    inactiveTrackColor = Color.White.copy(alpha = 0.08f),
                    thumbColor = StudioPrimary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentSeconds = (playPercent * 270).toInt()
                val totalSeconds = 270
                Text(
                    text = String.format("%02d:%02d", currentSeconds / 60, currentSeconds % 60),
                    color = SubText,
                    fontSize = 11.sp
                )

                // play-pause-btn
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(StudioPrimary)
                        .testTag("player_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Oynat/Duraklat",
                        tint = Color.White
                    )
                }

                Text(
                    text = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60),
                    color = SubText,
                    fontSize = 11.sp
                )
            }
        }

        // 2. Ses Klonlama ve Düzenleme Deck Alanı
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = activeMedia.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "DubStudio Yapay Kaynak Ses Sentezi tarafından deşifre edilmiş ve ses tonlamaları eşleştirilmiştir.",
                    color = SubText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Metin Karşılaştırma Havuzu
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = StudioAccent, modifier = Modifier.size(16.dp))
                            Text("Alt Yazı / Transkript Havuzu", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Original (${activeMedia.srcLang}):", color = SubText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(activeMedia.originalTranscript, color = WhiteText, fontSize = 13.sp, lineHeight = 17.sp)
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("AI Translated (${activeMedia.destLang}):", color = StudioAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(activeMedia.translatedTranscript, color = StudioAccent, fontSize = 13.sp, lineHeight = 17.sp)
                        }
                    }
                }
            }

            // Ses Ayarları Panel
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Yapay Ses Mikser Kontrolleri", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        // Tonlama/Klon benzerliği
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Ses Benzerliği Eşleşmesi", color = SubText, fontSize = 12.sp)
                                Text("${(soundTone * 100).toInt()}%", color = StudioPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = soundTone,
                                onValueChange = { soundTone = it },
                                valueRange = 0.5f..1.5f,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = StudioPrimary,
                                    thumbColor = StudioPrimary
                                )
                            )
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                        // Gürültü engelleme
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = null, tint = StudioAccent)
                                Column {
                                    Text("AI Dip Gürültü Engelleme", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text("Çevresel sesleri otomatik bastır", color = SubText, fontSize = 11.sp)
                                }
                            }
                            Switch(
                                checked = noiseSuppression,
                                onCheckedChange = { noiseSuppression = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = StudioAccent,
                                    checkedTrackColor = StudioAccent.copy(alpha = 0.35f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedSoundWaveform(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val animProgresses = List(15) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = (400..1000).random(), easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animProgresses.forEach { progress ->
            val heightMultiplier = if (isPlaying) progress.value else 0.15f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(heightMultiplier)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(StudioPrimary, StudioAccent)
                        )
                    )
            )
        }
    }
}

// ==========================================
// 6. PROFILE SCREEN (Preferences & Favorites)
// ==========================================
@Composable
fun ProfileScreen(
    viewModel: DubStudioViewModel,
    onWatchVideo: (Video) -> Unit
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var inAppNotification by remember { mutableStateOf(true) }
    var emailNotification by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            email = it.email
            role = it.role
            language = it.preferredLanguage
            inAppNotification = it.notificationInApp
            emailNotification = it.notificationEmail
        }
    }

    val themeLanguages = listOf("Türkçe", "İngilizce", "Almanca", "Fransızca")
    var langExpanded by remember { mutableStateOf(false) }

    // Favori videoları bul
    val favoriteVideos = videos.filter {
        profile?.favoriteVideoIds?.split(",")?.contains(it.id.toString()) == true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Stüdyo Hesabım",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Profil bilgilerinizi düzenleyebilir, bildirim kanallarını ve kaydedilmiş favori videolarınızı buradan takip edebilirsiniz.",
                color = SubText,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        // Temel Bilgiler Formu
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Temel Kullanıcı Kartı", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    // İsim
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input"),
                        label = { Text("Kullanıcı Adı") },
                        singleLine = true
                    )

                    // E-posta
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_email_input"),
                        label = { Text("E-posta Adresi") },
                        singleLine = true
                    )

                    // Rol
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_role_input"),
                        label = { Text("Stüdyo Unvanı / Rol") },
                        singleLine = true
                    )

                    // Tercih Dil Dropdown
                    Column {
                        Text("Tercih Edilen Arayüz Dili", color = SubText, fontSize = 11.sp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(8.dp))
                                .clickable { langExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                                .testTag("profile_lang_dropdown")
                        ) {
                            Text(language, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            DropdownMenu(expanded = langExpanded, onDismissRequest = { langExpanded = false }) {
                                themeLanguages.forEach { lang ->
                                    DropdownMenuItem(text = { Text(lang) }, onClick = {
                                        language = lang
                                        langExpanded = false
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bildirim Kanalları
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Haberleşme Tercihleri", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    // Uygulama İçi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Uygulama İçi Bildirimler", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Sentez işlemleri bittiğinde anlık uyarı al", color = SubText, fontSize = 11.sp)
                        }
                        Switch(
                            checked = inAppNotification,
                            onCheckedChange = { inAppNotification = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = StudioAccent)
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    // E-posta
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("E-posta Duyuruları", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Sistem raporlarını e-posta kutuna raporla", color = SubText, fontSize = 11.sp)
                        }
                        Switch(
                            checked = emailNotification,
                            onCheckedChange = { emailNotification = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = StudioAccent)
                        )
                    }
                }
            }
        }

        // Kaydet butonu
        item {
            Button(
                onClick = {
                    viewModel.updateProfile(
                        name = name,
                        email = email,
                        role = role,
                        preferredLanguage = language,
                        notificationEmail = emailNotification,
                        notificationInApp = inAppNotification
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Profil Bilgilerimi Güncelle", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Yıldızlı Favorilerim
        item {
            Text(
                text = "Favorilerime Eklediğim Videolar",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        if (favoriteVideos.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "Yıldızlı Videonuz Yok",
                    description = "Katalog sayfasından beğendiğiniz videolardaki yıldız tuşuna basarak favorilere ekleyebilirsiniz.",
                    icon = Icons.Default.StarOutline
                )
            }
        } else {
            items(favoriteVideos) { video ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWatchVideo(video) }
                        .testTag("favorite_card_${video.id}"),
                    colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(video.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(video.category, color = StudioAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.toggleFavorite(video.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Kaldır", tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. NOTIFICATIONS OVERLAY PANEL
// ==========================================
@Composable
fun NotificationsOverlay(
    notifications: List<Notification>,
    onMarkRead: (Int) -> Unit,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioSecondary.copy(alpha = 0.95f))
            .padding(16.dp)
            .testTag("notifications_panel"),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = StudioPrimary)
                    Text("Stüdyo Bildirim Akışı", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                }
            }

            // Quick Actions
            if (notifications.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onMarkAllRead,
                        modifier = Modifier.testTag("mark_all_read_btn")
                    ) {
                        Text("Tümünü Okundu İşaretle", color = StudioAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onClearAll,
                        modifier = Modifier.testTag("clear_all_notifications_btn")
                    ) {
                        Text("Tüm Geçmişi Sil", color = Color.Red.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // List or Empty state
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateCard(
                        title = "Bildirim Bulunmuyor",
                        description = "Şu anda okunmamış bir sistem uyarısı veya sentez tamamlanma raporu bulunmamaktadır.",
                        icon = Icons.Default.NotificationsNone
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications) { notif ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (notif.read) Color.White.copy(alpha = 0.02f) else StudioAccent.copy(alpha = 0.08f)
                                )
                                .border(
                                    BorderStroke(
                                        0.5.dp,
                                        if (notif.read) Color.White.copy(alpha = 0.05f) else StudioAccent.copy(alpha = 0.3f)
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { if (!notif.read) onMarkRead(notif.id) }
                                .padding(12.dp)
                                .testTag("notification_item_${notif.id}")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                val iconTint = when (notif.type) {
                                    "SUCCESS" -> Color(0xFF4CAF50)
                                    "ERROR" -> Color.Red
                                    else -> StudioPrimary
                                }
                                val icon = when (notif.type) {
                                    "SUCCESS" -> Icons.Default.CheckCircle
                                    "ERROR" -> Icons.Default.Warning
                                    else -> Icons.Default.Info
                                }

                                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        color = SubText,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }

                                if (!notif.read) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(StudioAccent)
                                            .align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// DECORATIVE / REUSABLE EMPTY STATE CARD
// ==========================================
@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SubText.copy(alpha = 0.45f),
            modifier = Modifier.size(52.dp)
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            color = SubText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}
