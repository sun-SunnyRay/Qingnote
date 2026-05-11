package com.qingguang.qingnote.ui.page.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qingguang.qingnote.R
import com.qingguang.qingnote.component.NoteCard
import com.qingguang.qingnote.component.NoteCardFrom
import com.qingguang.qingnote.ui.page.router.debouncedPopBackStack
import com.moriafly.salt.ui.SaltTheme
import kotlinx.coroutines.delay

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    navController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchBarExpanded by rememberSaveable { mutableStateOf(false) }
    val searchQuery by searchViewModel.query.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val filterList by searchViewModel.dataFlow.collectAsState(initial = emptyList())

    Box(Modifier.fillMaxSize().background(SaltTheme.colors.background)) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = searchViewModel::onQuery,
                    onSearch = {
                        searchBarExpanded = true
                        searchViewModel.onSearch(it)
                        keyboardController?.hide()
                    },
                    expanded = searchBarExpanded,
                    onExpandedChange = { },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search_hint),
                            color = SaltTheme.colors.subText
                        )
                    },
                    leadingIcon = {
                        IconButton(onClick = { navController.debouncedPopBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = SaltTheme.colors.text
                            )
                        }
                    },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = {
                                searchViewModel.clearSearchQuery()
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "",
                                    tint = SaltTheme.colors.text
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = SaltTheme.colors.text,
                        unfocusedTextColor = SaltTheme.colors.text,
                        cursorColor = SaltTheme.colors.text,
                        focusedContainerColor = SaltTheme.colors.subBackground,
                        unfocusedContainerColor = SaltTheme.colors.subBackground
                    )
                )
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .focusRequester(focusRequester),
            expanded = searchBarExpanded,
            onExpandedChange = { if (!it) navController.debouncedPopBackStack() },
            colors = SearchBarDefaults.colors(
                containerColor = SaltTheme.colors.subBackground,
                dividerColor = SaltTheme.colors.stroke
            )
        ) {
            LazyColumn(
                Modifier
                    .background(SaltTheme.colors.background)
                    .fillMaxSize()
                    .padding()
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(count = filterList.size, key = { index -> filterList[index].note.noteId }) { index ->
                    NoteCard(
                        noteShowBean = filterList[index], 
                        navHostController = navController, 
                        from = NoteCardFrom.SEARCH,
                        highlightText = searchQuery // 传入搜索关键词以实现高亮
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }
}
