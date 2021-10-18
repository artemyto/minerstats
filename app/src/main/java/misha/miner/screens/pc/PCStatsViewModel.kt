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

    companion object {
        const val Amd = "Amd"
        const val Intel = "Intel"
        const val Nvidia = "Nvidia"
    }

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
            name = "AMD CPU\n\n       temp: ",
            command = "sensors | grep Tdie | grep -E -o '[[:digit:]]{1,}.[[:digit:]].'"
        ),
        Command.ActionCommand(
            name = Intel,
            command = "sensors | grep 'Physical id 0'",
            action = this::processIntel
        ),
        Command.ActionCommand(
            name = Nvidia,
            command = "nvidia-smi | grep '[0-9]\\+C\\|Driver'",
            action = this::processNvidia
        ),
        Command.ActionCommand(
            name = Amd,
            command = "sensors | grep 'fan\\|junction\\|mem\\|power'",
            action = this::processAmd
        ),
        //Amd driver
        Command.SimpleCommand(
            name = "       driver: ",
            command = "DISPLAY=:0 glxinfo | grep 'OpenGL version' | grep -o '[0-9]\\{2\\}\\.[0-9].[0-9]'"
        ),
        Command.SimpleCommand(name = "Linux Kernel\n\n       ",
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
                                listOfOutputs[index].add("${it.name}$commandResult")
                            }
                        }

                        is Command.ActionCommand -> {
                            val commandResult = ssh.runCommand(command = it.command)
                            if (commandResult.isNotBlank()) {
                                val processed = it.action(it.name, commandResult)
                                listOfOutputs[index].addAll(processed)
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

    /* Example strings:
        fan1:        3266 RPM  (min =    0 RPM, max = 4600 RPM)     // 0 1 2 3            // fanRpm = 1, fanRpmMax = 3
        junction:     +85.0°C  (crit = +105.0°C, hyst = -273.1°C)   // 4 5 6 7 8 9        // temp = 4
        mem:         +104.0°C  (crit = +105.0°C, hyst = -273.1°C)   // 10 11 12 13 14 15  // mem = 10
        power1:       94.00 W  (cap = 140.00 W)                     // 16 17 18 19 20     // power = 17
     */
    private fun processAmd(name: String, string: String): List<String> {

        val fanRpm = 1
        val fanRpmMax = 3
        val temp = 4
        val mem = 10
        val power = 17

        val iteration = 21

        if (name != Amd) return listOf()

        val list = """\d+""".toRegex().findAll(string).toList()

        val outputList = mutableListOf<String>()

        for (buffer in list.indices step iteration) {

            val currentRate = list[buffer + fanRpm].value.toDouble()
            val maxRate = list[buffer + fanRpmMax].value.toDouble()

            val percentage = (currentRate / maxRate * 100).roundToInt()

            outputList.add("AMD GPU\n")
            outputList.add("       temp: ${list[buffer + temp].value} C\n")
            outputList.add("       mem temp: ${list[buffer + mem].value} C\n")
            outputList.add("       fan: ${currentRate.toInt()} RPM / $percentage%\n")
            outputList.add("       power: ${list[buffer + power].value} W\n")
        }

        return outputList
    }

    /*
        First line:
        | NVIDIA-SMI 470.74       Driver Version: 470.74       CUDA Version: 11.4     |  // 0 1 2  // driver = 1, cuda = 2
        Legend and second line:
        | Fan  Temp  Perf  Pwr:Usage/Cap|         Memory-Usage | GPU-Util  Compute M. |
        | 70%   84C    P2    90W / 200W |   4679MiB /  8117MiB |    100%      Default |  // 3 4 5 6 // fan = 3, temp = 4, power = 6
     */
    private fun processNvidia(name: String, string: String): List<String> {

        val driver = 1
        val cuda = 2
        val fan = 3
        val temp = 4
        val power = 6

        if (name != Nvidia) return listOf()

        val list = """\d+\.?\d*""".toRegex().findAll(string).toList()

        return listOf(
            "NVIDIA GPU\n",
            "       temp: ${list[temp].value.toInt()} C\n",
            "       fan: ${list[fan].value.toInt()}%\n",
            "       power: ${list[power].value.toInt()} W\n",
            "       driver: ${list[driver].value} / CUDA ${list[cuda].value}\n"
        )
    }

    // Physical id 0:  +37.0°C  (high = +80.0°C, crit = +100.0°C)
    private fun processIntel(name: String, string: String): List<String> {

        val temp = 1

        if (name != Intel) return listOf()

        val list = """\d+\.?\d*""".toRegex().findAll(string).toList()

        return listOf(
            "INTEL CPU\n",
            "       temp: ${list[temp].value} C\n"
        )
    }

    sealed class Command {

        data class SimpleCommand(
            val name: String,
            val command: String,
        ) : Command()

        data class ActionCommand(
            val name: String,
            val command: String,
            val action: (String, String) -> List<String>,
        ) : Command()
    }
}