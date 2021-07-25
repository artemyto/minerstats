package misha.miner.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import misha.miner.models.storage.PCViewModel
import misha.miner.models.storage.StorageViewModel
import misha.miner.services.storage.StorageManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val storageManager: StorageManager
): ViewModel() {

    private val _wallet: MutableStateFlow<String> = MutableStateFlow("")
    val wallet: StateFlow<String> = _wallet

    private val _address: MutableLiveData<String> = MutableLiveData("")
    val address: LiveData<String> = _address

    private val _pc: MutableLiveData<String> = MutableLiveData("")
    val pc: LiveData<String> = _pc

    private val _port: MutableLiveData<String> = MutableLiveData("")
    val port: LiveData<String> = _port

    private val _name: MutableLiveData<String> = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _password: MutableLiveData<String> = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _commandList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val commandList: LiveData<MutableList<String>> = _commandList

    private val _pcLabelList: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val pcLabelList: LiveData<MutableList<String>> = _pcLabelList

    private lateinit var storageContext : StorageViewModel

    private var index = -1

    private var notInitialized = true

    fun initialization() {
        if (notInitialized) {
            storageContext = storageManager.getStorage()

            _wallet.value = storageContext.wallet
            _commandList.value = storageContext.commandList

            _pcLabelList.value = storageContext.pcList.mapIndexed { index, _ -> "PC ${index + 1}" }.toMutableList()

            notInitialized = false
        }
    }

    fun inputWallet(input: String) {
        storageContext.wallet = input
        _wallet.value = input
    }

    fun inputAddress(input: String) {
        if (index in storageContext.pcList.indices)
            storageContext.pcList[index].address = input
        _address.value = input
    }

    fun inputPort(input: String) {
        if (index in storageContext.pcList.indices)
            storageContext.pcList[index].port = input
        _port.value = input
    }

    fun inputName(input: String) {
        if (index in storageContext.pcList.indices)
            storageContext.pcList[index].name = input
        _name.value = input
    }

    fun inputPassword(input: String) {
        if (index in storageContext.pcList.indices)
            storageContext.pcList[index].password = input
        _password.value = input
    }

    fun save() {
        storageManager.update(storageContext)
    }

    fun addToCommandList(command: String) {
        if (command !in storageContext.commandList) {
            storageContext.commandList.add(command)
            _commandList.value = storageContext.commandList
        }
    }

    fun selectedPC(name: String) {
        index = (name.filter { it.isDigit() }.toIntOrNull() ?: 0) - 1
        val pc = storageContext.pcList.getOrNull(index)
        pc?.let {
            _address.value = it.address
            _port.value = it.port
            _name.value = it.name
            _password.value = it.password

            _pc.value = name
        } ?: let {
            _address.value = ""
            _port.value = ""
            _name.value = ""
            _password.value = ""

            _pc.value = ""
        }
    }

    fun newPC() {
        val num = _pcLabelList.value?.size ?: 0
        val newPC = "PC ${num + 1}"
        val list = _pcLabelList.value?.toMutableList()
        list?.add(newPC)
        storageContext.pcList.add(PCViewModel("","","",""))
        _pcLabelList.value = list
        _pc.value = newPC
        selectedPC(newPC)
    }

    fun removePC() {
        val pc = storageContext.pcList.getOrNull(index)
        pc?.let {
            val list = _pcLabelList.value?.toMutableList()
            list?.removeAt(index)
            storageContext.pcList.removeAt(index)
            _pcLabelList.value = list

            if (index == storageContext.pcList.size)
                index = storageContext.pcList.size - 1
            selectedPC("PC $index")
        }
    }
}