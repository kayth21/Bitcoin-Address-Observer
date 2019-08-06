package com.ceaver.bao.blockstream

import com.ceaver.bao.network.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object BlockstreamRepository {

    private val blockstream = Retrofit.Builder()
        .baseUrl("https://blockstream.info/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(BlockstreamService::class.java)

    fun lookupAddress(address: String): Response<BlockstreamAddress> = try {
        mapResponse(blockstream.address(address).execute())
    } catch (e: IOException) {
        Response.error(e.toString())
    }
}

private fun <T> mapResponse(response: retrofit2.Response<T>): Response<T> =
    if (response.isSuccessful) {
        Response.success(response.body()!!)
    } else {
        val errorCode = response.code().toString()
        val errorJsonString = response.errorBody()?.string()
        Response.exception("Error " + errorCode + ": " + errorJsonString)
    }