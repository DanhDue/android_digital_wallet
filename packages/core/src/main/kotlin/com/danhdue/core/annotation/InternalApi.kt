/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.annotation

/**
 * Marks declarations that are internal to the super app architecture
 * and excluded from public binary contract validation (BCV).
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal API for the architecture. It is not part of the public contract.",
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS,
)
annotation class InternalApi
