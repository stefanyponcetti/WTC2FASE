package com.example.wtcapp.comunicados

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.topbar.TopBar

// 🔴 MODEL
data class Comunicado(
    val titulo: String,
    val descricao: String,
    val autor: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComunicadosScreen(
    onNavigateToCriar: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit
) {

    val azulFundo = Color(0xFF384B5B)
    val laranja = Color(0xFFF18A21)
    val cinzaCard = Color(0xFF4A5A68)

    val azulGradiente = Brush.verticalGradient(
        colors = listOf(Color(0xFF3F5466), Color(0xFF2E3D4A))
    )

    var comunicados by remember {
        mutableStateOf(
            listOf(
                Comunicado(
                    "WORKSHOP ACELERADOR DE VENDAS",
                    "Na última sexta-feira ocorreu o workshop para aceleração de vendas.",
                    "RH"
                ),
                Comunicado(
                    "Nova Norma Interna",
                    "Atualização das regras de segurança.",
                    "Gestão"
                ),
                Comunicado(
                    "Reunião Geral",
                    "Todos os colaboradores devem participar às 14h.",
                    "Diretoria"
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigate = { screen ->
                    when (screen) {
                        "chats" -> onNavigateToChats()
                        "perfil" -> onNavigateToPerfil()
                        "comunicados" -> onNavigateToComunicados()
                        "contatos" -> onNavigateToContatos()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCriar,
                containerColor = laranja,
                shape = CircleShape,
                modifier = Modifier.shadow(8.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Novo Comunicado",
                    tint = Color.White
                )
            }
        },
        containerColor = azulFundo
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(azulGradiente)
        ) {

            Text(
                text = "Últimos comunicados",
                color = Color(0xFFDDE5EC),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(comunicados) { comunicado ->

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = cinzaCard)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {

                                Text(
                                    comunicado.titulo,
                                    color = laranja,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    comunicado.descricao,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    "Por: ${comunicado.autor}",
                                    color = Color(0xFFDDE5EC),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}