package com.qingguang.qingnote.ui.page.input

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.qingguang.qingnote.bean.Attachment
import com.qingguang.qingnote.db.repo.TagNoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemoInputViewModel @Inject constructor(private val tagNoteRepo: TagNoteRepo) : ViewModel() {

    fun deleteResource(path: String) {
        uploadAttachments.remove(uploadAttachments.firstOrNull { it.path == path })
    }

    var uploadAttachments = mutableStateListOf<Attachment>()

}