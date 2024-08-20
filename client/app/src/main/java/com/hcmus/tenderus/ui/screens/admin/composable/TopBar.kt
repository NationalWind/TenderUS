package com.hcmus.tenderus.ui.screens.admin.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@Composable
fun TopBar() {
    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_topbar),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxHeight(0.5f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .shadow(0.5.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TenderUSTheme {
        TopBar()
    }
}