package it.supabase.remembermy

import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.progettomobile.composable.NavGraph
import com.example.progettomobile.composable.NavigationRoute
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionStatus
import it.supabase.remembermy.ui.screens.auth.AccessViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    val supabase : SupabaseClient by inject()
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supabase.handleDeeplinks(intent)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            val sessionStatus by supabase.auth.sessionStatus.collectAsState()
            val start = startingPage(sessionStatus, intent)
            NavGraph(navController, supabase,start)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        supabase.handleDeeplinks(intent)
        if(::navController.isInitialized){
            navController.handleDeepLink(intent)
        }

    }
    private fun startingPage(sessionStatus: SessionStatus, intent: Intent) : NavigationRoute{
            if(intent.data?.getQueryParameter("type")=="recovery" && sessionStatus is SessionStatus.Authenticated) return NavigationRoute.ResetPassword
            return if(sessionStatus is SessionStatus.Authenticated) NavigationRoute.HomeScreen
            else NavigationRoute.Login
    }

}