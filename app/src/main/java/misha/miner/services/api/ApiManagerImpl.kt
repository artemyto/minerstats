package misha.miner.services.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.models.BaseError
import misha.miner.models.coinmarketcap.CoinMarketCapStatus
import misha.miner.models.coinmarketcap.Quote
import misha.miner.models.coinmarketcap.currency.CurrencyType
import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.ehterscan.EtherscanResponse
import misha.miner.models.ehterscan.EtherscanResponseStatus
import misha.miner.models.ethermine.EthermineData
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class ApiManagerImpl(private val retrofitService: RetrofitService) : ApiManager {

    override fun getPoolStats(
        address: String,
        completion: (EthermineData) -> Unit,
        onError: (BaseError) -> Unit
    ) {

        val service = retrofitService

        val endPoint = Constants.Ethermine.statsByAddress(address)
        val apiCall = service::getPoolStats

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall(endPoint)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        when {
                            response.body() == null -> {
                                onError(BaseError("Пустой ответ"))
                            }
                            response.body()?.error != null -> {

                                onError(
                                    BaseError(
                                        response.body()?.error?.error ?: "Что-то пошло не так"
                                    )
                                )
                            }
                            else -> try {
                                completion(response.body()?.data!!)
                            } catch (e: NullPointerException) {
                                onError(BaseError("Пустой ответ"))
                            }
                        }
                    } else {
                        onError(BaseError(response.message() ?: "Что-то пошло не так"))
                    }
                }
            } catch (e: Exception) {
                doOnException(onError, e)
            }
        }
    }

    override fun getWalletStats(
        address: String,
        completion: (String) -> Unit,
        onError: (BaseError) -> Unit
    ) {

        val service = retrofitService

        val queries = mapOf(
            "module" to "account",
            "action" to "balance",
            "address" to address,
            "tag" to "latest",
            "apikey" to BuildConfig.ETHERSCAN_API_KEY
        )
        val endPoint = Constants.Etherscan.api
        val apiCall = service::getWalletStats

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall(endPoint, queries)
                doOnFinish(response = response, completion = completion, onError = onError)
            } catch (e: Exception) {
                doOnException(onError, e)
            }
        }
    }

    override fun convertCurrency(
        from: CurrencyType,
        to: CurrencyType,
        amount: Double,
        completion: (Double) -> Unit,
        onError: (BaseError) -> Unit
    ) {

        val service = retrofitService

        val headers = mapOf(
            Constants.CoinMarketCap.apiKeyName to BuildConfig.COINMARKETCAP_API_KEY
        )
        val queries = mapOf(
            "symbol" to from.value,
            "convert" to to.value,
            "amount" to amount.toString()
        )
        val endPoint = Constants.CoinMarketCap.priceConversion
        val apiCall = service::convertCurrency

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall(endPoint, headers, queries)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (body.status.errorCode != CoinMarketCapStatus.OK) {
                                onError(BaseError(response.body()?.status?.errorMessage ?: "Что-то пошло не так"))
                            } else {
                                completion(currencyAmountToReturn(to, body.data.quote))
                            }
                        } ?: run {
                            onError(BaseError("Пустой ответ"))
                        }
                    } else {
                        onError(BaseError(response.message() ?: "Что-то пошло не так"))
                    }
                }
            } catch (e: Exception) {
                doOnException(onError, e)
            }
        }
    }

    override fun getListings(
        completion: (List<Listing>) -> Unit,
        onError: (BaseError) -> Unit
    ) {

        val service = retrofitService

        val headers = mapOf(
            Constants.CoinMarketCap.apiKeyName to BuildConfig.COINMARKETCAP_API_KEY
        )
        val endPoint = Constants.CoinMarketCap.listings
        val apiCall = service::getListings

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall(endPoint, headers)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (body.status.errorCode != CoinMarketCapStatus.OK) {
                                onError(BaseError(response.body()?.status?.errorMessage ?: "Что-то пошло не так"))
                            } else {
                                completion(body.data)
                            }
                        } ?: run {
                            onError(BaseError("Пустой ответ"))
                        }
                    } else {
                        onError(BaseError(response.message() ?: "Что-то пошло не так"))
                    }
                }
            } catch (e: Exception) {
                doOnException(onError, e)
            }
        }
    }

    private fun currencyAmountToReturn(type: CurrencyType, quote: Quote) = when (type) {
        CurrencyType.RUB -> quote.rub.price
        CurrencyType.USD -> quote.usd.price
        CurrencyType.ETH -> quote.eth.price
    }

    private suspend fun <T>doOnFinish(
        response: Response<EtherscanResponse<T>>,
        completion: (T) -> Unit,
        onError: (BaseError) -> Unit,
    ) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    if (body.status == EtherscanResponseStatus.OK) {
                        completion(body.result)
                    }
                } ?: run {
                    onError(BaseError("Пустой ответ"))
                }
            } else {
                onError(BaseError(response.message() ?: "Что-то пошло не так"))
            }
        }
    }

    private suspend fun doOnException(
        onError: (BaseError) -> Unit,
        e: Exception
    ) {
        val error = if (e is IOException) "Нет интернета" else "Что-то пошло не так"
        withContext(Dispatchers.Main) {
            onError(BaseError(e.message ?: error))
        }
    }
}