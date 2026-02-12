package com.example.navya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun MarketPlantCard(
        plant: PlantEntity,
        quantity: Int = 0,
        onAddToCart: (PlantEntity) -> Unit,
        onIncrement: (PlantEntity) -> Unit = {},
        onDecrement: (PlantEntity) -> Unit = {},
        onClick: (PlantEntity) -> Unit,
        modifier: Modifier = Modifier
) {
        ElevatedCard(
                onClick = { onClick(plant) },
                modifier = modifier.fillMaxWidth().padding(8.dp),
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
                                                .aspectRatio(0.8f)
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                )
                        ) {
                                SubcomposeAsyncImage(
                                        model =
                                                ImageRequest.Builder(LocalContext.current)
                                                        .data(plant.image_url)
                                                        .crossfade(true)
                                                        .build(),
                                        contentDescription = plant.name,
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .background(
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant
                                                        ),
                                        contentScale = ContentScale.Crop,
                                        loading = {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxSize()
                                                                        .shimmerEffect()
                                                )
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
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                        }
                                )
                        }

                        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
                                Text(
                                        text = plant.name,
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                        text = plant.species ?: "Unknown Species",
                                        style =
                                                MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 14.sp
                                                ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                        text = "₹${plant.price?.toInt() ?: 0}",
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Spacer(modifier = Modifier.height(12.dp))

                                if (quantity > 0) {
                                        androidx.compose.foundation.layout.Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement =
                                                        androidx.compose.foundation.layout
                                                                .Arrangement.spacedBy(8.dp),
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .height(40.dp)
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer,
                                                                        RoundedCornerShape(20.dp)
                                                                )
                                                                .padding(horizontal = 4.dp)
                                        ) {
                                                androidx.compose.material3.IconButton(
                                                        onClick = { onDecrement(plant) },
                                                        modifier = Modifier.size(32.dp)
                                                ) {
                                                        androidx.compose.material3.Icon(
                                                                imageVector =
                                                                        androidx.compose.material
                                                                                .icons.Icons.Default
                                                                                .Remove,
                                                                contentDescription = "Remove",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimaryContainer,
                                                                modifier = Modifier.size(16.dp)
                                                        )
                                                }

                                                Text(
                                                        text = quantity.toString(),
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onPrimaryContainer,
                                                        modifier = Modifier.weight(1f),
                                                        textAlign =
                                                                androidx.compose.ui.text.style
                                                                        .TextAlign.Center
                                                )

                                                androidx.compose.material3.IconButton(
                                                        onClick = { onIncrement(plant) },
                                                        modifier = Modifier.size(32.dp)
                                                ) {
                                                        androidx.compose.material3.Icon(
                                                                imageVector =
                                                                        androidx.compose.material
                                                                                .icons.Icons.Default
                                                                                .Add,
                                                                contentDescription = "Add",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimaryContainer,
                                                                modifier = Modifier.size(16.dp)
                                                        )
                                                }
                                        }
                                } else {
                                        Button(
                                                onClick = { onAddToCart(plant) },
                                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                contentColor =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimary
                                                        ),
                                                shape = RoundedCornerShape(20.dp)
                                        ) {
                                                Text(
                                                        text = "Add to cart",
                                                        style =
                                                                MaterialTheme.typography.labelLarge
                                                                        .copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        )
                                                )
                                        }
                                }
                        }
                }
        }
}
