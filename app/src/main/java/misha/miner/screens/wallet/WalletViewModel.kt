package misha.miner.screens.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import misha.miner.common.util.getEthValue
import misha.miner.domain.GetWalletTransactionsUseCase
import misha.miner.models.common.ErrorState
import misha.miner.models.ehterscan.EtherscanTransaction
import misha.miner.services.storage.StorageManager
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val storageManager: StorageManager,
    private val getWalletTransactions: GetWalletTransactionsUseCase,
) : ViewModel() {

    private val _outputList: MutableLiveData<List<String>> =
        MutableLiveData(listOf())
    val outputList: LiveData<List<String>> = _outputList

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val error: MutableStateFlow<ErrorState> = MutableStateFlow(ErrorState.None)

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
        getCurrencies()
    }

    private fun getCurrencies() {

        val config = storageManager.getStorage()

        viewModelScope.launch(Dispatchers.IO) {
            getWalletTransactions
                .execute(config.wallet)
                .onSuccess {
                    _outputList.postValue(processList(it))
                    _isRefreshing.postValue(false)
                }
                .onFailure {
                    _isRefreshing.postValue(false)
                }
        }
    }

    private fun processList(list: List<EtherscanTransaction>): List<String> {

        val zoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())

        return list
            .map {

                val value = "%.5f".format(it.value.getEthValue())
                val fee = "%.5f".format((it.gasUsed * it.gasPrice).getEthValue())
                val date = LocalDateTime.ofEpochSecond(it.timeStamp, 0, zoneOffset)

                "value = $value\nfee = $fee\ndate = $date\n"
            }
            .reversed()
    }
}
