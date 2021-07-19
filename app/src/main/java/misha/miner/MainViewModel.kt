package misha.miner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import misha.miner.services.storage.StorageManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {
    fun getStorage() = storageManager.getStorage()
}