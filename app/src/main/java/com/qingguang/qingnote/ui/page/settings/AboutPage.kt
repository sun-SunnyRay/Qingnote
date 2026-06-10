package com.qingguang.qingnote.ui.page.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qingguang.qingnote.R
import com.moriafly.salt.ui.SaltTheme

@Composable
fun AboutComposeScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            color = SaltTheme.colors.text,
            text = stringResource(id = R.string.author),
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            color = SaltTheme.colors.text,
            text = stringResource(id = R.string.about_icon),
            modifier = Modifier.padding(top = 24.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            color = SaltTheme.colors.text,
            text = "https://www.iconfont.cn/",
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            color = SaltTheme.colors.text,
            text = "https://www.flaticon.com/",
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            color = SaltTheme.colors.text,
            text = "https://iconpark.oceanengine.com/",
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            color = SaltTheme.colors.text,
            text = "UI",
            modifier = Modifier.padding(top = 24.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            color = SaltTheme.colors.text,
            text = "https://github.com/Moriafly/SaltUI",
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
