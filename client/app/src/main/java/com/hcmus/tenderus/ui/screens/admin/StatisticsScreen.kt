package com.hcmus.tenderus.ui.screens.admin

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.R
import com.hcmus.tenderus.data.imageBaseUrl
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@Composable
fun StatisticsScreen(extract: (context: Context) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Account created statistics",
            color = colorResource(id = R.color.pink_primary),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        AsyncImage(
            model = "${imageBaseUrl}api/admin/statistics?duration=daily&event=ACCOUNT_CREATED",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            error = painterResource(id = R.drawable.ic_error),
        )
        AsyncImage(
            model = "${imageBaseUrl}api/admin/statistics?duration=monthly&event=ACCOUNT_CREATED",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            error = painterResource(id = R.drawable.ic_error)
        )
        AsyncImage(
            model = "${imageBaseUrl}api/admin/statistics?duration=yearly&event=ACCOUNT_CREATED",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            error = painterResource(id = R.drawable.ic_error)
        )
        Text(
            text = "Account online statistics",
            color = colorResource(id = R.color.pink_primary),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        AsyncImage(
            model = "${imageBaseUrl}api/admin/statistics?duration=daily&event=ACCOUNT_ONLINE",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            error = painterResource(id = R.drawable.ic_error)
        )
        AsyncImage(
            model = "${imageBaseUrl}api/admin/statistics?duration=monthly&event=ACCOUNT_ONLINE",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            error = painterResource(id = R.drawable.ic_error)
        )
        AsyncImage(
            model = "${imageBaseUrl}api/admin/statistics?duration=yearly&event=ACCOUNT_ONLINE",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            error = painterResource(id = R.drawable.ic_error)
        )
        Row(
            horizontalArrangement = Arrangement.Absolute.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { extract(context) }) {
                Text(text = "Export pdf")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    TenderUSTheme {
        StatisticsScreen(extract = {})
    }
}