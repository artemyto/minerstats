package misha.miner.domain

import misha.miner.models.coinmarketcap.data.Listing
import misha.miner.models.coinmarketcap.data.Metadata
import misha.miner.presentation.currencies.CurrencyVO
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(
    private val getListingsUseCase: GetListingsUseCase,
    private val getMetadataUseCase: GetMetadataUseCase,
) {
    suspend fun execute(): Result<List<CurrencyVO>> {
        var map = mapOf<String, Metadata>()
        var list = listOf<Listing>()
        getListingsUseCase
            .execute()
            .onSuccess { listings ->
                list = listings
                getMetadataUseCase
                    .execute(idList = listings.map { it.id.toString() })
                    .onSuccess {
                        map = it
                    }
                    .onFailure {
                        return runCatching {
                            throw it
                        }
                    }
            }
        return runCatching {
            list.map {
                CurrencyVO.fromListingAndMetadata(
                    listing = it,
                    metadata = map[it.id.toString()]!!,
                )
            }
        }
    }
}
