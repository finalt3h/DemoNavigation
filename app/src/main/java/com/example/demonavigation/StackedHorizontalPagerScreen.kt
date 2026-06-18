package com.example.demonavigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.demonavigation.ui.theme.DemoNavigationTheme
import kotlin.math.abs
import kotlin.math.ceil

private data class PagerPage(
    val title: String,
    val description: String,
    val colors: List<Color>
)

private val stackedPagerPages = listOf(
    PagerPage(
        title = "Page 1",
        description = "Trang dau tien nam ben duoi khi trang moi truot vao.",
        colors = listOf(Color(0xFF1D3557), Color(0xFF457B9D))
    ),
    PagerPage(
        title = "Page 2",
        description = "Trang moi de len, trang cu thu nho va mo dan.",
        colors = listOf(Color(0xFF2A9D8F), Color(0xFFE9C46A))
    ),
    PagerPage(
        title = "Page 3",
        description = "Hieu ung stack tao cam giac page cu chim xuong.",
        colors = listOf(Color(0xFFE76F51), Color(0xFF9B5DE5))
    ),
            PagerPage(
            title = "Page 4",
    description = "Hieu ung stack tao cam giac page cu chim xuong.",
    colors = listOf(Color(0xFF4276F46), Color(0x3F9B54E5)),
),PagerPage(
        title = "Page 5",
        description = "Hieu ung stack tao cam giac page cu chim xuong.",
        colors = listOf(Color(0xF69a6F51), Color(0xFF2143E5))
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StackedHorizontalPagerScreen(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { stackedPagerPages.size })
    var transitionBasePage by remember { mutableIntStateOf(pagerState.settledPage) }
    var transitionDirection by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState) {
        snapshotFlow {
            PagerMotionSample(
                isScrollInProgress = pagerState.isScrollInProgress,
                settledPage = pagerState.settledPage,
                scrollPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
            )
        }.collect { sample ->
            if (!sample.isScrollInProgress) {
                transitionBasePage = sample.settledPage
                transitionDirection = 0
            } else if (transitionDirection == 0) {
                val delta = sample.scrollPosition - transitionBasePage
                if (abs(delta) > 0.001f) {
                    transitionDirection = delta.compareTo(0f)
                }
            }
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = Color(0xFFF6F7FB)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${pagerState.settledPage + 1} / ${stackedPagerPages.size}",
                color = Color(0xFF263238),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                beyondViewportPageCount = 1
            ) { page ->
                StackedPagerCard(
                    page = stackedPagerPages[page],
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 20.dp)
                        .zIndex(
                            computeStackedPagerTransform(
                                page = page,
                                transitionBasePage = transitionBasePage,
                                transitionDirection = transitionDirection,
                                currentPage = pagerState.currentPage,
                                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                                pageWidth = 0f
                            ).zIndex
                        )
                        .graphicsLayer {
                            val transform = computeStackedPagerTransform(
                                page = page,
                                transitionBasePage = transitionBasePage,
                                transitionDirection = transitionDirection,
                                currentPage = pagerState.currentPage,
                                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                                pageWidth = size.width
                            )
                            translationX = transform.translationX
                            translationY = transform.translationY
                            scaleX = transform.scale
                            scaleY = transform.scale
                            alpha = transform.alpha
                            shadowElevation = transform.shadowElevation
                        }
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(stackedPagerPages.size) { index ->
                    val selected = pagerState.settledPage == index
                    Box(
                        modifier = Modifier
                            .size(width = if (selected) 24.dp else 8.dp, height = 8.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color(0xFF263238) else Color(0x33263238))
                    )
                }
            }
        }
    }
}

private data class PagerMotionSample(
    val isScrollInProgress: Boolean,
    val settledPage: Int,
    val scrollPosition: Float
)

@Composable
private fun StackedPagerCard(
    page: PagerPage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(page.colors))
            .padding(28.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = page.title,
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = page.description,
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 17.sp,
                lineHeight = 24.sp
            )
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

internal data class StackedPagerTransform(
    val translationX: Float,
    val translationY: Float,
    val scale: Float,
    val alpha: Float,
    val shadowElevation: Float,
    val zIndex: Float
)

internal fun computeStackedPagerTransform(
    page: Int,
    transitionBasePage: Int,
    transitionDirection: Int,
    currentPage: Int,
    currentPageOffsetFraction: Float,
    pageWidth: Float
): StackedPagerTransform {
    val scrollPosition = currentPage + currentPageOffsetFraction
    val travelFromBasePage = scrollPosition - transitionBasePage
    val direction = when {
        transitionDirection < 0 -> -1
        transitionDirection > 0 -> 1
        travelFromBasePage < 0f -> -1
        travelFromBasePage > 0f -> 1
        else -> 0
    }
    val completedSegments = (ceil(abs(travelFromBasePage)) - 1f)
        .coerceAtLeast(0f)
        .toInt()
    val segmentBasePage = transitionBasePage + direction * completedSegments
    val travelFromSegmentBase = scrollPosition - segmentBasePage
    val incomingPage = segmentBasePage + direction
    val isOutgoingPage = direction != 0 && page == segmentBasePage
    val isIncomingPage = direction != 0 && page == incomingPage
    val sinkProgress = if (isOutgoingPage) {
        abs(travelFromSegmentBase).coerceIn(0f, 1f)
    } else {
        0f
    }

    return StackedPagerTransform(
        translationX = if (isOutgoingPage) {
            (scrollPosition - page) * pageWidth
        } else {
            0f
        },
        translationY = lerp(0f, 96f, sinkProgress),
        scale = lerp(1f, 0.86f, sinkProgress),
        alpha = lerp(1f, 0.18f, sinkProgress),
        shadowElevation = lerp(24f, 4f, sinkProgress),
        zIndex = if (isIncomingPage) 1f else 1f - sinkProgress
    )
}

@Preview(showBackground = true)
@Composable
private fun StackedHorizontalPagerScreenPreview() {
    DemoNavigationTheme {
        StackedHorizontalPagerScreen()
    }
}
