package misha.miner.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import misha.miner.services.ssh.SSHConnectionManager
import misha.miner.services.storage.StorageManager

class HomeViewModel : ViewModel() {

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status

    private val _outputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val outputList: LiveData<MutableList<String>> = _outputList

    private val commandList = mutableListOf(

        "Amd mem temp" to "sensors | grep 'mem' | grep -o \"+[0-9]*\\.[0-9]*.C \""
    )
    private var outputListField = mutableListOf<String>()

    private var initialized = false

    fun initialize() {
        if (!initialized) {
            initialized = true
            run()
        }
    }

    private var opened = false

    fun runClicked() {
        run()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun run() {
        CoroutineScope(Dispatchers.IO).launch {
            val config = StorageManager.getStorage()

            if (!opened) {
                SSHConnectionManager.open(
                    hostname = config.address,
                    port = config.port.toInt(),
                    username = config.name,
                    password = config.password
                )
                opened = true
            }

            _status.emit("Connection to ${config.name}@${config.address}:${config.port} is established")

            outputListField = mutableListOf()
            commandList.forEach {
                outputListField.add("${it.first}: ${SSHConnectionManager.runCommand(command = it.second)}")
                _outputList.postValue(outputListField)
            }
        }
    }
}