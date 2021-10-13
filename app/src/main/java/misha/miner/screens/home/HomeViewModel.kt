package misha.miner.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import misha.miner.common.EthermineHelper
import misha.miner.common.fullEthAddr
import misha.miner.common.util.getEthValue
import misha.miner.models.coinmarketcap.currency.CurrencyType
import misha.miner.services.api.ApiManager
import misha.miner.services.storage.StorageManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storageManager: StorageManager,
    private val apiManager: ApiManager
): ViewModel() {

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    private val _eth: MutableLiveData<String> = MutableLiveData()
    val eth: LiveData<String> = _eth

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
        _isRefreshing.value = true
        getEth()
        makePoolStats()
        makeWalletStats()
    }

    private fun makePoolStats() {
        if (poolStatus == RunStatus.Finished) {

            val config = storageManager.getStorage()

            poolStatus = RunStatus.Launched
            apiManager.getPoolStats(
                config.wallet,
                completion = { data ->
                    val ethHelper = EthermineHelper(data)

                    unpaid = ethHelper.getUnpaidBalanceValue()
                    estimated = ethHelper.getEstimatedEthForMonthValue()

                    poolOutputListField = mutableListOf()
                    poolOutputListField.add("Hashrate:\n")
                    poolOutputListField.add("       Average: ${ethHelper.getAverageHashrate()}\n")
                    poolOutputListField.add("       Reported: ${ethHelper.getReportedHashrate()}\n")
                    poolOutputListField.add("       Current: ${ethHelper.getCurrentHashrate()}\n")
                    poolOutputListField.add("Workers: ${ethHelper.getWorkers()}\n")
                    poolOutputListField.add("Shares (1 hour):\n")
                    poolOutputListField.add("       valid: ${ethHelper.getValidShares()}\n")
                    poolOutputListField.add("       stale: ${ethHelper.getStaleShares()}\n")
                    poolOutputListField.add("       invalid: ${ethHelper.getInvalidShares()}\n")
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

            val config = storageManager.getStorage()

            walletStatus = RunStatus.Launched
            apiManager.getWalletStats(
                config.wallet.fullEthAddr(),
                completion = { result ->
                    balance = result.getEthValue()

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
        apiManager.convertCurrency(
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
                balanceOutputListField.add("Balance:\n")
                balanceOutputListField.add("       Wallet: " +
                        "${String.format("%.5f", balance)} ETH / " +
                        "${String.format("%.0f", balanceRub)} ₽\n")
                balanceOutputListField.add("       Unpaid: " +
                        "${String.format("%.5f", unpaid)} ETH / " +
                        "${String.format("%.0f", unpaidRub)} ₽\n")
                balanceOutputListField.add("       Wallet + unpaid: " +
                        "${String.format("%.5f", walletPoolSum)} ETH / " +
                        "${String.format("%.0f", walletPoolSumRub)} ₽\n")
                balanceOutputListField.add("Estimated for one month:\n")
                balanceOutputListField.add("       ${String.format("%.5f", estimated)} ETH / " +
                        "${String.format("%.0f", estimatedRub)} ₽\n")
                _balanceOutputList.value = balanceOutputListField

                _isRefreshing.value = false
            },
            onError = {
                _isRefreshing.value = false
            }
        )
    }

    private fun getEth() {
        apiManager.convertCurrency(
            from = CurrencyType.ETH,
            to = CurrencyType.USD,
            amount = 1.0,
            completion = { oneEth ->

                _eth.value = "1 eth = ${String.format("%.2f", oneEth)} $\n"
            },
            onError = {

            }
        )
    }

    enum class RunStatus {
        Launched, Finished
    }
}