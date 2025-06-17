package com.example.championcart.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * ChampionCart Custom Icons
 * Modern, organic-styled icons following 2025 design trends
 */
object ChampionCartIcons {

    /**
     * Home icon with organic curves and flowing lines
     */
    val Home: ImageVector
        get() {
            if (_home != null) {
                return _home!!
            }
            _home = ImageVector.Builder(
                name = "ChampionCart_Home",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                // Main house shape with rounded bottom
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(5f, 13.5f)
                    curveTo(5f, 12.5f, 5.4f, 11.6f, 6.1f, 11f)
                    lineTo(11.3f, 6.2f)
                    curveTo(11.7f, 5.9f, 12.3f, 5.9f, 12.7f, 6.2f)
                    lineTo(17.9f, 11f)
                    curveTo(18.6f, 11.6f, 19f, 12.5f, 19f, 13.5f)
                    verticalLineTo(18.5f)
                    curveTo(19f, 19.6f, 18.1f, 20.5f, 17f, 20.5f)
                    horizontalLineTo(15f)
                    curveTo(14.4f, 20.5f, 14f, 20.1f, 14f, 19.5f)
                    verticalLineTo(16f)
                    curveTo(14f, 15.4f, 13.6f, 15f, 13f, 15f)
                    horizontalLineTo(11f)
                    curveTo(10.4f, 15f, 10f, 15.4f, 10f, 16f)
                    verticalLineTo(19.5f)
                    curveTo(10f, 20.1f, 9.6f, 20.5f, 9f, 20.5f)
                    horizontalLineTo(7f)
                    curveTo(5.9f, 20.5f, 5f, 19.6f, 5f, 18.5f)
                    verticalLineTo(13.5f)
                    close()
                }

                // Chimney with rounded top
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(15.5f, 9f)
                    curveTo(15.5f, 8.4f, 16f, 8f, 16.5f, 8f)
                    curveTo(17f, 8f, 17.5f, 8.4f, 17.5f, 9f)
                    verticalLineTo(11f)
                    curveTo(17.5f, 11.6f, 17f, 12f, 16.5f, 12f)
                    curveTo(16f, 12f, 15.5f, 11.6f, 15.5f, 11f)
                    verticalLineTo(9f)
                    close()
                }
            }.build()
            return _home!!
        }

    private var _home: ImageVector? = null

    /**
     * Search icon with bold stroke and center accent
     */
    val Search: ImageVector
        get() {
            if (_search != null) {
                return _search!!
            }
            _search = ImageVector.Builder(
                name = "ChampionCart_Search",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                // Search circle
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color.Black),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(16.5f, 10.5f)
                    curveTo(16.5f, 13.8f, 13.8f, 16.5f, 10.5f, 16.5f)
                    curveTo(7.2f, 16.5f, 4.5f, 13.8f, 4.5f, 10.5f)
                    curveTo(4.5f, 7.2f, 7.2f, 4.5f, 10.5f, 4.5f)
                    curveTo(13.8f, 4.5f, 16.5f, 7.2f, 16.5f, 10.5f)
                    close()
                }

                // Search handle
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color.Black),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(15.5f, 15.5f)
                    lineTo(20.2f, 20.2f)
                    curveTo(20.6f, 20.6f, 20.6f, 21.2f, 20.2f, 21.6f)
                    curveTo(19.8f, 22f, 19.2f, 22f, 18.8f, 21.6f)
                    lineTo(14.1f, 16.9f)
                }

                // Center accent dot
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(11.7f, 10.5f)
                    curveTo(11.7f, 11.2f, 11.2f, 11.7f, 10.5f, 11.7f)
                    curveTo(9.8f, 11.7f, 9.3f, 11.2f, 9.3f, 10.5f)
                    curveTo(9.3f, 9.8f, 9.8f, 9.3f, 10.5f, 9.3f)
                    curveTo(11.2f, 9.3f, 11.7f, 9.8f, 11.7f, 10.5f)
                    close()
                }
            }.build()
            return _search!!
        }

    private var _search: ImageVector? = null

    /**
     * Shopping cart icon with organic curves and item accents
     */
    val Cart: ImageVector
        get() {
            if (_cart != null) {
                return _cart!!
            }
            _cart = ImageVector.Builder(
                name = "ChampionCart_Cart",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                // Cart handle
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color.Black),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(3f, 3f)
                    horizontalLineTo(4.5f)
                    curveTo(5.1f, 3f, 5.6f, 3.4f, 5.8f, 4f)
                    lineTo(6.2f, 5.5f)
                }

                // Cart basket
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(6f, 6f)
                    horizontalLineTo(19f)
                    curveTo(19.8f, 6f, 20.4f, 6.8f, 20.2f, 7.6f)
                    lineTo(18.5f, 15.6f)
                    curveTo(18.3f, 16.4f, 17.6f, 17f, 16.8f, 17f)
                    horizontalLineTo(9.2f)
                    curveTo(8.4f, 17f, 7.7f, 16.4f, 7.5f, 15.6f)
                    lineTo(6f, 6f)
                    close()
                }

                // Left wheel
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(10.5f, 20f)
                    curveTo(10.5f, 20.8f, 9.8f, 21.5f, 9f, 21.5f)
                    curveTo(8.2f, 21.5f, 7.5f, 20.8f, 7.5f, 20f)
                    curveTo(7.5f, 19.2f, 8.2f, 18.5f, 9f, 18.5f)
                    curveTo(9.8f, 18.5f, 10.5f, 19.2f, 10.5f, 20f)
                    close()
                }

                // Right wheel
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(17.5f, 20f)
                    curveTo(17.5f, 20.8f, 16.8f, 21.5f, 16f, 21.5f)
                    curveTo(15.2f, 21.5f, 14.5f, 20.8f, 14.5f, 20f)
                    curveTo(14.5f, 19.2f, 15.2f, 18.5f, 16f, 18.5f)
                    curveTo(16.8f, 18.5f, 17.5f, 19.2f, 17.5f, 20f)
                    close()
                }
            }.build()
            return _cart!!
        }

    private var _cart: ImageVector? = null

    /**
     * Profile icon with organic shape and personality accents
     */
    val Profile: ImageVector
        get() {
            if (_profile != null) {
                return _profile!!
            }
            _profile = ImageVector.Builder(
                name = "ChampionCart_Profile",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                // Head/face
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(16.5f, 8f)
                    curveTo(16.5f, 10.5f, 14.5f, 12.5f, 12f, 12.5f)
                    curveTo(9.5f, 12.5f, 7.5f, 10.5f, 7.5f, 8f)
                    curveTo(7.5f, 5.5f, 9.5f, 3.5f, 12f, 3.5f)
                    curveTo(14.5f, 3.5f, 16.5f, 5.5f, 16.5f, 8f)
                    close()
                }

                // Body/shoulders
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(5.5f, 20f)
                    curveTo(5.5f, 16.5f, 8.5f, 13.5f, 12f, 13.5f)
                    curveTo(15.5f, 13.5f, 18.5f, 16.5f, 18.5f, 20f)
                    verticalLineTo(21f)
                    curveTo(18.5f, 21.3f, 18.2f, 21.5f, 18f, 21.5f)
                    horizontalLineTo(6f)
                    curveTo(5.8f, 21.5f, 5.5f, 21.3f, 5.5f, 21f)
                    verticalLineTo(20f)
                    close()
                }
            }.build()
            return _profile!!
        }

    private var _profile: ImageVector? = null
}