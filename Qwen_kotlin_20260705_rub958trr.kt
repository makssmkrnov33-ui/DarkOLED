@Composable
fun WaveformView(amplitudes: List<Int>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(40.dp)) {
        val barWidth = 3.dp.toPx()
        val spacing = 2.dp.toPx()
        val maxAmp = amplitudes.maxOrNull() ?: 1
        
        amplitudes.forEachIndexed { index, amp ->
            val barHeight = (amp.toFloat() / maxAmp) * size.height
            val x = index * (barWidth + spacing)
            drawRect(
                color = MaterialTheme.colorScheme.primary,
                topLeft = Offset(x, (size.height - barHeight) / 2),
                size = Size(barWidth, barHeight)
            )
        }
    }
}