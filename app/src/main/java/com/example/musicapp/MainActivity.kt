package com.example.musicapp

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.net.toUri

val myFont = FontFamily(Font(R.font.cairo))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Home()
        }
    }
}

@Composable
fun Home() {
    val context = LocalContext.current

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
                painter = painterResource(id = AppData.artistImg),
                contentDescription = "Artist Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = AppData.artistName,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont,
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
                onClick = { /* Men Be3d */ },
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
                    text = "ابدأ الاستماع",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "⭐ قيمنا",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = myFont,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, "https://google.com".toUri())
                            context.startActivity(intent)
                        }
                    )

                    Text(
                        text = "|",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = myFont
                    )

                    Text(
                        text = "📱 تطبيقات أخرى",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = myFont,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, "https://google.com".toUri())
                            context.startActivity(intent)
                        }
                    )

                    Text(
                        text = "|",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = myFont
                    )

                    Text(
                        text = "🔐 سياسة الخصوصية",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = myFont,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, "https://google.com".toUri())
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
