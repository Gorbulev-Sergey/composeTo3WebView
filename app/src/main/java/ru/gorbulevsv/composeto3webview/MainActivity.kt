package ru.gorbulevsv.composeto3webview

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.MutableIntList
import androidx.collection.mutableIntListOf
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ru.gorbulevsv.composeto3webview.ui.theme.ComposeTo3WebViewTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
//    var listDate = mutableStateOf(
//        listOf(
//            LocalDateTime.now().minusDays(1),
//            LocalDateTime.now(),
//            LocalDateTime.now().plusDays(1)
//        )
//    )

    var listI = mutableStateOf(
        listOf(0, 1, 2)
    )
    var activeI = derivedStateOf { listI.value[1] }
    var step = mutableStateOf(0)

    var date0 = mutableStateOf(LocalDateTime.now())
    var date1 = mutableStateOf(LocalDateTime.now().plusDays(1))
    var date2 = mutableStateOf(LocalDateTime.now().plusDays(2))

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTo3WebViewTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(activeI.value.toString())
                            })
                    },
                    bottomBar = {
                        BottomAppBar {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Button(onClick = {
                                    step.value--
                                    when (activeI.value) {
                                        0 -> date1.value =
                                            LocalDateTime.now().plusDays(step.value.toLong())

                                        1 -> date2.value =
                                            LocalDateTime.now().plusDays(step.value.toLong())

                                        2 -> date0.value =
                                            LocalDateTime.now().plusDays(step.value.toLong())
                                    }
                                    var newListI = listOf(
                                        listI.value[2],
                                        listI.value[0],
                                        listI.value[1]
                                    )
                                    listI.value = newListI
                                }) {
                                    Text("Назад")
                                }
                                Button(onClick = {
                                    step.value++
                                    when (activeI.value) {
                                        0 -> date2.value =
                                            LocalDateTime.now().plusDays(step.value.toLong())

                                        1 -> date0.value =
                                            LocalDateTime.now().plusDays(step.value.toLong())

                                        2 -> date1.value =
                                            LocalDateTime.now().plusDays(step.value.toLong())
                                    }
                                    var newListI = listOf(
                                        listI.value[1],
                                        listI.value[2],
                                        listI.value[0]
                                    )
                                    listI.value = newListI
                                }) {
                                    Text("Вперёд")
                                }
                            }
                        }
                    }
                ) { innerPadding ->

                    Web(
                        url = url(date0.value),
                        isShow = activeI.value == 0,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                    Web(
                        url = url(date1.value),
                        isShow = activeI.value == 1,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                    Web(
                        url = url(date2.value),
                        isShow = activeI.value == 2,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )

                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Web(url: String, isShow: Boolean = false, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.alpha(if (isShow) 1f else 0f),
        factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onLoadResource(view: WebView?, url: String?) {
                        super.onLoadResource(view, url)
                        evaluateJavascript(
                            "document.querySelector('.header')?.remove(); document.querySelector('.main h1')?.remove(); document.querySelector('.main br')?.remove();",
                            null
                        )
                        evaluateJavascript(
                            "try {document.querySelectorAll('b')[0].innerHTML = document?.querySelectorAll('b')[0].innerHTML.replace(new RegExp('[0-9]{1,2}. '), '');} catch (e) {}",
                            null
                        )
                        evaluateJavascript(
                            "try {document.querySelectorAll('strong')[0].innerHTML = document?.querySelectorAll('strong')[0].innerHTML.replace(new RegExp('[0-9]{1,2}. '), '');} catch (e) {}",
                            null
                        )
                        evaluateJavascript(
                            "document.querySelectorAll('a').forEach(e=>e.style.color='blue'); document.querySelectorAll('div').forEach(e=>e.style.fontSize='1.16rem'); document.querySelector('.main').style.lineHeight='1.61rem'; document.querySelectorAll('p').forEach(e=>e.style.textIndent='0'); document.querySelector('body').style.margin='0rem'; document.querySelector('body').style.userSelect='none'; document.querySelector('.main').style.overflowWrap='break-word';",
                            null
                        )
                    }
                }
                loadUrl(url)
            }
        },
        update = {
            it.loadUrl(url)
        }
    )
}

fun url(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return "http://www.patriarchia.ru/bu/${date.format(formatter)}/print.html"
}