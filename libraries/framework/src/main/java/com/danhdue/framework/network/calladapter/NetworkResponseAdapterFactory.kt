package com.danhdue.framework.network.calladapter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

class NetworkResponseAdapterFactory : CallAdapter.Factory() {

    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {

        // suspend functions return Call<T>
        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        // check first generic type of Call<T>
        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<NetworkResponse<<Foo>> or Call<NetworkResponse<out Foo>>"
        }

        // get the response type inside the Call, e.g., NetworkResponse<Foo>
        val responseType = getParameterUpperBound(0, returnType)
        // if the response type is not NetworkResponse, then we can't handle this return type
        if (getRawType(responseType) != NetworkResponse::class.java) {
            return null
        }

        // the response type is NetworkResponse<Foo>
        check(responseType is ParameterizedType) {
            "Response must be parameterized as NetworkResponse<Foo> or NetworkResponse<out Foo>"
        }

        val successBodyType = getParameterUpperBound(0, responseType)

        val errorBodyConverter =
                retrofit.nextResponseBodyConverter<Any>(null, Any::class.java, annotations)

        return NetworkResponseAdapter<Any>(successBodyType, errorBodyConverter)
    }
}
