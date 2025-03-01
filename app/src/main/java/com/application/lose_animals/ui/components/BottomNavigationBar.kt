package com.application.lose_animals.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.lose_animals.ui.theme.ButtonShape
import com.application.lose_animals.ui.theme.ErrorRed
import com.application.lose_animals.ui.theme.OrangePrimary

@Composable
fun BottomNavigationBar(
    currentDestination: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddPerson: () -> Unit,
    onNavigateToPeople: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToRecognition: () -> Unit,
    onSosClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        NavigationBar(
            modifier = Modifier
                .height(64.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            // Список пропавших людей
            NavigationItem(
                selected = currentDestination == "people",
                onClick = onNavigateToPeople,
                selectedIcon = Icons.Filled.People,
                unselectedIcon = Icons.Outlined.People,
                label = "Пропавшие"
            )
            
            // Распознавание
            NavigationItem(
                selected = currentDestination == "recognition",
                onClick = onNavigateToRecognition,
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.PhotoCamera,
                label = "Поиск"
            )
            
            // SOS кнопка (в центре)
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = onSosClick,
                    containerColor = ErrorRed,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "SOS",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Добавить пропавшего
            NavigationItem(
                selected = currentDestination == "addPerson",
                onClick = onNavigateToAddPerson,
                selectedIcon = Icons.Filled.Add,
                unselectedIcon = Icons.Outlined.Add,
                label = "Добавить"
            )
            
            // Профиль
            NavigationItem(
                selected = currentDestination == "profile",
                onClick = onNavigateToProfile,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                label = "Профиль"
            )
        }
    }
}

@Composable
private fun RowScope.NavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 300),
        label = "color"
    )
    
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = animatedColor,
                modifier = Modifier.size(22.dp)
            )
        },
        label = {
            Text(
                text = label,
                color = animatedColor,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    )
}
