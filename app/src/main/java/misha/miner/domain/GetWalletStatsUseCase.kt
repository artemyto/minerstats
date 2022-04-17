package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.models.ehterscan.EtherscanResponseStatus
import misha.miner.services.api.RetrofitService
import javax.inject.Inject

class GetWalletStatsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String) : Result<String> {

        val queries = mapOf(
            "module" to "account",
            "action" to "balance",
            "address" to address,
            "tag" to "latest",
            "apikey" to BuildConfig.ETHERSCAN_API_KEY
        )
        val endPoint = Constants.Etherscan.api
        return runCatching {
            val response = api.getWalletStats(endPoint, queries)
            if (response.status != EtherscanResponseStatus.OK) throw Exception(response.message)
            response.result
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}