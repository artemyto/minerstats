package misha.miner.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import misha.miner.services.ssh.SSHConnectionManager
import misha.miner.services.storage.StorageManager

class HomeViewModel : ViewModel() {

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status

    private val _output: MutableStateFlow<String> = MutableStateFlow("")
    val output: StateFlow<String> = _output

    private val _commandList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val commandList: LiveData<MutableList<String>> = _commandList

    private var command = ""

    fun initialize() {
        _commandList.value = StorageManager.getStorage().commandList
    }
    
    fun commandSelected(command: String) {
        this.command = command
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun runClicked() {
        CoroutineScope(Dispatchers.IO).launch {
            val config = StorageManager.getStorage()
            SSHConnectionManager.open(
                hostname = config.address,
                port = config.port.toInt(),
                username = config.name,
                password = config.password
            )
            _status.emit("Connection to ${config.name}@${config.address}:${config.port} is established")
            _output.emit(SSHConnectionManager.runCommand(command = command))
        }
    }

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