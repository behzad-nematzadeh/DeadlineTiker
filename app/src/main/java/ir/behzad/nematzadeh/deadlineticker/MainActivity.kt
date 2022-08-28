package ir.behzad.nematzadeh.deadlineticker

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.behzad.nematzadeh.deadlineticker.ui.theme.DeadLineTickerTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

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
                    BuildListView(items = items)
                    //HelloContent()

                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BuildListView(items: List<Ticker>) {
    var job: Job? = null
    val state = rememberLazyListState()
    val fullyVisibleIndices: List<Int> by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                emptyList()
            } else {
                val fullyVisibleItemsInfo = visibleItemsInfo.toMutableList()

                val lastItem = fullyVisibleItemsInfo.last()

                val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

                if (lastItem.offset + lastItem.size > viewportHeight) {
                    fullyVisibleItemsInfo.removeLast()
                }

                val firstItemIfLeft = fullyVisibleItemsInfo.firstOrNull()
                if (firstItemIfLeft != null && firstItemIfLeft.offset < layoutInfo.viewportStartOffset) {
                    fullyVisibleItemsInfo.removeFirst()
                }

                fullyVisibleItemsInfo.map { it.index }
            }
        }
    }

    LazyColumn(state = state) {
        itemsIndexed(items) { index, it ->
            var name by remember { mutableStateOf(0L) }

            val item = items[index]
            if (fullyVisibleIndices.isNotEmpty() && fullyVisibleIndices.find { it == index } == index) {
                Log.e("MainActivity", "index: $index isVisible: $fullyVisibleIndices")
                job = MainScope().launch {
                    while (true) {
                        name =
                            TimeUnit.MILLISECONDS.toSeconds(item.deadline.time - Date().time)
                        Log.e("MainActivity", "BuildListView: index: $index value: $name")
                        delay(1000)
                    }
                }
            }
            else job?.cancel()

            key(it) {
                Item(item, name)
            }
        }
    }
}

@Composable
private fun Item(item: Ticker, time: Long) {
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
                text = calcTime(time),
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