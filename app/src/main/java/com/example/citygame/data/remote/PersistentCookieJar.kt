package com.example.citygame.data.remote

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    private var cache: MutableMap<String, List<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cache[url.host] = cookies
        val cookieStrings = cookies.map { it.toString() }.toSet()
        prefs.edit().putStringSet(url.host, cookieStrings).apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        if (cache[url.host] == null) {
            val cookieStrings = prefs.getStringSet(url.host, emptySet()) ?: emptySet()
            val cookies = cookieStrings.mapNotNull { Cookie.parse(url, it) }
            cache[url.host] = cookies
        }
        return cache[url.host] ?: emptyList()
    }

    fun getCookieHeader(
        cookieJar: PersistentCookieJar,
        url: HttpUrl,
        cookieName: String = "session"
    ): String? {
        val cookies = cookieJar.loadForRequest(url)
        val sessionCookie = cookies.find { it.name == cookieName }
        return sessionCookie?.let { "${it.name}=${it.value}" }
    }

}
