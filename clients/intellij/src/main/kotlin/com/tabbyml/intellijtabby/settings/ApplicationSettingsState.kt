package com.tabbyml.intellijtabby.settings

import com.google.gson.annotations.SerializedName
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

@Service
@State(
  name = "com.tabbyml.intellijtabby.settings.ApplicationSettingsState",
  storages = [Storage("intellij-tabby.xml")]
)
class ApplicationSettingsState : PersistentStateComponent<ApplicationSettingsState> {
  enum class TriggerMode {
    @SerializedName("manual")
    MANUAL,

    @SerializedName("automatic")
    AUTOMATIC,
  }

  private val completionTriggerModeFlow = MutableStateFlow(TriggerMode.AUTOMATIC)
  val completionTriggerModeState = completionTriggerModeFlow.asStateFlow()
  var completionTriggerMode: TriggerMode = TriggerMode.AUTOMATIC
    set(value) {
      field = value
      completionTriggerModeFlow.value = value
    }

  private val serverEndpointFlow = MutableStateFlow("")
  val serverEndpointState = serverEndpointFlow.asStateFlow()
  var serverEndpoint: String = ""
    set(value) {
      field = value
      serverEndpointFlow.value = value
    }

  private val nodeBinaryFlow = MutableStateFlow("")
  val nodeBinaryState = nodeBinaryFlow.asStateFlow()
  var nodeBinary: String = ""
    set(value) {
      field = value
      nodeBinaryFlow.value = value
    }

  private val chatModelApiKeyFlow = MutableStateFlow("")
  val chatModelApiKeyState = chatModelApiKeyFlow.asStateFlow()
  var chatModelApiKey: String = ""
    set(value) {
      field = value
      chatModelApiKeyFlow.value = value
    }

  private val chatModelNameFlow = MutableStateFlow("")
  val chatModelNameState = chatModelNameFlow.asStateFlow()
  var chatModelName: String = ""
    set(value) {
      field = value
      chatModelNameFlow.value = value
    }

  private val chatModelPathFlow = MutableStateFlow("")
  val chatModelPathState = chatModelPathFlow.asStateFlow()
  var chatModelPath: String = ""
    set(value) {
      field = value
      chatModelPathFlow.value = value
    }

  private val isAnonymousUsageTrackingDisabledFlow = MutableStateFlow(false)
  val isAnonymousUsageTrackingDisabledState = isAnonymousUsageTrackingDisabledFlow.asStateFlow()
  var isAnonymousUsageTrackingDisabled: Boolean = false
    set(value) {
      field = value
      isAnonymousUsageTrackingDisabledFlow.value = value
    }

  private val notificationsMutedFlow = MutableStateFlow(listOf<String>())
  val notificationsMutedState = notificationsMutedFlow.asStateFlow()
  var notificationsMuted: List<String> = listOf()
    set(value) {
      field = value
      notificationsMutedFlow.value = value
    }

  data class State(
    val completionTriggerMode: TriggerMode,
    val serverEndpoint: String,
    val nodeBinary: String,
    val isAnonymousUsageTrackingDisabled: Boolean,
    val notificationsMuted: List<String>,
    val chatModelName: String,
    val chatModelPath: String,
    val chatModelApiKey: String,
  )

  val data: State
    get() = State(
      completionTriggerMode = completionTriggerMode,
      serverEndpoint = serverEndpoint,
      nodeBinary = nodeBinary,
      isAnonymousUsageTrackingDisabled = isAnonymousUsageTrackingDisabled,
      notificationsMuted = notificationsMuted,
      chatModelName = chatModelName,
      chatModelPath = chatModelPath,
      chatModelApiKey = chatModelApiKey,
    )

  val state = combine(
    completionTriggerModeState,
    serverEndpointState,
    nodeBinaryState,
    isAnonymousUsageTrackingDisabledState,
    notificationsMutedState,
    chatModelNameState,
    chatModelPathState,
    chatModelApiKeyState,
  ) { args ->
    State(
      completionTriggerMode = args[0] as TriggerMode,
      serverEndpoint = args[1] as String,
      nodeBinary = args[2] as String,
      isAnonymousUsageTrackingDisabled = args[3] as Boolean,
      notificationsMuted = args[4] as List<String>,
      chatModelName = args[5] as String,
      chatModelPath = args[6] as String,
      chatModelApiKey = args[7] as String,
    )
  }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, this.data)

  override fun getState(): ApplicationSettingsState {
    return this
  }

  override fun loadState(state: ApplicationSettingsState) {
    XmlSerializerUtil.copyBean(state, this)
  }
}