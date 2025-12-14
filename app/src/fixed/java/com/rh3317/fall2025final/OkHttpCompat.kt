package com.rh3317.fall2025final

import okhttp3.OkHttpClient
import okhttp3.Response
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * OkHttp 4.x compatibility (CVE fixed)
 */
object OkHttpCompat {

    fun code(response: Response): Int =
        response.code

    fun bodyString(response: Response): String =
        response.body?.string().orEmpty()

    fun applySsl(
        builder: OkHttpClient.Builder,
        sslSocketFactory: SSLSocketFactory,
        trustManager: X509TrustManager
    ) {
        // OkHttp 4.x REQUIRES the 2-arg overload
        builder.sslSocketFactory(sslSocketFactory, trustManager)
    }
}