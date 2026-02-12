package com.example.navya.ui.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PlantDetailReminderControls(
        isEnabled: Boolean,
        intervalDays: Int,
        onToggleReminder: (Boolean) -> Unit,
        onIntervalChange: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(40.dp)
                                                        .background(Color(0xFFE3F2FD), CircleShape),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                imageVector = Icons.Outlined.Notifications,
                                                contentDescription = "Reminder",
                                                tint = Color(0xFF1976D2)
                                        )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = "Watering Reminder",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                        )
                                        if (isEnabled) {
                                                Text(
                                                        text = "Active",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFF1976D2)
                                                )
                                        }
                                }

                                Switch(
                                        checked = isEnabled,
                                        onCheckedChange = onToggleReminder,
                                        colors =
                                                SwitchDefaults.colors(
                                                        checkedThumbColor = Color.White,
                                                        checkedTrackColor = Color(0xFF1976D2)
                                                ),
                                        modifier =
                                                Modifier.semantics {
                                                        contentDescription = "Toggle water reminder"
                                                }
                                )
                        }

                        AnimatedVisibility(
                                visible = isEnabled,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                        ) {
                                Column(modifier = Modifier.padding(top = 16.dp)) {
                                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                                text = "Frequency",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                Text(
                                                        text = "Every $intervalDays days",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                )

                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        IconButton(
                                                                onClick = {
                                                                        if (intervalDays > 1)
                                                                                onIntervalChange(
                                                                                        intervalDays -
                                                                                                1
                                                                                )
                                                                },
                                                                modifier =
                                                                        Modifier.size(36.dp)
                                                                                .background(
                                                                                        Color(
                                                                                                0xFFF5F5F5
                                                                                        ),
                                                                                        CircleShape
                                                                                )
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Remove,
                                                                        contentDescription =
                                                                                "Decrease",
                                                                        tint = Color.Gray
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.width(16.dp))

                                                        IconButton(
                                                                onClick = {
                                                                        onIntervalChange(
                                                                                intervalDays + 1
                                                                        )
                                                                },
                                                                modifier =
                                                                        Modifier.size(36.dp)
                                                                                .background(
                                                                                        Color(
                                                                                                0xFFE3F2FD
                                                                                        ),
                                                                                        CircleShape
                                                                                )
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Add,
                                                                        contentDescription =
                                                                                "Increase",
                                                                        tint = Color(0xFF1976D2)
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}
