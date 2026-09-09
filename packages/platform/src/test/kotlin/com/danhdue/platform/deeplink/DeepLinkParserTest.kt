/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [DeepLinkParser].
 *
 * Verifies custom scheme and App Links parsing, path segments normalisation,
 * query parameter percent-decoding, unicode handling, duplicate parameter resolution (last wins),
 * trailing slashes, scheme case insensitivity, and malformed inputs.
 */
class DeepLinkParserTest {
    @Test
    fun `parse custom scheme with path and query parameters`() {
        val raw = "myapp://settings/profile?ref=push&version=1"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals(raw, link!!.raw)
        assertEquals("settings", link.feature)
        assertEquals(listOf("profile"), link.segments)
        assertEquals(mapOf("ref" to "push", "version" to "1"), link.params)
    }

    @Test
    fun `parse custom scheme with root feature only and no path`() {
        val raw = "myapp://settings"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals(raw, link!!.raw)
        assertEquals("settings", link.feature)
        assertEquals(emptyList<String>(), link.segments)
        assertEquals(emptyMap<String, String>(), link.params)
    }

    @Test
    fun `parse custom scheme with trailing slash yields empty segments`() {
        val raw = "myapp://settings/"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("settings", link!!.feature)
        assertEquals(emptyList<String>(), link.segments)
    }

    @Test
    fun `parse custom scheme with multi-level path`() {
        val raw = "myapp://settings/profile/security/pin"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("settings", link!!.feature)
        assertEquals(listOf("profile", "security", "pin"), link.segments)
    }

    @Test
    fun `parse custom scheme with path and trailing slash ignores trailing empty segment`() {
        val raw = "myapp://settings/profile/"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("settings", link!!.feature)
        assertEquals(listOf("profile"), link.segments)
    }

    @Test
    fun `parse App Links drops domain and treats first path segment as feature`() {
        val raw = "https://app.example.com/settings/profile?ref=push"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals(raw, link!!.raw)
        assertEquals("settings", link.feature)
        assertEquals(listOf("profile"), link.segments)
        assertEquals(mapOf("ref" to "push"), link.params)
    }

    @Test
    fun `parse App Links with root feature only and no further segments`() {
        val raw = "https://app.example.com/settings"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("settings", link!!.feature)
        assertEquals(emptyList<String>(), link.segments)
    }

    @Test
    fun `parse App Links with trailing slash`() {
        val raw = "https://app.example.com/settings/"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("settings", link!!.feature)
        assertEquals(emptyList<String>(), link.segments)
    }

    @Test
    fun `parse scheme case-insensitivity per RFC 3986`() {
        val customLink = DeepLinkParser.parse("MYAPP://settings/profile")
        assertNotNull(customLink)
        assertEquals("settings", customLink!!.feature)
        assertEquals(listOf("profile"), customLink.segments)

        val appLink = DeepLinkParser.parse("Https://app.example.com/settings/profile")
        assertNotNull(appLink)
        assertEquals("settings", appLink!!.feature)
        assertEquals(listOf("profile"), appLink.segments)
    }

    @Test
    fun `parse decodes percent-encoded query parameters`() {
        val raw = "myapp://settings/profile?greeting=Hello%20World&path=%2Fsub%2Ffolder"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("Hello World", link!!.params["greeting"])
        assertEquals("/sub/folder", link.params["path"])
    }

    @Test
    fun `parse decodes unicode in query parameters`() {
        val raw = "myapp://settings/profile?name=Nguy%E1%BB%85n"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("Nguyễn", link!!.params["name"])
    }

    @Test
    fun `parse duplicate query keys adheres to last-wins rule`() {
        val raw = "myapp://settings?tab=general&tab=security"
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals(1, link!!.params.size)
        assertEquals("security", link.params["tab"])
    }

    @Test
    fun `parse query param without value maps to empty string`() {
        val raw = "myapp://settings?flag&mode="
        val link = DeepLinkParser.parse(raw)

        assertNotNull(link)
        assertEquals("", link!!.params["flag"])
        assertEquals("", link!!.params["mode"])
    }

    @Test
    fun `parse returns null for empty or blank input`() {
        assertNull(DeepLinkParser.parse(""))
        assertNull(DeepLinkParser.parse("   "))
    }

    @Test
    fun `parse returns null for invalid URI syntax`() {
        assertNull(DeepLinkParser.parse("not a valid uri :::"))
    }

    @Test
    fun `parse returns null for custom scheme without authority or feature`() {
        assertNull(DeepLinkParser.parse("myapp://"))
        assertNull(DeepLinkParser.parse("myapp:///"))
    }

    @Test
    fun `parse returns null for App Link with no path segments`() {
        assertNull(DeepLinkParser.parse("https://app.example.com"))
        assertNull(DeepLinkParser.parse("https://app.example.com/"))
    }

    @Test
    fun `parse returns null for non-hierarchical or opaque URI`() {
        assertNull(DeepLinkParser.parse("mailto:user@example.com"))
    }
}
