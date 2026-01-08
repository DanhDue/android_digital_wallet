/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.domain.model

/**
 * Represents the main domain model for the Scanner feature.
 * This is the "clean" class used within the app (domain, presentation).
 *
 * @property id The unique identifier of the model.
 * @property data An example data field for the model.
 */
data class Scanner(
    val id: String,
    val data: String,
)
