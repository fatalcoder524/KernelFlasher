package com.github.capntrips.kernelflasher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.ui.screens.slot.SlotViewModel

@ExperimentalMaterial3Api
@Composable
fun SlotCard(
    title: String,
    viewModel: SlotViewModel,
    navController: NavController,
    isSlotScreen: Boolean = false,
    showDlkm: Boolean = true,
) {
    DataCard (
        title = "$title ${if(viewModel.isActive && viewModel.slotSuffix !="") "[${stringResource(R.string.active)}]" else ""}",
        button = {
            if (!isSlotScreen) {
                AnimatedVisibility(!viewModel.isRefreshing.value) {
                    ViewButton {
                        navController.navigate("slot${viewModel.slotSuffix}")
                    }
                }
            }
        }
    ) {
        val cardWidth = remember { mutableIntStateOf(0) }
        if (!viewModel.sha1.isNullOrEmpty()) {
            DataRow(
                label = stringResource(R.string.boot_sha1),
                value = viewModel.sha1!!.substring(0, 8),
                valueStyle = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                mutableMaxWidth = cardWidth
            )
        }
        AnimatedVisibility(!viewModel.isRefreshing.value && viewModel.slotInfo.bootImgInfo.kernelVersion != null) {
            DataRow(
                label = stringResource(R.string.kernel_version),
                value = viewModel.slotInfo.bootImgInfo.kernelVersion ?: "",
                mutableMaxWidth = cardWidth,
                clickable = true
            )
        }
        if (showDlkm && viewModel.hasVendorDlkm) {
            var vendorDlkmValue = stringResource(R.string.not_found)
            if (viewModel.isVendorDlkmMapped) {
                vendorDlkmValue = if (viewModel.isVendorDlkmMounted) {
                    String.format("%s, %s", stringResource(R.string.exists), stringResource(R.string.mounted))
                } else {
                    String.format("%s, %s", stringResource(R.string.exists), stringResource(R.string.unmounted))
                }
            }
            DataRow(stringResource(R.string.vendor_dlkm), vendorDlkmValue, mutableMaxWidth = cardWidth)
        }
        DataRow(
            label = stringResource(R.string.boot_fmt),
            value = viewModel.slotInfo.bootImgInfo.bootFmt ?: stringResource(R.string.not_found),
            mutableMaxWidth = cardWidth
        )
        DataRow(
            label = if (viewModel.slotInfo.ramdiskInfo.ramdiskLocation == "init_boot.img") stringResource(R.string.init_boot_fmt)
                    else if (viewModel.slotInfo.ramdiskInfo.ramdiskLocation == "vendor_boot.img") stringResource(R.string.vendor_boot_fmt)
                    else stringResource(R.string.ramdisk_fmt),
            value = viewModel.slotInfo.ramdiskInfo.ramdiskFmt ?: stringResource(R.string.not_found),
            mutableMaxWidth = cardWidth
        )
        if(isSlotScreen && viewModel.slotSuffix != "")
        {
            DataRow(
                label = "Unbootable",
                value = viewModel.slotInfo.bootSlotInfo.unbootable ?: stringResource(R.string.not_found),
                mutableMaxWidth = cardWidth,
                valueColor = if (viewModel.slotInfo.bootSlotInfo.unbootable == "Yes") Color.Red else Color.Unspecified
            )
            DataRow(
                label = "Successful",
                value = viewModel.slotInfo.bootSlotInfo.successful ?: stringResource(R.string.not_found),
                mutableMaxWidth = cardWidth,
                valueColor = if (viewModel.slotInfo.bootSlotInfo.successful == "No") Color.Red else Color.Unspecified
            )
        }
        if (!viewModel.isRefreshing.value && viewModel.hasError) {
            Row {
                DataValue(
                    value = viewModel.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall,
                    clickable = true
                )
            }
        }
    }
}
