package com.qingguang.qingnote.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.qingguang.qingnote.ui.page.router.debouncedPopBackStack
import com.qingguang.qingnote.utils.BlurTransformation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictureDisplayPage(
    pathList: List<String>, curIndex: Int, timestamps: List<Long>,
    navController: NavHostController
) {
    val pagerState = rememberPagerState(pageCount = { pathList.size }, initialPage = curIndex) // 定义10个页面
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    Box {
        HorizontalPager(state = pagerState) { page ->
            Surface(modifier = Modifier.fillMaxSize()) {
                DetailContent(
                    imgUrl = pathList[page],
                    requestImage = {
                        Image(pathList[page])
                    }
                )
            }
        }
        IconButton(onClick = { navController.debouncedPopBackStack() }, modifier = Modifier.padding(start = 12.dp, top = 24.dp, end = 0.dp, bottom = 0.dp)) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                tint = Color(0xFFFFFFFF)
            )
        }
        // Page indicator in top right corner
        Box(
            modifier = Modifier
                .padding(top = 24.dp, end = 12.dp)
                .align(Alignment.TopEnd)
                .wrapContentSize()
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${pathList.size}",
                color = Color(0xFFFFFFFF),
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
        // Time display in bottom left corner
        Box(
            modifier = Modifier
                .padding(bottom = 24.dp, start = 12.dp)
                .align(Alignment.BottomStart)
                .wrapContentSize()
        ) {
            val safeIndex = min(pagerState.currentPage, timestamps.size - 1)
            val timestamp = if (timestamps.isNotEmpty()) timestamps[safeIndex] else System.currentTimeMillis()
            Text(
                text = dateFormat.format(Date(timestamp)),
                color = Color(0xFFFFFFFF),
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun DetailContent(
    imgUrl: String?,
    modifier: Modifier = Modifier,
    requestImage: @Composable () -> Unit
) {
    Surface {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imgUrl)
                .transformations(
                    BlurTransformation(
                        LocalContext.current,
                        radius = 25f,
                        sampling = 5f
                    )
                )
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )

        requestImage()
    }
}

@Composable
private fun Image(imgUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imgUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
    )
}
