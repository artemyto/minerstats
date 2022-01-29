package misha.miner.domain

import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.models.coinmarketcap.CoinMarketCapStatus
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.services.api.RetrofitService
import javax.inject.Inject

class GetListingsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute() : Result<List<Listing>> {

        val headers = mapOf(
            Constants.CoinMarketCap.apiKeyName to BuildConfig.COINMARKETCAP_API_KEY
        )
        val endPoint = Constants.CoinMarketCap.listings
        return runCatching {
            val response = api.getListings(endPoint, headers)
            if (response.status.errorCode != CoinMarketCapStatus.OK) throw Exception(response.status.errorMessage)
            response.data
        }
    }
}