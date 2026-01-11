/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
@file:Suppress("MatchingDeclarationName")

package com.danhdue.jetframework

import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink

data class LinkTextData(
    val text: String,
    val tag: String? = null,
    val annotation: String? = null,
    val onClick: ((str: AnnotatedString.Range<String>) -> Unit)? = null,
)

@Composable
fun LinkText(
    linkTextData: List<LinkTextData>,
    modifier: Modifier = Modifier,
) {
    val annotatedString = createAnnotatedString(linkTextData)
    BasicText(
        modifier = modifier,
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun createAnnotatedString(data: List<LinkTextData>): AnnotatedString =
    buildAnnotatedString {
        data.forEach { linkTextData ->
            if (linkTextData.tag != null && linkTextData.annotation != null) {
                val link =
                    LinkAnnotation.Clickable(
                        tag = linkTextData.tag,
                        styles =
                            TextLinkStyles(
                                style =
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        textDecoration = TextDecoration.Underline,
                                    ),
                            ),
                        linkInteractionListener = {
                            linkTextData.onClick?.invoke(
                                AnnotatedString.Range(
                                    item = linkTextData.annotation,
                                    // This range is context-dependent, but for simple callback it works
                                    start = 0,
                                    end = 0,
                                    tag = linkTextData.tag,
                                ),
                            )
                        },
                    )
                withLink(link) {
                    append(linkTextData.text)
                }
            } else {
                append(linkTextData.text)
            }
        }
    }
