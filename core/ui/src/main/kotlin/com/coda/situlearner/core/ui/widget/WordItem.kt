package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.testing.data.wordsTestData
import com.coda.situlearner.core.ui.util.formatInstant

@Composable
fun WordItem(
    word: Word,
    showProficiency: Boolean,
    modifier: Modifier = Modifier,
) {
    // as the vertical padding for two line list item
    Column(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word.word,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = word.meanings?.firstOrNull()?.definition ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showProficiency) {
                Spacer(modifier = Modifier.width(8.dp))
                ProficiencyIconSet(
                    proficiency = word.proficiency,
                    onlyShowStarred = true,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.75f)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word.pronunciation ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = word.lastViewedDate?.let {
                    formatInstant(it)
                } ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun WordItemPreview() {
    val word = wordsTestData[5]

    WordItem(
        word = word,
        showProficiency = false
    )
    // Note that the pronunciation "これ⓪" will add an expected padding at the supporting
    // line when using ListItem
}