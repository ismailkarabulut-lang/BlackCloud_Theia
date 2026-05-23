package com.blackcloud.shell.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcloud.shell.ui.theme.BorderColor
import com.blackcloud.shell.ui.theme.DarkSurface
import com.blackcloud.shell.ui.theme.DarkSurfaceVariant
import com.blackcloud.shell.ui.theme.TextPrimary
import com.blackcloud.shell.ui.theme.ThemeType
import com.blackcloud.shell.ui.theme.TextSecondary
import com.blackcloud.shell.ui.theme.siberMeshBackground
import com.blackcloud.shell.ui.viewmodel.BlackCloudViewModel

@Composable
fun ConfigConsoleScreen(
    viewModel: BlackCloudViewModel,
    selectedTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Ses motoru yerel durumları (Örnek gerçek toggle)
    var sttEnabled by remember { mutableStateOf(true) }
    var ttsEnabled by remember { mutableStateOf(true) }
    var autoSync by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .siberMeshBackground()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Üst Başlık Bilişsel Bilgi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Config",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "NEURAL_STUDIO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Sistem Yapılandırması",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. TEMA SEÇİCİ (VISUAL SKIN SELECTOR)
            ThemeSelectorPanel(
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. SES MOTORU AYARLARI
            Text(
                text = "KABUK SES MOTORLARI",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // STT Toggle
            ConfigToggleItem(
                title = "Konuşmadan Metne (STT) Aktif",
                description = "Mikrofon ses girişi siber ortamda analiz edilir ve komut olarak yönlendirilir.",
                checked = sttEnabled,
                onCheckedChange = {
                    sttEnabled = it
                    Toast.makeText(context, "STT " + (if (it) "aktif" else "pasif") + " edildi.", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TTS Toggle
            ConfigToggleItem(
                title = "Metinden Konuşmaya (TTS) Aktif",
                description = "Asistanın ürettiği siber mesajlar yerel hoparlör tts motoruyla seslendirilir.",
                checked = ttsEnabled,
                onCheckedChange = {
                    ttsEnabled = it
                    Toast.makeText(context, "TTS " + (if (it) "aktif" else "pasif") + " edildi.", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. BAĞLANTI & SUNUCU TELEMETRİSİ
            Text(
                text = "SUNUCU BAĞLANTISI VE TELEMETRİ",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x0FFFFFFF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("FastAPI Sunucu Bağlantısı", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                            Text("Theia BLACKCLOUD siber ağ geçidi.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                        
                        Button(
                            onClick = {
                                viewModel.triggerManualReconnect()
                                Toast.makeText(context, "Bağlantı pinglemesi yeniden tetiklendi.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("TEST PING", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF000000).copy(alpha = 0.2f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Ağ Gecikmesi", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 9.sp))
                                Text("3ms (Stabil)", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF65DD96), fontWeight = FontWeight.Bold))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF000000).copy(alpha = 0.2f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Port Ağ Geçidi", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 9.sp))
                                Text("8000 / FastAPI", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Otomatik Veri Eşitleme
            ConfigToggleItem(
                title = "Otomatik Arka Plan Senkronizasyonu",
                description = "Proje değişiklikleri, Termux Python çekirdeği ile anında eşitlenir.",
                checked = autoSync,
                onCheckedChange = {
                    autoSync = it
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ConfigToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x06FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp, lineHeight = 14.sp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = Color(0x1AFFFFFF)
                )
            )
        }
    }
}

@Composable
fun ThemeSelectorPanel(
    selectedTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "KABUK TELEMETRİ / VISUAL SKIN SELECTOR",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeType.values().forEach { theme ->
                val isSelected = theme == selectedTheme
                val themeColor = when (theme) {
                    ThemeType.ELEGANT_DARK -> Color(0xFF3B82F6)
                    ThemeType.CYBERPUNK_NEON -> Color(0xFFD946EF)
                    ThemeType.COSMIC_OLED -> Color(0xFF06B6D4)
                    ThemeType.SLATE_GRAY -> Color(0xFFF97316)
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DarkSurfaceVariant else DarkSurface)
                        .border(
                            1.dp,
                            if (isSelected) themeColor else BorderColor,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onThemeSelected(theme) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(themeColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = theme.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = theme.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = themeColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
