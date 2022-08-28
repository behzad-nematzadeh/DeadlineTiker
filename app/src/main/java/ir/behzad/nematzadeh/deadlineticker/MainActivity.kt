package ir.behzad.nematzadeh.deadlineticker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.behzad.nematzadeh.deadlineticker.ui.theme.DeadLineTickerTheme
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeadLineTickerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val items = remember {
                        (1..25)
                            .map {
                                Ticker(
                                    title = "item $it",
                                    description = "description $it",
                                    deadline = Calendar.getInstance().apply { add(Calendar.HOUR, it) }.time
                                )
                            }
                    }
                    LazyColumn {
                        itemsIndexed(items) { index, it ->
                            Item(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(item: Ticker) {
    var time by remember { mutableStateOf(item.deadline.time - Date().time) }

    LaunchedEffect(key1 = item) {
        while (true) {
            Log.d("MainActivity", "${item.title} $time")
            time = item.deadline.time - Date().time
            delay(1_000)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(text = item.title)
                Text(text = item.description)
            }
            Text(
                text = calcTime(time / 1000),
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
    }
}

fun calcTime(totalTime: Long): String {
    var day = 0L
    var hour = 0L
    var minute = 0L
    var second = 0L

    if (3600 * 24 <= totalTime) day = totalTime / (3600 * 24)
    if (3600 <= totalTime) hour = totalTime / (60 * 60) % 24
    if (60 <= totalTime) minute = totalTime / 60 % 60
    if (0 < totalTime) second = totalTime % 60

    val sb = StringBuilder()

    if (day < 10) {
        sb.append("0").append(day).append(" : ")
    } else {
        sb.append(day).append(" : ")
    }

    if (hour < 10) {
        sb.append("0").append(hour).append(" : ")
    } else {
        sb.append(hour).append(" : ")
    }

    if (minute < 10) {
        sb.append("0").append(minute).append(" : ")
    } else {
        sb.append(minute).append(" : ")
    }

    if (second < 10) {
        sb.append("0").append(second)
    } else {
        sb.append(second)
    }

    return sb.toString()
}

data class Ticker(
    val title: String,
    val description: String,
    val deadline: Date,
)