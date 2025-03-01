package com.application.lose_animals.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.lose_animals.data.model.AddressSuggestion
import com.application.lose_animals.ui.viewModel.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressAutocompleteField(
    modifier: Modifier = Modifier,
    viewModel: AddressViewModel = hiltViewModel(),
    label: String = "Адрес",
    onAddressSelected: (AddressSuggestion) -> Unit = {},
    onCoordinatesReceived: (Pair<Double, Double>?) -> Unit = {}
) {
    val addressQuery by viewModel.addressQuery.collectAsState()
    val suggestions by viewModel.addressSuggestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val coordinates by viewModel.coordinates.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    // Отправляем координаты наверх при их изменении
    LaunchedEffect(coordinates) {
        onCoordinatesReceived(coordinates)
    }
    
    // Отправляем выбранный адрес наверх при его изменении
    LaunchedEffect(selectedAddress) {
        selectedAddress?.let { onAddressSelected(it) }
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Поле ввода адреса
        OutlinedTextField(
            value = addressQuery,
            onValueChange = { viewModel.updateAddressQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Адрес",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (addressQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearSelectedAddress() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        
        // Сообщение об ошибке
        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
        
        // Выпадающий список с предложениями адресов
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            DropdownMenu(
                expanded = isFocused && suggestions.isNotEmpty(),
                onDismissRequest = { focusManager.clearFocus() },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(suggestions) { suggestion ->
                        AddressSuggestionItem(
                            suggestion = suggestion,
                            onClick = {
                                viewModel.selectAddress(suggestion)
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }
        }
        
        // Отображение координат, если они доступны
        coordinates?.let { (lat, lon) ->
            Text(
                text = "Координаты: $lat, $lon",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun AddressSuggestionItem(
    suggestion: AddressSuggestion,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = suggestion.value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Дополнительная информация об адресе
        val addressData = suggestion.data
        val details = buildString {
            addressData.city?.let { append(it) }
            addressData.street?.let { 
                if (isNotEmpty()) append(", ")
                append(it) 
            }
            addressData.house?.let { 
                if (isNotEmpty()) append(", ")
                append("дом $it") 
            }
        }
        
        if (details.isNotEmpty()) {
            Text(
                text = details,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
} 