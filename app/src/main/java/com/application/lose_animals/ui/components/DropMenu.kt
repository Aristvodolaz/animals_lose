package com.application.lose_animals.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedItem) }

    // Логирование состояния раскрытия
    LaunchedEffect(expanded) {
        Log.d("DropdownMenuField", "Expanded state: $expanded")
    }

    // Оборачиваем в ExposedDropdownMenuBox для управления положением меню
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded } // Открываем/закрываем меню при клике
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { /* Не нужно изменять значение вручную */ },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor() // Привязываем меню к этому полю
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Black,
                focusedTextColor =   Color.Black,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor =  Color.Transparent,
                unfocusedTextColor = Color.Black
            )
        )

        // Выпадающее меню
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedText = option
                        onItemSelected(option)
                        expanded = false
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}