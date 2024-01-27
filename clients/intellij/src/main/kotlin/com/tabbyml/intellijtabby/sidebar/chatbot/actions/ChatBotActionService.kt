package com.tabbyml.intellijtabby.sidebar.chatbot.actions

import ChatBot
import ChatCompletionRequest
import ChatGptHttp
import com.tabbyml.intellijtabby.settings.ApplicationSettingsState
import com.tabbyml.intellijtabby.sidebar.ui.ContentPanelComponent
import com.tabbyml.intellijtabby.sidebar.ui.PromptFormatter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service


class ChatBotActionService(private var actionType: ChatBotActionType) {
    val action = if (actionType == ChatBotActionType.EXPLAIN) "explain" else "refactor"

    fun setActionType(actionType: ChatBotActionType) {
        this.actionType = actionType
    }

    fun getLabel(): String {
        val capitalizedAction = action.capitalize()
        return "$capitalizedAction Code"
    }


    private fun getCodeSection(content: String): String {
        val pattern = "```(.+?)```".toRegex(RegexOption.DOT_MATCHES_ALL)
        val match = pattern.find(content)

        if (match != null) return match.groupValues[1].trim()
        return ""
    }


    private fun makeChatBotRequest(prompt: String): String {
        val settings = service<ApplicationSettingsState>()
        val modelName = settings.chatModelName.ifEmpty { "gpt-3.5-turbo" }
        val modelPath = settings.chatModelPath
        val apiKey = settings.chatModelApiKey

        if (apiKey.isEmpty()) {
            return "Please add an API Key in the ChatBot settings"
        }

        if (modelName.isEmpty()) {
            return "Please add an model name in the ChatBot settings"
        }

        if (modelPath.isEmpty()) {
            return "Please add an model path in the ChatBot settings"
        }

        val chatbot = ChatBot(ChatGptHttp(apiKey, modelPath))
        val system = "Be as helpful as possible and concise with your response"
        val request = ChatCompletionRequest(modelName, system)
        request.addMessage(prompt)
        val generateResponse = chatbot.generateResponse(request)
        return generateResponse.choices[0].message.content
    }

    fun handlePromptAndResponse(
        ui: ContentPanelComponent,
        prompt: PromptFormatter,
        replaceSelectedText: ((response: String) -> Unit)? = null
    ) {
        ui.add(prompt.getUIPrompt(), true)
        ui.add("Loading...")

        ApplicationManager.getApplication().executeOnPooledThread {
            val response = this.makeChatBotRequest(prompt.getRequestPrompt())
            ApplicationManager.getApplication().invokeLater {
                when {
                    actionType === ChatBotActionType.REFACTOR -> ui.updateReplaceableContent(response) {
                        replaceSelectedText?.invoke(response)
                    }
                    else -> ui.updateMessage(response)
                }
            }
        }
    }
}

enum class ChatBotActionType {
    REFACTOR,
    EXPLAIN
}
