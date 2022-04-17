package misha.miner.models.coinmarketcap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import misha.miner.models.coinmarketcap.currency.Currency

@Serializable
data class Quote(
    @SerialName("RUB")
    val rub: Currency? = null,
    @SerialName("USD")
    val usd: Currency? = null,
    @SerialName("ETH")
    val eth: Currency? = null,
)
