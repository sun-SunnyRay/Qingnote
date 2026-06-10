package com.qingguang.qingnote.ui.page.tag

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.qingguang.qingnote.bean.NoteShowBean
import com.qingguang.qingnote.component.NoteCard
import com.qingguang.qingnote.component.NoteCardFrom
import com.qingguang.qingnote.component.RYScaffold
import com.qingguang.qingnote.utils.toMM
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.utils.SettingsPreferences

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YearDetailPage(year: String, navController: NavHostController) {
    val noteViewModel = LocalMemosViewModel.current
    var showYearBottomSheet by rememberSaveable { mutableStateOf(false) }
    val yearList = remember { mutableStateListOf<NoteShowBean>() }
    val filterYearList = remember { mutableStateListOf<NoteShowBean>() }
    var pageTitle = remember { mutableStateOf(year) }

    LaunchedEffect(key1 = Unit, block = {
        noteViewModel.getNotesByYear(year).collect {
            yearList.clear()
            yearList.addAll(it)
            filterYearList.clear()
            filterYearList.addAll(it)
        }
    })

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun YearBottomSheet(show: Boolean, onDismissRequest: () -> Unit) {

        val monthList = arrayListOf<String>("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        if (show) {
            ModalBottomSheet(onDismissRequest = onDismissRequest) {
                TextButton(onClick = {
                    pageTitle.value = year
                    filterYearList.clear()
                    filterYearList.addAll(yearList)
                    onDismissRequest()
                }) {
                    Text(text = year, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }

                FlowRow(Modifier.fillMaxWidth()) {
                    repeat(monthList.size) { index ->
                        ElevatedAssistChip(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .align(alignment = Alignment.CenterVertically),
                            onClick = {
                                val filteredList = yearList.filter { it.note.createTime.toMM() == monthList[index] }
                                filterYearList.clear()
                                filterYearList.addAll(filteredList)
                                pageTitle.value = year.plus("/").plus(monthList[index])
                                onDismissRequest()
                            },
                            label = {
                                Text(monthList[index])
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    RYScaffold(title = pageTitle.value,
        navController = navController,
        actions = {
            IconButton(
                onClick = {
                    showYearBottomSheet = true
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList, contentDescription = "sort"
                )
            }
        }
    ) {
        LazyColumn {
            items(count = filterYearList.size, key = { it }) { index ->
                NoteCard(noteShowBean = filterYearList[index], navController, from = NoteCardFrom.TAG_DETAIL)
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
        YearBottomSheet(showYearBottomSheet) {
            showYearBottomSheet = false
        }
    }
}