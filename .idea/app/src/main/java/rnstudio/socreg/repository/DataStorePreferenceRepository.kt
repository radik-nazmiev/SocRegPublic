package rnstudio.socreg.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DataStorePreferenceRepository(_context: Context) {
    private val context = _context
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val userGender = "unset"

    companion object {
        val PREF_USE_GENDER = stringPreferencesKey("user_gender")
        val PREF_BDATE_FROM = stringPreferencesKey("bdate_from")
        val PREF_BDATE_TO = stringPreferencesKey("bdate_to")
    }

    suspend fun setUserGender(gender: String) {
        context.dataStore.edit { settings ->
            settings[PREF_USE_GENDER] = gender
        }
    }

    suspend fun setUserBDateFrom(bdate: String) {
        context.dataStore.edit { settings ->
            settings[PREF_BDATE_FROM] = bdate
        }
    }

    suspend fun setUserBDateTo(bdate: String) {
        context.dataStore.edit { settings ->
            settings[PREF_BDATE_TO] = bdate
        }
    }

    val getUserGender: Flow<String> = context.dataStore.data
        .catch {
            it.printStackTrace()
        }
        .map { preferences ->
            preferences[PREF_USE_GENDER] ?: userGender
        }

    val getUserGenderSync: String? = runBlocking { context.dataStore.data.first()[PREF_USE_GENDER] }

    val getUserBDateFrom: Flow<String> = context.dataStore.data
        .catch {
            it.printStackTrace()
        }
        .map { preferences ->
            preferences[PREF_BDATE_FROM] ?: ""
        }

    val getUserBDateFromSync: String? = runBlocking { context.dataStore.data.first()[PREF_BDATE_FROM] }

    val getUserBDateTo: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PREF_BDATE_TO] ?: ""
        }

    val getUserBDateToSync: String? = runBlocking { context.dataStore.data.first()[PREF_BDATE_TO] }

}