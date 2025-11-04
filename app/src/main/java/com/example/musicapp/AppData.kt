package com.example.musicapp

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

object AppData {
    val myFont = FontFamily(Font(R.font.cairo))

    val artistName: String = "الشابة شوشو"

    val rateUs_link: String = "https://google.com"
    val otherApps_link: String = "https://google.com"
    val privacyPolicy_link: String = "https://google.com"

    val songsName: List<String> = listOf(
        "غنيلي",
        "بلينغ بلينغ",
        "يوم ورا يوم",
        "جودية",
        "كازا",
        "طوطو",
        "هشرب حشيش"

    )

    val songsResId = listOf(
        R.raw.s1,
        R.raw.s2,
        R.raw.s3,
        R.raw.s4,
        R.raw.s5,
        R.raw.s6,
        R.raw.s7
    )

}