package amulp.com.tomatotimer.ui.main

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {
    private var SHORT_BREAK_LENGTH = 300000L
    private var LONG_BREAK_LENGTH = 1500000L
    private var POMODORO_LENGTH = 1500000L
    private var INTERVALS = 4
    private var currentInterval = 1

    var currentTimer =  MutableLiveData<String>()
    var currentTimerType = MutableLiveData<String>()
    var buttonText = MutableLiveData<String>()
    var timerInMs = MutableLiveData<Long>()

    private lateinit var timer:CountDownTimer

    var timerRunning = false

    init {
        currentTimerType.postValue("None")
        timerInMs.postValue(POMODORO_LENGTH)
        buttonText.postValue("Start")
        currentTimer.postValue("Not Started")
    }


    fun startPomodoro(){
        currentInterval = 1
        if(!timerRunning) {
            timerRunning = true
            timerInMs.postValue(POMODORO_LENGTH)
            currentTimerType.postValue("Pomodoro")
            startTimer(POMODORO_LENGTH, "Pomodoro")
            buttonText.postValue("Stop")
        }
    }

    fun cancelTimer(){
        if(timerRunning){
            timer.cancel()
            timerRunning = false
            currentTimerType.postValue("None")
            currentTimer.postValue("Timer Cancelled")
            buttonText.postValue("Start")
        }
    }

    private fun startTimer(timerLength:Long, timerType:String){
        timer = object : CountDownTimer(timerLength, 10) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimer.postValue("${TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)}:${String.format("%1$02d",TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60)}")
            }

            override fun onFinish() {
                if(timerType == "Pomodoro") {
                    if(currentInterval < INTERVALS ) {
                        timerInMs.postValue(SHORT_BREAK_LENGTH)
                        currentTimerType.postValue("Short Break")
                        startTimer(SHORT_BREAK_LENGTH, "Short Break")
                    }
                    else {
                        timerInMs.postValue(LONG_BREAK_LENGTH)
                        currentTimerType.postValue("Long Break")
                        startTimer(LONG_BREAK_LENGTH, "Long Break")
                        currentInterval = 1
                    }
                }
                else {
                    currentInterval++
                    timerInMs.postValue(POMODORO_LENGTH)
                    currentTimerType.postValue("Pomodoro")
                    startTimer(POMODORO_LENGTH, "Pomodoro")
                }
            }
        }.start()
    }
}


