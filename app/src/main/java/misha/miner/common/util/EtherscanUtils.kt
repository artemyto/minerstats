package misha.miner.common.util

private const val WEI_IN_ETH = 1e18

fun String.getEthValue() =
    this.toDoubleOrNull()?.let {
        it / WEI_IN_ETH
    } ?: 0.0

fun Double.getEthValue() = this / WEI_IN_ETH