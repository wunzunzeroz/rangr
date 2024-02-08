package com.rangr.map.components

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange

@Composable
fun TextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, bgColor: Color = RangrOrange) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = RangrDark, backgroundColor = bgColor
        ),
    ) {
        Text(text)
    }
}
