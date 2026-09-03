/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.extension

val Any.classTag: String get() = this.javaClass.canonicalName.orEmpty()

@Suppress("UnnecessaryInheritance")
val Any.methodTag get() = classTag + object : Any() {}.javaClass.enclosingMethod?.name

fun Any.hashCodeAsString(): String = hashCode().toString()

inline fun <reified T : Any> Any.cast(): T = this as T
