package misha.miner

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import misha.miner.domain.LaunchChromeCustomTabUseCase
import misha.miner.services.storage.StorageManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storageManager: StorageManager,
    private val launchChromeCustomTabUseCase: LaunchChromeCustomTabUseCase,
) : ViewModel() {
    fun getStorage() = storageManager.getStorage()

    fun onContextAction(context: Context, action: MainAction) {
        when (action) {
            MainAction.LaunchUrlPoolStats -> {
                val url = "https://ethermine.org/miners/${getStorage().wallet}/dashboard"
                launchChromeCustomTabUseCase.execute(context = context, url)
            }
            MainAction.LaunchUrRate -> {
                val url = "https://ru.investing.com/crypto/ethereum/eth-usd"
                launchChromeCustomTabUseCase.execute(context = context, url)
            }
            MainAction.LaunchUrlWallet -> {
                val url = "https://www.etherchain.org/account/${getStorage().wallet}"
                launchChromeCustomTabUseCase.execute(context = context, url)
            }
        }
    }
}

sealed class MainAction {
    object LaunchUrlPoolStats : MainAction()
    object LaunchUrlWallet : MainAction()
    object LaunchUrRate : MainAction()
}
