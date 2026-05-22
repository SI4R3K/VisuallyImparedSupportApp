package com.example.visuallyimpared.analyzer

/**
 * Handles cleaning and formatting of text recognized by ML Kit.
 * Working on this class via Unit Tests is much faster than 
 * redeploying the app and uploading photos.
 */
class TextPostProcessor {
    
    fun process(rawText: String): String {
        if (rawText.isBlank()) return ""

        return rawText.lines()
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { fixCommonOcrErrors(it) }
            .joinToString("\n")
    }

    private fun fixCommonOcrErrors(line: String): String {
        // Collapse multiple spaces into one
        var processed = line.replace(Regex("\\s+"), " ")
        
        // Add more processing logic here as needed
        // e.g., processed = processed.replace("|", "I")
        
        return processed
    }
}
