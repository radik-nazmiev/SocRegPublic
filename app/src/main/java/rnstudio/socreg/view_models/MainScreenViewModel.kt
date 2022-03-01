package rnstudio.socreg.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import rnstudio.socreg.repository.DataStorePreferenceRepository
import rnstudio.socreg.utils.Gender

class MainScreenViewModel(private val dataStorePreferenceRepository: DataStorePreferenceRepository): ViewModel() {
    val userGenders: MutableLiveData<List<Gender>> = MutableLiveData()
    private val genders = listOf(
        Gender(1, "unset", false),
        Gender(2,"man", false),
        Gender(3,"woman", false)
    )

    init {
        userGenders.value = genders
    }

    fun getSelectedGender() {
        viewModelScope.launch {
            dataStorePreferenceRepository.getUserGender.collect { selectedGender ->
                genders.forEach {
                    it.isSelected = it.name == selectedGender
                }
                userGenders.value = emptyList()
                userGenders.value = genders
            }
        }
    }

    suspend fun saveSelectedGender(userGender: String) {
        dataStorePreferenceRepository.setUserGender(userGender)
        getSelectedGender()
    }
}