package misha.miner.models.coinmarketcap

import com.google.gson.annotations.SerializedName

data class CoinMarketCapStatus(
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("error_message")
    val errorMessage: String,
) {
    companion object {
        const val OK = 0
    }
}
