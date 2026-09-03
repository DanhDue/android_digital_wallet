/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.net.URL
import java.util.Collections
import java.util.ServiceLoader

/**
 * `:app` owns ONE `META-INF/services/com.danhdue.platform.FeatureEntry` listing
 * every on-demand `FeatureEntry` FQCN (bundletool forbids two feature splits
 * shipping the same root resource — design §4.4). At runtime some of those
 * classes belong to splits that are not installed, so `ServiceLoader` raises
 * `ServiceConfigurationError` for them.
 *
 * [installersFrom] must skip the un-loadable entries and return the installers
 * of the loadable ones, never propagating the error.
 */
class DynamicFeatureInstallersTest {
    @Test
    fun `skips an entry whose class is not on the classpath yet`() {
        val loader =
            serviceLoaderOver(
                OkFeatureEntry::class.java.name,
                "com.danhdue.notinstalled.SomeSplitFeatureEntry",
            )

        val installers = installersFrom(loader)

        assertEquals(1, installers.size)
    }

    @Test
    fun `skips an un-loadable entry even when it comes first`() {
        val loader =
            serviceLoaderOver(
                "com.danhdue.notinstalled.SomeSplitFeatureEntry",
                OkFeatureEntry::class.java.name,
            )

        val installers = installersFrom(loader)

        assertEquals(1, installers.size)
    }

    @Test
    fun `skips an entry whose class fails to instantiate`() {
        val loader =
            serviceLoaderOver(
                ExplodingFeatureEntry::class.java.name,
                OkFeatureEntry::class.java.name,
            )

        val installers = installersFrom(loader)

        assertEquals(1, installers.size)
    }

    @Test
    fun `returns every installer when all entries load`() {
        val loader =
            serviceLoaderOver(
                OkFeatureEntry::class.java.name,
                SecondOkFeatureEntry::class.java.name,
            )

        val installers = installersFrom(loader)

        assertEquals(2, installers.size)
    }

    @Test
    fun `returns empty and does not throw when no entry loads`() {
        val loader = serviceLoaderOver("com.danhdue.notinstalled.OnlyFeatureEntry")

        val installers = installersFrom(loader)

        assertEquals(0, installers.size)
    }

    /**
     * A [ServiceLoader] for [FeatureEntry] backed by a synthetic
     * `META-INF/services` file naming [fqcns]; classes still resolve from the
     * real test classpath via the parent loader.
     */
    private fun serviceLoaderOver(vararg fqcns: String): ServiceLoader<FeatureEntry> {
        val serviceFile =
            File.createTempFile("FeatureEntry-services", ".txt").apply {
                writeText(fqcns.joinToString("\n", postfix = "\n"))
                deleteOnExit()
            }
        val loader =
            object : ClassLoader(this::class.java.classLoader) {
                override fun getResources(name: String) =
                    if (name == "META-INF/services/${FeatureEntry::class.java.name}") {
                        Collections.enumeration(listOf<URL>(serviceFile.toURI().toURL()))
                    } else {
                        super.getResources(name)
                    }
            }
        return ServiceLoader.load(FeatureEntry::class.java, loader)
    }
}

/** Loadable, instantiable [FeatureEntry] used by the tests above. */
class OkFeatureEntry : FeatureEntry {
    override fun installer(): EntryProviderInstaller = {}
}

/** A second loadable entry, to prove more than one is returned. */
class SecondOkFeatureEntry : FeatureEntry {
    override fun installer(): EntryProviderInstaller = {}
}

/** Stands in for a class present on the classpath but broken — throws on init. */
class ExplodingFeatureEntry : FeatureEntry {
    init {
        error("simulated: dynamic-feature class present but not usable")
    }

    override fun installer(): EntryProviderInstaller = {}
}
