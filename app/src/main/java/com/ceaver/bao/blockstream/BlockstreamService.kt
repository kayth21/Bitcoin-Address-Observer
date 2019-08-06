package com.ceaver.bao.blockstream

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BlockstreamService {

    @GET("/api/address/{address}")
    fun address(@Path("address") address: String): Call<BlockstreamAddress>
}