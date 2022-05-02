package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.data.models.coinmarketcap.CoinMarketCapStatus
import misha.miner.data.models.coinmarketcap.Quote
import misha.miner.data.models.coinmarketcap.currency.CurrencyType
import misha.miner.data.RetrofitService
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(
        from: CurrencyType,
        to: CurrencyType,
        amount: Double,
    ): Result<Double> {

        val headers = mapOf(
            Constants.CoinMarketCap.apiKeyName to BuildConfig.COINMARKETCAP_API_KEY
        )
        val queries = mapOf(
            "symbol" to from.value,
            "convert" to to.value,
            "amount" to amount.toString()
        )
        return runCatching {
            val response = api.convertCurrency(headers, queries)
            if (response.status.errorCode != CoinMarketCapStatus.OK) throw Exception(response.status.errorMessage)
            currencyAmountToReturn(to, response.data.quote)
        }.onFailure {
            PlutoLog.d("mytag", it.message.toString())
        }
    }

    private fun currencyAmountToReturn(type: CurrencyType, quote: Quote) = when (type) {
        CurrencyType.RUB -> quote.rub?.price ?: 0.0
        CurrencyType.USD -> quote.usd?.price ?: 0.0
        CurrencyType.ETH -> quote.eth?.price ?: 0.0
    }
}
