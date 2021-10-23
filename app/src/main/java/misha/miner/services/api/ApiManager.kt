package misha.miner.services.api

import misha.miner.models.common.BaseError
import misha.miner.models.coinmarketcap.currency.CurrencyType
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.ehterscan.EtherscanTransaction
import misha.miner.models.ethermine.EthermineCurrentStats
import misha.miner.models.ethermine.EthermineDashboard

interface ApiManager {
    fun getPoolStats(
        address: String,
        completion: (EthermineCurrentStats) -> Unit,
        onError: (BaseError) -> Unit
    )

    fun getWalletStats(address: String, completion: (String) -> Unit, onError: (BaseError) -> Unit)

    fun getWalletTransactions(
        address: String,
        completion: (List<EtherscanTransaction>) -> Unit,
        onError: (BaseError) -> Unit
    )

    fun convertCurrency(
        from: CurrencyType,
        to: CurrencyType,
        amount: Double,
        completion: (Double) -> Unit,
        onError: (BaseError) -> Unit
    )

    fun getListings(completion: (List<Listing>) -> Unit, onError: (BaseError) -> Unit)
    fun getPoolDashboard(
        address: String,
        completion: (EthermineDashboard) -> Unit,
        onError: (BaseError) -> Unit
    )
}