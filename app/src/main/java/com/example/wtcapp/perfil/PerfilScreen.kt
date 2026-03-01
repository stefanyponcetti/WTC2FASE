package com.example.wtcapp.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfilScreen(
    nome: String = "Eliane Meliz Cruz",
    email: String = "eli.meliz@wtc.com.br",
    telefone: String = "(11) 98342-13421",
    dataNascimento: String = "02/03/1981",
    cargo: String = "Analista de Recursos Humanos",
    tempoEmpresa: String = "3 anos e 2 meses",
    unidade: String = "Av. Paulista - P. Central",
    tipoUsuario: String = "Colaborador",
    onLogout: () -> Unit = {}
) {
    val azulFundo = Color(0xFF384B5B)
    val laranja = Color(0xFFF18A21)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(azulFundo)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "PERFIL",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tipo de usuário
            Text(
                text = "Tipo: $tipoUsuario",
                color = laranja,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Exibição de informações do usuário
            ProfileField(label = "Nome", value = nome)
            ProfileField(label = "E-mail", value = email)
            ProfileField(label = "Telefone", value = telefone)
            ProfileField(label = "Data de Nascimento", value = dataNascimento)
            ProfileField(label = "Cargo", value = cargo)
            ProfileField(label = "Tempo na Empresa", value = tempoEmpresa)
            ProfileField(label = "Unidade", value = unidade)

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de sair
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = laranja),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sair", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFF4A5A68), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFFF18A21),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}


