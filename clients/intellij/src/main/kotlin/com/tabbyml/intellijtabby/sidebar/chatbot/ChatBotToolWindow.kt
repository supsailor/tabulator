package com.tabbyml.intellijtabby.sidebar.chatbot

import com.tabbyml.intellijtabby.sidebar.ui.ContentPanelComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.tabbyml.intellijtabby.sidebar.chatbot.actions.ChatBotActionService
import com.tabbyml.intellijtabby.sidebar.chatbot.actions.ChatBotActionType


class ChatBotToolWindow : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val chatBotActionService = ChatBotActionService(ChatBotActionType.EXPLAIN)
        val contentPanel = ContentPanelComponent(chatBotActionService)
        val createContent = contentFactory?.createContent(contentPanel, "ChatBot", false)
        toolWindow.contentManager.addContent(createContent!!)
    }
}
