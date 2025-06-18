package org.example.kmpsamples.presentation.transitions

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.kmpsamples.presentation.fillMaxWidthModifier

data class ListItem(val id: Int, val category: String, val color: Color)

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TransitionListScreen(
    modifier: Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentTransitionScope: AnimatedContentScope,
    customItems: List<ListItem>,
    onItemClicked: (itemId: Int) -> Unit
) {
    LazyColumn(
        modifier,
        contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 10.dp, bottom = 10.dp)
    ) {
        customItems.groupBy { it.category }.forEach { groupOfItems ->
            stickyHeader {
                Text(
                    groupOfItems.key,
                    fillMaxWidthModifier.padding(bottom = 10.dp) .background(MaterialTheme.colorScheme.primary)
                        .requiredHeight(50.dp).padding(8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
            val chunkedItems = groupOfItems.value.chunked(3)
            items(chunkedItems, key = { it.joinToString() }) { rowItems ->
                Row(
                    fillMaxWidthModifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
                ) {
                    rowItems.forEach {
                        with(sharedTransitionScope) {
                            TransitionListItem(
                                Modifier.sharedBounds(
                                rememberSharedContentState(it.id),
                                animatedContentTransitionScope
                            )
                                .clickable { onItemClicked(it.id) }
                                .weight(1f)
                                .padding(4.dp)
                                .height(300.dp), it)
                        }
                    }
                    val spacerWeight = (3 - rowItems.size).toFloat()
                    if (spacerWeight > 0) {
                        Spacer(modifier = Modifier.weight(spacerWeight))
                    }
                }
            }
        }
    }
}