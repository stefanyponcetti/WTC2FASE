package com.example.wtcapp.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.wtcapp.ui.theme.laranja
import java.util.Calendar

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val calendar = remember(value) {
        Calendar.getInstance().apply {
            val partes = formatarDataParaExibicao(value).split("/")
            if (partes.size == 3) {
                set(partes[2].toInt(), partes[1].toInt() - 1, partes[0].toInt())
            }
        }
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val displayValue = formatarDataParaExibicao(value).ifBlank { "Selecionar data" }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = laranja
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = laranja,
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.Transparent,
                focusedLabelColor = laranja,
                unfocusedLabelColor = Color.LightGray,
                disabledLabelColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White,
                cursorColor = laranja
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = enabled) { showDatePicker = true }
        )
    }

    if (showDatePicker) {
        LaunchedEffect(Unit) {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    onDateSelected("%02d/%02d/%04d".format(day, month + 1, year))
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setOnDismissListener { showDatePicker = false }
            }.show()
        }
    }
}
