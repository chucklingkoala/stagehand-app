package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSubmitSheet(
    isSubmitting: Boolean,
    onSubmit: (url: String, title: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var urlText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StagehandColors.BackgroundSecondary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.submit_url_title),
                style = MaterialTheme.typography.titleMedium,
                color = StagehandColors.TextPrimary
            )

            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.url_label)) },
                placeholder = { Text("https://\u2026") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = StagehandColors.InputBackground,
                    unfocusedContainerColor = StagehandColors.InputBackground,
                    focusedTextColor = StagehandColors.TextPrimary,
                    unfocusedTextColor = StagehandColors.TextPrimary,
                    focusedBorderColor = StagehandColors.AccentColor,
                    unfocusedBorderColor = StagehandColors.BorderColor,
                    focusedLabelColor = StagehandColors.TextSecondary,
                    unfocusedLabelColor = StagehandColors.TextSecondary
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.title_optional_label)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = StagehandColors.InputBackground,
                    unfocusedContainerColor = StagehandColors.InputBackground,
                    focusedTextColor = StagehandColors.TextPrimary,
                    unfocusedTextColor = StagehandColors.TextPrimary,
                    focusedBorderColor = StagehandColors.AccentColor,
                    unfocusedBorderColor = StagehandColors.BorderColor,
                    focusedLabelColor = StagehandColors.TextSecondary,
                    unfocusedLabelColor = StagehandColors.TextSecondary
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                onClick = {
                    val trimmedUrl = urlText.trim()
                    if (trimmedUrl.isNotEmpty() && !isSubmitting) {
                        onSubmit(trimmedUrl, titleText.trim().takeIf { it.isNotEmpty() })
                    }
                },
                enabled = !isSubmitting && urlText.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StagehandColors.ButtonPrimary
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = StagehandColors.TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.submit_action))
                }
            }
        }
    }
}
