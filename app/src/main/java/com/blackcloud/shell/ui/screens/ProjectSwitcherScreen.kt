package com.blackcloud.shell.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcloud.shell.data.model.Project
import com.blackcloud.shell.ui.theme.BorderColor
import com.blackcloud.shell.ui.theme.DarkSurface
import com.blackcloud.shell.ui.theme.DarkSurfaceVariant
import com.blackcloud.shell.ui.theme.TextPrimary
import com.blackcloud.shell.ui.theme.ThemeType
import com.blackcloud.shell.ui.theme.TextSecondary
import com.blackcloud.shell.ui.theme.siberMeshBackground
import kotlin.math.sin

/**
 * PROJECT THEIA BLACKCLOUD Proje Seçim Ekranı.
 * Kullanılabilir tüm bilişsel ikiz projelerini yatay kartlar halinde listeler
 * ve kullanıcının odaklanacağı çalışma alanını seçmesini sağlar.
 * Tasarımı tamamen NEURAL_STUDIO siber arayüzü ile güncellenmiştir.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectSwitcherScreen(
    projects: List<Project>,
    onProjectSelected: (Project) -> Unit,
    onGeneralChatSelected: () -> Unit,
    selectedTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .siberMeshBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 1. TOP APP BAR (Siber Üst Panel)
            // Header: #10131a opacity, blurred background effect with solid padding. Base height 64dp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color(0x6610131A))
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sol Bölüm: Kullanıcı Profil görsel çerçevesi & Başlık
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        // Holografik profil ikonu temsilcisi
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Mimar Portresi",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = "NEURAL_STUDIO",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }

                // Sağ Bölüm: Bildirim İkonu
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Mevcut sinaps bildirimleri güncel.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Ana İçerik Gövdesi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                
                // 2. WELCOME SECTION (Karşılama Ekranı)
                Text(
                    text = "Geri geldin, Mimar",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 44.sp
                    )
                )
                Text(
                    text = "Sinaps düğümleriniz 4 paralel kümede aktif çalışıyor.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 3. RECENT PROJECTS HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Recent Projects",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    
                    Row(
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Tüm siber projeler listelendi.", Toast.LENGTH_SHORT).show()
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "ForwardArrow",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. RECENT PROJECTS HORIZONTAL SCROLL
                if (projects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Bilgi",
                                tint = TextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aktif Proje Bulunamadı",
                                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                            )
                            Text(
                                text = "Lütfen projelerinizin yüklü olduğundan emin olun.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .testTag("projects_horizontal_list"),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(projects) { index, project ->
                            SiberProjectCard(
                                project = project,
                                index = index,
                                onClick = { onProjectSelected(project) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. GENERAL CHAT QUICK ACTION (Hızlı Genel Sohbet Başlatıcı)
                // Preserving required general_chat_quick_action and logic
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .clickable { onGeneralChatSelected() }
                        .testTag("general_chat_quick_action"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AKTİF KABUK SOHBETİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Genel Sohbete Başla",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Proje bağlamı olmadan Theia kabuğuyla doğrudan konuşun ve sohbete sonradan proje ekleyin.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Sohbete Başla",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 6. RESOURCE UTILIZATION SECTION (Kaynak Kullanımı)
                Text(
                    text = "Resource Utilization",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Gri/recessed kart yapısı (ve mobil dikey istifleme)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stat Card 1
                    SiberStatCard(
                        title = "Token Consumption",
                        value = "12.4M",
                        limit = "/ 50M",
                        trendText = "12% from last cycle",
                        trendIcon = Icons.Default.TrendingDown,
                        trendColor = Color(0xFF65DD96)
                    )

                    // Stat Card 2
                    SiberStatCard(
                        title = "Compute Load",
                        value = "68.2",
                        limit = "TFLOPS",
                        trendText = "Peak usage detected",
                        trendIcon = Icons.Default.TrendingUp,
                        trendColor = Color(0xFFEE4444)
                    )

                    // Stat Card 3
                    SiberStatCard(
                        title = "Active Instances",
                        value = "14",
                        limit = "Nodes",
                        trendText = "All systems nominal",
                        trendIcon = Icons.Default.CheckCircle,
                        trendColor = Color(0xFF65DD96)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 7. BENTO LAYOUT INSIGHT & TOPOLOGY SECTION (Bento Bilgileri & Canvas)
                Text(
                    text = "System Diagnostics",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bento 1: Sistem Sağlığı Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
                    border = BorderStroke(1.dp, Color(0x1BFFFFFF))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "System Health",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cluster 'Alpha-7' is exhibiting optimal neural plasticity. No retraining required for the next 48 hours based on current drift analysis.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                Toast.makeText(context, "Sapma analizi gerçekleştiriliyor... Sistem optimal durumda.", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(30.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "Analyze Drift",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bento 2: Nöral Topoloji Görselleştirici (Canvas)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0E14)),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Neural Topology Visualizer",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Real-time mapping of data flow across nodes.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        // Canlı siber nöral ağları çizen Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            val w = size.width
                            val h = size.height

                            // Düğüm Koordinatları (Cyber Nodes)
                            val nodePoints = listOf(
                                Pair(w * 0.15f, h * 0.5f),
                                Pair(w * 0.4f, h * 0.25f),
                                Pair(w * 0.4f, h * 0.75f),
                                Pair(w * 0.7f, h * 0.35f),
                                Pair(w * 0.7f, h * 0.65f),
                                Pair(w * 0.9f, h * 0.5f)
                            )

                            // 1. Fiber optik bağlantı çizgilerini çiz (Bezier curve representation)
                            val linePath = Path()
                            nodePoints.forEachIndexed { i, fromNode ->
                                nodePoints.forEachIndexed { j, toNode ->
                                    if (i < j && (j - i <= 2)) {
                                        linePath.reset()
                                        linePath.moveTo(fromNode.first, fromNode.second)
                                        linePath.cubicTo(
                                            (fromNode.first + toNode.first) / 2, fromNode.second,
                                            (fromNode.first + toNode.first) / 2, toNode.second,
                                            toNode.first, toNode.second
                                        )
                                        drawPath(
                                            path = linePath,
                                            color = Color(0xFFadc6ff).copy(alpha = 0.15f),
                                            style = Stroke(width = 1.5.dp.toPx())
                                        )
                                    }
                                }
                            }

                            // 2. Glowing Nodes
                            nodePoints.forEachIndexed { idx, point ->
                                val nodeColor = when (idx) {
                                    1 -> Color(0xFF65DD96) // NLP Mint
                                    3 -> Color(0xFFD946EF) // Lavender Purple
                                    else -> Color(0xFFadc6ff) // Siber Mavi
                                }
                                
                                // Node Aura glow
                                drawCircle(
                                    color = nodeColor.copy(alpha = 0.2f),
                                    radius = 12.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                                )
                                // Node Core solid
                                drawCircle(
                                    color = nodeColor,
                                    radius = 4.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

/**
 * HTML bento tasarımına uygun kaynak kullanımı stat kart bileşeni.
 */
