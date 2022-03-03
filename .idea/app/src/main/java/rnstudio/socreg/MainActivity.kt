package rnstudio.socreg

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import rnstudio.socreg.services.RegServices
import rnstudio.socreg.ui.custom.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val systemBarColor = colorResource(id = R.color.colorPrimaryLight)
    val context = LocalContext.current
    var serviceIsRunning: Boolean by remember {
        mutableStateOf(isMyServiceRunning(RegServices::class.java, context))
    }

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = systemBarColor,
            darkIcons = false
        )

        // setStatusBarsColor() and setNavigationBarColor() also exist
    }

    Scaffold(
//        topBar = { TopBar() },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(65.dp)
                    .padding(0.dp)
                    .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                cutoutShape = CircleShape,
                backgroundColor = colorResource(id = R.color.colorPrimary),
                elevation = 22.dp
            ) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    if(serviceIsRunning) {
                        stopService(context)
                        serviceIsRunning = false
                    }
                    else{
                        startService(context)
                        serviceIsRunning = true
                    }
                },
                contentColor = Color.White,
                backgroundColor = colorResource(id = R.color.colorPrimaryDark)
            ) {
                if(serviceIsRunning) {
                    Icon(painterResource(id = R.drawable.baseline_stop_24), contentDescription = "Stop icon")
                }
                else{
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Run icon")
                }
            }
        }
    ) {
        Navigation(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Main.route.toString()) {
        composable(NavigationItem.Main.route.toString().toString()) {
            HomeScreen()
        }
        composable(NavigationItem.SMS.route.toString()) {
            SMSScreen()
        }
        composable(NavigationItem.Empty.route.toString()) {
            SMSScreen()
        }
        composable(NavigationItem.Proxy.route.toString()) {
            ProxyScreen()
        }
        composable(NavigationItem.Logs.route.toString()) {
            ProfileScreen()
        }
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
        backgroundColor = colorResource(id = R.color.colorPrimary),
        contentColor = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar()
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Main,
        NavigationItem.SMS,
        NavigationItem.Empty,
        NavigationItem.Proxy,
        NavigationItem.Logs
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.colorPrimary),
        contentColor = Color.White,
        modifier = Modifier
            .padding(12.dp, 0.dp, 12.dp, 0.dp)
            .height(100.dp),
        elevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                        item.icon?.let{
                            Icon(painterResource(id = it),
                                contentDescription = item.title?.let { it1 -> stringResource(id = it1) })
                        }
                },
                label = { item.title?.let { Text(text = stringResource(id = it)) } },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    item.route?.let {
                        navController.navigate(it) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    // BottomNavigationBar()
}

private fun startService(context: Context){
    val intentService = Intent(context, RegServices::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intentService)
    } else {
        context.startService(intentService)
    }
}

private fun stopService(context: Context){
    val intentService = Intent(context, RegServices::class.java)
    context.stopService(intentService)
}

private fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}