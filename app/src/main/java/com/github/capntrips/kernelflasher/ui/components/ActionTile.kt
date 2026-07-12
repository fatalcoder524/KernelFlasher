package com.github.capntrips.kernelflasher.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * A small, fixed accent palette for the colored icon badges. These are intentionally
 * hard-coded vivid tones so each action keeps a recognisable colour across light, dark
 * and AMOLED-black themes (dynamic-colour surfaces would wash them out).
 */
object TileColors {
    val Blue = Color(0xFF3B82F6)
    val Green = Color(0xFF10B981)
    val Amber = Color(0xFFF59E0B)
    val Violet = Color(0xFF8B5CF6)
    val Rose = Color(0xFFF43F5E)
    val Cyan = Color(0xFF06B6D4)
}

/**
 * Modern list-item style action row: a rounded card holding a colored circular icon
 * badge, a label, and (for navigation actions) a trailing chevron. Replaces the flat
 * tonal [ActionButton] where a richer, more tactile look is wanted.
 */
@Composable
fun ActionTile(
    text: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showChevron: Boolean = false,
    onClick: () -> Unit
) {
    val view = androidx.compose.ui.platform.LocalView.current
    Surface(
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            onClick()
        },
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .heightIn(min = 64.dp)
            .alpha(if (enabled) 1f else 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            if (showChevron) {
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
