package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.openclassrooms.realestatemanager.domain.models.RealEstateDatabase
import com.openclassrooms.realestatemanager.presentation.viewModels.RealEstateViewModel
import com.openclassrooms.realestatemanager.presentation.viewModels.UserViewModel
import com.openclassrooms.realestatemanager.ui.screens.*
import com.openclassrooms.realestatemanager.ui.ui.theme.Projet_9_OC_RealEstateManagerTheme

import com.openclassrooms.realestatemanager.utils.ConnectionReceiver
import com.openclassrooms.realestatemanager.utils.WindowType
import com.openclassrooms.realestatemanager.utils.rememberWindowSizeComposable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var startScreen : String
    private var receiver: ConnectionReceiver? = null
    private val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

    private val realEstateViewModel: RealEstateViewModel by viewModels()

    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        receiver = ConnectionReceiver(realEstateViewModel)
        registerReceiver(receiver,filter)


        setContent {
            Projet_9_OC_RealEstateManagerTheme {
                val userViewModel: UserViewModel = hiltViewModel()






                        val currentUser = auth.currentUser

                        startScreen = if (currentUser == null) {
                            "signInScreen"
                        } else {
                            "mainScreen"
                        }



                val windowSize = rememberWindowSizeComposable()

                var realEstateId by remember { mutableStateOf("")}
                var photoUrl by remember { mutableStateOf("") }






                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = startScreen) {
                        composable("mainScreen") {
                            MainScreen(
                                navControllerDrawer = navController,
                                auth = auth,
                                userViewModel,
                                realEstateViewModel,
                                windowSize,
                                realEstateId,
                                realEstateIdSet = {
                                    realEstateId = it
                                }
                            )
                        }
                        composable("settingsScreen") { SettingsScreen(navController = navController) }
                        composable("registerScreen") {
                            RegisterScreen(
                                navController = navController,
                                userViewModel = userViewModel
                            )
                        }
                        composable("signInScreen") { SignInScreen(
                            navigateToMainScreen = {
                                navController.navigate("mainScreen")
                        }, navigateToRegisterScreen = {
                                navController.navigate("registerScreen")
                        }) }
                        composable("editScreen/{item}",
                            arguments = listOf(
                                navArgument("item") {
                                    type = RealEstateDatabase.NavigationType
                                }
                            )
                        ) { backStackEntry ->

                            val item = backStackEntry.arguments?.getParcelable<RealEstateDatabase>("item")

                            EditScreenRealEstate(
                                realEstateViewModel,
                                item,
                                navController,
                                setPhotoUrl = {
                                    photoUrl = it
                                }
                            )
                        }

                        composable(
                            route = "picdetail"

                        ) {
                            PictureDetail(photoUrl, navController)
                        }


                        if(windowSize.width != WindowType.Expanded) {

                            composable(
                                route = "detailScreen"
                            ) {
                                if (realEstateId != "") {

                                    val itemRealEstate by realEstateViewModel.realEstateById(realEstateId).observeAsState()

                                    RealEstateDetailScreen(
                                        realEstateViewModel,
                                        navController,
                                        windowSize,
                                        itemRealEstate
                                    )

                                }


                            }


                        }


                    }






            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}




























