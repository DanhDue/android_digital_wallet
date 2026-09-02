/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.uikit.ui.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DefaultSpacer(modifier: Modifier = Modifier) =
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(4.dp),
    )

@Composable
fun ExtraSmallSpacer(modifier: Modifier = Modifier) =
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(6.dp),
    )

@Composable
fun SmallSpacer(modifier: Modifier = Modifier) =
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(12.dp),
    )

@Composable
fun MediumSpacer(modifier: Modifier = Modifier) =
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(18.dp),
    )

@Composable
fun LargeSpacer(modifier: Modifier = Modifier) =
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(24.dp),
    )

@Composable
fun ExtraLargeSpacer(modifier: Modifier = Modifier) =
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(30.dp),
    )
