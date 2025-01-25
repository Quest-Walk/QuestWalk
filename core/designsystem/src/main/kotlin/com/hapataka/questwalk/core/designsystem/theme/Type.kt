package com.hapataka.questwalk.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.hapataka.questwalk.core.designsystem.R

private val GalmuriTextStyle = TextStyle.Default.copy(
    fontFamily = FontFamily(Font(R.font.galmuri14)),
    fontWeight = FontWeight.Bold,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    ),
    baselineShift = BaselineShift(-0.15f)
)
private val NeoDunggeunmoTextStyle = TextStyle.Default.copy(
    fontFamily = FontFamily(Font(R.font.neo_dunggeunmo_pro_r)),
    fontWeight = FontWeight.Medium,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    ),
//    baselineShift = BaselineShift(-0.15f)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = GalmuriTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = NeoDunggeunmoTextStyle.copy(
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    labelLarge = GalmuriTextStyle.copy(
        fontSize = 14.sp,
    ),
    labelMedium = GalmuriTextStyle.copy(
        fontSize = 12.sp,
    ),
    displayMedium = NeoDunggeunmoTextStyle.copy(
        fontSize = 48.sp,
        lineHeight = 56.sp,
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)