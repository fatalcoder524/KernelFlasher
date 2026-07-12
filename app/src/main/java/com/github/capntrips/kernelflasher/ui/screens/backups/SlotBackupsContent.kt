package com.github.capntrips.kernelflasher.ui.screens.backups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.common.PartitionUtil
import com.github.capntrips.kernelflasher.ui.components.ActionTile
import com.github.capntrips.kernelflasher.ui.components.TileColors
import com.github.capntrips.kernelflasher.ui.components.DataCard
import com.github.capntrips.kernelflasher.ui.components.DataRow
import com.github.capntrips.kernelflasher.ui.components.DataSet
import com.github.capntrips.kernelflasher.ui.components.FlashList
import com.github.capntrips.kernelflasher.ui.components.PartitionToggle
import com.github.capntrips.kernelflasher.ui.components.SlotCard
import com.github.capntrips.kernelflasher.ui.components.ViewButton
import com.github.capntrips.kernelflasher.ui.screens.slot.SlotViewModel

@ExperimentalMaterial3Api
@ExperimentalUnitApi
@Composable
fun ColumnScope.SlotBackupsContent(
    slotViewModel: SlotViewModel,
    backupsViewModel: BackupsViewModel,
    slotSuffix: String,
    navController: NavController
) {
    val context = LocalContext.current

    val monoStyle = MaterialTheme.typography.titleSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium
    )
    val currentRoute = navController.currentDestination?.route.orEmpty()

    if (!currentRoute.contains("/backups/{backupId}/restore")) {
        SlotCard(
            title = stringResource(if (slotSuffix == "_a") R.string.slot_a else if (slotSuffix == "_b") R.string.slot_b else R.string.slot),
            viewModel = slotViewModel,
            navController = navController,
            isSlotScreen = true,
            showDlkm = false,
        )
        Spacer(Modifier.height(16.dp))
        if (backupsViewModel.currentBackup != null && backupsViewModel.backups.containsKey(backupsViewModel.currentBackup)) {
            val currentBackup = backupsViewModel.backups.getValue(backupsViewModel.currentBackup!!)
            DataCard(backupsViewModel.currentBackup!!) {
                val cardWidth = remember { mutableIntStateOf(0) }
                DataRow(stringResource(R.string.backup_type), currentBackup.type, mutableMaxWidth = cardWidth)
                DataRow(stringResource(R.string.kernel_version), currentBackup.kernelVersion, mutableMaxWidth = cardWidth, clickable = true)
                if (currentBackup.type == "raw") {
                    currentBackup.bootSha1?.takeIf { it.length >= 8 }?.let { sha1 ->
                        DataRow(
                            label = stringResource(R.string.boot_sha1),
                            value = sha1.substring(0, 8),
                            valueStyle = monoStyle,
                            mutableMaxWidth = cardWidth
                        )
                    }
                    if (currentBackup.hashes != null) {
                        val hashWidth = remember { mutableIntStateOf(0) }
                        DataSet(stringResource(R.string.hashes)) {
                            for (partitionName in PartitionUtil.PartitionNames) {
                                val hash = currentBackup.hashes[partitionName]
                                if (hash != null) {
                                    DataRow(
                                        label = partitionName,
                                        value = hash.takeIf { it.isNotEmpty() && it.length >= 8 }?.substring(0, 8) ?: "Hash not found!",
                                        valueStyle = monoStyle,
                                        mutableMaxWidth = hashWidth
                                    )
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(!slotViewModel.isRefreshing.value) {
                Column {
                    Spacer(Modifier.height(5.dp))
                    if (slotViewModel.isActive) {
                        if (currentBackup.type == "raw") {
                            ActionTile(
                                text = stringResource(R.string.restore),
                                icon = Icons.Outlined.SettingsBackupRestore,
                                accent = TileColors.Violet,
                                showChevron = true,
                                onClick = {
                                    navController.navigate("slot$slotSuffix/backups/${backupsViewModel.currentBackup!!}/restore")
                                }
                            )
                        } else if (currentBackup.type == "ak3") {
                            ActionTile(
                                text = stringResource(R.string.flash),
                                icon = Icons.Outlined.FlashOn,
                                accent = TileColors.Amber,
                                onClick = {
                                    slotViewModel.flashAk3(context, backupsViewModel.currentBackup!!, currentBackup.filename!!)
                                    navController.navigate("slot$slotSuffix/backups/${backupsViewModel.currentBackup!!}/flash/ak3") {
                                        popUpTo("slot$slotSuffix")
                                    }
                                }
                            )
                            ActionTile(
                                text = stringResource(R.string.flash_ak3_zip_mkbootfs),
                                icon = Icons.Outlined.FlashOn,
                                accent = TileColors.Amber,
                                onClick = {
                                    slotViewModel.flashAk3_mkbootfs(context, backupsViewModel.currentBackup!!, currentBackup.filename!!)
                                    navController.navigate("slot$slotSuffix/backups/${backupsViewModel.currentBackup!!}/flash/ak3") {
                                        popUpTo("slot$slotSuffix")
                                    }
                                }
                            )
                        }
                    }
                    ActionTile(
                        text = stringResource(R.string.delete),
                        icon = Icons.Outlined.Delete,
                        accent = TileColors.Rose,
                        onClick = { backupsViewModel.delete(context) { navController.popBackStack() } }
                    )
                }
            }
        } else {
            DataCard(stringResource(R.string.backups))
            val backups = backupsViewModel.backups.filter { it.value.bootSha1.isNullOrEmpty() || it.value.bootSha1.equals(slotViewModel.sha1) || it.value.type == "ak3" }
            if (backups.isNotEmpty()) {
                for (id in backups.keys.sortedByDescending { it }) {
                    Spacer(Modifier.height(16.dp))
                    DataCard(
                        title = id,
                        button = {
                            AnimatedVisibility(!slotViewModel.isRefreshing.value) {
                                ViewButton(onClick = {
                                    navController.navigate("slot$slotSuffix/backups/$id")
                                })
                            }
                        }
                    ) {
                        DataRow(stringResource(R.string.kernel_version), backups[id]!!.kernelVersion, clickable = true)
                    }
                }
            } else {
                Spacer(Modifier.height(32.dp))
                Text(
                    stringResource(R.string.no_backups_found),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    } else if (navController.currentDestination!!.route!!.endsWith("/backups/{backupId}/restore")) {
        DataCard (stringResource(R.string.restore))
        Spacer(Modifier.height(5.dp))
        val currentBackup = backupsViewModel.backups.getValue(backupsViewModel.currentBackup!!)
        if (currentBackup.hashes != null) {
            for (partitionName in PartitionUtil.PartitionNames) {
                val hash = currentBackup.hashes[partitionName]
                if (hash != null) {
                    PartitionToggle(
                        name = partitionName,
                        checked = backupsViewModel.backupPartitions[partitionName] == true,
                        enabled = backupsViewModel.backupPartitions[partitionName] != null,
                        onToggle = {
                            backupsViewModel.backupPartitions[partitionName] = !backupsViewModel.backupPartitions[partitionName]!!
                        }
                    )
                }
            }
        } else {
            Text(
                stringResource(R.string.partition_selection_unavailable),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )
            Spacer(Modifier.height(5.dp))
        }
        ActionTile(
            text = stringResource(R.string.restore),
            icon = Icons.Outlined.SettingsBackupRestore,
            accent = TileColors.Violet,
            enabled = currentBackup.hashes == null || (PartitionUtil.PartitionNames.none {
                currentBackup.hashes[it] != null && backupsViewModel.backupPartitions[it] == null
            } && backupsViewModel.backupPartitions.filter { it.value }.isNotEmpty()),
            onClick = {
                backupsViewModel.restore(context, slotSuffix)
                navController.navigate("slot$slotSuffix/backups/${backupsViewModel.currentBackup!!}/restore/restore") {
                    popUpTo("slot$slotSuffix")
                }
            }
        )
    } else {
        FlashList(
            stringResource(R.string.restore),
            backupsViewModel.restoreOutput
        ) {
            AnimatedVisibility(!backupsViewModel.isRefreshing && backupsViewModel.wasRestored != null) {
                Column {
                    if (backupsViewModel.wasRestored != false) {
                        ActionTile(
                            text = stringResource(R.string.reboot),
                            icon = Icons.Outlined.RestartAlt,
                            accent = TileColors.Rose,
                            onClick = { navController.navigate("reboot") }
                        )
                    }
                }
            }
        }
    }
}