@Composable
fun SiberStatCard(
    title: String,
    value: String,
    limit: String,
    trendText: String,
    trendIcon: androidx.compose.ui.graphics.vector.ImageVector,
    trendColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x11000000)),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = when (title) {
                        "Token Consumption" -> Icons.Default.Toll
                        "Compute Load" -> Icons.Default.Memory
                        else -> Icons.Default.Hub
                    },
                    contentDescription = null,
                    tint = trendColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = trendColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = limit,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = trendIcon,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = trendText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = trendColor,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

/**
 * Yatay kaydırılan listedeki tek projenin siber tasarımlı kart bileşeni.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SiberProjectCard(
    project: Project,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 3 farklı stile göre eşleştirme (HTML'deki Card 1, Card 2, Card 3 gibi)
    val cardThemeColor = when (index % 3) {
        0 -> Color(0xFF3B82F6) // Deep_Synthesis_v4 Mavi
        1 -> Color(0xFF65DD96) // Linguist_Mainframe Mint yeşili
        else -> Color(0xFFD946EF) // Vision_Oracle_X Mor
    }

    val illustrativeIcon = when (index % 3) {
        0 -> Icons.Default.Psychology
        1 -> Icons.Default.Translate
        else -> Icons.Default.Visibility
    }

    val rightDecorativeIcon = when (index % 3) {
        0 -> Icons.Default.AutoAwesome
        1 -> Icons.Default.Language
        else -> Icons.Default.CompassCalibration
    }

    val subtitleLabel = when (index % 3) {
        0 -> "Active Training • 84%"
        1 -> "Ready • 2.4B Params"
        else -> "Optimizing • Latency: 12ms"
    }

    val progressValue = when (index % 3) {
        0 -> 0.84f
        1 -> 1.0f
        else -> 0.45f
    }

    Card(
        modifier = modifier
            .width(260.dp)
            .height(230.dp)
            .testTag("project_card_${project.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
        border = BorderStroke(1.dp, Color(0x1BFFFFFF))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Elegant top edge gradient stroke accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                cardThemeColor.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Right top giant decorative transparent icon
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = rightDecorativeIcon,
                    contentDescription = null,
                    tint = cardThemeColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(56.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Icon and Titles
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(cardThemeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = illustrativeIcon,
                            contentDescription = null,
                            tint = cardThemeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtitleLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Bottom section: Progress indicator & Tags
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (progressValue < 1.0f) {
                        LinearProgressIndicator(
                            progress = { progressValue },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = cardThemeColor,
                            trackColor = Color(0x11FFFFFF)
                        )
                    } else {
                        // Tag pills (NLP and PRODUCTION tags)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFF65DD96).copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                    .background(Color(0xFF65DD96).copy(alpha = 0.08f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("NLP", color = Color(0xFF65DD96), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFF65DD96).copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                    .background(Color(0xFF65DD96).copy(alpha = 0.08f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PRODUCTION", color = Color(0xFF65DD96), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
