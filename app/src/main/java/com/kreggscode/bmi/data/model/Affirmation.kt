package com.kreggscode.bmi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affirmations")
data class Affirmation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val category: String, // Health, Confidence, Motivation, Success, Peace, Gratitude
    val author: String = "",
    val isFavorite: Boolean = false,
    val backgroundColor: String = "#6366F1", // Hex color for card background
    val createdAt: Long = System.currentTimeMillis()
)

// Pre-populated affirmations
object AffirmationData {
    val defaultAffirmations = listOf(
        Affirmation(text = "I am becoming healthier and stronger every day", category = "Health", backgroundColor = "#10B981"),
        Affirmation(text = "My body is a temple, and I treat it with love and respect", category = "Health", backgroundColor = "#3B82F6"),
        Affirmation(text = "I choose nourishing foods that fuel my body and mind", category = "Health", backgroundColor = "#8B5CF6"),
        Affirmation(text = "I am worthy of taking time to care for myself", category = "Health", backgroundColor = "#EC4899"),
        Affirmation(text = "Every healthy choice I make is an investment in my future", category = "Health", backgroundColor = "#F59E0B"),
        
        Affirmation(text = "I am confident in my ability to achieve my health goals", category = "Confidence", backgroundColor = "#EF4444"),
        Affirmation(text = "I believe in myself and my journey to wellness", category = "Confidence", backgroundColor = "#6366F1"),
        Affirmation(text = "I am strong, capable, and resilient", category = "Confidence", backgroundColor = "#14B8A6"),
        Affirmation(text = "I trust my body's wisdom and listen to its needs", category = "Confidence", backgroundColor = "#F97316"),
        
        Affirmation(text = "Today, I choose to move my body with joy", category = "Motivation", backgroundColor = "#8B5CF6"),
        Affirmation(text = "I am motivated to make healthy choices that serve me", category = "Motivation", backgroundColor = "#10B981"),
        Affirmation(text = "Progress, not perfection, is my goal", category = "Motivation", backgroundColor = "#3B82F6"),
        Affirmation(text = "I am committed to my health and well-being", category = "Motivation", backgroundColor = "#EC4899"),
        
        Affirmation(text = "I am at peace with my body and grateful for all it does", category = "Peace", backgroundColor = "#6366F1"),
        Affirmation(text = "I release stress and embrace calm in this moment", category = "Peace", backgroundColor = "#14B8A6"),
        Affirmation(text = "My mind is clear, and my heart is open", category = "Peace", backgroundColor = "#8B5CF6"),
        
        Affirmation(text = "I am grateful for my health and vitality", category = "Gratitude", backgroundColor = "#F59E0B"),
        Affirmation(text = "I appreciate my body for all the amazing things it does", category = "Gratitude", backgroundColor = "#10B981"),
        Affirmation(text = "I am thankful for this new day and new opportunities", category = "Gratitude", backgroundColor = "#EC4899"),
        
        Affirmation(text = "I am creating the healthy life I deserve", category = "Success", backgroundColor = "#EF4444"),
        Affirmation(text = "My healthy habits are leading me to success", category = "Success", backgroundColor = "#3B82F6"),
        Affirmation(text = "I celebrate every step forward on my wellness journey", category = "Success", backgroundColor = "#8B5CF6")
    )
}

