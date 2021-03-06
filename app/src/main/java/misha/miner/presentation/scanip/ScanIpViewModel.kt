package misha.miner.presentation.scanip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import misha.miner.common.getIfNotBlankOrElse
import misha.miner.data.SettingsRepository
import misha.miner.data.models.common.ErrorState
import misha.miner.domain.RunTerminalCommandUseCase
import javax.inject.Inject

@HiltViewModel
class ScanIpViewModel @Inject constructor(
    private val runTerminalCommandUseCase: RunTerminalCommandUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    companion object {
        const val DEFAULT_IP = "192.168."
    }

    private val _status: MutableStateFlow<String> = MutableStateFlow("Connection is not opened yet")
    val status: StateFlow<String> = _status

    private val _selectedAddress: MutableLiveData<String> = MutableLiveData()
    val selectedAddress: LiveData<String> = _selectedAddress

    private val _outputList: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val outputList: LiveData<List<String>> = _outputList

    val error: MutableStateFlow<ErrorState> = MutableStateFlow(ErrorState.None)

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var initialized = false

    fun initialize() {
        if (!initialized) {
            initialized = true

            _selectedAddress.value = settingsRepository.getSavedScanIp().getIfNotBlankOrElse(
                DEFAULT_IP
            )

            run()
        }
    }

    fun runClicked() {
        run()
    }

    private fun run() {
        _isRefreshing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val savedIp = selectedAddress.value ?: return@launch
            settingsRepository.setSavedScanIp(savedIp)
            val output = listOf(
                runTerminalCommandUseCase.execute(
                    command = listOf(
                        "/bin/sh",
                        "-c",
                        "address=1; " +
                            "while [ \$address -lt 255 ]; do " +
                            "echo \"$savedIp.\$address\"; " +
                            "address=`expr \$address + 1`; " +
                            "done | xargs -n1 -P0 ping -c1 | grep 'bytes from'"
                    )
                )
            )
            _isRefreshing.postValue(false)
            _outputList.postValue(output)
        }
    }

    fun inputAddress(address: String) {
        val reducedText = """\d{0,3}+\.?\d{0,3}+\.?\d{0,3}+""".toRegex().find(address)?.value ?: ""
        _selectedAddress.value = reducedText
    }
}
