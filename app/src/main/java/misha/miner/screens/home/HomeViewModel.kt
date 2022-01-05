package misha.miner.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import misha.miner.common.EthermineHelper
import misha.miner.common.util.getEthValue
import misha.miner.common.util.hashrateToMegahashLabel
import misha.miner.domain.ConvertCurrencyUseCase
import misha.miner.domain.GetPoolDashboardUseCase
import misha.miner.domain.GetPoolStatsUseCase
import misha.miner.domain.GetWalletStatsUseCase
import misha.miner.models.coinmarketcap.currency.CurrencyType
import misha.miner.services.storage.StorageManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    storageManager: StorageManager,
    private val getPoolStats: GetPoolStatsUseCase,
    private val getWalletStats: GetWalletStatsUseCase,
    private val getPoolDashboard: GetPoolDashboardUseCase,
    private val convertCurrency: ConvertCurrencyUseCase,
) : ViewModel() {

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    private val _eth: MutableLiveData<String> = MutableLiveData()
    val eth: LiveData<String> = _eth

    private val _poolOutputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val poolOutputList: LiveData<MutableList<String>> = _poolOutputList
    private lateinit var poolOutputListField: MutableList<String>

    private val _sharesOutputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val sharesOutputList: LiveData<MutableList<String>> = _sharesOutputList
    private lateinit var sharesOutputListField: MutableList<String>

    private val _balanceOutputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val balanceOutputList: LiveData<MutableList<String>> = _balanceOutputList
    private lateinit var balanceOutputListField: MutableList<String>

    private val _workerOutputList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val workerOutputList: LiveData<MutableList<String>> = _workerOutputList
    private lateinit var workerOutputListField: MutableList<String>

    private val address = storageManager.getStorage().wallet

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
        makeAdditionalPoolStats()
    }

    private fun makePoolStats() {
        if (poolStatus == RunStatus.Finished) {

            poolStatus = RunStatus.Launched
            viewModelScope.launch(Dispatchers.IO) {
                getPoolStats
                    .execute(address)
                    .onSuccess { data ->
                        val ethHelper = EthermineHelper(data)

                        unpaid = ethHelper.getUnpaidBalanceValue()
                        estimated = ethHelper.getEstimatedEthForMonthValue()

                        poolOutputListField = mutableListOf(
                            "Hashrate:\n",
                            "       Average: ${ethHelper.getAverageHashrate()}\n",
                            "       Reported: ${ethHelper.getReportedHashrate()}\n",
                            "       Current: ${ethHelper.getCurrentHashrate()}\n",
                        )
                        _poolOutputList.postValue(poolOutputListField)

                        sharesOutputListField = mutableListOf(
                            "Shares (1 hour):\n",
                            "       valid: ${ethHelper.getValidShares()}\n",
                            "       stale: ${ethHelper.getStaleShares()}\n",
                            "       invalid: ${ethHelper.getInvalidShares()}\n",
                        )
                        _sharesOutputList.postValue(sharesOutputListField)

                        if (walletStatus == RunStatus.Finished)
                            makeBalanceStats()
                        poolStatus = RunStatus.Finished
                    }
                    .onFailure { poolStatus = RunStatus.Finished }
            }
        }
    }

    private fun makeWalletStats() {
        if (walletStatus == RunStatus.Finished) {

            walletStatus = RunStatus.Launched
            viewModelScope.launch(Dispatchers.IO) {
                getWalletStats
                    .execute(address)
                    .onSuccess { result ->
                        balance = result.getEthValue()

                        if (poolStatus == RunStatus.Finished)
                            makeBalanceStats()
                        walletStatus = RunStatus.Finished
                    }
                    .onFailure {
                        walletStatus = RunStatus.Finished
                    }
            }
        }
    }

    private fun makeBalanceStats() {
        viewModelScope.launch(Dispatchers.IO) {
            convertCurrency
                .execute(
                    from = CurrencyType.ETH,
                    to = CurrencyType.RUB,
                    amount = 1.0,
                )
                .onSuccess { oneEth ->

                    val balanceRub = oneEth * balance
                    val unpaidRub = oneEth * unpaid
                    val estimatedRub = oneEth * estimated
                    val walletPoolSum = balance + unpaid
                    val walletPoolSumRub = oneEth * walletPoolSum

                    balanceOutputListField = mutableListOf(
                        "Balance:\n",

                        "       Wallet: " +
                                "${String.format("%.5f", balance)} ETH / " +
                                "${String.format("%.0f", balanceRub)} ₽\n",

                        "       Unpaid: " +
                                "${String.format("%.5f", unpaid)} ETH / " +
                                "${String.format("%.0f", unpaidRub)} ₽\n",

                        "       Wallet + unpaid: " +
                                "${String.format("%.5f", walletPoolSum)} ETH / " +
                                "${String.format("%.0f", walletPoolSumRub)} ₽\n",
                        "Estimated for one month:\n",

                        "       ${String.format("%.5f", estimated)} ETH / " +
                                "${String.format("%.0f", estimatedRub)} ₽\n",
                    )
                    _balanceOutputList.postValue(balanceOutputListField)

                    _isRefreshing.postValue(false)

                }
                .onFailure {
                    _isRefreshing.postValue(false)
                }
        }
    }

    private fun makeAdditionalPoolStats() {
        viewModelScope.launch(Dispatchers.IO) {
            getPoolDashboard
                .execute(address)
                .onSuccess {
                    workerOutputListField = mutableListOf("Workers:\n")
                    for (worker in it.workers) {
                        workerOutputListField.add(
                            "       ${worker.worker} / reported: " +
                                    "${worker.reportedHashrate.hashrateToMegahashLabel()} / " +
                                    "current: ${worker.currentHashrate.hashrateToMegahashLabel()}\n"
                        )
                    }
                    _workerOutputList.postValue(workerOutputListField)
                }
        }
    }

    private fun getEth() {
        viewModelScope.launch(Dispatchers.IO) {
            convertCurrency
                .execute(
                    from = CurrencyType.ETH,
                    to = CurrencyType.USD,
                    amount = 1.0,
                )
                .onSuccess { oneEth ->
                    _eth.postValue("1 eth = ${String.format("%.2f", oneEth)} $\n")
                }
        }
    }

    enum class RunStatus {
        Launched, Finished
    }
}