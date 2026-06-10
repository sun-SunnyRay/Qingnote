package com.qingguang.qingnote.ui.page.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qingguang.qingnote.bean.NoteShowBean
import com.qingguang.qingnote.db.repo.TagNoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val tagNoteRepo: TagNoteRepo) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow(value = "")
    val query: StateFlow<String>
        get() = _query


    fun clearSearchQuery() {
        _query.value = ""
        dataFlow.value = emptyList()
    }

    fun onQuery(query: String) {
        _query.value = query
    }

    lateinit var notes: List<NoteShowBean>

    init {

        viewModelScope.launch(Dispatchers.IO) {
            notes = tagNoteRepo.queryAllNoteShowBeanList()
        }
    }

    val dataFlow: MutableStateFlow<List<NoteShowBean>> = MutableStateFlow(value = emptyList())

    private fun getSearchResults(
        searchKey: String,
        notes: List<NoteShowBean>,
    ): List<NoteShowBean> = notes.filter { note ->
        fun String.matches(): Boolean = contains(searchKey.trim(), true)
        note.note.content.matches() || note.note.attachments.any { it.description.matches() } || note.tagList.any { it.tag.matches() }
    }

    fun onSearch(str: String) {
        dataFlow.value = getSearchResults(str, notes)
    }
}