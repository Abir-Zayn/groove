package com.example.groove.util

import java.util.Calendar

fun greeting(name: String): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val salutation = when (hour) {
        in 0..4   -> "Night Owls"
        in 5..7   -> "Early Dove"
        in 8..11  -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else      -> "Vibe at Night"   // 21–23
    }
    return "$salutation, $name"
}
