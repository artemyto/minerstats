package misha.miner.screens.home

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
import misha.miner.services.api.ApiManager
import misha.miner.services.ssh.SSHConnectionManager
import misha.miner.services.storage.StorageManager

class HomeViewModel : ViewModel() {

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status

    private val _outputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val outputList: LiveData<MutableList<String>> = _outputList
    private lateinit var outputListField: MutableList<String>

    private val _poolOutputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val poolOutputList: LiveData<MutableList<String>> = _poolOutputList
    private lateinit var poolOutputListField: MutableList<String>

    private val _balanceOutputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val balanceOutputList: LiveData<MutableList<String>> = _balanceOutputList
    private lateinit var balanceOutputListField: MutableList<String>

    @Volatile
    private var poolStatus = RunStatus.Finished
    @Volatile
    private var walletStatus = RunStatus.Finished

    @Volatile
    private var balance = 0.0
    @Volatile
    private var unpaid = 0.0
    @Volatile
    private var estimated = 0.0

    private val commandList = mutableListOf(
        "CPU temp" to "sensors | grep Tdie | grep -E -o '[[:digit:]]{1,}.[[:digit:]].'",
        "Nvidia temp" to "nvidia-smi | grep -o \"[0-9]\\+C\"",
        "Amd temp" to "sensors | grep 'junction' | grep -o \"+[0-9]*\\.[0-9]*.C \"",
        "Amd mem temp" to "sensors | grep 'mem' | grep -o \"+[0-9]*\\.[0-9]*.C \"",
        "Nvidia driver" to "nvidia-smi | grep -o '[0-9]\\{3\\}\\.[0-9]\\{2\\}\\.\\{0,1\\}[0-9]\\{0,2\\}' | head -1",
        "Amd driver" to "DISPLAY=:0 glxinfo | grep \"OpenGL version\" | grep -o '[0-9]\\{2\\}\\.[0-9].[0-9]'",
    )

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

        makeSshStats()
        makePoolStats()
        makeWalletStats()
    }

    private fun makeSshStats() {
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

            outputListField = mutableListOf("PC miner stats:\n")
            commandList.forEach {
                outputListField.add("${it.first}: ${SSHConnectionManager.runCommand(command = it.second)}")
            }
            _outputList.postValue(outputListField)
        }
    }

    private fun makePoolStats() {
        if (poolStatus == RunStatus.Finished) {

            val config = StorageManager.getStorage()

            poolStatus = RunStatus.Launched
            ApiManager.getPoolStats(
                config.wallet,
                completion = { data ->
                    val ethHelper = EthermineHelper(data)

                    unpaid = ethHelper.getUnpaidBalanceValue()
                    estimated = ethHelper.getEstimatedEthForMonthValue()

                    poolOutputListField = mutableListOf()
                    poolOutputListField.add("Pool miner stats:\n")
                    poolOutputListField.add("Average hashrate: ${ethHelper.getAverageHashrate()}\n")
                    poolOutputListField.add("Reported hashrate: ${ethHelper.getReportedHashrate()}\n")
                    poolOutputListField.add("Current hashrate: ${ethHelper.getCurrentHashrate()}\n")
                    poolOutputListField.add("Workers: ${ethHelper.getWorkers()}\n")
                    poolOutputListField.add("Shares during last hour (valid/stale/invalid): ${ethHelper.getShares()}\n")
                    _poolOutputList.value = poolOutputListField

                    if (walletStatus == RunStatus.Finished)
                        makeBalanceStats()
                    poolStatus = RunStatus.Finished
                },
                onError = {
                    poolStatus = RunStatus.Finished
                }
            )
        }
    }

    private fun makeWalletStats() {
        if (walletStatus == RunStatus.Finished) {

            val config = StorageManager.getStorage()

            walletStatus = RunStatus.Launched
            ApiManager.getWalletStats(
                config.wallet.fullEthAddr(),
                completion = { result ->
                    val ethHelper = EtherscanHelper(result)

                    balance = ethHelper.getBalanceValue()

                    if (poolStatus == RunStatus.Finished)
                        makeBalanceStats()
                    walletStatus = RunStatus.Finished
                },
                onError = {
                    walletStatus = RunStatus.Finished
                }
            )
        }
    }

    private fun makeBalanceStats() {
        ApiManager.convertCurrency(
            from = CurrencyType.ETH,
            to = CurrencyType.RUB,
            amount = 1.0,
            completion = { oneEth ->

                val balanceRub = oneEth * balance
                val unpaidRub = oneEth * unpaid
                val estimatedRub = oneEth * estimated
                val walletPoolSum = balance + unpaid
                val walletPoolSumRub = oneEth * walletPoolSum

                balanceOutputListField = mutableListOf()
                balanceOutputListField.add("Balance stats:\n")
                balanceOutputListField.add("Wallet balance: " +
                        "${String.format("%.5f", balance)} ETH / " +
                        "${String.format("%.0f", balanceRub)} ₽\n")
                balanceOutputListField.add("Pool unpaid: " +
                        "${String.format("%.5f", unpaid)} ETH / " +
                        "${String.format("%.0f", unpaidRub)} ₽\n")
                balanceOutputListField.add("Wallet + unpaid: " +
                        "${String.format("%.5f", walletPoolSum)} ETH / " +
                        "${String.format("%.0f", walletPoolSumRub)} ₽\n")
                balanceOutputListField.add("Estimated for one month: " +
                        "${String.format("%.5f", estimated)} ETH / " +
                        "${String.format("%.0f", estimatedRub)} ₽\n")
                _balanceOutputList.value = balanceOutputListField
            },
            onError = {

            }
        )
    }

    enum class RunStatus {
        Launched, Finished
    }
}