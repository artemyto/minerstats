package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.BuildConfig
import misha.miner.data.models.ehterscan.EtherscanResponseStatus
import misha.miner.data.RetrofitService
import javax.inject.Inject

class GetWalletStatsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String): Result<String> {

        val queries = mapOf(
            "module" to "account",
            "action" to "balance",
            "address" to address,
            "tag" to "latest",
            "apikey" to BuildConfig.ETHERSCAN_API_KEY
        )
        return runCatching {
            val response = api.getWalletStats(queries)
            if (response.status != EtherscanResponseStatus.OK) throw Exception(response.message)
            response.result
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}
