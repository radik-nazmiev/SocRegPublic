package rnstudio.socreg.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import rnstudio.socreg.repository.DataStorePreferenceRepository

class BDateViewModel(private val dataStorePreferenceRepository: DataStorePreferenceRepository): ViewModel() {
    val bDateFrom: MutableLiveData<String> = MutableLiveData()
    val bDateTo: MutableLiveData<String> = MutableLiveData()

    fun getBDates() {
        viewModelScope.launch {
            dataStorePreferenceRepository.getUserBDateFrom.collect { dateFrom ->
                bDateFrom.value = dateFrom
            }
        }
        viewModelScope.launch {
            dataStorePreferenceRepository.getUserBDateTo.collect { dateTo ->
                bDateTo.value = dateTo
            }
        }
    }

    suspend fun saveBDateFrom(bDateFrom: String) {
        dataStorePreferenceRepository.setUserBDateFrom(bDateFrom)
    }

    suspend fun saveBDateTo(bDateTo: String) {
        dataStorePreferenceRepository.setUserBDateTo(bDateTo)
    }
}