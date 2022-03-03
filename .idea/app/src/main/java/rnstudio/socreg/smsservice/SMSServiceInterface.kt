package rnstudio.socreg.smsservice

import io.reactivex.Observable
import org.json.JSONObject

interface SMSServiceInterface {
    fun getBalance(): Observable<JSONObject>
}