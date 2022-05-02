package misha.miner.presentation.pc

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
import misha.miner.data.SSHConnectionManager
import misha.miner.data.models.common.ErrorState
import misha.miner.data.models.storage.PCViewModel
import misha.miner.services.storage.StorageManager
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class PCStatsViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {

    companion object {
        const val Amd = "Amd"
        const val AmdDriver = "AmdDriver"
        const val Intel = "Intel"
        const val Nvidia = "Nvidia"
    }

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status

    private val _outputList: MutableLiveData<List<String>> =
        MutableLiveData(listOf())
    val outputList: LiveData<List<String>> = _outputList

    private val _pcLabelList: MutableLiveData<List<String>> =
        MutableLiveData(listOf())
    val pcLabelList: LiveData<List<String>> = _pcLabelList

    private val _selectedPC: MutableStateFlow<Int> = MutableStateFlow(-1)
    val selectedPC: StateFlow<Int> = _selectedPC
    private var selectedIndex = -1

    val error: MutableStateFlow<ErrorState> = MutableStateFlow(ErrorState.None)

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val commandList = listOf(
        Command.SimpleCommand(
            name = "AMD CPU\n\n       temp: ",
            command = "sensors | grep Tctl | grep -E -o '[[:digit:]]{1,}.[[:digit:]].'"
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
            command = "sensors | grep amdgpu -A 10 | grep 'fan\\|junction\\|mem\\|power\\|slowPPT'",
            action = this::processAmd
        ),
        // Amd driver
        Command.ActionCommand(
            name = AmdDriver,
            command = "pacman -Q mesa | grep -o '[0-9]\\{2\\}\\.[0-9].[0-9]' ; " +
                "pacman -Q opencl-amd | grep -o '[0-9]\\{2\\}\\.[0-9]\\{2\\}'",
            action = this::processAmdDriver
        ),
        Command.SimpleCommand(
            name = "Linux Kernel\n\n       ",
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
        run()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun run() {

        makeSshStats()
    }

    private fun makeSshStats() {
        val config = storageManager.getStorage()

        val pcLabelListValue = mutableListOf<String>()

        config.pcList.forEachIndexed { index, _ ->
            pcLabelListValue.add("PC ${index + 1}")
        }

        try {
            runSsh(selectedIndex, config.pcList[selectedIndex])

            _pcLabelList.value = pcLabelListValue
        } catch (e: IndexOutOfBoundsException) {
            viewModelScope.launch(Dispatchers.IO) {
                _status.emit("Add pc in settings")
            }
        }
    }

    private fun runSsh(index: Int, item: PCViewModel) {
        viewModelScope.launch(Dispatchers.IO) {

            _outputList.postValue(listOf())
            _status.emit("")
            _isRefreshing.postValue(true)

            val ssh = SSHConnectionManager()

            try {
                ssh.open(
                    hostname = item.address,
                    port = item.port.toInt(),
                    username = item.name,
                    password = item.password
                )
                _status.emit(
                    "Connection to ${item.name}@${item.address}:${item.port} is established"
                )

                val outputListField = mutableListOf("PC ${index + 1} miner stats:\n")
                commandList.forEach {
                    when (it) {
                        is Command.SimpleCommand -> {
                            val commandResult = ssh.runCommand(command = it.command)
                            if (commandResult.isNotBlank()) {
                                outputListField.add("${it.name}$commandResult")
                            }
                        }

                        is Command.ActionCommand -> {
                            val commandResult = ssh.runCommand(command = it.command)
                            if (commandResult.isNotBlank()) {
                                val processed = it.action(it.name, commandResult)
                                outputListField.addAll(processed)
                            }
                        }
                    }

                    if (index == selectedIndex) {
                        _outputList.postValue(outputListField.toMutableList())
                    }
                }
            } catch (e: Exception) {
                _status.emit("Connection to ${item.name}@${item.address}:${item.port} is not established")

                error.emit(ErrorState.Error(e.message ?: ""))
                PlutoLog.e("ssh", e.message.toString())
            }
            ssh.close()
            if (index == selectedIndex) {
                _isRefreshing.postValue(false)
            }
        }
    }

    fun selectPC(index: Int) {
        _selectedPC.value = index
        selectedIndex = index

        run()
    }

    /* Example strings:
        fan1:        3266 RPM  (min =    0 RPM, max = 4600 RPM)     // 0 1 2 3            // fanRpm = 1, fanRpmMax = 3
        junction:     +85.0°C  (crit = +105.0°C, hyst = -273.1°C)   // 4 5 6 7 8 9        // temp = 4
        mem:         +104.0°C  (crit = +105.0°C, hyst = -273.1°C)   // 10 11 12 13 14 15  // mem = 10
        slowPPT:       94.00 W  (cap = 140.00 W)                     // 16 17 18 19     // power = 16
     */
    private fun processAmd(name: String, string: String): List<String> {

        val fanRpm = 1
        val fanRpmMax = 3
        val temp = 4
        val mem = 10
        val power = 16

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

    private fun processAmdDriver(name: String, string: String): List<String> {
        if (name != AmdDriver) return listOf()

        val (mesa, opencl) = string.split('\n')

        return listOf("       driver: mesa $mesa / opencl $opencl\n")
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
            "       temp: ${list[temp].value} C\n",
            "       fan: ${list[fan].value}%\n",
            "       power: ${list[power].value} W\n",
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
