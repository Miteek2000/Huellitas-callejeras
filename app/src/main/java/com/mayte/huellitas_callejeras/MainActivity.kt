package com.mayte.huellitas_callejeras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mayte.huellitas_callejeras.Screens.InicioSesion
import com.mayte.huellitas_callejeras.ui.theme.Huellitas_callejerasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Huellitas_callejerasTheme {
              InicioSesion()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Huellitas_callejerasTheme {

    }
}
