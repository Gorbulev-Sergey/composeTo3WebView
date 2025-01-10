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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import ru.gorbulevsv.composeto3webview.ui.theme.ComposeTo3WebViewTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    var listDate = mutableStateOf(
        listOf(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        )
    )

    var url0 = mutableStateOf(url(listDate.value[0]))
    var url1 = mutableStateOf(url(listDate.value[1]))
    var url2 = mutableStateOf(url(listDate.value[2]))

    var activeDate = mutableStateOf(listDate.value[1])

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
                                Text(activeDate.value.toString())
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
                                    val newList =
                                        listOf(
                                            listDate.value[0].minusDays(1),
                                            listDate.value[0],
                                            listDate.value[1]
                                        )
                                    url0.value = url(newList[0])
                                    listDate.value = newList
                                }) {
                                    Text("Назад")
                                }
                                Button(onClick = {
                                    activeDate.value = listDate.value[2]
                                    val newList =
                                        listOf(
                                            listDate.value[1],
                                            listDate.value[2],
                                            listDate.value[2].plusDays(1)
                                        )
                                    url2.value = url(newList[2])
                                    listDate.value = newList
                                }) {
                                    Text("Вперёд")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Web(
                        url = url0.value,
                        isShow = activeDate.value == listDate.value[0],
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                    Web(
                        url = url1.value,
                        isShow = activeDate.value == listDate.value[1],
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                    Web(
                        url = url2.value,
                        isShow = activeDate.value == listDate.value[2],
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
fun Web(url: String, isShow: Boolean, modifier: Modifier = Modifier) {
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