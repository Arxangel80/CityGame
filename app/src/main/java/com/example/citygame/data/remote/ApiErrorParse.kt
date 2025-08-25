package com.example.citygame.data.remote

import org.json.JSONObject
import retrofit2.Response

object ApiErrorParser {
    fun parseError(response: Response<*>): String {
        val errorJson = response.errorBody()?.string()
        return try {
            val json = JSONObject(errorJson)
            json.optString("message", "Unknown error (${response.code()})")
        } catch (e: Exception) {
            "Error (${response.code()})"
        }
    }
}
