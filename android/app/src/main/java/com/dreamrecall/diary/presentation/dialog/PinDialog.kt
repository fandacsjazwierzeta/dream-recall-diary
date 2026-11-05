package com.dreamrecall.diary.presentation.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dreamrecall.diary.core.theme.BorderSoft
import com.dreamrecall.diary.core.theme.Danger
import com.dreamrecall.diary.core.theme.SurfaceCard
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.ui.PinDots
import com.dreamrecall.diary.core.ui.PinKeypad

@Composable
fun PinDialog(
    title: String,
    entered: String,
    wrong: Boolean,
    wrongLabel: String,
    dotsStateLabel: String,
    clearLabel: String,
    confirmDescription: String,
    cancelLabel: String,
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, BorderSoft)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                PinDots(
                    filled = entered.length,
                    wrong = wrong,
                    stateLabel = dotsStateLabel
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (wrong) {
                    Text(
                        text = wrongLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Danger
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                PinKeypad(
                    clearLabel = clearLabel,
                    confirmDescription = confirmDescription,
                    enabled = true,
                    onDigit = onDigit,
                    onClear = onClear,
                    onConfirm = onConfirm
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.height(48.dp)) {
                    Text(
                        text = cancelLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
