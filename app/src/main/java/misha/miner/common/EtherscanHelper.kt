package misha.miner.common

class EtherscanHelper(private val data: String) {

    private companion object {
        const val WEI_IN_ETH = 1e18
    }

    fun getBalanceValue() =
        data.toDoubleOrNull()?.let {
            it / WEI_IN_ETH
        } ?: 0.0

    fun getBalanceLabel() =
        "${String.format("%.5f", getBalanceValue())} ETH"
}