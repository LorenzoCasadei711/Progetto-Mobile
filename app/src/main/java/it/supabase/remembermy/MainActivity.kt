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
import org.koin.android.ext.android.inject


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

            val start = startingPage(sessionStatus)
            NavGraph(navController, supabase,start)
            navController.handleDeepLink(intent)
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



    private fun startingPage(sessionStatus: SessionStatus) : NavigationRoute{
            return if(sessionStatus is SessionStatus.Authenticated) NavigationRoute.HomeScreen
            else NavigationRoute.Login

    }

}