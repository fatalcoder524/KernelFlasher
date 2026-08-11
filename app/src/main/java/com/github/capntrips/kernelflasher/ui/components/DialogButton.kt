package com.github.capntrips.kernelflasher.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DialogButton(
    buttonText: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(buttonText, maxLines = 1)
    }
}
