package com.example.navya.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NavyaTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        isError: Boolean = false,
        errorMessage: String? = null,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        singleLine: Boolean = true,
        readOnly: Boolean = false
) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()

        val borderColor by
                animateColorAsState(
                        targetValue =
                                when {
                                        isError -> MaterialTheme.colorScheme.error
                                        isFocused -> MaterialTheme.colorScheme.primary
                                        else -> Color.Gray.copy(alpha = 0.3f)
                                },
                        animationSpec = tween(durationMillis = 300),
                        label = "borderColor"
                )

        val borderWidth by
                animateDpAsState(
                        targetValue = if (isFocused) 2.dp else 1.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "borderWidth"
                )

        val backgroundColor by
                animateColorAsState(
                        targetValue =
                                if (isFocused) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                else Color.Transparent,
                        animationSpec = tween(durationMillis = 300),
                        label = "backgroundColor"
                )

        Column(modifier = modifier) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(backgroundColor, RoundedCornerShape(12.dp))
                                        .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                        Column {
                                val labelColor by
                                        animateColorAsState(
                                                targetValue =
                                                        if (isFocused)
                                                                MaterialTheme.colorScheme.primary
                                                        else Color.Gray,
                                                label = "labelColor"
                                        )

                                if (value.isNotEmpty() || isFocused) {
                                        Text(
                                                text = label,
                                                style =
                                                        TextStyle(
                                                                fontSize = 12.sp,
                                                                color = labelColor,
                                                                fontWeight = FontWeight.Medium
                                                        ),
                                                modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                }

                                androidx.compose.foundation.layout.Row(
                                        verticalAlignment =
                                                androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                        if (leadingIcon != null) {
                                                leadingIcon()
                                                androidx.compose.foundation.layout.Spacer(
                                                        modifier = Modifier.padding(end = 12.dp)
                                                )
                                        }

                                        Box(modifier = Modifier.weight(1f)) {
                                                if (value.isEmpty() && !isFocused) {
                                                        Text(
                                                                text = label,
                                                                style =
                                                                        TextStyle(
                                                                                fontSize = 16.sp,
                                                                                color =
                                                                                        Color.Gray
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.6f
                                                                                                )
                                                                        )
                                                        )
                                                }

                                                BasicTextField(
                                                        value = value,
                                                        onValueChange = onValueChange,
                                                        textStyle =
                                                                TextStyle(
                                                                        fontSize = 16.sp,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurface,
                                                                        fontWeight =
                                                                                FontWeight.Normal
                                                                ),
                                                        singleLine = singleLine,
                                                        readOnly = readOnly,
                                                        visualTransformation = visualTransformation,
                                                        keyboardOptions = keyboardOptions,
                                                        keyboardActions = keyboardActions,
                                                        interactionSource = interactionSource,
                                                        cursorBrush =
                                                                SolidColor(
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                                ),
                                                        modifier = Modifier.fillMaxWidth()
                                                )
                                        }

                                        if (trailingIcon != null) {
                                                trailingIcon()
                                        }
                                }
                        }
                }

                if (isError && errorMessage != null) {
                        Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                }
        }
}
