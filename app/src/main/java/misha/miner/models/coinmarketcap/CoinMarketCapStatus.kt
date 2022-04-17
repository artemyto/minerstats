package misha.miner.models.coinmarketcap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinMarketCapStatus(
    @SerialName("error_code")
    val errorCode: Int,
    @SerialName("error_message")
    val errorMessage: String?,
) {
    companion object {
        const val OK = 0
    }
}
