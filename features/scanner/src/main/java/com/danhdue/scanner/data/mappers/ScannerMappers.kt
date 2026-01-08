/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.data.mappers

import com.danhdue.scanner.data.model.ScannerDto
import com.danhdue.scanner.domain.model.Scanner

/**
 * Maps a ScannerDto (Data Layer) object to a Scanner (Domain Layer) object.
 *
 * @return The mapped Scanner object.
 */
fun ScannerDto.toDomain(): Scanner =
    Scanner(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
