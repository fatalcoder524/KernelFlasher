package com.github.capntrips.kernelflasher.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.view.HapticFeedbackConstants
import com.github.capntrips.kernelflasher.R

@Composable
fun ViewButton(
    onClick: () -> Unit
) {
    val view = LocalView.current
    FilledTonalButton(
        modifier = Modifier.heightIn(min = 36.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            onClick()
        }
    ) {
        Text(
            stringResource(R.string.view),
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
