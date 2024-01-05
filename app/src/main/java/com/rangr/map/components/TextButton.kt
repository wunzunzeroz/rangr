package com.rangr.map.components

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange

@Composable
fun TextButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = RangrDark, backgroundColor = RangrOrange
        ),
    ) {
        Text(text)
    }
}
