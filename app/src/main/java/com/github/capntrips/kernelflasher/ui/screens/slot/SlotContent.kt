package com.github.capntrips.kernelflasher.ui.screens.slot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LinkOff
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.ui.components.ActionTile
import com.github.capntrips.kernelflasher.ui.components.SlotCard
import com.github.capntrips.kernelflasher.ui.components.TileColors

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalUnitApi
@Composable
fun ColumnScope.SlotContent(
    viewModel: SlotViewModel,
    slotSuffix: String,
    navController: NavController
) {
    val context = LocalContext.current
    SlotCard(
        title = stringResource(if (slotSuffix == "_a") R.string.slot_a else if (slotSuffix == "_b") R.string.slot_b else R.string.slot),
        viewModel = viewModel,
        navController = navController,
        isSlotScreen = true
    )
    AnimatedVisibility(!viewModel.isRefreshing.value) {
        Column {
            Spacer(Modifier.height(12.dp))
            ActionTile(
                text = stringResource(R.string.flash),
                icon = Icons.Outlined.FlashOn,
                accent = TileColors.Amber,
                showChevron = true,
                onClick = { navController.navigate("slot$slotSuffix/flash") }
            )
            ActionTile(
                text = stringResource(R.string.backup),
                icon = Icons.Outlined.Save,
                accent = TileColors.Green,
                showChevron = true,
                onClick = {
                    viewModel.clearFlash(context)
                    navController.navigate("slot$slotSuffix/backup")
                }
            )
            ActionTile(
                text = stringResource(R.string.restore),
                icon = Icons.Outlined.SettingsBackupRestore,
                accent = TileColors.Violet,
                showChevron = true,
                onClick = { navController.navigate("slot$slotSuffix/backups") }
            )
            if (viewModel.hasVendorDlkm) {
                AnimatedVisibility(!viewModel.isRefreshing.value) {
                    AnimatedVisibility(viewModel.isVendorDlkmMounted) {
                        ActionTile(
                            text = stringResource(R.string.unmount_vendor_dlkm),
                            icon = Icons.Outlined.LinkOff,
                            accent = TileColors.Rose,
                            onClick = { viewModel.unmountVendorDlkm(context) }
                        )
                    }
                    AnimatedVisibility(!viewModel.isVendorDlkmMounted && viewModel.isVendorDlkmMapped) {
                        Column {
                            ActionTile(
                                text = stringResource(R.string.mount_vendor_dlkm),
                                icon = Icons.Outlined.Link,
                                accent = TileColors.Cyan,
                                onClick = { viewModel.mountVendorDlkm(context) }
                            )
                            ActionTile(
                                text = stringResource(R.string.unmap_vendor_dlkm),
                                icon = Icons.Outlined.LinkOff,
                                accent = TileColors.Amber,
                                onClick = { viewModel.unmapVendorDlkm(context) }
                            )
                        }
                    }
                    AnimatedVisibility(!viewModel.isVendorDlkmMounted && !viewModel.isVendorDlkmMapped) {
                        ActionTile(
                            text = stringResource(R.string.map_vendor_dlkm),
                            icon = Icons.Outlined.Storage,
                            accent = TileColors.Cyan,
                            onClick = { viewModel.mapVendorDlkm(context) }
                        )
                    }
                }
            }
        }
    }
}
