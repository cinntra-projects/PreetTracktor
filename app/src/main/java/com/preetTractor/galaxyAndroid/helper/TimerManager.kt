package com.preetTractor.galaxyAndroid.helper

import kotlinx.coroutines.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

object TimerManager {

    private val timerLiveData = MutableLiveData<String>()
    private var timerJob: Job? = null
    private var secondsElapsed = 0

    fun startTimer(scope: CoroutineScope, startTime: String, timeFormat: String = "yyyy-MM-dd hh:mm:ss a") {
        stopTimer() // Stop any existing timer
        secondsElapsed = calculateElapsedSeconds(startTime, timeFormat)

        timerJob = scope.launch {
            while (isActive) {
                timerLiveData.postValue(formatTime(secondsElapsed))
                delay(1000)
                secondsElapsed++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        stopTimer()
        secondsElapsed = 0
        timerLiveData.postValue(formatTime(secondsElapsed))
    }

    fun getTimerLiveData(): LiveData<String> = timerLiveData

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    private fun calculateElapsedSeconds(startTime: String, timeFormat: String): Int {
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        val startDate = dateFormat.parse(startTime)
        val currentDate = Date()

        return if (startDate != null) {
            ((currentDate.time - startDate.time) / 1000).toInt()
        } else {
            0 // Default to 0 if parsing fails
        }
    }


/*
    class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val timerTextView = findViewById<TextView>(R.id.timerTextView)

            // Observe the timer's LiveData
            TimerManager.getTimerLiveData().observe(this) { time ->
                timerTextView.text = time
            }

            // Start the timer with a specific time
            findViewById<Button>(R.id.startButton).setOnClickListener {
                val startTime = "2024-11-20 10:30:00 AM" // Example start time
                TimerManager.startTimer(lifecycleScope, startTime)
            }

            // Stop the timer
            findViewById<Button>(R.id.stopButton).setOnClickListener {
                TimerManager.stopTimer()
            }

            // Reset the timer
            findViewById<Button>(R.id.resetButton).setOnClickListener {
                TimerManager.resetTimer()
            }
        }
    }*/


}
