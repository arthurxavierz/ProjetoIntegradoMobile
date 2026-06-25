package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financaspessoais1.ui.theme.*

/**
 * Porta de login / cadastro (sem backend). A validação de formato é feita aqui;
 * [onLogin]/[onRegister] acessam o store local de contas e devolvem uma mensagem
 * de erro, ou null em caso de sucesso.
 */
@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> String?,
    onRegister: (name: String, email: String, password: String) -> String?
) {
    var isRegister by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }

    fun submit() {
        error = when {
            isRegister && name.isBlank() -> "Informe seu nome."
            !email.contains("@") -> "Informe um e-mail válido."
            password.length < 6 -> "Senha deve ter ao menos 6 caracteres."
            isRegister && confirm != password -> "As senhas não coincidem."
            else -> (if (isRegister) onRegister(name.trim(), email.trim(), password)
                     else onLogin(email.trim(), password)) ?: ""
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
            .padding(top = 52.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Box(
            Modifier
                .size(84.dp)
                .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = Blue500)
                .background(brandGradient(), RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AttachMoney, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp))
        }

        Spacer(Modifier.height(20.dp))
        Text("Minhas Finanças", fontFamily = DMSans, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = (-0.3).sp)
        Spacer(Modifier.height(6.dp))
        Text(
            if (isRegister) "Crie sua conta para começar" else "Controle inteligente do seu dinheiro",
            fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            color = TextMuted, textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(36.dp))

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (isRegister) {
                FieldLabel("Nome")
                AppTextField(
                    value = name,
                    onValueChange = { name = it; error = "" },
                    placeholder = "Seu nome",
                    radius = 14.dp
                )
            }

            FieldLabel("E-mail")
            AppTextField(
                value = email,
                onValueChange = { email = it; error = "" },
                placeholder = "seu@email.com",
                keyboardType = KeyboardType.Email,
                radius = 14.dp
            )
            FieldLabel("Senha")
            AppTextField(
                value = password,
                onValueChange = { password = it; error = "" },
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                radius = 14.dp
            )

            if (isRegister) {
                FieldLabel("Confirmar senha")
                AppTextField(
                    value = confirm,
                    onValueChange = { confirm = it; error = "" },
                    placeholder = "••••••••",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    radius = 14.dp
                )
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        "Esqueceu a senha?",
                        fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = Blue500, modifier = Modifier.clickableNoRipple { }
                    )
                }
            }

            if (error.isNotEmpty()) {
                Text(
                    error, fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    color = Red500, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
            }

            // Botão principal
            Button(
                onClick = { submit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(16.dp, RoundedCornerShape(14.dp), spotColor = Blue500),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                Text(if (isRegister) "Criar conta" else "Entrar", fontFamily = DMSans, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // divisor "ou"
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HorizontalDivider(Modifier.weight(1f), thickness = 1.dp, color = InputBorder)
                Text("ou", fontFamily = DMSans, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                HorizontalDivider(Modifier.weight(1f), thickness = 1.dp, color = InputBorder)
            }

            // Alternar login/cadastro
            OutlinedButton(
                onClick = {
                    isRegister = !isRegister
                    error = ""
                    confirm = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, InputBorder),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite)
            ) {
                Text(
                    if (isRegister) "Já tenho conta" else "Criar conta",
                    fontFamily = DMSans, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextLabel
                )
            }
        }
    }
}

fun brandGradient(): Brush = Brush.linearGradient(listOf(Blue500, Blue700))

@Composable
fun FieldLabel(text: String) {
    Text(text, fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextLabel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    radius: androidx.compose.ui.unit.Dp = 12.dp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontFamily = DMSans, fontSize = 14.sp, color = TextMuted) },
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = DMSans, fontSize = 14.sp, color = TextPrimary),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        shape = RoundedCornerShape(radius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue500,
            unfocusedBorderColor = InputBorder,
            focusedContainerColor = SurfaceWhite,
            unfocusedContainerColor = SurfaceWhite,
            cursorColor = Blue500
        )
    )
}
