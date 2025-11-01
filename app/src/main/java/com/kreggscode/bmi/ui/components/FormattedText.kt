package com.kreggscode.bmi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.bmi.ui.theme.*

@Composable
fun FormattedAIText(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val sections = parseAIText(text)
        sections.forEach { section ->
            when (section) {
                is TextSection.Header -> ColorfulHeader(section.text)
                is TextSection.SubHeader -> ColorfulSubHeader(section.text)
                is TextSection.BulletPoint -> BulletPointText(section.text)
                is TextSection.NumberedPoint -> NumberedPointText(section.number, section.text)
                is TextSection.Paragraph -> ParagraphText(section.text)
            }
        }
    }
}

@Composable
private fun ColorfulHeader(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AccentPurple.copy(alpha = 0.2f),
                        AccentPink.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun ColorfulSubHeader(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(AccentBlue, AccentTeal)
                        )
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentBlue,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun BulletPointText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AccentGreen)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NumberedPointText(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentOrange, AccentYellow)
                    )
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ParagraphText(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
        lineHeight = 22.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    )
}

sealed class TextSection {
    data class Header(val text: String) : TextSection()
    data class SubHeader(val text: String) : TextSection()
    data class BulletPoint(val text: String) : TextSection()
    data class NumberedPoint(val number: Int, val text: String) : TextSection()
    data class Paragraph(val text: String) : TextSection()
}

private fun parseAIText(text: String): List<TextSection> {
    val sections = mutableListOf<TextSection>()
    val lines = text.lines()
    
    var currentParagraph = StringBuilder()
    
    lines.forEach { line ->
        val trimmedLine = line.trim()
        
        when {
            // Skip empty lines
            trimmedLine.isEmpty() -> {
                if (currentParagraph.isNotEmpty()) {
                    sections.add(TextSection.Paragraph(currentParagraph.toString().trim()))
                    currentParagraph.clear()
                }
            }
            // Main headers (ALL CAPS or specific keywords)
            trimmedLine.matches(Regex("^[A-Z][A-Z\\s]+$")) && trimmedLine.length > 3 -> {
                if (currentParagraph.isNotEmpty()) {
                    sections.add(TextSection.Paragraph(currentParagraph.toString().trim()))
                    currentParagraph.clear()
                }
                sections.add(TextSection.Header(trimmedLine))
            }
            // Numbered points
            trimmedLine.matches(Regex("^\\d+\\.\\s+.*")) -> {
                if (currentParagraph.isNotEmpty()) {
                    sections.add(TextSection.Paragraph(currentParagraph.toString().trim()))
                    currentParagraph.clear()
                }
                val number = trimmedLine.substringBefore('.').toIntOrNull() ?: 1
                val text = trimmedLine.substringAfter('.').trim()
                sections.add(TextSection.NumberedPoint(number, text))
            }
            // Bullet points (-, •, or starting with specific keywords)
            trimmedLine.startsWith("-") || trimmedLine.startsWith("•") -> {
                if (currentParagraph.isNotEmpty()) {
                    sections.add(TextSection.Paragraph(currentParagraph.toString().trim()))
                    currentParagraph.clear()
                }
                val text = trimmedLine.removePrefix("-").removePrefix("•").trim()
                sections.add(TextSection.BulletPoint(text))
            }
            // Sub-headers (Title Case or ending with colon)
            (trimmedLine.endsWith(":") || trimmedLine.matches(Regex("^[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*$"))) 
                && !trimmedLine.contains(".") && trimmedLine.length < 50 -> {
                if (currentParagraph.isNotEmpty()) {
                    sections.add(TextSection.Paragraph(currentParagraph.toString().trim()))
                    currentParagraph.clear()
                }
                sections.add(TextSection.SubHeader(trimmedLine.removeSuffix(":")))
            }
            // Regular paragraph text
            else -> {
                if (currentParagraph.isNotEmpty()) {
                    currentParagraph.append(" ")
                }
                currentParagraph.append(trimmedLine)
            }
        }
    }
    
    // Add any remaining paragraph
    if (currentParagraph.isNotEmpty()) {
        sections.add(TextSection.Paragraph(currentParagraph.toString().trim()))
    }
    
    return sections
}

