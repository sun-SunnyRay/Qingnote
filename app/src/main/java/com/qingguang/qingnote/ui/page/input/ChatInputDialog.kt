package com.qingguang.qingnote.ui.page.input

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.qingguang.qingnote.R
import com.qingguang.qingnote.bean.Note
import com.qingguang.qingnote.bean.NoteShowBean
import com.qingguang.qingnote.bean.Tag
import com.qingguang.qingnote.component.PIconButton
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.ui.page.LocalTags
import com.qingguang.qingnote.ui.page.home.clickable
import com.qingguang.qingnote.utils.handlePickFiles
import com.qingguang.qingnote.utils.str
import com.qingguang.qingnote.utils.toast
import com.moriafly.salt.ui.SaltTheme
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ChatInputDialog(
    isShow: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    parentNote: NoteShowBean? = null,
    dismiss: () -> Unit
) {
    var bottomSheetState by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isShow) {
        bottomSheetState = isShow
    }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var text: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    var tagMenuExpanded by remember { mutableStateOf(false) }
    var tagSearchQuery by remember { mutableStateOf<String?>(null) }
    var photoImageUri by remember { mutableStateOf<Uri?>(null) }
    val tagList = LocalTags.current.filterNot { it.isCityTag }
    val memosViewModel = LocalMemosViewModel.current
    val memoInputViewModel = hiltViewModel<MemoInputViewModel>()

    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoImageUri?.let {
                coroutineScope.launch {
                    handlePickFiles(setOf(it)) {
                        memoInputViewModel.uploadAttachments.addAll(it)
                    }
                }
            }
        }
    }

    // 创建一个 launcher，用于选择多张图片
    val pickMultipleMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(3) // 最多选择 3 张图片
    ) { uris ->
        coroutineScope.launch {
            handlePickFiles(uris.toSet()) {
                memoInputViewModel.uploadAttachments.addAll(it)
            }
        }
    }

    fun submit() = coroutineScope.launch {
        try {
            softwareKeyboardController?.hide()
            focusRequester.freeFocus()
        } catch (_: Exception) {
            // focusRequester 可能已脱离 composition
        }
        val content = text.text
        if (content.isBlank() && memoInputViewModel.uploadAttachments.isEmpty()) return@launch
        memosViewModel.insertOrUpdate(Note(content = content, attachments = memoInputViewModel.uploadAttachments.toList(), parentNoteId = parentNote?.note?.noteId))
        text = TextFieldValue("")
        memoInputViewModel.uploadAttachments.clear()
        dismiss()
    }


    LaunchedEffect(bottomSheetState) {
        if (bottomSheetState) {
            focusRequester.requestFocus()
            softwareKeyboardController?.show()
        } else {
            softwareKeyboardController?.hide()
            // 弹框消失时执行你想要的操作
        }
    }
    @Composable
    fun TagButton(tagList: List<Tag>) {
        // 根据搜索内容过滤标签
        val filteredTags = remember(tagList, tagSearchQuery) {
            if (tagSearchQuery == null) {
                tagList
            } else {
                tagList.filter { it.tag.contains(tagSearchQuery!!, ignoreCase = true) }
            }
        }

        fun insertTagText(tagContent: String) {
            val newText = text.text.replaceRange(text.selection.min, text.selection.max, tagContent)
            val newSelection = TextRange(text.selection.min + tagContent.length)
            text = text.copy(newText, newSelection)
        }

        // 替换光标前的 #搜索词
        fun replaceTagText(tagContent: String) {
            val cursorPos = text.selection.start
            val textBeforeCursor = text.text.substring(0, cursorPos)
            val lastHashIndex = textBeforeCursor.lastIndexOf('#')
            if (lastHashIndex != -1) {
                val cleanTag = tagContent.removePrefix("#")
                val replacement = "#$cleanTag "
                val newText = text.text.replaceRange(lastHashIndex, cursorPos, replacement)
                val newSelection = TextRange(lastHashIndex + replacement.length)
                text = text.copy(newText, newSelection)
            }
        }

        // 根据tagList是否为空选择不同的图标
        val tagIcon = if (tagList.isEmpty()) Icons.Filled.Tag else Icons.Outlined.Tag

        // 统一的图标按钮
        PIconButton(
            imageVector = tagIcon,
            contentDescription = stringResource(R.string.tag),
        ) {
            val cursorPos = text.selection.start
            val textBeforeCursor = text.text.substring(0, cursorPos)
            val lastHashIndex = textBeforeCursor.lastIndexOf('#')
            val fragment = if (lastHashIndex != -1) textBeforeCursor.substring(lastHashIndex + 1) else null

            if (fragment != null && !fragment.contains(" ") && !fragment.contains("\n")) {
                // 如果当前已经在输入标签，则直接显示/隐藏菜单，并保留搜索词
                tagSearchQuery = fragment
                tagMenuExpanded = !tagMenuExpanded
            } else {
                // 否则插入 # 并显示菜单
                insertTagText("#")
                tagSearchQuery = ""
                tagMenuExpanded = tagList.isNotEmpty()
            }
        }

        // 仅当有标签且菜单展开时显示下拉菜单
        if (filteredTags.isNotEmpty() && tagMenuExpanded) {
            Box {
                DropdownMenu(
                    modifier = Modifier.wrapContentHeight().heightIn(max = 400.dp),
                    expanded = tagMenuExpanded,
                    onDismissRequest = {
                        tagMenuExpanded = false
                        tagSearchQuery = null
                    },
                    properties = PopupProperties(focusable = false)
                ) {
                    filteredTags.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag.tag) },
                            onClick = {
                                if (tagSearchQuery != null) {
                                    replaceTagText(tag.tag)
                                } else {
                                    val cleanTag = tag.tag.removePrefix("#")
                                    insertTagText("#$cleanTag ")
                                }
                                tagMenuExpanded = false
                                tagSearchQuery = null
                            },
                        )
                    }
                }
            }
        }
    }

    if (isShow) {
        Box(
            contentAlignment = Alignment.BottomCenter, modifier = Modifier
                .fillMaxSize()
//                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(showRipple = false) {
                    dismiss()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), color = SaltTheme.colors.background)
            ) {
                if (parentNote != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(SaltTheme.colors.subBackground, RoundedCornerShape(8.dp))
                            .border(1.dp, SaltTheme.colors.subText.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = parentNote.note.content,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            style = SaltTheme.textStyles.paragraph.copy(fontSize = 13.sp, color = SaltTheme.colors.subText)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    minLines = 5,
                    textStyle = SaltTheme.textStyles.paragraph,
                    onValueChange = { it: TextFieldValue ->
                        text = it
                        val cursorPos = it.selection.start
                        val textBeforeCursor = it.text.substring(0, cursorPos)
                        val lastHashIndex = textBeforeCursor.lastIndexOf('#')

                        if (lastHashIndex != -1) {
                            val fragment = textBeforeCursor.substring(lastHashIndex + 1)
                            // 如果包含空格或换行，隐藏弹窗
                            if (fragment.contains(" ") || fragment.contains("\n")) {
                                tagMenuExpanded = false
                                tagSearchQuery = null
                            } else {
                                tagSearchQuery = fragment
                                // 检查是否有匹配项
                                val hasMatch = tagList.any { it.tag.contains(fragment, ignoreCase = true) }
                                tagMenuExpanded = hasMatch
                            }
                        } else {
                            tagMenuExpanded = false
                            tagSearchQuery = null
                        }
                    },
                    modifier =
                        modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .clickable { },
                    keyboardOptions = keyboardOptions,
                    label = { Text(R.string.any_thoughts.str) },
                )

                if (memoInputViewModel.uploadAttachments.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .height(80.dp)
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(memoInputViewModel.uploadAttachments.toList()) { resource ->
                            InputImage(attachment = resource, true, delete = { pat ->
                                memoInputViewModel.uploadAttachments.remove(memoInputViewModel.uploadAttachments.firstOrNull { it.path == pat })
                            })
                        }
                    }
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TagButton(tagList)
                    PIconButton(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = stringResource(R.string.add_image),
                    ) {
                        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    PIconButton(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = stringResource(R.string.take_photo),
                    ) {
                        try {
                            val imagesFolder = File(context.cacheDir, "capture_picture")
                            if (!imagesFolder.exists()) {
                                imagesFolder.mkdirs()
                            }
                            val file = File.createTempFile("capture_picture_", ".jpg", imagesFolder)
                            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                            photoImageUri = uri
                            takePhoto.launch(uri)
                        } catch (e: ActivityNotFoundException) {
                            toast(e.localizedMessage ?: "Unable to take picture.")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    PIconButton(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = stringResource(R.string.send),
                    ) {
                        submit()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
