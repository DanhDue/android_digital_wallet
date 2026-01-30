package com.danhdue.framework.network.calladapter

import java.lang.reflect.Type
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter

class NetworkResponseAdapter<S : Any>(
        private val successType: Type,
        private val errorBodyConverter: Converter<ResponseBody, Any>
) : CallAdapter<S, Call<NetworkResponse<S>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<NetworkResponse<S>> {
        return NetworkResponseCall(call, errorBodyConverter)
    }
}
