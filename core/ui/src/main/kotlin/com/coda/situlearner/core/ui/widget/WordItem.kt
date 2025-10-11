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
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.testing.data.wordsTestData
import com.coda.situlearner.core.ui.util.formatInstant
import kotlin.time.Instant

@Composable
fun WordItem(
    word: Word,
    proficiencyType: WordProficiencyType,
    modifier: Modifier = Modifier,
) {
    WordItem(
        word = word.word,
        pronunciation = word.pronunciation,
        definition = word.meanings.firstOrNull()?.definition,
        proficiency = word.proficiency(proficiencyType),
        lastViewedDate = word.lastViewedDate,
        modifier = modifier
    )
}

@Composable
fun WordItem(
    word: String,
    modifier: Modifier = Modifier,
    pronunciation: String? = null,
    definition: String? = null,
    proficiency: WordProficiency? = null,
    lastViewedDate: Instant? = null
) {
    // as the vertical padding for two line list item
    Column(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = definition ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (proficiency != null) {
                Spacer(modifier = Modifier.width(8.dp))
                ProficiencyIconSet(
                    proficiency = proficiency,
                    onlyShowStarred = true,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.75f)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = pronunciation ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (lastViewedDate != null) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatInstant(lastViewedDate),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun WordItemPreview() {
    val word = wordsTestData[5]

    WordItem(word = word, proficiencyType = WordProficiencyType.Meaning)
    // Note that the pronunciation "これ⓪" will add an expected padding at the supporting
    // line when using ListItem
}