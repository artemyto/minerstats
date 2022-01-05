package misha.miner.domain

import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.models.coinmarketcap.CoinMarketCapStatus
import misha.miner.models.coinmarketcap.Quote
import misha.miner.models.coinmarketcap.currency.CurrencyType
import misha.miner.services.api.RetrofitService
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(
        from: CurrencyType,
        to: CurrencyType,
        amount: Double,
    ) : Result<Double> {

        val headers = mapOf(
            Constants.CoinMarketCap.apiKeyName to BuildConfig.COINMARKETCAP_API_KEY
        )
        val queries = mapOf(
            "symbol" to from.value,
            "convert" to to.value,
            "amount" to amount.toString()
        )
        val endPoint = Constants.CoinMarketCap.priceConversion
        return runCatching {
            val response = api.convertCurrency(endPoint, headers, queries)
            if (response.status.errorCode != CoinMarketCapStatus.OK) throw Exception(response.status.errorMessage)
            currencyAmountToReturn(to, response.data.quote)
        }
    }

    private fun currencyAmountToReturn(type: CurrencyType, quote: Quote) = when (type) {
        CurrencyType.RUB -> quote.rub.price
        CurrencyType.USD -> quote.usd.price
        CurrencyType.ETH -> quote.eth.price
    }
}