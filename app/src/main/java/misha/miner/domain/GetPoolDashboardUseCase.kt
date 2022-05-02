package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.data.models.ethermine.EthermineDashboard
import misha.miner.data.models.ethermine.EthermineResponseStatus
import misha.miner.data.RetrofitService
import javax.inject.Inject

class GetPoolDashboardUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String): Result<EthermineDashboard> {

        return runCatching {
            val response = api.getPoolDashboard(address)
            if (response.status != EthermineResponseStatus.Ok) throw Exception(response.error?.error)
            response.data!!
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}
