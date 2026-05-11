package com.qingguang.qingnote.state

import com.qingguang.qingnote.bean.NoteShowBean

data class NoteState(
    val notes: List<NoteShowBean> = emptyList(),
    val title: String = "",
    val content: String = "",
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val editingNote: NoteShowBean? = null,
)
