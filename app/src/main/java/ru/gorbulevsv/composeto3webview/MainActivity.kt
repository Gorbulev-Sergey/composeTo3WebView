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

class MainActivity : ComponentActivity() {
    var url0 = mutableStateOf("http://www.patriarchia.ru/bu/2025-01-09/print.html")
    var url1 = mutableStateOf("http://www.patriarchia.ru/bu/2025-01-10/print.html")
    var url2 = mutableStateOf("http://www.patriarchia.ru/bu/2025-01-11/print.html")
    var listIndex = mutableStateOf(listOf(0, 1, 2))
    var activeIndex = derivedStateOf { listIndex.value[1] }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTo3WebViewTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomAppBar {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Button(onClick = {
                                    val newList =
                                        listOf<Int>(
                                            listIndex.value[2],
                                            listIndex.value[0],
                                            listIndex.value[1]
                                        )
                                    listIndex.value = newList
                                }) {
                                    Text("Назад")
                                }
                                Button(onClick = {
                                    val newList =
                                        listOf<Int>(
                                            listIndex.value[1],
                                            listIndex.value[2],
                                            listIndex.value[0]
                                        )
                                    listIndex.value = newList
                                }) {
                                    Text("Вперёд")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Web(
                        url = url0.value,
                        isShow = activeIndex.value == 0,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                    Web(
                        url = url1.value,
                        isShow = activeIndex.value == 1,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                    Web(
                        url = url2.value,
                        isShow = activeIndex.value == 2,
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
fun Web(url: String = "", isShow: Boolean = false, modifier: Modifier = Modifier) {
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