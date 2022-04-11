package misha.miner.common

fun String.fullEthAddr() = "0x$this"

fun String?.getIfNotBlankOrElse(alternative: String) =
    if (this != null && this.isNotBlank()) this else alternative
