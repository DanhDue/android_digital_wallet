/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.domain.model

/**
 * Represents the main domain model for the MyNFTs feature.
 * This is the "clean" class used within the app (domain, presentation).
 *
 * @property id The unique identifier of the model.
 * @property data An example data field for the model.
 */
data class MyNFTs(
    val id: String,
    val data: String,
)
