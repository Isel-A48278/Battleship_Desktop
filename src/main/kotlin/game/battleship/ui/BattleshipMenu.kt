package game.battleship.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import battleship.model.*
import battleship.ui.*

@Composable
fun FrameWindowScope.BattleshipMenu(state: GameState, onExit: () -> Unit) {

    MenuBar {
        Menu("Game"){
            Item("Start Battle",
                enabled= state.game.allShipsPlaced ,
                onClick = {state.start(state.game.name)})
            Item("Refresh",
                enabled = !state.game.gameNotStarted && state.jobAutoRefresh == null,
                onClick = { state.refresh()}
            )
            Item("Deactivate auto Refresh",
                enabled = state.autoRefreshEnabled && !state.game.gameNotStarted, onClick = {state.cancelAutoRefresh()},
            )
            Item("Activate auto Refresh", enabled = !state.autoRefreshEnabled && !state.game.gameNotStarted,
                onClick = {state.activateAutoRefresh()})
            Item("Exit", onClick = onExit)
        }
        /*Menu("Place Ship"){
            Item("Put all(remaining)",
                enabled=state.game.allShipsPlaced && state.game.gameNotStarted, onClick = {state.putAllShips()})
            Item("Remove all(remaining)",
                enabled=state.game.fleet.isNotEmpty()&& state.game.gameNotStarted,onClick={state.removeAllShips()})
        }*/
    }

}