package com.example.financaspessoais1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.financaspessoais1.ui.FinanceApp
import com.example.financaspessoais1.ui.theme.FinancasPessoaisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancasPessoaisTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FinanceApp()
                }
            }
        }
    }
}
