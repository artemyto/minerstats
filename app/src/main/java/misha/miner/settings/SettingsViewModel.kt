package misha.miner.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import misha.miner.models.StorageViewModel
import misha.miner.services.storage.StorageManager

class SettingsViewModel : ViewModel() {

    private val _wallet: MutableStateFlow<String> = MutableStateFlow("")
    val wallet: StateFlow<String> = _wallet

    private val _address: MutableLiveData<String> = MutableLiveData("")
    val address: LiveData<String> = _address

    private val _port: MutableLiveData<String> = MutableLiveData("")
    val port: LiveData<String> = _port

    private val _name: MutableLiveData<String> = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _password: MutableLiveData<String> = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _commandList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val commandList: LiveData<MutableList<String>> = _commandList

    private lateinit var storageContext : StorageViewModel

    private var notInitialized = true

    fun initialization() {
        if (notInitialized) {
            storageContext = StorageManager.getStorage()

            _wallet.value = storageContext.wallet
            _address.value = storageContext.address
            _port.value = storageContext.port
            _name.value = storageContext.name
            _password.value = storageContext.password
            _commandList.value = storageContext.commandList

            Log.d("mytag", storageContext.toString())

            notInitialized = false
        }
    }

    fun inputWallet(input: String) {
        storageContext.wallet = input
        _wallet.value = input
    }

    fun inputAddress(input: String) {
        storageContext.address = input
        _address.value = input
    }

    fun inputPort(input: String) {
        storageContext.port = input
        _port.value = input
    }

    fun inputName(input: String) {
        storageContext.name = input
        _name.value = input
    }

    fun inputPassword(input: String) {
        storageContext.password = input
        _password.value = input
    }

    fun save() {
//        storageContext.commandList

//        commandList.value?.forEach {
//            storageContext.command
//        }

        Log.d("mytag", storageContext.toString())

        StorageManager.update(storageContext)
    }
}