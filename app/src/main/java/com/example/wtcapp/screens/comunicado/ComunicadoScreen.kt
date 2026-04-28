package com.example.wtcapp.comunicados

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.screens.topbar.TopBar

data class Comunicado(
    val titulo: String,
    val descricao: String,
    val autor: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComunicadosScreen(
    onNavigateToCriar: () -> Unit,
    onNavigateToChats: () -> Unit,// vai chamar a tela de criação
    onNavigateToPerfil: () -> Unit, // opcional
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
                Comunicado("WORKSHOP ACELERADOR DE VENDAS", "Na última sexta-feira (20/09), ocorreu o workshop para acelerar vendas no setor varejista. Ao todo, 300 representantes compareceram.", "RH"),
                Comunicado("Nova Norma Interna", "Atualização das regras de segurança.", "Gestão"),
                Comunicado("Reunião Geral", "Todos os colaboradores devem participar às 14h.", "Diretoria")
            )
        )
    }

    Scaffold(
        topBar = { TopBar(
            onNavigate = { destino ->
                when (destino) {
                    "chats" -> onNavigateToChats()
                    "perfil" -> onNavigateToPerfil()
                    "comunicados" -> onNavigateToComunicados()
                    "contatos" -> onNavigateToContatos()
                }
            }
        )
        } ,
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
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
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
                    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(14.dp)),
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
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Por: ${comunicado.autor}",
                                    color = Color(0xFFDDE5EC),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DirectoryScreenPreview() {
    MaterialTheme {
        ComunicadosScreen({},{},{},{},{})
    }
}
