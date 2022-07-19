package game.battleship.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState

@Composable
fun DialogMessage(message: String, onOk: () -> Unit) =
    Dialog(
        onCloseRequest = onOk, title = "HEY",
        state = DialogState(height = Dp.Unspecified, width = 400.dp)
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message)
            Button(onClick = onOk) { Text("Ok understandable") }
        }
    }