/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.model

/**
 * Represents the main domain model for the Login feature.
 * This is the "clean" class used within the app (domain, presentation).
 *
 * @property id The unique identifier of the model.
 * @property data An example data field for the model.
 */
data class Login(
    val id: String,
    val data: String,
)
