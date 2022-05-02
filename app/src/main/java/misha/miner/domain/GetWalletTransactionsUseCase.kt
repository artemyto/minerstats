package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.BuildConfig
import misha.miner.data.RetrofitService
import misha.miner.data.models.ehterscan.EtherscanResponseStatus
import misha.miner.data.models.ehterscan.EtherscanTransaction
import javax.inject.Inject

class GetWalletTransactionsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String): Result<List<EtherscanTransaction>> {

        val queries = mapOf(
            "module" to "account",
            "action" to "txlist",
            "address" to address,
            "startblock" to "0",
            "endblock" to "99999999",
            "sort" to "asc",
            "apikey" to BuildConfig.ETHERSCAN_API_KEY
        )
        return runCatching {
            val response = api.getWalletTransactions(queries)
            if (response.status != EtherscanResponseStatus.OK) throw Exception(response.message)
            response.result
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}
