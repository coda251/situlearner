package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.ui.R

@Composable
fun ProficiencyIconSet(
    proficiency: WordProficiency,
    modifier: Modifier = Modifier,
    onlyShowStarred: Boolean = false,
    tint: Color = LocalContentColor.current,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        (0 until WordProficiency.entries.size - 1).forEach {
            val starred = it <= (proficiency.level - 1)
            if (onlyShowStarred) {
                if (starred) {
                    ProficiencyIcon(true, tint = tint)
                }
            } else {
                ProficiencyIcon(starred, tint = tint)
            }
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
    ProficiencyIconSet(
        onlyShowStarred = true,
        proficiency = WordProficiency.Beginner,
        tint = MaterialTheme.colorScheme.primary
    )
}
