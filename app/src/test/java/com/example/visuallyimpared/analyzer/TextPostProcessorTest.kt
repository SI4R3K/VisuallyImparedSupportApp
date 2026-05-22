package com.example.visuallyimpared.analyzer

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Run these tests by clicking the 'Play' icon in the gutter.
 * This allows you to test your text processing logic instantly 
 * without building or running the full app.
 */
class TextPostProcessorTest {

    private val processor = TextPostProcessor()

    @Test
    fun `process should return empty string for blank input`() {
        assertEquals("", processor.process(""))
        assertEquals("", processor.process("   "))
    }

    @Test
    fun `process should trim lines and remove empty lines`() {
        val input = """
            Line 1    
            
               Line 2
        """.trimIndent()
        
        val expected = "Line 1\nLine 2"
        assertEquals(expected, processor.process(input))
    }

    @Test
    fun `process should collapse multiple spaces`() {
        val input = "Too    many      spaces"
        val expected = "Too many spaces"
        assertEquals(expected, processor.process(input))
    }
}
