package misha.miner.screens.pc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import misha.miner.models.common.ErrorState
import misha.miner.models.storage.PCViewModel
import misha.miner.services.ssh.SSHConnectionManager
import misha.miner.services.storage.StorageManager
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class PCStatsViewModel @Inject constructor(
    private val storageManager: StorageManager
): ViewModel() {

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status
    private var statuses: MutableList<String> = mutableListOf()

    private val _outputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val outputList: LiveData<MutableList<String>> = _outputList
    private lateinit var outputListField: MutableList<String>

    private var listOfOutputs: MutableList<MutableList<String>> = mutableListOf()

    private val _pcLabelList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val pcLabelList: LiveData<MutableList<String>> = _pcLabelList
    private var pcLabelListValue: MutableList<String> = mutableListOf()

    private val _selectedPC: MutableStateFlow<Int> = MutableStateFlow(-1)
    val selectedPC: StateFlow<Int> = _selectedPC
    private var selectedIndex = -1

    val error: MutableStateFlow<ErrorState> = MutableStateFlow(ErrorState.None)

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val commandList = mutableListOf(
        Command.SimpleCommand(
            name = "CPU temp",
            command = "sensors | grep Tdie | grep -E -o '[[:digit:]]{1,}.[[:digit:]].'"
        ),
        Command.SimpleCommand(
            name = "Nvidia temp",
            command = "nvidia-smi | grep -o \"[0-9]\\+C\""
        ),
        Command.SimpleCommand(
            name = "Amd temp",
            command = "sensors | grep 'junction' | grep -o \"+[0-9]*\\.[0-9]*.C \""
        ),
        Command.SimpleCommand(
            name = "Amd mem temp",
            command = "sensors | grep 'mem' | grep -o \"+[0-9]*\\.[0-9]*.C \""
        ),
        Command.ActionCommand(
            name = "Amd fan",
            command = "sensors | grep 'fan'",
            action = this::processFanOutput
        ),
        Command.SimpleCommand(
            name = "Amd power",
            command = "sensors | grep 'power' | grep -o \"[0-9]*\\.[0-9]* W \""
        ),
        Command.SimpleCommand(
            name = "Nvidia driver",
            command = "nvidia-smi | grep -o '[0-9]\\{3\\}\\.[0-9]\\{2\\}\\.\\{0,1\\}[0-9]\\{0,2\\}' | head -1"
        ),
        Command.SimpleCommand(
            name = "Amd driver",
            command = "DISPLAY=:0 glxinfo | grep \"OpenGL version\" | grep -o '[0-9]\\{2\\}\\.[0-9].[0-9]'"
        ),
        Command.SimpleCommand(name = "Kernel",
            command = "uname -r"
        ),
    )

    private var initialized = false

    fun initialize() {
        if (!initialized) {
            initialized = true

            _selectedPC.value = 0
            selectedIndex = 0

            run()
        }
    }

    fun runClicked() {
        _isRefreshing.value = true
        run()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun run() {

        makeSshStats()
    }

    private fun makeSshStats() {
        outputListField = mutableListOf("PC miner stats:\n")
        val config = storageManager.getStorage()

        listOfOutputs = mutableListOf()
        statuses = mutableListOf()
        pcLabelListValue = mutableListOf()

        config.pcList.forEachIndexed { index, item ->
            listOfOutputs.add(mutableListOf())
            statuses.add("")
            pcLabelListValue.add("PC ${index + 1}")
            runSsh(index, item)
        }

        _pcLabelList.value = pcLabelListValue
    }

    private fun runSsh(index: Int, item: PCViewModel) {
        CoroutineScope(Dispatchers.IO).launch {

            val ssh = SSHConnectionManager()

            try {
                ssh.open(
                    hostname = item.address,
                    port = item.port.toInt(),
                    username = item.name,
                    password = item.password
                )
                statuses[index] =
                    "Connection to ${item.name}@${item.address}:${item.port} is established"

                listOfOutputs[index] = mutableListOf("PC ${index + 1}:\n")
                commandList.forEach {
                    when (it) {
                        is Command.SimpleCommand -> {
                            val commandResult = ssh.runCommand(command = it.command)
                            if (commandResult.isNotBlank()) {
                                listOfOutputs[index].add("${it.name}: $commandResult")
                            }
                        }

                        is Command.ActionCommand -> {
                            val commandResult = ssh.runCommand(command = it.command)
                            if (commandResult.isNotBlank()) {
                                val processed = it.action(commandResult)
                                listOfOutputs[index].add("${it.name}: $processed")
                            }
                        }
                    }

                    if (index == selectedIndex) {
                        _outputList.postValue(listOfOutputs[index].toMutableList())
                        _status.emit(statuses[index])
                        _isRefreshing.postValue(false)
                    }
                }
            } catch (e: Exception) {
                error.emit(ErrorState.Error(e.message ?: ""))

                if (index == selectedIndex) {
                    _isRefreshing.postValue(false)
                }
            }
            ssh.close()
        }
    }

    fun selectPC(index: Int) {
        _selectedPC.value = index
        selectedIndex = index

        listOfOutputs.getOrNull(index)?.let {
            _outputList.value = it
        }
    }

    /* Example string:
       fan1:        3266 RPM  (min =    0 RPM, max = 4600 RPM) */
    private fun processFanOutput(string: String): String {

        val list = """\d+""".toRegex().findAll(string).toList()

        val currentRate = list[1].value.toDouble()
        val maxRate = list.last().value.toDouble()

        val percentage = (currentRate / maxRate * 100).roundToInt()

        return "${currentRate.toInt()} RPM / $percentage%\n"
    }

    sealed class Command {

        data class SimpleCommand(
            val name: String,
            val command: String,
        ) : Command()

        data class ActionCommand(
            val name: String,
            val command: String,
            val action: (String) -> String,
        ) : Command()
    }
}