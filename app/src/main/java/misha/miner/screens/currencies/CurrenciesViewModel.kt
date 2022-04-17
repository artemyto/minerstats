package misha.miner.screens.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import misha.miner.domain.GetListingsUseCase
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.common.ErrorState
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val getListings: GetListingsUseCase,
) : ViewModel() {

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
        viewModelScope.launch(Dispatchers.IO) {
            getListings
                .execute()
                .onSuccess {
                    _currencies.postValue(it.toMutableList())
                    _isRefreshing.postValue(false)
                }
                .onFailure {
                    _isRefreshing.postValue(false)
                }
        }
    }
}
