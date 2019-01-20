package com.example.floodfill.ui.util

fun getIntNumber(text: String, default: Int = 0) = try {
    text.toInt()
} catch (e: NumberFormatException) {
    default
}