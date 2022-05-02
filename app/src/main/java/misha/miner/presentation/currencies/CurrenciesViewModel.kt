package misha.miner.presentation.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import misha.miner.domain.GetCurrenciesUseCase
import misha.miner.data.models.common.ErrorState
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
) : ViewModel() {

    private val _currencies: MutableLiveData<List<CurrencyVO>> =
        MutableLiveData(listOf())
    val currencies: LiveData<List<CurrencyVO>> = _currencies

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
            getCurrenciesUseCase
                .execute()
                .onSuccess {
                    _currencies.postValue(it)
                    _isRefreshing.postValue(false)
                }
                .onFailure {
                    _isRefreshing.postValue(false)
                }
        }
    }
}
