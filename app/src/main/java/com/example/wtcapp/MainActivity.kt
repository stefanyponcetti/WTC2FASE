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
import com.example.wtcapp.screens.login.LoginScreen
import com.example.wtcapp.screens.cadastro.CadastroScreen
import com.example.wtcapp.screens.chats.ChatListScreen
import com.example.wtcapp.screens.chats.ChatScreen
import com.example.wtcapp.comunicados.ComunicadosScreen
import com.example.wtcapp.redefinicaoSenha.RedefinirSenhaScreen
import com.example.wtcapp.screens.contatos.ContatosScreen
import com.example.wtcapp.screens.criarcomunicado.CriarComunicadoScreen
import com.example.wtcapp.screens.perfil.PerfilScreen
import com.example.wtcapp.ui.theme.WTCAPPTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContent {
            WTCAPPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val screenStack = remember { mutableStateListOf("login") }
                    var selectedChatId by remember { mutableStateOf("") }
                    var selectedChatName by remember { mutableStateOf("") }

                    var jwtToken by remember { mutableStateOf("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiI2OWYwMTYwMGNlNTkxMjU1ZjhkNGYwMTMiLCJ1bmlxdWVfbmFtZSI6InN0cmluZyIsImVtYWlsIjoic3RyaW5nIiwibmJmIjoxNzc3MzQxOTU3LCJleHAiOjE3NzczNDU1NTcsImlhdCI6MTc3NzM0MTk1N30.iIfDExyDhQru3yxTJ5MUiapDQoOqQ79O86J_Uj-XgSw") }
                    var currentUserId by remember { mutableStateOf("69c5cecee406f5ca4e7d5da1") }

                    val currentScreen = screenStack.last()

                    BackHandler(enabled = screenStack.size > 1) {
                        screenStack.removeLast()
                    }

                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onNavigateToCadastro = { screenStack.add("cadastro") },
                            onLoginSuccess = { screenStack.add("chats") },
                            onNavigateToRedefinirSenha = { screenStack.add("redefinirSenha") }
                        )

                        "redefinirSenha" -> RedefinirSenhaScreen(
                            onBackToLogin = { screenStack.add("login") }
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
                            onNavigateToNovaMensagem = { chatId, chatName ->
                                selectedChatId = chatId
                                selectedChatName = chatName
                                screenStack.add("chat")
                            }
                        )

                        "chat" -> ChatScreen(
                            chatId = selectedChatId,
                            chatName = selectedChatName,
                            currentUserId = currentUserId,
                            jwtToken = jwtToken,
                            onBack = { screenStack.removeLast() }
                        )

                        "comunicados" -> ComunicadosScreen(
                            onNavigateToCriar = { screenStack.add("criarComunicado") },
                            onNavigateToChats = { screenStack.add("chats") },
                            onNavigateToPerfil = { screenStack.add("perfil") },
                            onNavigateToComunicados = { screenStack.add("comunicados") },
                            onNavigateToContatos = { screenStack.add("contatos") }
                        )

                        "criarComunicado" -> CriarComunicadoScreen(
                            onBack = { screenStack.add("comunicados") }
                        )

                        "contatos" -> ContatosScreen(
                            onNavigateToChats = { screenStack.add("chats") },
                            onNavigateToPerfil = { screenStack.add("perfil") },
                            onNavigateToComunicados = { screenStack.add("comunicados") },
                            onNavigateToContatos = { screenStack.add("contatos") }
                        )

                        "perfil" -> PerfilScreen(
                            idUsuario = "69c5cecee406f5ca4e7d5da1",
                            onLogout = {
                                screenStack.clear()
                                screenStack.add("login")
                            },
                            onNavigateToChats = { screenStack.add("chats") },
                            onNavigateToPerfil = { screenStack.add("perfil") },
                            onNavigateToComunicados = { screenStack.add("comunicados") },
                            onNavigateToContatos = { screenStack.add("contatos") }
                        )

                    }
                    FirebaseCrashlytics.getInstance().log("Tela "+ currentScreen+  " carregada")
                }
            }
        }
    }
}