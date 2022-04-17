package misha.miner.services.api

import misha.miner.models.coinmarketcap.CoinMarketCapResponse
import misha.miner.models.coinmarketcap.data.Converted
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.ehterscan.EtherscanResponse
import misha.miner.models.ehterscan.EtherscanTransaction
import misha.miner.models.ethermine.EthermineCurrentStats
import misha.miner.models.ethermine.EthermineDashboard
import misha.miner.models.ethermine.EthermineResponse
import retrofit2.http.*

interface RetrofitService {

    @GET("https://api.ethermine.org/miner/{address}/currentStats")
    suspend fun getPoolStats(
        @Path("address")
        address: String,
    ): EthermineResponse<EthermineCurrentStats>

    @GET("https://api.ethermine.org/miner/{address}/dashboard")
    suspend fun getPoolDashboard(
        @Path("address")
        address: String,
    ): EthermineResponse<EthermineDashboard>

    @GET("https://api.etherscan.io/api")
    suspend fun getWalletStats(
        @QueryMap
        queries: Map<String, String>,
    ): EtherscanResponse<String>

    @GET("https://api.etherscan.io/api")
    suspend fun getWalletTransactions(
        @QueryMap
        queries: Map<String, String>,
    ): EtherscanResponse<List<EtherscanTransaction>>

    @GET("https://pro-api.coinmarketcap.com/v1/tools/price-conversion")
    suspend fun convertCurrency(
        @HeaderMap
        headers: Map<String, String>,
        @QueryMap
        queries: Map<String, String>,
    ): CoinMarketCapResponse<Converted>

    @GET("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest")
    suspend fun getListings(
        @HeaderMap
        headers: Map<String, String>,
    ): CoinMarketCapResponse<List<Listing>>
}