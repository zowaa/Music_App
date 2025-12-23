package com.example.musicapp

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

object AppData {
    val myFont = FontFamily(Font(R.font.cairo))
    const val ARTIST_NAME: String = "الشابة شوشو"
    const val RATE_US_LINK: String = "https://google.com"
    const val OTHER_APPS_LINK: String = "https://google.com"
    const val PRIVACY_POLICY_LINK: String = "https://google.com"

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