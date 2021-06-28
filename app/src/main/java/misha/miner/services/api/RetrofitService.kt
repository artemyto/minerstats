package misha.miner.services.api

import misha.miner.models.coinmarketcap.CoinMarketCapResponse
import misha.miner.models.ehterscan.EtherscanResponse
import misha.miner.models.ethermine.EthermineResponse
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @GET
    suspend fun getPoolStats(
        @Url
        url: String
    ): Response<EthermineResponse>

    @GET
    suspend fun getWalletStats(
        @Url
        url: String,
        @QueryMap
        queries: Map<String, String>
    ): Response<EtherscanResponse>

    @GET
    suspend fun convertCurrency(
        @Url
        url: String,
        @HeaderMap
        headers: Map<String, String>,
        @QueryMap
        queries: Map<String, String>
    ): Response<CoinMarketCapResponse>
}