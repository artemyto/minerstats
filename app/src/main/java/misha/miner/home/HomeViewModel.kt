package misha.miner.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _title: MutableSharedFlow<String> = MutableStateFlow("Screen 1: title 0")
    val title: Flow<String> = _title

    init {
        viewModelScope.launch {
            for (i in 1..5) {
                _title.emit("Screen 1: title $i")
                delay(2000)
            }
        }
    }
}