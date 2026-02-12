package com.example.navya.ui.screens.details

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.local.entity.ReminderEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun OwnedPlantDashboard(
        reminder: ReminderEntity?,
        plant: PlantEntity,
        onWaterNow: () -> Unit,
        onArClick: () -> Unit,
        onToggleReminder: (Boolean) -> Unit,
        onIntervalChange: (Int) -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        Icons.Outlined.WaterDrop,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                        "Care Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val nextDue =
                    remember(reminder?.next_reminder_at) {
                        formatNextWaterDate(reminder?.next_reminder_at)
                    }
            Text(
                    "Next Water",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Text(
                    nextDue,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                        onClick = onWaterNow,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                ),
                        shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.WaterDrop, null,
                        tint = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Water Now",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                        onClick = onArClick,
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(0.dp)
                ) { Icon(Icons.Default.ViewInAr, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(24.dp))

            PlantDetailReminderControls(
                    isEnabled = reminder != null,
                    intervalDays = reminder?.interval_days ?: plant.water_interval_days ?: 7,
                    onToggleReminder = onToggleReminder,
                    onIntervalChange = onIntervalChange,
                    modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun formatNextWaterDate(timestamp: Long?): String {
    if (timestamp == null) return "Set Reminder"
    val now = System.currentTimeMillis()
    val diff = timestamp - now
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        diff < 0 -> "Overdue!"
        days < 1 -> "Today"
        days < 2 -> "Tomorrow"
        else -> {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
