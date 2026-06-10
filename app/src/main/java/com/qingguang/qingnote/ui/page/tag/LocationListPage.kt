package com.qingguang.qingnote.ui.page.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.qingguang.qingnote.R
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.ui.page.NoteViewModel
import com.qingguang.qingnote.ui.page.home.clickable
import com.qingguang.qingnote.ui.page.router.Screen
import com.qingguang.qingnote.ui.page.router.debouncedPopBackStack
import com.qingguang.qingnote.utils.str
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltApi
import com.moriafly.salt.ui.dialog.YesNoDialog


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, UnstableSaltApi::class)
@Composable
fun LocationListPage(navHostController: NavHostController) {
    val noteViewModel: NoteViewModel = LocalMemosViewModel.current
    val locationInfoList by noteViewModel.getAllLocationInfo().collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var selectedLocationInfo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SaltTheme.colors.background)
            .padding(top = 30.dp)
    ) {
        TitleBar(
            onBack = {
                navHostController.debouncedPopBackStack()
            },
            text = stringResource(R.string.location_info)
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            content = {
                repeat(locationInfoList.size) { index ->
                    ElevatedAssistChip(
                        onClick = {
                            navHostController.navigate(Screen.LocationDetail(locationInfoList[index]))
                        },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        label = {
                            Text(locationInfoList[index])
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Localized description",
                                Modifier
                                    .size(AssistChipDefaults.IconSize)
                                    .clickable {
                                        selectedLocationInfo = locationInfoList[index]
                                        showDialog = true
                                    }
                            )
                        }
                    )
                }
            }
        )

        if (showDialog) {
            YesNoDialog(
                title = R.string.warm_reminder.str,
                content = stringResource(R.string.delete_location, selectedLocationInfo),
                onConfirm = {
                    noteViewModel.clearLocationInfo(selectedLocationInfo)
                    showDialog = false
                },
                onDismissRequest = {
                    showDialog = false
                }
            )
        }
    }
}