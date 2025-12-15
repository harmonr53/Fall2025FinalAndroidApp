@file:Suppress("DEPRECATION")

package com.rh3317.fall2025final

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rh3317.fall2025final.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.Proxy

class Cve20162402Fragment : Fragment(R.layout.fragment_cve_2016_2402) {

    private lateinit var resultText: TextView
    private lateinit var flavorText: TextView
    private lateinit var btnPinned: Button

    private val host = "10.211.55.9"

    private val pinSha256 = "sha256/Bcx1CwgobSuDImIjPwaThTIu3lDR5iD+K4AkMB/pNoQ="
    private val pinSha1 = "sha1/hQhZWHxzR+EHuQWQknLll74JaZs="
    private val leafPinSha256 = "sha256/Bcx1CwgobSuDImIjPwaThTIu3lDR5iD+K4AkMB/pNoQ="

    private val url = "https://$host/test"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultText = view.findViewById(R.id.result_text)
        flavorText = view.findViewById(R.id.flavor_text)
        btnPinned = view.findViewById(R.id.btn_pinned_request)

        flavorText.text = "Flavor: ${BuildConfig.FLAVOR}"

        btnPinned.setOnClickListener {
            resultText.text = "Running pinned request:\n$url\n\nFlavor=${BuildConfig.FLAVOR}"
            runPinnedRequest()
        }
    }

    private fun runPinnedRequest() {
        val chosenPin = if (BuildConfig.FLAVOR == "vuln2402") pinSha1 else pinSha256
        if (!chosenPin.startsWith("sha1/") && !chosenPin.startsWith("sha256/")) {
            postResult("Pin string is invalid: $chosenPin")
            return
        }
        val pinner = CertificatePinner.Builder()
            .add(host, chosenPin)
            .build()

        val client = OkHttpClient.Builder()
//            .proxy(Proxy.NO_PROXY)
            .certificatePinner(pinner)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                postResult("FAIL:\n${e.javaClass.simpleName}: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val code = OkHttpCompat.code(response)
                val bodyStr = OkHttpCompat.bodyString(response)
                postResult("OK: HTTP $code\n\n$bodyStr")
            }
        })
    }

    private fun postResult(msg: String) {
        activity?.runOnUiThread {
            resultText.text = msg
        }
    }
}