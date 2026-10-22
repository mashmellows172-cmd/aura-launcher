package dev.senda.launcher.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

fun Modifier.skinPattern(skin: SkinStyle): Modifier = drawBehind {
                val tint = skin.accent.copy(alpha = if (skin.pattern == WallpaperPattern.DOTS) 0.19f else 0.10f)
                when (skin.pattern) {
                    WallpaperPattern.DOTS -> {
                        val step = 20.dp.toPx()
                        for (x in 0..(size.width / step).toInt()) for (y in 0..(size.height / step).toInt()) {
                            drawCircle(tint, radius = 1.25.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x * step, y * step))
                        }
                    }
                    WallpaperPattern.ORBS -> {
                        drawCircle(tint, size.width * .35f, androidx.compose.ui.geometry.Offset(size.width * .98f, size.height * .12f))
                        drawCircle(skin.accent.copy(alpha = .045f), size.width * .43f, androidx.compose.ui.geometry.Offset(size.width * .03f, size.height * .82f), style = Stroke(width = 1.5.dp.toPx()))
                    }
                    WallpaperPattern.DIAGONAL -> for (i in -10..30) {
                        val x = i * 38.dp.toPx()
                        drawLine(tint, androidx.compose.ui.geometry.Offset(x, 0f), androidx.compose.ui.geometry.Offset(x - size.height * .35f, size.height), 1.dp.toPx())
                    }
                    WallpaperPattern.WATER_RIPPLE -> {
                        val center = androidx.compose.ui.geometry.Offset(size.width * .50f, size.height * .39f)
                        for (ring in 1..9) {
                            drawCircle(
                                color = skin.accent.copy(alpha = .035f + (ring % 3) * .012f),
                                radius = ring * size.width * .105f,
                                center = center,
                                style = Stroke(width = if (ring % 3 == 0) 2.dp.toPx() else 1.dp.toPx()),
                            )
                        }
                        drawCircle(skin.accent.copy(alpha = .07f), radius = 3.dp.toPx(), center = center)
                    }
                    WallpaperPattern.WAVE -> {
                        val path = Path().apply {
                            moveTo(0f, size.height * .68f)
                            cubicTo(size.width * .32f, size.height * .45f, size.width * .68f, size.height * .86f, size.width, size.height * .57f)
                            lineTo(size.width, size.height); lineTo(0f, size.height); close()
                        }
                        drawPath(path, skin.accent.copy(alpha = .045f))
                    }
                    WallpaperPattern.HORIZON -> {
                        drawLine(skin.accent.copy(alpha = .16f), androidx.compose.ui.geometry.Offset(size.width * .52f, 0f), androidx.compose.ui.geometry.Offset(size.width * .52f, size.height), 1.dp.toPx())
                        drawLine(skin.accent.copy(alpha = .08f), androidx.compose.ui.geometry.Offset(0f, size.height * .72f), androidx.compose.ui.geometry.Offset(size.width, size.height * .72f), 1.dp.toPx())
                    }
                    WallpaperPattern.SENSE_LANDSCAPE -> {
                        drawCircle(androidx.compose.ui.graphics.Brush.radialGradient(listOf(androidx.compose.ui.graphics.Color(0xFFE9C384).copy(alpha = .17f), androidx.compose.ui.graphics.Color.Transparent), center = androidx.compose.ui.geometry.Offset(size.width * .82f, size.height * .12f), radius = size.width * .7f), radius = size.width * .7f, center = androidx.compose.ui.geometry.Offset(size.width * .82f, size.height * .12f))
                        for (layer in 0..2) {
                            val y = size.height * (.70f + layer * .08f)
                            val path = Path().apply { moveTo(0f, y); cubicTo(size.width * .3f, y - size.height * .12f, size.width * .65f, y + size.height * .14f, size.width, y - size.height * .04f); lineTo(size.width, size.height);lineTo(0f,size.height);close() }
                            drawPath(path, androidx.compose.ui.graphics.Color(0xFF759783).copy(alpha = .09f + layer * .025f))
                        }
                    }
                    WallpaperPattern.NATURE -> {
                        val base = androidx.compose.ui.geometry.Offset(size.width * .90f, size.height * .88f)
                        val head = androidx.compose.ui.geometry.Offset(size.width * .79f, size.height * .48f)
                        val stem = Path().apply { moveTo(base.x,base.y); quadraticTo(size.width*.68f,size.height*.67f,head.x,head.y) }
                        drawPath(stem, androidx.compose.ui.graphics.Color(0xFFCDE6B3).copy(alpha=.2f), style=Stroke(2.dp.toPx()))
                        for (seed in 0..17) {
                            val a = seed * kotlin.math.PI.toFloat() * 2f / 18
                            val radius = size.width * .18f
                            val tip = head + androidx.compose.ui.geometry.Offset(kotlin.math.cos(a)*radius,kotlin.math.sin(a)*radius)
                            drawLine(androidx.compose.ui.graphics.Color.White.copy(alpha=.16f), head,tip,.7.dp.toPx())
                            drawCircle(androidx.compose.ui.graphics.Color.White.copy(alpha=.23f),2.dp.toPx(),tip)
                        }
                        for (ring in 1..4) drawOval(skin.accent.copy(alpha=.065f), androidx.compose.ui.geometry.Offset(size.width*.05f-ring*size.width*.10f,size.height*.89f-ring*size.height*.018f),androidx.compose.ui.geometry.Size(ring*size.width*.42f,ring*size.height*.06f), style=Stroke(1.dp.toPx()))
                    }
                    WallpaperPattern.OPTIMUS_RIBBONS, WallpaperPattern.XPERIA_FLOW -> {
                        val xperia = skin.pattern == WallpaperPattern.XPERIA_FLOW
                        for (ribbon in 0..5) {
                            val offset = ribbon * size.height * .028f
                            val path = Path().apply {moveTo(-size.width*.15f,size.height*.82f+offset);cubicTo(size.width*.24f,size.height*.39f+offset,size.width*.63f,size.height*1.07f-offset,size.width*1.15f,size.height*.47f+offset) }
                            val color = if (ribbon%2==0) skin.accent else androidx.compose.ui.graphics.Color(if(xperia) 0xFF5C9DFF else 0xFF52B8D5)
                            drawPath(path,color.copy(alpha=if(xperia) .13f else .11f),style=Stroke(width=if(xperia) (ribbon+1).dp.toPx() else (16+ribbon*5).dp.toPx()))
                        }
                    }
                    WallpaperPattern.NONE -> Unit
                }
            }
