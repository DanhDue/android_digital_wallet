/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.uikit.ui.widgets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danhdue.uikit.R

@Composable
fun LottieErrorView(
    e: Throwable,
    modifier: Modifier = Modifier,
    action: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically),
    ) {
        LottieView(
            file = "error.json",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
        )
        SmallSpacer()
        Text(
            text = e.localizedMessage ?: "",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            textAlign = TextAlign.Center,
        )
        SmallSpacer()
        Button(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
            onClick = action,
        ) {
            Text(text = stringResource(id = R.string.text_retry))
        }
    }
}

@Preview(
    showBackground = true,
    name = "Light Mode",
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
)
@Composable
private fun LottieErrorViewPreview() {
    MaterialTheme { LottieErrorView(e = Exception("Cannot load lottie file")) {} }
}
