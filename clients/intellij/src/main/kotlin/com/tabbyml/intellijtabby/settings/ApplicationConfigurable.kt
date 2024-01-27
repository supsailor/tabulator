package com.tabbyml.intellijtabby.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class ApplicationConfigurable : Configurable {
    private lateinit var settingsPanel: ApplicationSettingsPanel

    override fun getDisplayName(): String {
        return "MTS Copilot Settings"
    }

    override fun createComponent(): JComponent {
        settingsPanel = ApplicationSettingsPanel()
        return settingsPanel.mainPanel
    }

    override fun isModified(): Boolean {
        val settings = service<ApplicationSettingsState>()
        val keymapSettings = service<KeymapSettings>()
        return settingsPanel.completionTriggerMode != settings.completionTriggerMode ||
                (settingsPanel.keymapStyle != keymapSettings.getCurrentKeymapStyle() &&
                        settingsPanel.keymapStyle != KeymapSettings.KeymapStyle.CUSTOMIZE) ||
                settingsPanel.serverEndpoint != settings.serverEndpoint ||
                settingsPanel.nodeBinary != settings.nodeBinary ||
                settingsPanel.isAnonymousUsageTrackingDisabled != settings.isAnonymousUsageTrackingDisabled ||
                settingsPanel.chatModelName != settings.chatModelName ||
                settingsPanel.chatModelPath != settings.chatModelPath ||
                settingsPanel.chatModelApiKey != settings.chatModelApiKey
    }

    override fun apply() {
        val settings = service<ApplicationSettingsState>()
        val keymapSettings = service<KeymapSettings>()
        settings.completionTriggerMode = settingsPanel.completionTriggerMode
        keymapSettings.applyKeymapStyle(settingsPanel.keymapStyle)
        settings.serverEndpoint = settingsPanel.serverEndpoint
        settings.nodeBinary = settingsPanel.nodeBinary
        settings.isAnonymousUsageTrackingDisabled = settingsPanel.isAnonymousUsageTrackingDisabled
        settings.chatModelName = settingsPanel.chatModelName
        settings.chatModelPath = settingsPanel.chatModelPath
        settings.chatModelApiKey = settingsPanel.chatModelApiKey
    }

    override fun reset() {
        val settings = service<ApplicationSettingsState>()
        val keymapSettings = service<KeymapSettings>()
        settingsPanel.completionTriggerMode = settings.completionTriggerMode
        settingsPanel.keymapStyle = keymapSettings.getCurrentKeymapStyle()
        settingsPanel.serverEndpoint = settings.serverEndpoint
        settingsPanel.nodeBinary = settings.nodeBinary
        settingsPanel.isAnonymousUsageTrackingDisabled = settings.isAnonymousUsageTrackingDisabled
        settingsPanel.chatModelName = settings.chatModelName
        settingsPanel.chatModelPath = settings.chatModelPath
        settingsPanel.chatModelApiKey = settings.chatModelApiKey
    }
}