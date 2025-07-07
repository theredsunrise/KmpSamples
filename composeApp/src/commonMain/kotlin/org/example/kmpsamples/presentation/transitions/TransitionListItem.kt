package org.example.kmpsamples.presentation.transitions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.pineapple
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.fillMaxWidthModifier
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TransitionListItem(modifier: Modifier, item: ListItem) {
    ElevatedCard(
        modifier,
        colors = CardDefaults.elevatedCardColors()
            .copy(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(fillMaxSizeModifier) {
            Image(
                vectorResource(Res.drawable.pineapple),
                item.id.toString(),
                Modifier.background(Color.Transparent).fillMaxWidth().weight(0.5f)
            )
            Box(
                fillMaxWidthModifier.weight(0.5f).background(item.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${item.category} ${item.id}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}