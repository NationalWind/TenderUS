package com.hcmus.tenderus.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

object ApiClient {
    private const val BASE_URL = "https://192.168.1.183:8000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(getUnsafeOkHttpClient()!!)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    val SyncSignUpApi by lazy {
        create(SyncSignUp::class.java)
    }

    val SyncPasswordResetApi by lazy {
        create(SyncPasswordReset::class.java)
    }
    val LoginApi by lazy {
        create(Login::class.java)
    }
    val GetMatchesApi by lazy {
        create(GetMatch::class.java)
    }
    val MessagePollingApi by lazy {
        create(MessagePolling::class.java)
    }
    val MessageSendingApi by lazy {
        create(MessageSending::class.java)
    }
    val HaveReadMessageApi by lazy {
        create(HaveReadMessage::class.java)
    }
    val GetProfile by lazy {
        create(GetProfile::class.java)
    }
    val ProcessProfile by lazy {
        create(ProfileService::class.java)
    }
    val SignOutApi by lazy {
        create(SignOut::class.java)
    }



    private fun getUnsafeOkHttpClient(): OkHttpClient? {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager


            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.addInterceptor(loggingInterceptor)
            builder.readTimeout(30000, java.util.concurrent.TimeUnit.SECONDS)
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}