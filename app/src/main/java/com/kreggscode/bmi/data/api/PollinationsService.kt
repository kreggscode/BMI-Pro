package com.kreggscode.bmi.data.api

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class PollinationsService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://text.pollinations.ai/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(PollinationsApi::class.java)
    private val gson = Gson()

    suspend fun generateText(
        prompt: String,
        systemPrompt: String = "You are a helpful health and fitness assistant.",
        temperature: Float = 1.0f
    ): Result<String> {
        return try {
            val requestBody = mapOf(
                "model" to "openai",
                "messages" to listOf(
                    mapOf("role" to "system", "content" to systemPrompt),
                    mapOf("role" to "user", "content" to prompt)
                ),
                "temperature" to temperature,
                "max_tokens" to 2000
            )

            val json = gson.toJson(requestBody)
            val body = json.toRequestBody("application/json".toMediaType())

            val response = api.generateText(body)
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Empty response from API"))
                }
            } else {
                Result.failure(Exception("API request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeImage(
        bitmap: Bitmap,
        prompt: String,
        systemPrompt: String = "You are a nutrition expert analyzing food images.",
        temperature: Float = 1.0f
    ): Result<String> {
        return try {
            // Convert bitmap to base64
            val base64Image = bitmapToBase64(bitmap)

            val requestBody = mapOf(
                "model" to "openai",
                "messages" to listOf(
                    mapOf("role" to "system", "content" to systemPrompt),
                    mapOf(
                        "role" to "user",
                        "content" to listOf(
                            mapOf("type" to "text", "text" to prompt),
                            mapOf(
                                "type" to "image_url",
                                "image_url" to mapOf(
                                    "url" to "data:image/jpeg;base64,$base64Image"
                                )
                            )
                        )
                    )
                ),
                "temperature" to temperature,
                "max_tokens" to 2000
            )

            val json = gson.toJson(requestBody)
            val body = json.toRequestBody("application/json".toMediaType())

            val response = api.analyzeImage(body)
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Empty response from API"))
                }
            } else {
                Result.failure(Exception("API request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        // Resize bitmap to smaller size to avoid API errors
        val maxDimension = 512 // Reduced from 1024
        val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val scale = minOf(
                maxDimension.toFloat() / bitmap.width,
                maxDimension.toFloat() / bitmap.height
            )
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
        
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream) // Reduced quality
        val byteArray = outputStream.toByteArray()
        
        // Clean up if we created a new bitmap
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }
        
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    suspend fun analyzeBMI(
        bmi: Float,
        weight: Float,
        height: Float,
        category: String
    ): Result<String> {
        val prompt = """
            Analyze this BMI data and provide detailed health insights:
            - BMI: $bmi
            - Weight: $weight kg
            - Height: $height cm
            - Category: $category
            
            Provide:
            1. Health assessment
            2. Personalized recommendations
            3. Potential health risks or benefits
            4. Action steps to maintain or improve health
            
            Format the response with clear sections using these exact headers:
            HEALTH ASSESSMENT
            RECOMMENDATIONS
            HEALTH INSIGHTS
            ACTION STEPS
        """.trimIndent()

        return generateText(
            prompt = prompt,
            systemPrompt = "You are an expert health advisor providing BMI analysis. Format your response with clear sections and bullet points. Do not use asterisks or hashtags for formatting.",
            temperature = 1.0f
        )
    }

    suspend fun generateDietPlan(
        bmi: Float,
        goal: String,
        preferences: String = ""
    ): Result<String> {
        val prompt = """
            Create a personalized diet plan based on:
            - Current BMI: $bmi
            - Goal: $goal
            - Preferences: ${if (preferences.isNotEmpty()) preferences else "None specified"}
            
            Provide a comprehensive 7-day meal plan with:
            1. Daily calorie targets
            2. Meal suggestions for breakfast, lunch, dinner, and snacks
            3. Nutritional breakdown
            4. Shopping list highlights
            
            Format the response with clear sections using these exact headers:
            OVERVIEW
            DAILY CALORIE TARGET
            7-DAY MEAL PLAN
            NUTRITIONAL GUIDELINES
            SHOPPING LIST
        """.trimIndent()

        return generateText(
            prompt = prompt,
            systemPrompt = "You are a certified nutritionist creating personalized diet plans. Format your response with clear sections and organized meal plans. Do not use asterisks or hashtags for formatting.",
            temperature = 1.0f
        )
    }

    suspend fun analyzeFoodImage(bitmap: Bitmap): Result<String> {
        // Try image analysis first
        val prompt = """
            Analyze this food image and provide detailed nutritional information.
            
            CRITICAL INSTRUCTION: The VERY FIRST LINE of your response MUST be ONLY the specific food name. Nothing else on that line.
            
            Examples of correct first lines:
            - "Pepperoni Pizza"
            - "Grilled Chicken Salad"
            - "Chocolate Cake"
            - "Chicken Biryani"
            
            DO NOT write "Food Items", "Scanned Food", or any generic term.
            DO NOT write "Food Name:" or any label.
            JUST write the specific food name on the first line.
            
            After the food name, provide:
            
            Portion: [size estimate]
            
            Calories: [number]
            Protein: [number]g
            Carbs: [number]g
            Fat: [number]g
            Fiber: [number]g
            Sugar: [number]g
            
            Health Insights: [your insights]
            
            Example complete response:
            Pepperoni Pizza
            
            Portion: 2 slices (approximately 280g)
            
            Calories: 620
            Protein: 28g
            Carbs: 68g
            Fat: 26g
            Fiber: 4g
            Sugar: 8g
            
            Health Insights: High in calories and fat...
        """.trimIndent()

        val result = analyzeImage(
            bitmap = bitmap,
            prompt = prompt,
            systemPrompt = "You are a nutrition expert analyzing food images. CRITICAL: Your response MUST start with ONLY the specific food name on the first line (e.g., 'Pepperoni Pizza'). Do NOT use generic terms like 'Food Items' or 'Scanned Food'. Be specific with the actual food name.",
            temperature = 1.0f
        )
        
        // If image analysis fails, return a helpful message
        return if (result.isFailure) {
            Result.success("""
                Scanned Food Item
                
                Portion: 1 serving (estimated)
                
                Calories: 250
                Protein: 15g
                Carbs: 30g
                Fat: 8g
                Fiber: 3g
                Sugar: 5g
                
                Health Insights: Please note that image analysis is currently unavailable. These are estimated average values for a typical meal. For accurate nutritional information, please manually enter the food details or try scanning again.
            """.trimIndent())
        } else {
            result
        }
    }

    suspend fun chatWithAI(
        userMessage: String,
        conversationHistory: List<Map<String, String>> = emptyList()
    ): Result<String> {
        return try {
            val messages = mutableListOf<Map<String, String>>()
            messages.add(
                mapOf(
                    "role" to "system",
                    "content" to "You are a friendly and knowledgeable health, fitness, and nutrition assistant. Provide helpful, accurate, and encouraging advice. Format your responses clearly without using asterisks or hashtags."
                )
            )
            messages.addAll(conversationHistory)
            messages.add(mapOf("role" to "user", "content" to userMessage))

            val requestBody = mapOf(
                "model" to "openai",
                "messages" to messages,
                "temperature" to 1.0f,
                "max_tokens" to 2000
            )

            val json = gson.toJson(requestBody)
            val body = json.toRequestBody("application/json".toMediaType())

            val response = api.generateText(body)
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Empty response from API"))
                }
            } else {
                Result.failure(Exception("API request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

