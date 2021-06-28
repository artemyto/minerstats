package misha.miner.models.coinmarketcap

import com.google.gson.annotations.SerializedName
import misha.miner.models.coinmarketcap.currency.Currency

data class Quote(
    @SerializedName("RUB")
    val rub: Currency,
    @SerializedName("USD")
    val usd: Currency,
    @SerializedName("ETH")
    val eth: Currency
)
