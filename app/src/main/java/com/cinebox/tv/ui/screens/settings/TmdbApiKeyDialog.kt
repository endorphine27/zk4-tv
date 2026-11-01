@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.cinebox.tv.ui.screens.settings

import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.cinebox.tv.R
import com.cinebox.tv.ui.components.CineboxDialog
import com.cinebox.tv.ui.theme.CineboxTheme

@Composable
internal fun TmdbApiKeyDialog(
    currentValue: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember(currentValue) { mutableStateOf(currentValue) }
    var isInputFocused by remember { mutableStateOf(false) }
    val inputFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)

    CineboxDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.tmdb_api_key_title),
        subtitle = stringResource(R.string.tmdb_api_key_dialog_subtitle),
        width = 700.dp
    ) {
        Card(
            onClick = { inputFocusRequester.requestFocus() },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isInputFocused = it.isFocused || it.hasFocus },
            colors = CardDefaults.colors(
                containerColor = CineboxTheme.colors.BackgroundElevated,
                focusedContainerColor = CineboxTheme.colors.BackgroundElevated
            ),
            border = CardDefaults.border(
                border = Border(
                    border = androidx.compose.foundation.BorderStroke(CineboxTheme.spacing.hairline, CineboxTheme.colors.Border),
                    shape = shape
                ),
                focusedBorder = Border(
                    border = CineboxTheme.focusRing.border(CineboxTheme.spacing.xxs),
                    shape = shape
                )
            ),
            shape = CardDefaults.shape(shape),
            scale = CardDefaults.scale(focusedScale = 1f)
        ) {
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = CineboxTheme.spacing.md)) {
                BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(inputFocusRequester)
                        .onKeyEvent { event ->
                            event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER &&
                                event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = CineboxTheme.colors.TextPrimary,
                        textDirection = TextDirection.Content
                    ),
                    cursorBrush = SolidColor(
                        if (isInputFocused) CineboxTheme.colors.Primary
                        else androidx.compose.ui.graphics.Color.Transparent
                    ),
                    decorationBox = { innerTextField ->
                        if (value.isBlank()) {
                            Text(
                                text = stringResource(R.string.tmdb_api_key_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                color = CineboxTheme.colors.TextTertiary
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.colors(
                    containerColor = CineboxTheme.colors.BackgroundElevated,
                    contentColor = CineboxTheme.colors.TextPrimary
                )
            ) { Text(stringResource(R.string.action_cancel)) }
            Spacer(modifier = Modifier.width(CineboxTheme.spacing.sm))
            Button(
                onClick = { onSave("") },
                colors = ButtonDefaults.colors(
                    containerColor = CineboxTheme.colors.BackgroundElevated,
                    contentColor = CineboxTheme.colors.TextPrimary
                )
            ) { Text(stringResource(R.string.action_clear)) }
            Spacer(modifier = Modifier.width(CineboxTheme.spacing.sm))
            Button(
                onClick = { onSave(value) },
                colors = ButtonDefaults.colors(
                    containerColor = CineboxTheme.colors.BackgroundCard,
                    contentColor = CineboxTheme.colors.TextPrimary
                )
            ) { Text(stringResource(R.string.action_save)) }
        }
    }
}
