package com.example.wtcapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.wtcapp.data.SessionManager
import com.example.wtcapp.data.remote.RetrofitClient
import com.example.wtcapp.data.services.NotificacaoService
import com.example.wtcapp.ui.screens.cadastro.CadastroScreen
import com.example.wtcapp.ui.screens.chats.ChatListScreen
import com.example.wtcapp.ui.screens.chats.ChatScreen
import com.example.wtcapp.ui.screens.comunicados.ComunicadosScreen
import com.example.wtcapp.ui.screens.comunicados.CriarComunicadoScreen
import com.example.wtcapp.ui.screens.contatos.ContatosScreen
import com.example.wtcapp.ui.screens.login.LoginScreen
import com.example.wtcapp.ui.screens.perfil.PerfilScreen
import com.example.wtcapp.ui.screens.redefinicaoSenha.RedefinirSenhaScreen
import com.example.wtcapp.ui.theme.WTCAPPTheme
import com.example.wtcapp.ui.theme.laranja
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

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
                    val sessionManager = remember { SessionManager(applicationContext) }
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    val screenStack = remember { mutableStateListOf<String>() }
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        Log.d("MainActivity", "POST_NOTIFICATIONS granted=$granted")
                    }

                    var selectedChatId by remember { mutableStateOf("") }
                    var selectedChatName by remember { mutableStateOf("") }
                    var jwtToken by remember { mutableStateOf<String?>(null) }
                    var currentUserId by remember { mutableStateOf<String?>(null) }
                    var tipoCliente by remember { mutableStateOf("") }
                    var isSessionLoaded by remember { mutableStateOf(false) }

                    remember {
                        RetrofitClient.initialize(
                            onUnauthorized = {
                                Handler(Looper.getMainLooper()).post {
                                    scope.launch {
                                        sessionManager.clearSession()
                                        jwtToken = null
                                        currentUserId = null
                                        tipoCliente = ""
                                    }
                                    context.stopService(Intent(context, NotificacaoService::class.java))
                                    screenStack.clear()
                                    screenStack.add("login")
                                }
                            }
                        )
                        true
                    }

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    fun goToLogin() {
                        screenStack.clear()
                        screenStack.add("login")
                    }

                    fun goToChats() {
                        screenStack.clear()
                        screenStack.add("chats")
                    }

                    fun reloadSessionAfterLogin() {
                        scope.launch {
                            jwtToken = sessionManager.getToken()
                            currentUserId = sessionManager.getUserId()
                            tipoCliente = sessionManager.getTipoCliente() ?: ""
                            if (!jwtToken.isNullOrBlank() && !currentUserId.isNullOrBlank()) {
                                goToChats()
                            } else {
                                goToLogin()
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        jwtToken = sessionManager.getToken()
                        currentUserId = sessionManager.getUserId()
                        tipoCliente = sessionManager.getTipoCliente() ?: ""
                        if (!jwtToken.isNullOrBlank() && !currentUserId.isNullOrBlank()) {
                            goToChats()
                        } else {
                            goToLogin()
                        }
                        isSessionLoaded = true
                    }

                    LaunchedEffect(jwtToken) {
                        val token = jwtToken
                        if (!token.isNullOrBlank() && tipoCliente.isBlank()) {
                            try {
                                val me = RetrofitClient.chatApi.getMe("Bearer $token")
                                tipoCliente = me.tipoCliente
                                sessionManager.saveTipoCliente(me.tipoCliente)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    LaunchedEffect(jwtToken, tipoCliente) {
                        val token = jwtToken
                        if (!token.isNullOrBlank() && tipoCliente.isNotBlank()) {
                            startNotificacaoService(context, token, tipoCliente)
                        } else {
                            Log.d(
                                "MainActivity",
                                "NotificacaoService nao iniciado: token blank=${token.isNullOrBlank()} tipoCliente=$tipoCliente"
                            )
                        }
                    }

                    if (!isSessionLoaded || screenStack.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = laranja)
                        }
                        return@Surface
                    }

                    BackHandler(enabled = screenStack.size > 1) {
                        screenStack.removeLast()
                    }

                    val currentScreen = screenStack.last()

                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onNavigateToCadastro = { screenStack.add("cadastro") },
                            onLoginSuccess = { reloadSessionAfterLogin() },
                            onNavigateToRedefinirSenha = { screenStack.add("redefinirSenha") }
                        )

                        "redefinirSenha" -> RedefinirSenhaScreen(
                            onBackToLogin = { goToLogin() }
                        )

                        "cadastro" -> CadastroScreen(
                            onNavigateToLogin = { goToLogin() },
                            onCadastroSuccess = { goToLogin() }
                        )

                        "chats" -> {
                            val token = jwtToken
                            val userId = currentUserId
                            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                                goToLogin()
                            } else {
                                ChatListScreen(
                                    jwtToken = token,
                                    currentUserId = userId,
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
                            }
                        }

                        "chat" -> {
                            val token = jwtToken
                            val userId = currentUserId
                            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                                goToLogin()
                            } else {
                                ChatScreen(
                                    chatId = selectedChatId,
                                    chatName = selectedChatName,
                                    currentUserId = userId,
                                    jwtToken = token,
                                    onBack = { screenStack.removeLast() }
                                )
                            }
                        }

                        "comunicados" -> {
                            val token = jwtToken
                            if (token.isNullOrBlank()) {
                                goToLogin()
                            } else {
                                ComunicadosScreen(
                                    jwtToken = token,
                                    tipoCliente = tipoCliente,
                                    onNavigateToCriar = { screenStack.add("criarComunicado") },
                                    onNavigateToChats = { screenStack.add("chats") },
                                    onNavigateToPerfil = { screenStack.add("perfil") },
                                    onNavigateToComunicados = { screenStack.add("comunicados") },
                                    onNavigateToContatos = { screenStack.add("contatos") }
                                )
                            }
                        }

                        "criarComunicado" -> {
                            val token = jwtToken
                            if (token.isNullOrBlank()) {
                                goToLogin()
                            } else {
                                CriarComunicadoScreen(
                                    jwtToken = token,
                                    onBack = {
                                        if (screenStack.size > 1) {
                                            screenStack.removeLast()
                                        } else {
                                            screenStack.add("comunicados")
                                        }
                                    }
                                )
                            }
                        }

                        "contatos" -> {
                            val token = jwtToken
                            val userId = currentUserId
                            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                                goToLogin()
                            } else {
                                ContatosScreen(
                                    jwtToken = token,
                                    currentUserId = userId,
                                    onNavigateToChats = { screenStack.add("chats") },
                                    onNavigateToPerfil = { screenStack.add("perfil") },
                                    onNavigateToComunicados = { screenStack.add("comunicados") },
                                    onNavigateToContatos = { screenStack.add("contatos") },
                                    onNavigateToChat = { chatId, chatName ->
                                        selectedChatId = chatId
                                        selectedChatName = chatName
                                        screenStack.add("chat")
                                    }
                                )
                            }
                        }

                        "perfil" -> {
                            val token = jwtToken
                            val userId = currentUserId
                            if (userId.isNullOrBlank()) {
                                goToLogin()
                            } else {
                                PerfilScreen(
                                    jwtToken = "Bearer $token",
                                    idUsuario = userId,
                                    tipoCliente = tipoCliente,
                                    onBack = {
                                        if (screenStack.size > 1) {
                                            screenStack.removeLast()
                                        } else {
                                            goToChats()
                                        }
                                    },
                                    onLogout = {
                                        scope.launch {
                                            sessionManager.clearSession()
                                            jwtToken = null
                                            currentUserId = null
                                            tipoCliente = ""
                                            context.stopService(Intent(context, NotificacaoService::class.java))
                                            goToLogin()
                                        }
                                    },
                                    onNavigateToChats = { screenStack.add("chats") },
                                    onNavigateToPerfil = { screenStack.add("perfil") },
                                    onNavigateToComunicados = { screenStack.add("comunicados") },
                                    onNavigateToContatos = { screenStack.add("contatos") }
                                )
                            }
                        }
                    }

                    FirebaseCrashlytics.getInstance().log("Tela $currentScreen carregada")
                }
            }
        }
    }
}

private fun startNotificacaoService(context: Context, token: String, tipoCliente: String) {
    Log.d("MainActivity", "Tentando iniciar NotificacaoService")
    Log.d("MainActivity", "token blank=${token.isBlank()}")
    Log.d("MainActivity", "tipoCliente=$tipoCliente")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            Log.e("MainActivity", "Permissao POST_NOTIFICATIONS negada")
        }
    }

    val intent = Intent(context, NotificacaoService::class.java).apply {
        putExtra("token", token)
        putExtra("tipoCliente", tipoCliente)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Log.d("MainActivity", "Usando startForegroundService")
        context.startForegroundService(intent)
    } else {
        Log.d("MainActivity", "Usando startService")
        context.startService(intent)
    }
}
