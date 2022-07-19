package game.battleship.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.FrameWindowScope
import battleship.storage.Storage

import battleship.ui.*
import battleship.model.*

@Composable
@Preview
fun FrameWindowScope.BattleshipApp(st: Storage, onExit : () -> Unit) {
    val scope = rememberCoroutineScope()
    var state = remember { GameState(st,scope) }
    MaterialTheme {
        BattleshipMenu(state,onExit = onExit)
        if (state.openDialogName){
            DialogName(onCancel= { state.closeDialog() }){
                state.start(it)
            }
        }
        state.message?.let {
            DialogMessage(it) { state.messageAck() }
        }
        Column() {
            BoardView(state, state::clickManagement)
            StatusView(state)
        }

    }
}