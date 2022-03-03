package rnstudio.socreg.random_person

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RandomPersonRetrofit {
    companion object{
        val BASE_URL = "https://api.randomdatatools.ru"
        var retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}