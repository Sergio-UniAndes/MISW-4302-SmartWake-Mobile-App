package com.smartwake.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.smartwake.app.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
)

private val Default = Typography()

// Figma uses "auto" line height and no tracking for most text, so lineHeight and
// letterSpacing stay unspecified unless the design sets them (Title Medium does).
val Typography = Typography(
    displayLarge = Default.displayLarge.copy(fontFamily = PlusJakartaSans),
    displayMedium = Default.displayMedium.copy(fontFamily = PlusJakartaSans),
    displaySmall = Default.displaySmall.copy(fontFamily = PlusJakartaSans),
    headlineLarge = Default.headlineLarge.copy(fontFamily = PlusJakartaSans),
    headlineMedium = Default.headlineMedium.copy(fontFamily = PlusJakartaSans),
    headlineSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = Default.titleSmall.copy(fontFamily = PlusJakartaSans),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = Default.bodyMedium.copy(fontFamily = PlusJakartaSans),
    bodySmall = Default.bodySmall.copy(fontFamily = PlusJakartaSans),
    labelLarge = Default.labelLarge.copy(fontFamily = PlusJakartaSans),
    labelMedium = Default.labelMedium.copy(fontFamily = PlusJakartaSans),
    labelSmall = Default.labelSmall.copy(fontFamily = PlusJakartaSans),
)

/** Material 3 defaults, which use Roboto (the platform font) like the Figma kit components. */
val KitTypography = Typography()

private fun jakarta(
    size: Int,
    weight: FontWeight,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)

private fun roboto(size: Int, weight: FontWeight, lineHeight: Int) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
)

/** The custom (non-kit) text styles used across the Figma screens, named by what they're for. */
object SmartWakeType {
    val barTitle = jakarta(20, FontWeight.SemiBold, lineHeight = 28.sp)
    val badge = jakarta(12, FontWeight.Medium)
    val emptyTitle = jakarta(28, FontWeight.Medium, lineHeight = 36.sp)
    val emptyBody = jakarta(16, FontWeight.Normal, lineHeight = 24.sp, letterSpacing = 0.5.sp)

    val cardHeader = jakarta(16, FontWeight.Medium)
    val fieldLabel = jakarta(12, FontWeight.Medium)
    val fieldValue = jakarta(14, FontWeight.Normal)
    val bigValue = jakarta(28, FontWeight.SemiBold)
    val unit = jakarta(12, FontWeight.Medium)
    val dayChip = jakarta(12, FontWeight.SemiBold)

    val placeTag = jakarta(12, FontWeight.Medium, letterSpacing = 0.5.sp)
    val wakeTime = jakarta(32, FontWeight.Bold)
    val wakePeriod = jakarta(16, FontWeight.Medium)
    val arrival = jakarta(14, FontWeight.Normal)
    val warning = jakarta(12, FontWeight.Normal)
    val warningGlyph = jakarta(12, FontWeight.Medium)

    val sectionTitle = jakarta(16, FontWeight.SemiBold, lineHeight = 24.sp)
    val rowTitle = jakarta(16, FontWeight.Medium, lineHeight = 24.sp)
    val rowBody = jakarta(14, FontWeight.Normal, lineHeight = 20.sp)
    val stepperValue = jakarta(28, FontWeight.Bold, lineHeight = 36.sp)

    // The Resumen screen is set in Roboto in Figma.
    val summaryTitle = roboto(20, FontWeight.Bold, lineHeight = 28)
    val summaryDate = roboto(14, FontWeight.Normal, lineHeight = 20)
    val timelineValue = roboto(16, FontWeight.Bold, lineHeight = 24)
    val timelineLabel = roboto(14, FontWeight.Normal, lineHeight = 20)
    val timelineNote = roboto(12, FontWeight.Normal, lineHeight = 16)
}
