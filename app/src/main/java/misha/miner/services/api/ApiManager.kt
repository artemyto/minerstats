package misha.miner.services.api

import misha.miner.models.BaseError
import misha.miner.models.coinmarketcap.currency.CurrencyType
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.ethermine.EthermineData

interface ApiManager {
    fun getPoolStats(
        address: String,
        completion: (EthermineData) -> Unit,
        onError: (BaseError) -> Unit
    )

    fun getWalletStats(address: String, completion: (String) -> Unit, onError: (BaseError) -> Unit)
    fun convertCurrency(
        from: CurrencyType,
        to: CurrencyType,
        amount: Double,
        completion: (Double) -> Unit,
        onError: (BaseError) -> Unit
    )

    fun getListings(completion: (List<Listing>) -> Unit, onError: (BaseError) -> Unit)
}