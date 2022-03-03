package rnstudio.socreg.random_person

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class RandomPerson {
    @SerializedName("LastName")
    @Expose
    var lastName: String? = null

    @SerializedName("FirstName")
    @Expose
    var firstName: String? = null

    @SerializedName("FatherName")
    @Expose
    var fatherName: String? = null

    fun get(gender: String): RandomPerson? {
        val apiService =
            RandomPersonRetrofit.retrofit.create(RandomPersonEndpointInterface::class.java)
        val call = apiService.getPerson(gender)
        return call.execute().body()
    }
}