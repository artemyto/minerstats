package misha.miner.models.coinmarketcap.currency

import kotlinx.serialization.Serializable

@Serializable
enum class CurrencyType(val value: String) {
    ETH("ETH"),
    RUB("RUB"),
    USD("USD")
}
