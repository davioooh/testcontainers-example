package com.davioooh.tctest.data

import org.testcontainers.shaded.org.apache.commons.io.IOUtils
import java.io.IOException
import java.nio.charset.Charset

object ResourceFetcher {
    fun readResource(name: String): String {
        return try {
            javaClass.classLoader.getResourceAsStream(name).use { stream ->
                IOUtils.toString(stream, Charset.defaultCharset())
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}