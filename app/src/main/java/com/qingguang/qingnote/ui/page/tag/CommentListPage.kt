package com.qingguang.qingnote.ui.page.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.qingguang.qingnote.R
import com.qingguang.qingnote.component.NoteCard
import com.qingguang.qingnote.component.NoteCardFrom
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.ui.page.router.debouncedPopBackStack
import com.qingguang.qingnote.utils.str
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltApi

@OptIn(UnstableSaltApi::class)
@Composable
fun CommentListPage(parentNoteId: Long, navController: NavHostController) {
    val noteViewModel = LocalMemosViewModel.current
    val commentList by noteViewModel.getCommentsByParentId(parentNoteId).collectAsState(initial = emptyList())
    val parentNote by noteViewModel.getNoteShowBeanByIdFlow(parentNoteId).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SaltTheme.colors.background)
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        TitleBar(
            onBack = {
                navController.debouncedPopBackStack()
            },
            text = R.string.comment.str
        )

        LazyColumn {
            items(count = commentList.size, key = { commentList[it].note.noteId }) { index ->
                val comment = commentList[index]
                // 1. 显示批注的内容卡片
                NoteCard(
                    noteShowBean = comment.copy(parentNote = null), // 这里设为null，防止NoteCard内部再次显示引用框
                    navHostController = navController,
                    from = NoteCardFrom.TAG_DETAIL,
                    isCanNavigate = false
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 2. 显示被批注的原卡片（如果有）
                parentNote?.let { parent ->
                    NoteCard(
                        noteShowBean = parent,
                        navHostController = navController,
                        from = NoteCardFrom.TAG_DETAIL,
                        isCanNavigate = false
                    )
                }

                // 3. 组与组之间的空间隔
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

}