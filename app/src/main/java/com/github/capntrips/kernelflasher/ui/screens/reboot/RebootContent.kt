package com.github.capntrips.kernelflasher.ui.screens.reboot

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.ui.components.ActionTile
import com.github.capntrips.kernelflasher.ui.components.TileColors

@Suppress("UnusedReceiverParameter")
@Composable
fun ColumnScope.RebootContent(
    viewModel: RebootViewModel,
    @Suppress("UNUSED_PARAMETER") ignoredNavController: NavController
) {
    ActionTile(
        text = stringResource(R.string.reboot),
        icon = Icons.Outlined.RestartAlt,
        accent = TileColors.Blue,
        onClick = { viewModel.rebootSystem() }
    )
    ActionTile(
        text = stringResource(R.string.reboot_recovery),
        icon = Icons.Outlined.Build,
        accent = TileColors.Amber,
        onClick = { viewModel.rebootRecovery() }
    )
    ActionTile(
        text = stringResource(R.string.reboot_bootloader),
        icon = Icons.Outlined.Code,
        accent = TileColors.Violet,
        onClick = { viewModel.rebootBootloader() }
    )
    ActionTile(
        text = stringResource(R.string.reboot_download),
        icon = Icons.Outlined.Download,
        accent = TileColors.Cyan,
        onClick = { viewModel.rebootDownload() }
    )
    ActionTile(
        text = stringResource(R.string.reboot_edl),
        icon = Icons.Outlined.Usb,
        accent = TileColors.Rose,
        onClick = { viewModel.rebootEdl() }
    )
}
