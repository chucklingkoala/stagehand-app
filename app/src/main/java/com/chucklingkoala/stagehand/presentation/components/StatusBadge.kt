package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors

@Composable
fun StatusBadge(
    status: UrlStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text) = when (status) {
        UrlStatus.ON_SHOW -> Triple(
            StagehandColors.OnShowGreen,
            Color.Black,
            stringResource(R.string.on_show).uppercase()
        )
        UrlStatus.DUMP -> Triple(
            StagehandColors.DumpRed,
            Color.White,
            stringResource(R.string.dump_badge)
        )
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DuplicateBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = StagehandColors.DuplicateOrange,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = stringResource(R.string.duplicate_badge),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}
