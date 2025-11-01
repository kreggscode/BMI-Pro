package com.kreggscode.bmi.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.api.PollinationsService
import com.kreggscode.bmi.data.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class ChatViewModel : ViewModel() {

    private val pollinationsService = PollinationsService()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val conversationHistory = mutableListOf<Map<String, String>>()

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            // Add user message
            val userChatMessage = ChatMessage(
                content = userMessage,
                isUser = true
            )
            _messages.value = _messages.value + userChatMessage

            // Add to conversation history
            conversationHistory.add(mapOf("role" to "user", "content" to userMessage))

            // Show loading
            _isLoading.value = true

            try {
                val result = pollinationsService.chatWithAI(userMessage, conversationHistory)
                result.onSuccess { aiResponse ->
                    // Add AI response
                    val aiChatMessage = ChatMessage(
                        content = aiResponse,
                        isUser = false
                    )
                    _messages.value = _messages.value + aiChatMessage

                    // Add to conversation history
                    conversationHistory.add(mapOf("role" to "assistant", "content" to aiResponse))
                }.onFailure { error ->
                    val errorMessage = ChatMessage(
                        content = "Sorry, I encountered an error: ${error.message}",
                        isUser = false
                    )
                    _messages.value = _messages.value + errorMessage
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "Sorry, I encountered an error: ${e.message}",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessageWithImage(context: Context, imageUri: Uri, userMessage: String) {
        viewModelScope.launch {
            // Add user message
            val userChatMessage = ChatMessage(
                content = "$userMessage 📷",
                isUser = true
            )
            _messages.value = _messages.value + userChatMessage

            // Show loading
            _isLoading.value = true

            try {
                // Convert URI to Bitmap
                val bitmap = uriToBitmap(context, imageUri)
                if (bitmap != null) {
                    // Use Pollinations API with base64 image
                    val result = pollinationsService.analyzeImage(
                        bitmap = bitmap,
                        prompt = userMessage,
                        systemPrompt = "You are a helpful health and nutrition assistant. Analyze this image and provide detailed insights about the food, health-related items, or activities shown. Be specific and helpful.",
                        temperature = 1.0f
                    )
                    
                    result.onSuccess { aiResponse ->
                        // Add AI response
                        val aiChatMessage = ChatMessage(
                            content = aiResponse,
                            isUser = false
                        )
                        _messages.value = _messages.value + aiChatMessage

                        // Add to conversation history
                        conversationHistory.add(mapOf("role" to "user", "content" to userMessage))
                        conversationHistory.add(mapOf("role" to "assistant", "content" to aiResponse))
                    }.onFailure { error ->
                        val errorMessage = ChatMessage(
                            content = "Sorry, I couldn't analyze the image: ${error.message}",
                            isUser = false
                        )
                        _messages.value = _messages.value + errorMessage
                    }
                    
                    bitmap.recycle()
                } else {
                    val errorMessage = ChatMessage(
                        content = "Sorry, I couldn't load the image. Please try again.",
                        isUser = false
                    )
                    _messages.value = _messages.value + errorMessage
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "Sorry, I encountered an error: ${e.message}",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
        conversationHistory.clear()
    }
}

