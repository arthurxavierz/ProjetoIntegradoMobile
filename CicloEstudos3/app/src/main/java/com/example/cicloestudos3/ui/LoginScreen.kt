package com.example.cicloestudos3.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cicloestudos3.ui.theme.*

/**
 * Login / sign-up gate matching the design's `showLogin` overlay. No backend:
 * format validation happens here; [onLogin]/[onRegister] hit the local account store
 * and return an error message, or null on success.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> String?,
    onRegister: (name: String, email: String, password: String) -> String?
) {
    var isRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun submit() {
        when {
            isRegister && name.isBlank() -> error = "Informe seu nome."
            !email.contains("@") -> error = "Informe um e-mail válido."
            password.length < 6 -> error = "Senha deve ter ao menos 6 caracteres."
            isRegister && confirm != password -> error = "As senhas não coincidem."
            else -> error = if (isRegister) onRegister(name.trim(), email.trim(), password)
                            else onLogin(email.trim(), password)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EstudosBackground)
            .verticalScroll(rememberScrollState())
            .padding(start = 32.dp, end = 32.dp, top = 52.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(84.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(26.dp),
                    clip = false,
                    ambientColor = EstudosPrimary.copy(alpha = 0.28f),
                    spotColor = EstudosPrimary.copy(alpha = 0.28f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(EstudosPrimary, EstudosDark))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = Color.White, modifier = Modifier.size(42.dp))
        }
        Spacer(Modifier.height(20.dp))

        Text(
            "Ciclo de Estudos",
            fontFamily = DmSans, fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = EstudosTitle, letterSpacing = (-0.3).sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (isRegister) "Crie sua conta para começar" else "Aprenda com método e consistência",
            fontFamily = DmSans, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            color = EstudosMuted, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))

        if (isRegister) {
            LoginField(
                label = "Nome",
                value = name,
                onValueChange = { name = it; error = null },
                placeholder = "Seu nome",
                keyboardType = KeyboardType.Text
            )
            Spacer(Modifier.height(14.dp))
        }

        LoginField(
            label = "E-mail",
            value = email,
            onValueChange = { email = it; error = null },
            placeholder = "seu@email.com",
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(14.dp))

        LoginField(
            label = "Senha",
            value = password,
            onValueChange = { password = it; error = null },
            placeholder = "••••••••",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        if (isRegister) {
            Spacer(Modifier.height(14.dp))
            LoginField(
                label = "Confirmar senha",
                value = confirm,
                onValueChange = { confirm = it; error = null },
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
        } else {
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    "Esqueceu a senha?",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = EstudosPrimary
                )
            }
        }

        error?.let {
            Spacer(Modifier.height(14.dp))
            Text(
                it,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = EstudosDanger,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { submit() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(14.dp),
                    clip = false,
                    ambientColor = EstudosPrimary.copy(alpha = 0.32f),
                    spotColor = EstudosPrimary.copy(alpha = 0.32f)
                ),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EstudosPrimary, contentColor = Color.White)
        ) {
            Text(if (isRegister) "Criar conta" else "Entrar", fontFamily = DmSans, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f), color = EstudosBorder)
            Text(
                "ou",
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.labelMedium,
                color = EstudosMuted
            )
            HorizontalDivider(Modifier.weight(1f), color = EstudosBorder)
        }
        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = {
                isRegister = !isRegister
                error = null
                confirm = ""
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, EstudosBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = EstudosLabel)
        ) {
            Text(
                if (isRegister) "Já tenho conta" else "Criar conta",
                fontFamily = DmSans, fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    Column(Modifier.fillMaxWidth()) {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = EstudosMuted) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = fieldColors()
        )
    }
}
