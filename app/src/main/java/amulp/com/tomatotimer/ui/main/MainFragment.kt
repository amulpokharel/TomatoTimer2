package amulp.com.tomatotimer.ui.main

import amulp.com.tomatotimer.R
import amulp.com.tomatotimer.databinding.MainFragmentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    lateinit var binding:MainFragmentBinding
    private lateinit var viewModel: MainViewModel

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(viewModel.timerRunning) {
            outState.putLong("Time Remaining", circleTimer.getRemainingTime())
            viewModel.timerInMs.removeObserver(circleTimer.timerObserver)
            circleTimer.stop()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModelData = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(viewModel.timerRunning){
            viewModel.timerInMs.observe(this, circleTimer.timerObserver)
            val tempLong = viewModel.timerInMs.value!!
            viewModel.timerInMs.postValue(tempLong - savedInstanceState!!.getLong("Time Remaining"))
        }
        timerButton.setOnClickListener { when(viewModel.timerRunning){
            true -> {
                viewModel.cancelTimer()
                viewModel.timerInMs.removeObserver(circleTimer.timerObserver)
                circleTimer.stop()
            }
            false -> {
                viewModel.timerInMs.observe(this, circleTimer.timerObserver)
                viewModel.startPomodoro()
            }
        }}
    }

}
