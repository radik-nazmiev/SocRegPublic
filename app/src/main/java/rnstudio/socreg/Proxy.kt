package rnstudio.socreg

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.androidnetworking.AndroidNetworking
import io.reactivex.Observer
import okhttp3.Authenticator
import okhttp3.Credentials.basic
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import rnstudio.socreg.utils.App
import rnstudio.socreg.utils.UserAgentInterceptor
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

@Entity(tableName = "proxy")
class Proxy {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    @ColumnInfo(name = "host") var host: String? = ""
    @ColumnInfo(name = "port") var port: String? = ""
    @ColumnInfo(name = "user_name") var userName: String? = ""
    @ColumnInfo(name = "password") var password: String? = ""
    @Ignore
    var isAviable: Boolean? = null

    fun check() {
        initialization( "")
        val request = Request.Builder()
            .url("https://vkclient.info/api/ip/")
            .build()

        App.mClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()
            //val jsonObject = JSONObject(body)

            val ip: String = body //jsonObject.getString("ip")
            isAviable = host == ip
        }
    }

    fun initialization(userAgent: String) {
        var client: OkHttpClient? = null
        if (this.userName == "") {
            val proxy = Proxy(
                Proxy.Type.HTTP,
                port?.let { InetSocketAddress.createUnresolved(this.host, it.toInt()) }
            )
            val builder: OkHttpClient.Builder = OkHttpClient.Builder().proxy(proxy)
            if (!userAgent.isEmpty()) {
                builder.addInterceptor(UserAgentInterceptor(userAgent))
            }
            val clientBuilder: OkHttpClient.Builder = builder
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
            App.mClientBuilder = clientBuilder
            client = clientBuilder
                .build()
        } else {
            val proxyAuthenticator = Authenticator { route, response ->
                val credential = userName?.let { password?.let { it1 -> basic(it, it1) } }
                if (response.request.header("Proxy-Authorization") != null) {
                    null // If we already failed with these credentials, don't retry.
                } else credential?.let {
                    response.request.newBuilder().header("Proxy-Authorization", it)
                        .build()
                }
            }
            val proxy = Proxy(
                Proxy.Type.HTTP,
                port?.toInt()?.let { InetSocketAddress.createUnresolved(this.host, it) }
            )
            try {
                val sslContext = SSLContext.getDefault()
                val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
                    .proxy(proxy)
                    .proxyAuthenticator(proxyAuthenticator)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                App.mClientBuilder = clientBuilder
                client = clientBuilder
                    .build()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
        App.mClient = client
        App.initializeNetwork(client)
    }
}