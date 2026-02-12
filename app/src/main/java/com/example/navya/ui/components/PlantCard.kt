package com.example.navya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.navya.R
import com.example.navya.data.local.entity.PlantEntity

@Composable
fun PlantCard(
        plant: PlantEntity,
        quantity: Int = 0,
        onAddToCart: (PlantEntity) -> Unit,
        onIncrement: (PlantEntity) -> Unit = {},
        onDecrement: (PlantEntity) -> Unit = {},
        onClick: (PlantEntity) -> Unit = {},
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.padding(4.dp).clickable { onClick(plant) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
                Column {
                        AsyncImage(
                                model =
                                        ImageRequest.Builder(
                                                        androidx.compose.ui.platform.LocalContext
                                                                .current
                                                )
                                                .data(plant.image_url)
                                                .crossfade(true)
                                                .placeholder(
                                                        com.example
                                                                .navya
                                                                .R
                                                                .drawable
                                                                .ic_launcher_foreground
                                                )
                                                .error(
                                                        com.example
                                                                .navya
                                                                .R
                                                                .drawable
                                                                .ic_launcher_foreground
                                                )
                                                .build(),
                                contentDescription = plant.name,
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(140.dp)
                                                .clip(
                                                        RoundedCornerShape(
                                                                topStart = 12.dp,
                                                                topEnd = 12.dp
                                                        )
                                                ),
                                contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                        text = plant.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                )
                                Text(
                                        text = "${plant.currency} ${plant.price}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                if (quantity > 0) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                                IconButton(
                                                        onClick = { onDecrement(plant) },
                                                        modifier =
                                                                Modifier.size(32.dp)
                                                                        .background(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .secondaryContainer,
                                                                                RoundedCornerShape(
                                                                                        8.dp
                                                                                )
                                                                        )
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Remove,
                                                                contentDescription = "Remove",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onSecondaryContainer,
                                                                modifier = Modifier.size(16.dp)
                                                        )
                                                }

                                                Text(
                                                        text = quantity.toString(),
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier =
                                                                Modifier.padding(horizontal = 8.dp)
                                                )

                                                IconButton(
                                                        onClick = { onIncrement(plant) },
                                                        modifier =
                                                                Modifier.size(32.dp)
                                                                        .background(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primaryContainer,
                                                                                RoundedCornerShape(
                                                                                        8.dp
                                                                                )
                                                                        )
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Add,
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
                                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary
                                                        ),
                                                contentPadding =
                                                        PaddingValues(
                                                                horizontal = 8.dp,
                                                                vertical = 0.dp
                                                        ),
                                                shape = RoundedCornerShape(8.dp)
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.ShoppingCart,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                        "Add",
                                                        style = MaterialTheme.typography.labelLarge
                                                )
                                        }
                                }
                        }
                }
        }
}
