package rnstudio.socreg.ui.custom

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import rnstudio.socreg.R
import rnstudio.socreg.repository.DataStorePreferenceRepository
import rnstudio.socreg.utils.App
import rnstudio.socreg.utils.Gender
import rnstudio.socreg.view_models.BDateViewModel
import rnstudio.socreg.view_models.MainScreenViewModel

@Composable
fun SexSelectRow() {
    val coroutineScope = rememberCoroutineScope()
    val dataStorePreferenceRepository = App.getDataStore()
    val context = LocalContext.current
    val viewModel: MainScreenViewModel = viewModel(factory = DataStoreViewModelFactory(dataStorePreferenceRepository))
    val genders by viewModel.userGenders.observeAsState()
    viewModel.getSelectedGender()

    val onSelectionChange = { gender: Gender ->
        coroutineScope.launch {
            viewModel.saveSelectedGender(gender.name)
        }
    }
    Row(
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)
    ){
        Text(
            text = stringResource(id = R.string.gender) + ":",
            color = Color.White,
            fontSize = 18.sp
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
//                .selectableGroup()
    ) {
        genders?.forEach { gender ->
            Column( Modifier
//                    .fillMaxWidth()
                .height(56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    text = genderMatching(gender.name, context),
                    style = MaterialTheme.typography.body1.merge(),
                    color = Color.White,
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                size = 12.dp,
                            ),
                        )
                        .clickable {
                            onSelectionChange(gender)
                        }
                        .background(
                            if (
                                gender.isSelected //&& gender.id == selectedOption
                            ) {
                                colorResource(id = R.color.colorPrimaryDark)
                            } else {
                                colorResource(id = R.color.colorPrimary)
                            }
                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ),
                )
            }
        }
    }
}

class DataStoreViewModelFactory(private val dataStorePreferenceRepository: DataStorePreferenceRepository) :
    ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
                return MainScreenViewModel(dataStorePreferenceRepository) as T
            }
            else if(modelClass.isAssignableFrom(BDateViewModel::class.java)){
                return BDateViewModel(dataStorePreferenceRepository) as T
            }
            throw IllegalStateException()
        }
    }

private fun genderMatching(selectedGender: String, context: Context): String{
    return when(selectedGender){
        "unset" -> context.getString(R.string.anyone)
        "woman" -> context.getString(R.string.female)
        "man" -> context.getString(R.string.male)
        else -> context.getString(R.string.anyone)
    }
}

@Preview(showBackground = true)
@Composable
fun SexSelectRowPreview() {
    SexSelectRow()
}