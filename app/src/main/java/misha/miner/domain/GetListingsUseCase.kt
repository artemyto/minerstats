package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.data.models.coinmarketcap.CoinMarketCapStatus
import misha.miner.data.models.coinmarketcap.data.Listing
import misha.miner.data.RetrofitService
import javax.inject.Inject

class GetListingsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(): Result<List<Listing>> {

        val headers = mapOf(
            Constants.CoinMarketCap.apiKeyName to BuildConfig.COINMARKETCAP_API_KEY
        )
        return runCatching {
            val response = api.getListings(headers)
            if (response.status.errorCode != CoinMarketCapStatus.OK) throw Exception(response.status.errorMessage)
            response.data
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}
