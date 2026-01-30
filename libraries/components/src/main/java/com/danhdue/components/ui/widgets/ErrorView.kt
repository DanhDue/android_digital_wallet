/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.components.ui.widgets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.danhdue.components.ui.theme.Red
import com.danhdue.libraries.components.R

@Suppress("ForbiddenComment")
@Composable
fun ErrorView(
    e: Throwable,
    modifier: Modifier = Modifier,
    action: () -> Unit,
) {
    // handleThrowable- create extension method
    e.printStackTrace()
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically),
    ) {
        Icon(
            painter = rememberVectorPainter(Icons.Default.ErrorOutline),
            contentDescription = null,
            tint = Red,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
        )
        SmallSpacer()
        Text(
            text = "${e.localizedMessage}",
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
private fun ErrorPageViewPreview() {
    MaterialTheme { ErrorView(e = Exception("There is an error")) {} }
}
