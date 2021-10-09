package misha.miner.screens.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.common.ErrorState
import misha.miner.services.api.ApiManager
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val apiManager: ApiManager,
): ViewModel() {

    private val _currencies: MutableLiveData<MutableList<Listing>> =
        MutableLiveData(mutableListOf())
    val currencies: LiveData<MutableList<Listing>> = _currencies
    private lateinit var currenciesField: MutableList<Listing>

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
        apiManager.getListings(
            completion = {
                _currencies.value = it.toMutableList()
                _isRefreshing.value = false
            },
            onError = {
                _isRefreshing.value = false
            }
        )
    }
}