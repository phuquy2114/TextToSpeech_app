package com.uits.texttospeech

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_SPEECH_INPUT = 1000

    // View from activity
    private lateinit var mTextTv: TextView
    private lateinit var mVoiceBtn: ImageButton
    private lateinit var mTextToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init TextToSpeech
        // Init TextToSpeech
        mTextToSpeech = TextToSpeech(this, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result: Int = mTextToSpeech.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Toast.makeText(
                        applicationContext, "This language is not supported!",
                        Toast.LENGTH_SHORT
                    )
                } else {
                    mTextTv.setEnabled(true)
                    mTextToSpeech.setPitch(0.6f)
                    mTextToSpeech.setSpeechRate(1.0f)
                    speak()
                }
            }
        })

        mTextTv = findViewById<TextView>(R.id.textTv)
        mVoiceBtn = findViewById<ImageButton>(R.id.voiceBtn)

        // Button click on show speech to text dialog
        mVoiceBtn.setOnClickListener {
            speak()
        }

        mTextTv.setOnClickListener {
            speakTextToSpeech(mTextTv.text.toString())
        }
    }

    private fun speakTextToSpeech(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun speak() {
        // Intent to show speech to text dialogs
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...")

        // Start Intent
        try {
            // If there was no error
            // showing dialogs
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            // If there was some error

            // get Message of error and show
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Receive voice input and handle it
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (requestCode != Activity.RESULT_OK && null != data) {
                    // get the text array from voice intent
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    // set the voice view
                    mTextTv.setText(result[0])
                }
            }
        }
    }

    override fun onDestroy() {
        mTextToSpeech.stop()
        mTextToSpeech.shutdown()
        super.onDestroy()
    }
}