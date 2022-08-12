/*
 * Copyright 2021 Vivek Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.vivvvek.astro.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.vivvvek.astro.domain.SortOrder
import dev.vivvvek.astro.domain.models.AstroImage

@Composable
fun HomeScreen(viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.homeScreenState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllImages(SortOrder.LATEST)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AstroTopAppBar() }
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        if (state.error == null && state.images.isNotEmpty()) {
            ImageGrid(
                images = state.images,
                modifier = Modifier.fillMaxHeight()
            )
        }
        if (state.error != null) {
            Text(text = state.error ?: "")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGrid(
    modifier: Modifier = Modifier,
    images: Map<Int, List<AstroImage>>
) {
    ZoomableGrid(
        maximumColumns = 7,
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        images.forEach { (weekNumber, images) ->
            item(span = { GridItemSpan(10) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 2.dp, vertical = 16.dp),
                    horizontalArrangement = SpaceBetween
                ) {
                    Text(text = "Week $weekNumber", fontWeight = FontWeight.Bold)
                    Text(text = images.size.toString())
                }
            }
            items(images) { image ->
                AstroGridItem(
                    image = image,
                    modifier = Modifier
                        .padding(2.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray)
                )
            }
        }
    }
}

@Composable
fun AstroGridItem(
    image: AstroImage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.url)
                .crossfade(true)
                .build(),
            contentDescription = image.title,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AstroTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(text = "Astro") },
        elevation = 2.dp,
        backgroundColor = Color.White,
        modifier = modifier
    )
}
