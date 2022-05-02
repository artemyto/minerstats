package misha.miner.presentation.currencies

import misha.miner.data.models.coinmarketcap.Quote
import misha.miner.data.models.coinmarketcap.data.Listing
import misha.miner.data.models.coinmarketcap.data.Metadata

data class CurrencyVO(
    val id: Int,
    val name: String,
    val symbol: String,
    val slug: String,
    val cmc_rank: String,
    val quote: Quote,
    val logo: String,
) {
    companion object {
        fun fromListingAndMetadata(
            listing: Listing,
            metadata: Metadata,
        ) = CurrencyVO(
            id = listing.id,
            name = listing.name,
            symbol = listing.symbol,
            slug = listing.slug,
            cmc_rank = listing.cmc_rank,
            quote = listing.quote,
            logo = metadata.logo,
        )
    }
}
