package com.mustafa.movieguideapp.utils

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class ActivityResultApiObserver(
    private val registry: ActivityResultRegistry,
    private val onReceivedData: (String?) -> Unit
) : DefaultLifecycleObserver {

    private lateinit var startForResult: ActivityResultLauncher<Intent>


    override fun onCreate(owner: LifecycleOwner) {
        startForResult = registry.register(
            START_FOR_RESULT_KEY,
            owner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val voiceQuery =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                onReceivedData(voiceQuery?.get(0))
            }

        }
    }

    fun startVoiceRecognitionActivityForResult(intent: Intent) {
        startForResult.launch(intent)
    }


    companion object {
        private const val START_FOR_RESULT_KEY = "START_FOR_RESULT_KEY"
    }
}