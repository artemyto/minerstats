package misha.miner.screens.pc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import misha.miner.common.EthermineHelper
import misha.miner.common.EtherscanHelper
import misha.miner.common.fullEthAddr
import misha.miner.models.coinmarketcap.currency.CurrencyType
import misha.miner.models.storage.PCViewModel
import misha.miner.services.api.ApiManager
import misha.miner.services.ssh.SSHConnectionManager
import misha.miner.services.storage.StorageManager

class PCStatsViewModel : ViewModel() {

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status

    private val _outputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val outputList: LiveData<MutableList<String>> = _outputList
    private lateinit var outputListField: MutableList<String>

    private val commandList = mutableListOf(
        "CPU temp" to "sensors | grep Tdie | grep -E -o '[[:digit:]]{1,}.[[:digit:]].'",
        "Nvidia temp" to "nvidia-smi | grep -o \"[0-9]\\+C\"",
        "Amd temp" to "sensors | grep 'junction' | grep -o \"+[0-9]*\\.[0-9]*.C \"",
        "Amd mem temp" to "sensors | grep 'mem' | grep -o \"+[0-9]*\\.[0-9]*.C \"",
        "Nvidia driver" to "nvidia-smi | grep -o '[0-9]\\{3\\}\\.[0-9]\\{2\\}\\.\\{0,1\\}[0-9]\\{0,2\\}' | head -1",
        "Amd driver" to "DISPLAY=:0 glxinfo | grep \"OpenGL version\" | grep -o '[0-9]\\{2\\}\\.[0-9].[0-9]'",
        "Kernel" to "uname -r",
    )

    private var initialized = false

    fun initialize() {
        if (!initialized) {
            initialized = true
            run()
        }
    }

    fun runClicked() {
        run()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun run() {

        makeSshStats()
    }

    private fun makeSshStats() {
        outputListField = mutableListOf("PC miner stats:\n")
        val config = StorageManager.getStorage()
        config.pcList.forEachIndexed { index, item ->
            runSsh(index, item)
        }
    }

    private fun runSsh(index: Int, item: PCViewModel) {
        CoroutineScope(Dispatchers.IO).launch {

            SSHConnectionManager.open(
                hostname = item.address,
                port = item.port.toInt(),
                username = item.name,
                password = item.password
            )
            _status.emit("Connection to ${item.name}@${item.address}:${item.port} is established")

            val localList = mutableListOf("PC ${index + 1}:\n")
            commandList.forEach {
                localList.add("${it.first}: ${SSHConnectionManager.runCommand(command = it.second)}")
            }
            outputListField.addAll(localList)
            _outputList.postValue(outputListField)

            SSHConnectionManager.close()
        }
    }
}