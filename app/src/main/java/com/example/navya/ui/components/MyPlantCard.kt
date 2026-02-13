package com.example.navya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.navya.data.local.entity.PlantEntity

@Composable
fun MyPlantCard(plant: PlantEntity, onClick: (PlantEntity) -> Unit, modifier: Modifier = Modifier) {
        ElevatedCard(
                onClick = { onClick(plant) },
                modifier = modifier.fillMaxWidth(),
                colors =
                        CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                        ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
        ) {
                Column {
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .aspectRatio(1f)
                                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                                SubcomposeAsyncImage(
                                        model =
                                                ImageRequest.Builder(LocalContext.current)
                                                        .data(plant.image_url)
                                                        .crossfade(true)
                                                        .build(),
                                        contentDescription = plant.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        loading = {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxSize()
                                                                        .background(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        CircularProgressIndicator(
                                                                modifier = Modifier.size(24.dp),
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                strokeWidth = 2.dp
                                                        )
                                                }
                                        },
                                        error = {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxSize()
                                                                        .background(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Text(
                                                                "No Image",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                        }
                                )
                        }
                        Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                        text = plant.name,
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                        text = plant.species ?: "Unknown",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                        onClick = { onClick(plant) },
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme
                                                                        .primaryContainer,
                                                        contentColor =
                                                                MaterialTheme.colorScheme
                                                                        .onPrimaryContainer
                                                ),
                                        shape = RoundedCornerShape(50),
                                        contentPadding = PaddingValues(0.dp)
                                ) {
                                        Text(
                                                text = "View Care",
                                                style =
                                                        MaterialTheme.typography.labelMedium.copy(
                                                                fontWeight = FontWeight.Bold
                                                        )
                                        )
                                }
                        }
                }
        }
}
