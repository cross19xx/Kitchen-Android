package com.cross19xx.filmnest.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DateUtilsTest {

    @Test
    fun `formatDate converts a valid date to default output format`() {
        val result = DateUtils.formatDate("2026-06-27")
        assertEquals("June 27, 2026", result)
    }

    @Test
    fun `returns null if date passed is null`() {
        val result = DateUtils.formatDate(input = null)
        assertNull(result)
    }

    @Test
    fun `returns null for blank inputs`() {
        val result = DateUtils.formatDate(input = "")
        assertNull(result)
    }

    @Test
    fun `formatDate returns null if the date passed is an invalid date`() {
        val result = DateUtils.formatDate(input = "An invalid date passed")
        assertNull(result)
    }

    @Test
    fun `custom input formats return a valid output`() {
        val assertion = "June 27, 2026"

        val resultOne = DateUtils.formatDate(input = "27-06-2026", inputFormat = "dd-MM-yyyy")
        assertEquals(assertion, resultOne)

        val resultTwo = DateUtils.formatDate(input = "06-27-2026", inputFormat = "MM-dd-yyyy")
        assertEquals(assertion, resultTwo)
    }

    @Test
    fun `custom output format returns a valid output`() {
        val input = "2026-06-27"

        val result = DateUtils.formatDate(input, outputFormat = "EEEE, MMMM dd, yyyy")
        assertEquals("Saturday, June 27, 2026", result)
    }
}
