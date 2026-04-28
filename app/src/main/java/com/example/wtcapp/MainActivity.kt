package com.example.wtcapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.wtcapp.cadastro.CadastroScreen
import com.example.wtcapp.chats.ChatListScreen
import com.example.wtcapp.comunicados.ComunicadosScreen
import com.example.wtcapp.contatos.ContatosScreen
import com.example.wtcapp.criarcomunicado.CriarComunicadoScreen
import com.example.wtcapp.login.LoginScreen
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
                    modifier = Modifier.fillMaxSize()
                ) {

                    var currentScreen by remember { mutableStateOf("login") }

                    fun navigate(screen: String) {
                        currentScreen = screen
                    }

                    when (currentScreen) {

                        "login" -> LoginScreen(
                            onNavigateToCadastro = { navigate("cadastro") },
                            onLoginSuccess = { navigate("chats") }
                        )

                        "cadastro" -> CadastroScreen(
                            onNavigateToLogin = { navigate("login") },
                            onCadastroSuccess = { navigate("login") }
                        )

                        "chats" -> ChatListScreen(
                            onNavigateToChats = { navigate("chats") },
                            onNavigateToPerfil = { navigate("perfil") },
                            onNavigateToComunicados = { navigate("comunicados") },
                            onNavigateToContatos = { navigate("contatos") },
                            onNavigateToNovaMensagem = { navigate("mensagem") }
                        )

                        "comunicados" -> ComunicadosScreen(
                            onNavigateToCriar = { navigate("criarComunicado") },
                            onNavigateToChats = { navigate("chats") },
                            onNavigateToPerfil = { navigate("perfil") },
                            onNavigateToComunicados = { navigate("comunicados") },
                            onNavigateToContatos = { navigate("contatos") }
                        )

                        "mensagem" -> MensagemScreen(
                            onNavigateToChats = { navigate("chats") },
                            onNavigateToPerfil = { navigate("perfil") },
                            onNavigateToComunicados = { navigate("comunicados") },
                            onNavigateToContatos = { navigate("contatos") }
                        )

                        "criarComunicado" -> CriarComunicadoScreen(
                            onBack = { navigate("comunicados") }
                        )

                        "contatos" -> ContatosScreen(
                            onNavigateToChats = { navigate("chats") },
                            onNavigateToPerfil = { navigate("perfil") },
                            onNavigateToComunicados = { navigate("comunicados") },
                            onNavigateToContatos = { navigate("contatos") }
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
                            onLogout = { navigate("login") },

                            // 🔥 IMPORTANTE: adiciona navegação pra TopBar funcionar
                            onNavigateToChats = { navigate("chats") },
                            onNavigateToPerfil = { navigate("perfil") },
                            onNavigateToComunicados = { navigate("comunicados") },
                            onNavigateToContatos = { navigate("contatos") }
                        )
                    }
                }
            }
        }
    }
}