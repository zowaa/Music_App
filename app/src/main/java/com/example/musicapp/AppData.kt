package com.example.musicapp

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

object AppData {
    val myFont = FontFamily(Font(R.font.cairo))

    var artistName: String = "الشابة شوشو"

    var rateUs_link: String = "https://google.com"
    var otherApps_link: String = "https://google.com"
    var privacyPolicy_link: String = "https://google.com"

    var songsName: List<String> = listOf(
        "يا ليلي ويا ليلا",
        "إنت إيه",
        "مشاعر",
        "الغرام المستحيل",
        "أما براوة",
        "حبيبي يا نور العين",
        "كل القصايد",
        "الأطلال",
        "ثلاث دقات",
        "أهو ده اللي صار",
        "يا حبيبي تعال الحقني",
        "لو على قلبي",
        "أكتر واحد حبيته",
        "أنا مش أنانية",
        "قال إيه بيسألوني",
        "الدنيا ربيع",
        "سلامات يا حبايبنا",
        "أنا بعشق البحر",
        "إرجعولي تاني",
        "حلم بعيد"
    )

}