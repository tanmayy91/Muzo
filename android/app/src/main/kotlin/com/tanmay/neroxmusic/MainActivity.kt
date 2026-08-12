package com.tanmay.neroxmusic

import android.media.audiofx.EnvironmentalReverb
import android.media.audiofx.Equalizer
import com.ryanheise.audioservice.AudioServiceActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : AudioServiceActivity() {
    private val CHANNEL = "com.tanmay.neroxmusic/audio_effects"
    private var reverb: EnvironmentalReverb? = null
    private var equalizer: Equalizer? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "enableReverb" -> {
                    val sessionId = call.argument<Int>("sessionId")
                    val enable = call.argument<Boolean>("enable") ?: false
                    if (sessionId != null) {
                        toggleReverb(sessionId, enable)
                        result.success(null)
                    } else {
                        result.error("INVALID_SESSION_ID", "Session ID is null", null)
                    }
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun toggleReverb(sessionId: Int, enable: Boolean) {
        try {
            if (enable) {
                if (equalizer == null) {
                    equalizer = Equalizer(0, sessionId)
                    equalizer?.enabled = true
                }

                if (reverb == null) {
                    reverb = EnvironmentalReverb(0, sessionId)
                    reverb?.decayTime = 2000
                    reverb?.roomLevel = -1000
                    reverb?.reverbLevel = 0
                    reverb?.reverbDelay = 50
                    reverb?.enabled = true
                } else {
                    reverb?.enabled = true
                }
            } else {
                reverb?.enabled = false
                reverb?.release()
                reverb = null

                equalizer?.enabled = false
                equalizer?.release()
                equalizer = null
            }
        } catch (e: Exception) {
            println("Error toggling reverb: ${e.message}")
        }
    }
}