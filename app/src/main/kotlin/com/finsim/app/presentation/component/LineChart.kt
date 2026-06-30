package com.finsim.app.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Gráfico de linha simples usando Canvas do Compose.
 * Zero dependências externas — adequado para MVP.
 *
 * @param points lista de valores numéricos no eixo Y (ordem cronológica)
 * @param lineColor cor da linha
 * @param fillColor cor do preenchimento abaixo da linha (use com alpha)
 */
@Composable
fun LineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = lineColor.copy(alpha = 0.15f),
) {
    if (points.size < 2) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        drawLineChart(points, lineColor, fillColor)
    }
}

private fun DrawScope.drawLineChart(
    points: List<Float>,
    lineColor: Color,
    fillColor: Color,
) {
    val min = points.min()
    val max = points.max()
    val range = (max - min).coerceAtLeast(1f)

    val xStep = size.width / (points.size - 1)
    val padV = size.height * 0.08f

    fun xAt(i: Int) = i * xStep
    fun yAt(v: Float) = padV + (1f - (v - min) / range) * (size.height - 2 * padV)

    val linePath = Path()
    val fillPath = Path()

    points.forEachIndexed { i, v ->
        val x = xAt(i)
        val y = yAt(v)
        if (i == 0) {
            linePath.moveTo(x, y)
            fillPath.moveTo(x, size.height)
            fillPath.lineTo(x, y)
        } else {
            linePath.lineTo(x, y)
            fillPath.lineTo(x, y)
        }
    }

    fillPath.lineTo(xAt(points.lastIndex), size.height)
    fillPath.close()

    drawPath(fillPath, fillColor)
    drawPath(linePath, lineColor, style = Stroke(width = 3.dp.toPx()))

    // pontos de início e fim
    drawCircle(lineColor, radius = 5.dp.toPx(), center = Offset(xAt(0), yAt(points.first())))
    drawCircle(lineColor, radius = 5.dp.toPx(), center = Offset(xAt(points.lastIndex), yAt(points.last())))
}
