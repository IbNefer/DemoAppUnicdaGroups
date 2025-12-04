import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.demounicdagroups.R
import com.example.demounicdagroups.features.auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        if (authState != null) {
            delay(1000L) // splash delay if needed
            when (authState) {
                is AuthState.Authenticated -> {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.Unauthenticated -> {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                else -> Unit
            }
        }
    }

    Splash()
}
@Composable
fun Splash() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.unicda_group),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable

fun SplashScreenPreview(){
    Splash()
}