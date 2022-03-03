package rnstudio.socreg.random_person

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RandomPersonEndpointInterface {

    @GET("?unescaped=false&params=LastName,FirstName,FatherName")
    fun getPerson(@Query("gender") gender: String?): Call<RandomPerson>
}