package misha.miner.screens.ssh

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pluto.plugins.logger.PlutoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import misha.miner.models.common.ErrorState
import misha.miner.models.storage.StorageViewModel
import misha.miner.services.ssh.SSHConnectionManager
import misha.miner.services.storage.StorageManager
import javax.inject.Inject

@HiltViewModel
class RunCommandViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status

    private val _output: MutableStateFlow<String> = MutableStateFlow("")
    val output: StateFlow<String> = _output

    private val _pc: MutableLiveData<String> = MutableLiveData("")
    val pc: LiveData<String> = _pc

    private val _pcLabelList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val pcLabelList: LiveData<MutableList<String>> = _pcLabelList

    private val _commandList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val commandList: LiveData<MutableList<String>> = _commandList

    val error: MutableStateFlow<ErrorState> = MutableStateFlow(ErrorState.None)

    private var command = ""

    private lateinit var storageContext: StorageViewModel

    private var notInitialized = true

    fun initialize() {
        if (notInitialized) {
            storageContext = storageManager.getStorage()
            _commandList.value = storageContext.commandList
            _pcLabelList.value = storageContext.pcList.mapIndexed { index, _ -> "PC ${index + 1}" }.toMutableList()
        }
    }

    fun commandSelected(command: String) {
        this.command = command
    }

    private var index = -1

    private var address = ""
    private var name = ""
    private var port = ""
    private var password = ""

    @Suppress("BlockingMethodInNonBlockingContext")
    fun runClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val config = storageManager.getStorage()

            val ssh = SSHConnectionManager()

            config.pcList.getOrNull(index)?.let {

                try {
                    ssh.open(
                        hostname = it.address,
                        port = it.port.toInt(),
                        username = it.name,
                        password = it.password
                    )

                    _status.emit("Connection to ${it.name}@${it.address}:${it.port} is established")
                    _output.emit(ssh.runCommand(command = command))
                } catch (e: Exception) {
                    error.emit(ErrorState.Error(e.message ?: ""))
                    PlutoLog.e("ssh", e.message.toString())
                }
            }

            commandList.value?.let {
                if (command !in it) {
                    var storageContext = storageManager.getStorage()
                    storageContext.commandList.add(command)
                    storageManager.update(storageContext)
                    storageContext = storageManager.getStorage()
                    _commandList.postValue(storageContext.commandList)
                }
            }

            ssh.close()
        }
    }

    fun selectedPC(pcName: String) {
        index = (pcName.filter { it.isDigit() }.toIntOrNull() ?: 0) - 1
        val pc = storageContext.pcList.getOrNull(index)
        pc?.let {
            address = it.address
            port = it.port
            name = it.name
            password = it.password

            _pc.value = pcName
        } ?: let {
            address = ""
            port = ""
            name = ""
            password = ""

            _pc.value = ""
        }
    }
}
