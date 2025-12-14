package com.rh3317.fall2025final

import okhttp3.OkHttpClient
import okhttp3.Response
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * OkHttp 3.1.1 compatibility (CVE-2016-2402 vulnerable)
 */
object OkHttpCompat {

    fun code(response: Response): Int =
        response.code()

    fun bodyString(response: Response): String {
        val body = response.body()
        return try {
            body?.string().orEmpty()
        } finally {
            body?.close()
        }
    }

    fun applySsl(
        builder: OkHttpClient.Builder,
        sslSocketFactory: SSLSocketFactory,
        trustManager: X509TrustManager
    ) {
        // OkHttp 3.x ONLY supports the 1-arg overload
        builder.sslSocketFactory(sslSocketFactory)
    }
}
