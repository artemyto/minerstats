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

    /**
     * Miner - Statistics
     *
     * [Docs](https://api.ethermine.org/docs/#api-Miner-miner_currentStats)
     */
    @GET("https://api.ethermine.org/miner/{address}/currentStats")
    suspend fun getPoolStats(
        @Path("address")
        address: String,
    ): EthermineResponse<EthermineCurrentStats>

    /**
     * Miner - Dashboard
     *
     * [Docs](https://api.ethermine.org/docs/#api-Miner-miner_dashboard)
     */
    @GET("https://api.ethermine.org/miner/{address}/dashboard")
    suspend fun getPoolDashboard(
        @Path("address")
        address: String,
    ): EthermineResponse<EthermineDashboard>

    /**
     * Returns the Ether balance of a given address.
     *
     * Module - account
     *
     * Action - balance
     *
     * [Docs](https://docs.etherscan.io/api-endpoints/accounts)
     */
    @GET("https://api.etherscan.io/api")
    suspend fun getWalletStats(
        @QueryMap
        queries: Map<String, String>,
    ): EtherscanResponse<String>

    /**
     * Returns the Ether balance of a given address.
     *
     * Module - account
     *
     * Action - txlist
     *
     * [Docs](https://docs.etherscan.io/api-endpoints/accounts)
     */
    @GET("https://api.etherscan.io/api")
    suspend fun getWalletTransactions(
        @QueryMap
        queries: Map<String, String>,
    ): EtherscanResponse<List<EtherscanTransaction>>

    /**
     * Convert an amount of one cryptocurrency or fiat currency into one or more different
     * currencies utilizing the latest market rate for each currency.
     *
     * [Docs](https://coinmarketcap.com/api/documentation/v1/#operation/getV1ToolsPriceconversion)
     */
    @GET("https://pro-api.coinmarketcap.com/v1/tools/price-conversion")
    suspend fun convertCurrency(
        @HeaderMap
        headers: Map<String, String>,
        @QueryMap
        queries: Map<String, String>,
    ): CoinMarketCapResponse<Converted>

    /**
     * Returns a paginated list of all active cryptocurrencies with latest market data.
     *
     * [Docs](https://coinmarketcap.com/api/documentation/v1/#operation/getV1CryptocurrencyListingsLatest)
     */
    @GET("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest")
    suspend fun getListings(
        @HeaderMap
        headers: Map<String, String>,
    ): CoinMarketCapResponse<List<Listing>>
}
