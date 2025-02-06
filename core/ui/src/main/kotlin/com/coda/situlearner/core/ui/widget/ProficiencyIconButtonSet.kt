package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.ui.R

@Composable
fun ProficiencyIconButtonSet(
    proficiency: WordProficiency,
    onSetProficiency: (WordProficiency) -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (0 until WordProficiency.entries.size - 1).forEach {
            val starred = it <= (proficiency.level - 1)
            IconButton(
                onClick = {
                    if (it == proficiency.level - 1) onSetProficiency(WordProficiency.Unset)
                    else onSetProficiency(WordProficiency.entries.first { entry ->
                        entry.level - 1 == it
                    })
                }
            ) {
                ProficiencyIcon(starred, tint = tint)
            }
        }
    }
}

@Composable
fun ProficiencyIconSet(
    proficiency: WordProficiency,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (0 until WordProficiency.entries.size - 1).forEach {
            val starred = it <= (proficiency.level - 1)
            ProficiencyIcon(starred, tint = tint)
        }
    }
}

@Composable
private fun ProficiencyIcon(
    starred: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        painter = if (starred) painterResource(R.drawable.star_rate_24dp_000000_fill1_wght400_grad0_opsz24)
        else painterResource(R.drawable.star_rate_24dp_000000_fill0_wght400_grad0_opsz24),
        contentDescription = null,
        modifier = modifier,
        tint = tint,
    )
}

@Preview(showBackground = true)
@Composable
private fun ProficiencyIconButtonSetPreview() {

    var proficiency by remember {
        mutableStateOf(WordProficiency.Beginner)
    }

    ProficiencyIconButtonSet(
        proficiency = proficiency,
        onSetProficiency = { proficiency = it },
        tint = MaterialTheme.colorScheme.primary
    )
}
