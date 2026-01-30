/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.components.ui.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danhdue.components.ui.theme.GreenVogue
import com.danhdue.libraries.components.R

@Composable
fun WalletActionBar(
    modifier: Modifier = Modifier,
    onSendClick: () -> Unit = {},
    onReceiveClick: () -> Unit = {},
    onBuyClick: () -> Unit = {},
    onStakingClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WalletActionButton(
            iconRes = R.drawable.ic_send,
            labelRes = R.string.wallet_action_send,
            onClick = onSendClick,
        )

        WalletActionButton(
            iconRes = R.drawable.ic_receive,
            labelRes = R.string.wallet_action_receive,
            onClick = onReceiveClick,
        )

        WalletActionButton(
            iconRes = R.drawable.ic_buy,
            labelRes = R.string.wallet_action_buy,
            onClick = onBuyClick,
        )

        WalletActionButton(
            iconRes = R.drawable.ic_staking,
            labelRes = R.string.wallet_action_staking,
            onClick = onStakingClick,
        )
    }
}

@Composable
private fun WalletActionButton(
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Circular icon background
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(id = labelRes),
            modifier =
                Modifier
                    .size(68.dp),
        )
        Text(
            text = stringResource(id = labelRes),
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
            color = GreenVogue,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WalletActionBarPreview() {
    WalletActionBar()
}
