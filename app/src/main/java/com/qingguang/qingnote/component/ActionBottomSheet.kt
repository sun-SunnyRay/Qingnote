package com.qingguang.qingnote.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.qingguang.qingnote.R
import com.qingguang.qingnote.bean.NoteShowBean
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.ui.page.router.Screen
import com.qingguang.qingnote.utils.BackUp
import com.qingguang.qingnote.utils.ExportMarkDownContract
import com.qingguang.qingnote.utils.copy
import com.qingguang.qingnote.utils.lunchIo
import com.qingguang.qingnote.utils.str
import com.qingguang.qingnote.utils.toast
import com.qingguang.qingnote.utils.withIO
import com.moriafly.salt.ui.SaltTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBottomSheet(
    navHostController: NavHostController,
    noteShowBean: NoteShowBean,
    show: Boolean,
    onCommentClick: ((NoteShowBean) -> Unit)? = null,
    onDismissRequest: () -> Unit
) {

    val viewModel = LocalMemosViewModel.current
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    val fileName = if (noteShowBean.note.content.length > 4) noteShowBean.note.content.take(4) else noteShowBean.note.content

    val exportMarkDownLauncher = rememberLauncherForActivityResult(ExportMarkDownContract(fileName)) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        lunchIo {
            BackUp.exportMarkDownFile(list = arrayListOf(noteShowBean), uri)
            toast(R.string.excute_success.str)
        }
    }

    // Sheet content
    if (show) {
        ModalBottomSheet(
            containerColor = SaltTheme.colors.popup,
            onDismissRequest = onDismissRequest, sheetState = bottomSheetState
        ) {
            Row(
                Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                LazyColumn {
//                    item {
//                        TextButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
//                            Text(text = R.string.pin.str, style = MaterialTheme.typography.titleMedium)
//                        }
//                    }

                    item {
                        TextButton(onClick = {
                            onCommentClick?.invoke(noteShowBean)
                            onDismissRequest()
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = R.string.comment.str, style = SaltTheme.textStyles.paragraph)
                        }
                    }

                    item {
                        TextButton(onClick = {
                            copy(noteShowBean.note)
                            onDismissRequest()
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = R.string.copy.str, style = SaltTheme.textStyles.paragraph)
                        }
                    }

                    item {
                        TextButton(onClick = {
                            navHostController.navigate(Screen.Share(noteShowBean.note.noteId))
                            onDismissRequest()
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = R.string.share.str, style = SaltTheme.textStyles.paragraph)
                        }
                    }

                    item {
                        TextButton(onClick = {
                            scope.launch {
                                viewModel.deleteNote(noteShowBean.note, noteShowBean.tagList)
                                onDismissRequest()
                            }
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = R.string.delete.str, style = SaltTheme.textStyles.paragraph)
                        }
                    }
                    item {
                        TextButton(onClick = {
                            scope.launch {
                                withIO {
                                    exportMarkDownLauncher.launch(null)
                                }
                                onDismissRequest()
                            }
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(text = R.string.export.str, style = SaltTheme.textStyles.paragraph)
                        }
                    }
                }
            }
        }
    }
}