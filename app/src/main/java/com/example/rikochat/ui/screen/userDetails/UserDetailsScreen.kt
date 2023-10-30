package com.example.rikochat.ui.screen.userDetails

import android.util.EventLogTags.Description
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rikochat.R
import com.example.rikochat.ui.theme.RikoChatTheme
import com.example.rikochat.utils.pxToDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UserDetailsScreen(
    viewModel: UserDetailsViewModel
) {
    val pagerState = rememberPagerState(pageCount = {
        5
    })

    var indicatorSectionSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val pagerItems = listOf(
        "Media",
        "Files",
        "Links",
        "Voice",
        "Groups"
    )


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Surface(shadowElevation = 8.dp) {
                TopBar(scrollBehavior = scrollBehavior)
            }

        }
    ) { paddingValues ->
        BoxWithConstraints(modifier = Modifier.padding(paddingValues)) {
            val screenHeight = maxHeight

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
            ) {

                Description()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight)
                ) {
                    MediaPagerIndicator(
                        pagerItems = pagerItems,
                        pagerState = pagerState,
                        indicatorSectionSize = indicatorSectionSize,
                        updateIndicatorSectionSize = {
                            indicatorSectionSize = it
                        },
                        coroutineScope = coroutineScope
                    )

                    MediaPager(
                        pagerItems = pagerItems,
                        pagerState = pagerState,
                        scrollState = scrollState
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .border(1.dp, Color.White, CircleShape)
                        .size(48.dp)
                        .clip(CircleShape),
                    painter = painterResource(id = R.drawable.chat_room_placeholder),
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )

                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Riko",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Text(
                        text = "Last seen recently",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }

            }
        },

        actions = {
            IconButton(
                onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Message,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Search through contacts"
                )
            }

            IconButton(
                onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Search through contacts"
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Menu button",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        scrollBehavior = scrollBehavior
    )

}

@Composable
private fun Description() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(text = "Info", color = MaterialTheme.colorScheme.onSecondary, fontSize = 18.sp)

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Username", color = MaterialTheme.colorScheme.onSecondary, fontSize = 16.sp)
            Text(text = "Riko", color = MaterialTheme.colorScheme.onSecondary, fontSize = 14.sp)
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {

            Text(text = "Bio", color = MaterialTheme.colorScheme.onSecondary, fontSize = 16.sp)

            Text(
                text = "im 20y",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 14.sp
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaPagerIndicator(
    pagerItems: List<String>,
    pagerState: PagerState,
    indicatorSectionSize: IntSize,
    updateIndicatorSectionSize: (IntSize) -> Unit,
    coroutineScope: CoroutineScope
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            repeat(pagerItems.size) { iteration ->
                Text(
                    text = pagerItems[iteration],
                    modifier = Modifier
                        .onGloballyPositioned {
                            updateIndicatorSectionSize(it.size)
                        }
                        .weight(1f)
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(iteration)
                            }

                        }
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .graphicsLayer {
                    val scrollPosition =
                        pagerState.currentPage + pagerState.currentPageOffsetFraction
                    translationX = scrollPosition * indicatorSectionSize.width
                }
                .size(
                    width = indicatorSectionSize.width.pxToDp(),
                    height = 4.dp
                )
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(color = MaterialTheme.colorScheme.onSecondary)
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaPager(
    pagerItems: List<String>,
    pagerState: PagerState,
    scrollState: ScrollState
) {
    HorizontalPager(
        state = pagerState, modifier = Modifier
            .fillMaxSize()
            .nestedScroll(
                remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            Log.d("riko", "available.y: ${available.y}")
                            return if (available.y > 0) Offset.Zero else Offset(
                                x = 0f,
                                y = -scrollState.dispatchRawDelta(-available.y)
                            )
                        }
                    }
                }
            )
    ) { page ->
        LazyColumn(content = {
            val list = MutableList(20) {
                "${pagerItems[page]}: $it"
            }

            itemsIndexed(list) { index, item ->
                Text(
                    text = item,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

        })


    }

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview
@Composable
private fun Preview() {



}