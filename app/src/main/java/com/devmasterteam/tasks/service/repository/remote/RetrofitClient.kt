package com.devmasterteam.tasks.service.repository.remote

import com.devmasterteam.tasks.service.constants.TaskConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {
    companion object {
        private lateinit var INSTANCE: Retrofit
        private var token: String = ""
        private var personKey: String = ""

        private const val BASE_URL = "http://devmasterteam.com/CursoAndroidAPI/"

        private fun getRetrofitInstance(): Retrofit {
            val http = OkHttpClient.Builder()

            http.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                        .newBuilder()
                        .addHeader(TaskConstants.HEADER.TOKEN_KEY, token)
                        .addHeader(TaskConstants.HEADER.PERSON_KEY, personKey)
                        .build()

                    return chain.proceed(request)
                }
            })

            if (!::INSTANCE.isInitialized) {
                synchronized(RetrofitClient::class) {
                    INSTANCE = Retrofit.Builder().baseUrl(BASE_URL).client(http.build())
                        .addConverterFactory(GsonConverterFactory.create()).build()
                }
            }

            return INSTANCE
        }

        fun <S> createService(service: Class<S>): S {
            return getRetrofitInstance().create(service)
        }

        fun addHeaders(tokenValue: String, personKeyValue: String) {
            token = tokenValue
            personKey = personKeyValue
        }
    }
}