package com.example.wtcapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.wtcapp.login.LoginScreen
import com.example.wtcapp.cadastro.CadastroScreen
import com.example.wtcapp.chats.ChatListScreen
import com.example.wtcapp.comunicados.ComunicadosScreen
import com.example.wtcapp.contatos.ContatosScreen
import com.example.wtcapp.criarcomunicado.CriarComunicadoScreen
import com.example.wtcapp.mensagem.MensagemScreen
import com.example.wtcapp.perfil.PerfilScreen
import com.example.wtcapp.ui.theme.WTCAPPTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WTCAPPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pilha de telas
                    val screenStack = remember { mutableStateListOf("login") }

                    // Tela atual é sempre o último da pilha
                    val currentScreen = screenStack.last()

                    // Intercepta o botão físico de back
                    BackHandler(enabled = screenStack.size > 1) {
                        screenStack.removeLast() // volta para a tela anterior
                    }

                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onNavigateToCadastro = { screenStack.add("cadastro") },
                            onLoginSuccess = { screenStack.add("chats") }
                        )

                        "cadastro" -> CadastroScreen(
                            onNavigateToLogin = { screenStack.add("login") },
                            onCadastroSuccess = { screenStack.add("login") }
                        )

                        "chats" -> ChatListScreen(
                            onNavigateToChats = { screenStack.add("chats") },
                            onNavigateToPerfil = { screenStack.add("perfil") },
                            onNavigateToComunicados = { screenStack.add("comunicados") },
                            onNavigateToContatos = { screenStack.add("contatos") },
                            onNavigateToNovaMensagem = { screenStack.add("mensagem") }
                        )

                        "comunicados" -> ComunicadosScreen(
                            onNavigateToCriar = { screenStack.add("criarComunicado") },
                            onNavigateToChats = { screenStack.add("chats") },
                            onNavigateToPerfil = { screenStack.add("perfil") },
                            onNavigateToComunicados = { screenStack.add("comunicados") },
                            onNavigateToContatos = { screenStack.add("contatos") }
                        )

                        "mensagem" -> MensagemScreen(
                            onNavigateToChats = { screenStack.add("chats") },
                            onNavigateToPerfil = { screenStack.add("perfil") },
                            onNavigateToComunicados = { screenStack.add("comunicados") },
                            onNavigateToContatos = { screenStack.add("contatos") }
                        )

                        "criarComunicado" -> CriarComunicadoScreen(
                            onBack = { screenStack.removeLast() }
                        )

                        "contatos" -> ContatosScreen(
                            onNavigateToChats = { screenStack.add("chats") },
                            onNavigateToPerfil = { screenStack.add("perfil") },
                            onNavigateToComunicados = { screenStack.add("comunicados") },
                            onNavigateToContatos = { screenStack.add("contatos") }
                        )

                        "perfil" -> PerfilScreen(
                            nome = "Eliane Meliz Cruz",
                            telefone = "(11) 98342-13421",
                            email = "eli.meliz@wtc.com.br",
                            dataNascimento = "02/03/1981",
                            cargo = "Analista de Recursos Humanos",
                            tempoEmpresa = "3 anos e 2 meses",
                            unidade = "Av. Paulista • P Central",
                            tipoUsuario = "Colaborador",
                            onLogout = { screenStack.add("comunicados") }
                        )
                    }
                }
            }
        }
    }
}
