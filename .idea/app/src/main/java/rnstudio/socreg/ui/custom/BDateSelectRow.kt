package rnstudio.socreg.ui.custom

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Job
import rnstudio.socreg.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BDateSelect(
    datePicked1 : String?,
    updatedDate1 : ( date : Long? ) -> Job,
    datePicked2 : String?,
    updatedDate2 : ( date : Long? ) -> Job
){
    Row(){
        Text(
            text = stringResource(R.string.date_birth_from),
            color = Color.White,
            fontSize = 18.sp
        )
    }
    Row(){
        BDateSelectView(datePicked1, updatedDate1)
    }
    Row(){
        Text(
            text = stringResource(id = R.string.date_of_birth_to),
            color = Color.White,
            fontSize = 18.sp
        )
    }
    Row(){
        BDateSelectView(datePicked2, updatedDate2)
    }
}

@ExperimentalComposeUiApi
@Composable
private fun BDateSelectView(datePicked : String?, updatedDate : ( date : Long? ) -> Job){
    val activity = LocalContext.current as AppCompatActivity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .padding(top = 10.dp, bottom = 10.dp)
            .border(5.dp, Color.White, RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .clickable {
                showDatePicker(activity, updatedDate)
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = timeToDate(datePicked)?:"Выберите дату",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp, 20.dp),
                tint = MaterialTheme.colors.onSurface
            )

        }

    }
}

private fun timeToDate(time: String?): String?{
    val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance()
    if (time != null && time.isNotEmpty()) {
        calendar.timeInMillis = time.toLong()
    }
    return simpleDateFormat.format(calendar.time)
}

private fun showDatePicker(
    activity : AppCompatActivity,
    updatedDate: (Long?) -> Job)
{
    val picker = MaterialDatePicker.Builder.datePicker().build()
    picker.show(activity.supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener {
        updatedDate(it)
    }
}

@Preview(showBackground = true)
@Composable
fun BDateSelectPreview() {
//    bDateSelect()
}