package com.application.lose_animals.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Определение форм для Material 3
val Shapes = Shapes(
    // Маленькие компоненты (кнопки, чипы и т.д.)
    small = RoundedCornerShape(4.dp),
    
    // Средние компоненты (карточки, диалоги и т.д.)
    medium = RoundedCornerShape(12.dp),
    
    // Большие компоненты (боковые панели, листы и т.д.)
    large = RoundedCornerShape(16.dp),
    
    // Экстра большие компоненты
    extraLarge = RoundedCornerShape(28.dp)
)

// Дополнительные формы для специфических компонентов
val TextFieldShape = RoundedCornerShape(12.dp)
val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(24.dp)
val FABShape = RoundedCornerShape(28.dp) 