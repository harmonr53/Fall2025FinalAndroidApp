package com.rh3317.fall2025final

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Cve20210341Fragment())
                .commit()
            bottomNav.selectedItemId = R.id.nav_cve_2021_0341
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_cve_2021_0341 -> Cve20210341Fragment()
                R.id.nav_cve_2016_2402 -> Cve20162402Fragment()
//                R.id.nav_cve_2 -> PlaceholderFragment.new("CVE #2 (coming soon)")
//                R.id.nav_cve_3 -> PlaceholderFragment.new("CVE #3 (coming soon)")
                else -> Cve20210341Fragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }
}