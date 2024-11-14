package com.msbahng.commonutils.compose

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> EndlessLazyPaging(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    currentPage: Int,
    pageChanged: (Int) -> Unit,
    loadMore: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = {
            items.count()
        }
    )

    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            Log.d("EndlessLazyPaging", "Page changed to $page")

            if (page != 0 && page == items.count() - 1) {
                loadMore()
            }

            pageChanged(page)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        pageSpacing = 10.dp,
        contentPadding = PaddingValues(
            horizontal = 30.dp
        )
    ) { page ->
        itemContent(items[page])
    }
}