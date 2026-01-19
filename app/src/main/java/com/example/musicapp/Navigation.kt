package com.example.musicapp

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Listening : Screen("listening")
}
