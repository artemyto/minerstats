package misha.miner.services.api

import misha.miner.models.coinmarketcap.CoinMarketCapResponse
import misha.miner.models.coinmarketcap.data.Converted
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.ehterscan.EtherscanResponse
import misha.miner.models.ehterscan.EtherscanTransaction
import misha.miner.models.ethermine.EthermineCurrentStats
import misha.miner.models.ethermine.EthermineDashboard
import misha.miner.models.ethermine.EthermineResponse
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface RetrofitService {

    @GET
    suspend fun getPoolStats(
        @Url
        url: String
    ): EthermineResponse<EthermineCurrentStats>

    @GET
    suspend fun getPoolDashboard(
        @Url
        url: String
    ): EthermineResponse<EthermineDashboard>

    @GET
    suspend fun getWalletStats(
        @Url
        url: String,
        @QueryMap
        queries: Map<String, String>
    ): EtherscanResponse<String>

    @GET
    suspend fun getWalletTransactions(
        @Url
        url: String,
        @QueryMap
        queries: Map<String, String>
    ): EtherscanResponse<List<EtherscanTransaction>>

    @GET
    suspend fun convertCurrency(
        @Url
        url: String,
        @HeaderMap
        headers: Map<String, String>,
        @QueryMap
        queries: Map<String, String>
    ): CoinMarketCapResponse<Converted>

    @GET
    suspend fun getListings(
        @Url
        url: String,
        @HeaderMap
        headers: Map<String, String>,
    ): CoinMarketCapResponse<List<Listing>>
}