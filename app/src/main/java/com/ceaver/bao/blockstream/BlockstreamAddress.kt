package com.ceaver.bao.blockstream

import com.google.gson.annotations.SerializedName

data class BlockstreamAddress(
    @SerializedName("address")
    val address: String,
    @SerializedName("chain_stats")
    val chainStats: BlockstreamAddressDetails,
    @SerializedName("mempool_stats")
    val mempoolStats: BlockstreamAddressDetails
)

data class BlockstreamAddressDetails(
    @SerializedName("funded_txo_count")
    val fundedTxoCount: Int,
    @SerializedName("funded_txo_sum")
    val fundedTxoSum: Long,
    @SerializedName("spent_txo_count")
    val spentTxoCount: Int,
    @SerializedName("spent_txo_sum")
    val spentTxoSum: Long,
    @SerializedName("tx_count")
    val txCount: Int
)
