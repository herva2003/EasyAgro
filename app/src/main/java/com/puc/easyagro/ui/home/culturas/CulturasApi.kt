package com.puc.easyagro.ui.home.culturas

import retrofit2.Call
import retrofit2.http.GET

interface CulturasApi {
    @GET("http://172.16.225.44:8080/games/")
    fun getCulturas(): Call<List<Cultura>>
}