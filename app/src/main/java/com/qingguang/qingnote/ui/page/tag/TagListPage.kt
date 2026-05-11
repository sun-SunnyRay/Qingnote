package com.qingguang.qingnote.ui.page.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.qingguang.qingnote.R
import com.qingguang.qingnote.bean.Tag
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.ui.page.LocalTags
import com.qingguang.qingnote.ui.page.NoteViewModel
import com.qingguang.qingnote.ui.page.router.Screen
import com.qingguang.qingnote.ui.page.router.debouncedPopBackStack
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltApi

@OptIn(ExperimentalLayoutApi::class, UnstableSaltApi::class)
@Composable
fun TagListPage(navController: NavHostController) {
    val tagList = LocalTags.current.filter { it.tag.isNotBlank() }
    val noteViewModel: NoteViewModel = LocalMemosViewModel.current
    val allYears = remember { mutableStateListOf<String>() }

    // 对标签进行分组：按一级目录分组
    val groupedTags = remember(tagList) {
        tagList.groupBy { it.tag.substringBefore("/") }
            .toSortedMap()
    }

    LaunchedEffect(key1 = Unit) {
        allYears.clear()
        allYears.addAll(noteViewModel.getAllDistinctYears())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SaltTheme.colors.background)
            .statusBarsPadding()
    ) {
        TitleBar(
            onBack = { navController.debouncedPopBackStack() },
            text = stringResource(R.string.tag)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp), // 间距缩小一半 (16dp -> 8dp)
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp) // 跟head间距保持一致
        ) {
            // 1. 顶部统计概览
            item {
                StatsHeader(tagList.size, groupedTags.size)
            }
            // 3. 时间维度分类
            if (allYears.isNotEmpty()) {
                item {
                    YearCategoryCard(
                        years = allYears,
                        onYearClick = { year ->
                            navController.navigate(Screen.YearDetail(year))
                        }
                    )
                }
            }
            // 2. 标签分类卡片
            groupedTags.forEach { (parentTag, tags) ->
                item {
                    TagCategoryCard(
                        parentTag = parentTag.removePrefix("#"),
                        tags = tags,
                        onTagClick = { tag ->
                            navController.navigate(Screen.TagDetail(tag.tag))
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun StatsHeader(totalTags: Int, totalCategories: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // 左右对齐，移除垂直padding由LazyColumn spacedBy接管
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItem(Modifier.weight(1f), totalTags.toString(), stringResource(R.string.tag), Icons.Outlined.Label, Color(0xFF4D84F7))
        StatItem(Modifier.weight(1f), totalCategories.toString(), "分类", Icons.Outlined.Style, Color(0xFFF7844D))
    }
}

@Composable
fun StatItem(modifier: Modifier, value: String, label: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier,
        color = SaltTheme.colors.subBackground,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = color)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(value, style = SaltTheme.textStyles.main.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))
                Text(label, style = SaltTheme.textStyles.sub.copy(fontSize = 12.sp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, UnstableSaltApi::class)
@Composable
fun TagCategoryCard(parentTag: String, tags: List<Tag>, onTagClick: (Tag) -> Unit) {
    RoundedColumn {
        Column(modifier = Modifier.padding(12.dp)) { // 内部边距微调
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(4.dp, 16.dp).background(Color(0xFF4D84F7), RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = parentTag,
                    style = SaltTheme.textStyles.main.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.sortedByDescending { it.count }.forEach { tag ->
                    TagPill(tag = tag, parentTag = parentTag, onClick = { onTagClick(tag) })
                }
            }
        }
    }
}

@Composable
fun TagPill(tag: Tag, parentTag: String, onClick: () -> Unit) {
    val displayLabel = tag.tag.removePrefix("#").let {
        if (it == parentTag) it else it.substringAfter("/")
    }

    Surface(
        modifier = Modifier.clip(CircleShape).clickable { onClick() },
        color = SaltTheme.colors.background,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayLabel,
                style = SaltTheme.textStyles.main.copy(fontSize = 13.sp)
            )
            if (tag.count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .background(Color.Gray.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        tag.count.toString(),
                        style = SaltTheme.textStyles.sub.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, UnstableSaltApi::class)
@Composable
fun YearCategoryCard(years: List<String>, onYearClick: (String) -> Unit) {
    RoundedColumn(

    ) {
        Column(modifier = Modifier.padding(12.dp)) { // 内部边距微调，与TagCategoryCard一致
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp), tint = SaltTheme.colors.text)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.year),
                    style = SaltTheme.textStyles.main.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                years.forEach { year ->
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onYearClick(year) },
                        color = SaltTheme.colors.background,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            text = year,
                            style = SaltTheme.textStyles.main.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
    }
}
