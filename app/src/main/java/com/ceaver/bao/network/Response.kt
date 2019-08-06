package com.ceaver.bao.network

data class Response<T> private constructor(
    val result: T?,
    val exception: String?,
    val error: String?

) {
    companion object {
        fun <T> success(result: T): Response<T> {
            return Response(result, null, null)
        }

        fun <T> exception(exception: String): Response<T> {
            return Response(null, exception, null)
        }

        fun <T> error(error: String): Response<T> {
            return Response(null, null, error)
        }
    }

    fun isSuccessful(): Boolean = result != null

    fun isException(): Boolean = exception != null

    fun isError(): Boolean = error != null

    fun failureText(): String? = exception ?: error
}

