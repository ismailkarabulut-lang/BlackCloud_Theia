package com.blackcloud.shell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcloud.shell.data.model.ModelManager
import com.blackcloud.shell.data.model.ModelProvider
import com.blackcloud.shell.ui.viewmodel.BlackCloudViewModel

@Composable
fun ModelsMainframeScreen(
    viewModel: BlackCloudViewModel,
    modifier: Modifier = Modifier
) {
    var activeModel by remember { mutableStateOf(ModelManager.getActiveModel()) }
    val models = ModelManager.getAllModels()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
            .padding(16.dp)
    ) {
        Text(
            text = "NEURAL_STUDIO",
            color = Color(0xFF4FC3F7),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = "Model Mainframe",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111120)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AKTİF MODEL",
                    color = Color(0xFF4FC3F7),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = activeModel.displayName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = activeModel.description,
                    color = Color(0xFF888888),
                    fontSize = 13.sp
                )
            }
        }

        Text(
            text = "YÜKLÜ SİBER MODELLER",
            color = Color(0xFF888888),
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(models) { model ->
                ModelCard(
                    model = model,
                    isActive = model == activeModel,
                    onSelect = {
                        ModelManager.setActiveModel(model)
                        activeModel = model
                    }
                )
            }
        }
    }
}

@Composable
fun ModelCard(
    model: ModelProvider,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isActive) Color(0xFF4FC3F7) else Color(0xFF222233)
    val statusColor = when {
        isActive -> Color(0xFF4FC3F7)
        model.isFree -> Color(0xFF4CAF50)
        else -> Color(0xFFFF9800)
    }
    val statusText = when {
        isActive -> "AKTİF"
        model.isFree -> "HAZIR"
        else -> "API KEY GEREKLİ"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) Color(0xFF0D1B2A) else Color(0xFF111120))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.displayName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
            Text(
                text = model.description,
                color = Color(0xFF888888),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (isActive) {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF4FC3F7),
                    trackColor = Color(0xFF222233)
                )
            }
        }
    }
}
