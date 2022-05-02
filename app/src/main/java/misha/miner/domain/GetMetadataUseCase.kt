package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.data.models.coinmarketcap.CoinMarketCapStatus
import misha.miner.data.models.coinmarketcap.data.Metadata
import misha.miner.data.RetrofitService
import javax.inject.Inject

class GetMetadataUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(idList: List<String>): Result<Map<String, Metadata>> {

        val headers = mapOf(
            Constants.CoinMarketCap.apiKeyName to BuildConfig.COINMARKETCAP_API_KEY
        )
        val queries = mapOf(
            "id" to idList.joinToString(","),
        )
        return runCatching {
            val response = api.getCurrencyMetadata(headers, queries)
            if (response.status.errorCode != CoinMarketCapStatus.OK) throw Exception(response.status.errorMessage)
            response.data
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}
