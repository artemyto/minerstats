package misha.miner.data.models.coinmarketcap

import kotlinx.serialization.Serializable

@Serializable
data class CoinMarketCapResponse<T>(
    val status: CoinMarketCapStatus,
    val data: T,
)
