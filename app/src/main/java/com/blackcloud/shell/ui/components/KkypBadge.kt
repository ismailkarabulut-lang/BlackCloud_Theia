// === app/src/main/java/com/blackcloud/shell/ui/components/KkypBadge.kt ===
package com.blackcloud.shell.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcloud.shell.ui.theme.CyberGreen
import com.blackcloud.shell.ui.theme.WarningOrange

/**
 * KKYP (Kişisel Bilgi Güvenliği) Doğrulama Gösterge Rozeti.
 * Güvenlik/Veri bütünlüğü doğrulandığında yeşil tik, aksi durumda turuncu uyarı bandını görüntüler.
 */
@Composable
fun KkypBadge(
    verified: Boolean,
    issues: List<String>,
    modifier: Modifier = Modifier
) {
    if (verified && issues.isEmpty()) {
        // Doğrulanmış asistan mesajının yanındaki küçük onay rozeti
        Box(
            modifier = modifier
                .testTag("kkyp_verified_badge")
                .padding(start = 4.dp, top = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "KKYP Doğrulandı",
                tint = CyberGreen,
                modifier = Modifier.size(16.dp)
            )
        }
    } else {
        // Doğrulanmamış veya sorunlu (issues) mesaj altındaki turuncu uyarı bandı
        val displayIssues = if (issues.isEmpty()) listOf("Güvenli kaynak doğrulaması tamamlanamadı.") else issues
        
        Column(
            modifier = modifier
                .fillMaxWidth()
                .testTag("kkyp_warning_band")
                .padding(top = 8.dp)
                .background(WarningOrange.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .border(1.dp, WarningOrange.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Güvenlik Uyarısı",
                    tint = WarningOrange,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "KKYP GÜVENLİK/BÜTÜNLÜK NOTU",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = WarningOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
            
            Column(modifier = Modifier.padding(start = 26.dp, top = 6.dp)) {
                displayIssues.forEach { issue ->
                    Text(
                        text = "• $issue",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WarningOrange.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
