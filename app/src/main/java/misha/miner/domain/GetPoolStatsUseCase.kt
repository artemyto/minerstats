package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.data.RetrofitService
import misha.miner.data.models.ethermine.EthermineCurrentStats
import misha.miner.data.models.ethermine.EthermineResponseStatus
import javax.inject.Inject

class GetPoolStatsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String): Result<EthermineCurrentStats> {

        return runCatching {
            val response = api.getPoolStats(address)
            if (response.status != EthermineResponseStatus.Ok) throw Exception(response.error?.error)
            response.data!!
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}
