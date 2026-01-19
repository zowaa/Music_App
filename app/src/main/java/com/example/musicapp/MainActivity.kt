package com.example.musicapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    Home(navController)
                }
                composable(Screen.Listening.route) {
                    ListeningScreen()
                }
            }
        }
    }
}

@Composable
fun Home(navController: NavController) {
    val context = LocalContext.current

    val openLink = remember(context) {
        { url: String ->
            try {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, url.toUri())
                )
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(context,R.string.open_link_error, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.artist),
                contentDescription = "Artist Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = AppData.ARTIST_NAME,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppData.myFont,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.Listening.route) { launchSingleTop = true }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(66.dp)
            ) {
                Text(
                    text = stringResource(R.string.start_listening),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppData.myFont
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FooterLink(stringResource(R.string.rate_us), onClick = { openLink(AppData.RATE_US_LINK) })

                    Separator()

                    FooterLink(stringResource(R.string.other_apps), onClick = { openLink(AppData.OTHER_APPS_LINK) })

                    Separator()

                    FooterLink(stringResource(R.string.privacy_policy), onClick= { openLink(AppData.PRIVACY_POLICY_LINK) })
                }
            }
        }
    }
}

@Composable
private fun FooterLink(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = Color.White,
        fontFamily = AppData.myFont,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun Separator() {
    Text(
        text = "|",
        fontSize = 15.sp,
        color = Color.White,
        fontFamily = AppData.myFont
    )
}

