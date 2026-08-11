package com.github.capntrips.kernelflasher.ui.screens.slot

import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.common.PartitionUtil
import com.github.capntrips.kernelflasher.ui.components.ActionTile
import com.github.capntrips.kernelflasher.ui.components.DataCard
import com.github.capntrips.kernelflasher.ui.components.FlashButton
import com.github.capntrips.kernelflasher.ui.components.FlashList
import com.github.capntrips.kernelflasher.ui.components.PartitionToggle
import com.github.capntrips.kernelflasher.ui.components.SlotCard
import com.github.capntrips.kernelflasher.ui.components.TileColors
import com.github.capntrips.kernelflasher.ui.components.DialogButton
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalUnitApi
@ExperimentalSerializationApi
@Composable
fun ColumnScope.SlotFlashContent(
    viewModel: SlotViewModel,
    slotSuffix: String,
    navController: NavController
) {
    val context = LocalContext.current

    val isRefreshing by remember { derivedStateOf { viewModel.isRefreshing } }
    val currentRoute = navController.currentDestination?.route.orEmpty()

    val isAk3 = currentRoute.contains("ak3")
    val isFlashImage = currentRoute.endsWith("/flash/image")
    val isBackup = currentRoute.endsWith("/backup")
    val isBackupResult = currentRoute.endsWith("/backup/backup")
    var showBackupDialog by remember { mutableStateOf(false) }
    var customBackupName by remember { mutableStateOf("") }
    val isFlashAk3 = currentRoute.endsWith("/flash/ak3")
    val isImageFlashResult = currentRoute.endsWith("/flash/image/flash")

    val isFlashScreen = currentRoute.endsWith("/flash")
    val isSlotScreen = !(isFlashAk3 || isImageFlashResult || isBackupResult) // Not in Flashing Screen; So Its considered Slot Screen

    BackHandler(enabled = ((isFlashAk3 || isImageFlashResult || isBackupResult) && isRefreshing.value)) { }

    if (isSlotScreen) {
        SlotCard(
            title = stringResource(if (slotSuffix == "_a") R.string.slot_a else if (slotSuffix == "_b") R.string.slot_b else R.string.slot),
            viewModel = viewModel,
            navController = navController,
            isSlotScreen = true,
            showDlkm = false
        )
        Spacer(Modifier.height(16.dp))
        if (isFlashScreen) {
            DataCard (stringResource(R.string.flash))
            Spacer(Modifier.height(5.dp))
            FlashButton(stringResource(R.string.flash_ak3_zip), "zip", accent = TileColors.Amber, callback = { uri ->
                viewModel.flashActionType = "flashAk3"
                viewModel.flashActionURI = uri
                viewModel.showConfirmDialog()
            })
            FlashButton(stringResource(R.string.flash_ak3_zip_mkbootfs), "zip", accent = TileColors.Amber, callback = { uri ->
                viewModel.flashActionType = "flashAk3_mkbootfs"
                viewModel.flashActionURI = uri
                viewModel.showConfirmDialog()
            })
            FlashButton(stringResource(R.string.flash_ksu_lkm), "ko", accent = TileColors.Cyan, callback = { uri ->
                viewModel.flashActionType = "flashKsuDriver"
                viewModel.flashActionURI = uri
                viewModel.showConfirmDialog()
            })
            ActionTile(
                text = stringResource(R.string.flash_partition_image),
                icon = Icons.Outlined.Storage,
                accent = TileColors.Blue,
                showChevron = true,
                onClick = {
                    navController.navigate("slot$slotSuffix/flash/image")
                }
            )
        } else if (isFlashImage) {
            DataCard (stringResource(R.string.flash_partition_image))
            Spacer(Modifier.height(5.dp))
            for (partitionName in PartitionUtil.AvailablePartitions) {
                FlashButton(partitionName, "img", accent = TileColors.Blue, callback = { uri ->
                    viewModel.flashActionType = "flashImage"
                    viewModel.flashActionURI = uri
                    viewModel.flashActionPartName = partitionName
                    viewModel.showConfirmDialog()
                })
            }
        } else if (isBackup) {
            DataCard (stringResource(R.string.backup))
            Spacer(Modifier.height(5.dp))
            for (partitionName in PartitionUtil.AvailablePartitions) {
                PartitionToggle(
                    name = partitionName,
                    checked = viewModel.backupPartitions[partitionName] == true,
                    onToggle = {
                        viewModel.backupPartitions[partitionName] = !viewModel.backupPartitions[partitionName]!!
                    }
                )
            }
            ActionTile(
                text = stringResource(R.string.backup_now),
                icon = Icons.Outlined.Save,
                accent = TileColors.Green,
                enabled = viewModel.backupPartitions.filter { it.value }.isNotEmpty(),
                onClick = { showBackupDialog = true }
            )
        }
    } else {
        Text("")
        FlashList(
            stringResource(if (isBackupResult) R.string.backup else R.string.flash),
            if (isAk3)
                viewModel.uiPrintedOutput
            else viewModel.flashOutput
        ) {
            AnimatedVisibility(!viewModel.isRefreshing.value && viewModel.wasFlashSuccess.value != null) {
                Column {
                    if (isAk3) {
                        ActionTile(
                            text = stringResource(R.string.save_ak3_log),
                            icon = Icons.Outlined.Description,
                            accent = TileColors.Cyan,
                            onClick = { viewModel.saveLog(context) }
                        )
                    }
                    if (isAk3) {
                        AnimatedVisibility(!currentRoute.endsWith("/backups/{backupId}/flash/ak3") && viewModel.wasFlashSuccess.value != false) {
                            ActionTile(
                                text = stringResource(R.string.save_ak3_zip_as_backup),
                                icon = Icons.Outlined.Archive,
                                accent = TileColors.Violet,
                                onClick = {
                                    viewModel.backupZip(context) {
                                        navController.navigate("slot$slotSuffix/backups") {
                                            popUpTo("slot$slotSuffix")
                                        }
                                    }
                                }
                            )
                        }
                    }
					if (viewModel.wasFlashSuccess.value == true && viewModel.showCautionDialog == true){
						AlertDialog(
							onDismissRequest = { viewModel.hideCautionDialog() },
							title = { Text("CAUTION!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
							text = {
								Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
									Text("You have flashed to inactive slot!", fontWeight = FontWeight.Bold)
									Text("But the active slot is not changed after flashing.", fontWeight = FontWeight.Bold)
									Text("Change active slot or return to System Updater to complete OTA.", fontWeight = FontWeight.Bold)
									Text("Do not reboot from here, unless you know what you are doing.", fontWeight = FontWeight.Bold)
								}
							},
							confirmButton = {
								DialogButton(
									"CHANGE SLOT"
                                ) {
                                    viewModel.hideCautionDialog()
                                    viewModel.switchSlot(context)
                                }
                            },
							dismissButton = {
								DialogButton(
									"CANCEL"
                                ) {
                                    viewModel.hideCautionDialog()
                                }
                            },
							modifier = Modifier.padding(16.dp)
						)
					}
                    if (viewModel.wasFlashSuccess.value != false && isBackupResult) {
                        ActionTile(
                            text = stringResource(R.string.back),
                            icon = Icons.AutoMirrored.Outlined.ArrowBack,
                            accent = TileColors.Blue,
                            onClick = { navController.popBackStack() }
                        )
                    } else {
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
    if(viewModel.showConfirmDialog == true)
    {
        var filename = when {
            viewModel.flashActionURI?.scheme == "file" -> {
                File(viewModel.flashActionURI?.path ?: "").name
            }
            viewModel.flashActionURI != null -> {
                context.contentResolver.query(viewModel.flashActionURI!!, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex != -1) {
                        cursor.getString(nameIndex)
                    } else null
                } ?: "Unable to determine filename!"
            }
            else -> "Unable to determine filename!"
        }

        AlertDialog(
            onDismissRequest = { viewModel.hideConfirmDialog() },
            title = { Text("CAUTION!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Are you sure you want to flash this file?", fontWeight = FontWeight.Bold)

                    Text("Source: $filename")

                    if (viewModel.flashActionType == "flashImage" && viewModel.flashActionPartName != null) {
                        Text("Destination Partition: ${viewModel.flashActionPartName}", fontWeight = FontWeight.Bold)
                    } else if (viewModel.flashActionType == "flashAk3" || viewModel.flashActionType == "flashAk3_mkbootfs") {
                        Text("Destination: AnyKernel3 (Auto-detect)", fontWeight = FontWeight.Bold)
                    }

                    if (slotSuffix != "") {
                        Text(
                            "Destination Slot: ${if (slotSuffix == "_a") "A" else "B"} " +
                                    "(${if (viewModel.isActive) "active" else "inactive"})",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                DialogButton(
                    "Flash"
                ) {
                    viewModel.hideConfirmDialog()
                    val isOtherFlash =
                        viewModel.flashActionType != "flashImage" && viewModel.flashActionURI != null
                    val isPartitionFlash =
                        viewModel.flashActionType == "flashImage" && viewModel.flashActionPartName != null && viewModel.flashActionURI != null

                    if (isOtherFlash || isPartitionFlash) {
                        val uri = viewModel.flashActionURI!!
                        val partitionName: String? = viewModel.flashActionPartName

                        when (viewModel.flashActionType) {
                            "flashAk3" -> {
                                navController.navigate("slot$slotSuffix/flash/ak3") {
                                    popUpTo("slot$slotSuffix")
                                }
                                viewModel.flashAk3(context, uri)
                            }

                            "flashAk3_mkbootfs" -> {
                                navController.navigate("slot$slotSuffix/flash/ak3") {
                                    popUpTo("slot$slotSuffix")
                                }
                                viewModel.flashAk3_mkbootfs(context, uri)
                            }

                            "flashKsuDriver" -> {
                                navController.navigate("slot$slotSuffix/flash/image/flash") {
                                    popUpTo("slot$slotSuffix")
                                }
                                viewModel.flashKsuDriver(context, uri)
                            }

                            "flashImage" -> {
                                navController.navigate("slot$slotSuffix/flash/image/flash") {
                                    popUpTo("slot$slotSuffix")
                                }
                                viewModel.flashImage(
                                    context,
                                    uri,
                                    partitionName!!
                                )
                            }
                        }
                        viewModel.flashActionType = ""
                        viewModel.flashActionURI = null
                        viewModel.flashActionPartName = null
                    }
                }
            },
            dismissButton = {
                DialogButton(
                    "CANCEL"
                ) {
                    viewModel.hideConfirmDialog()
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Custom Backup Name") },
            text = {
                OutlinedTextField(
                    value = customBackupName,
                    onValueChange = { customBackupName = it },
                    label = { Text("Optional Prefix (e.g. Sultan)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showBackupDialog = false
                    viewModel.backup(context, customBackupName, slotSuffix)
                    navController.navigate("slot$slotSuffix/backup/backup") {
                        popUpTo("slot$slotSuffix") 
                    }
                }) {
                    Text("Start Backup")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
        
