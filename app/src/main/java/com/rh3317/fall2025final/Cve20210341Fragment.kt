package com.rh3317.fall2025final

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class Cve20210341Fragment : Fragment(R.layout.fragment_cve_2021_0341) {

    private val baseUrl = "https://10.211.55.9"
//    private val baseUrl = "https://10.0.2.2:8080"

    private lateinit var resultText: TextView
    private lateinit var btnSecure: Button
    private lateinit var btnInsecure: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultText = view.findViewById(R.id.result_text)
        btnSecure = view.findViewById(R.id.btn_secure_request)
        btnInsecure = view.findViewById(R.id.btn_insecure_request)

        btnSecure.setOnClickListener {
            setResult("Running SECURE request… (expected to fail under MITM)")
            val client = secureClient()
            makeGet(client, "$baseUrl/test")
        }

        btnInsecure.setOnClickListener {
            setResult("Running INSECURE request… (expected to succeed under MITM)")
            val client = insecureTrustAllClient()
            makeGet(client, "$baseUrl/test")
        }
    }

    private fun makeGet(client: OkHttpClient, url: String) {
        val req = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                setResult("FAIL:\n${e.javaClass.name}\n${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                val code = OkHttpCompat.code(response)
                val bodyStr = OkHttpCompat.bodyString(response)
                setResult("OK: HTTP $code\n\n$bodyStr")
            }
        })
    }

    private fun setResult(msg: String) {
        activity?.runOnUiThread {
            resultText.text = msg
        }
    }

    private fun secureClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            // Uses system trust + hostname verification (normal behavior)
            .build()
    }

    /**
     * INSECURE ON PURPOSE:
     * - Trusts all server certificates
     * - Disables hostname verification
     */
    private fun insecureTrustAllClient(): OkHttpClient {
        // Trust manager that accepts all certs (INSECURE ON PURPOSE)
        val trustAll = object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {}

            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        // Create SSL context using the insecure trust manager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(
            null,
            arrayOf<TrustManager>(trustAll),
            SecureRandom()
        )

        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }

        OkHttpCompat.applySsl(
            builder,
            sslContext.socketFactory,
            trustAll
        )

        return builder.build()
    }
}