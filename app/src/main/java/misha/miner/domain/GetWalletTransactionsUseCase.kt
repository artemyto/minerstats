package misha.miner.domain

import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.models.ehterscan.EtherscanResponseStatus
import misha.miner.models.ehterscan.EtherscanTransaction
import misha.miner.services.api.RetrofitService
import javax.inject.Inject

class GetWalletTransactionsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String) : Result<List<EtherscanTransaction>> {

        val queries = mapOf(
            "module" to "account",
            "action" to "txlist",
            "address" to address,
            "startblock" to "0",
            "endblock" to "99999999",
            "sort" to "asc",
            "apikey" to BuildConfig.ETHERSCAN_API_KEY
        )
        val endPoint = Constants.Etherscan.api
        return runCatching {
            val response = api.getWalletTransactions(endPoint, queries)
            if (response.status != EtherscanResponseStatus.OK) throw Exception(response.message)
            response.result
        }
    }
}