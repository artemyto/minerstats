package misha.miner.common.util

private const val MEGAHASH = 1000000

fun Double.hashrateToMegahash(): Double {
    return this / MEGAHASH
}

fun Double.hashrateToMegahashLabel(): String {
    return "${String.format("%.2f", this.hashrateToMegahash())} MH/s"
}
