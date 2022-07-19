package battleship.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import battleship.model.*

import battleship.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameState(val storage: Storage, val scope: CoroutineScope) {
    var game by mutableStateOf(Battleship(player = Player.A, name = null))
        private set
    var alignment by mutableStateOf(Direction.VERTICAL)
        private set
    var shipType by mutableStateOf("Carrier".toShipType())
        private set
    var openDialogName by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    var jobAutoRefresh by mutableStateOf<Job?>(null)
        private set
    var autoRefreshEnabled by mutableStateOf<Boolean>(true)

    fun putShip(pos: Position) {
        game = putShipAction(shipType,pos,alignment,game,storage)
    }

    private fun removeShip(pos: Position) {
        game = removeAction(game,pos,storage)
    }

    private fun shootShip(pos: Position) {
       game = shotAction(game,pos,storage)
    }

    fun clickManagement(pos: Position) =
        if (game.name == null) {
            if (game.board.get(pos, game.player) != null) removeShip(pos) else putShip(pos)
        } else shootShip(pos)

    fun start(name: String?) {
        if (name == null) openDialogName = true
        else {
            scope.launch {
                game =  startAction(game, name, storage)
                openDialogName = false
                if (autoRefreshEnabled)waitForOther()
            }
        }
    }

    fun refresh()  {
        scope.launch {
            game = refreshAction(game, storage)
        }
    }

    fun waitForOther() {
        val waitingPlayerB = game.player == Player.A && game.fleet.ships.isEmpty()
        if ((game.isYourTurn && !waitingPlayerB) || game.isOver) return
        jobAutoRefresh = scope.launch {
            do {
                delay(3000)
                refresh()
            } while (!game.isOver &&(
                        game.isYourTurn || (!game.board.grid.any { it.player == Player.B }&&waitingPlayerB))
            )
            jobAutoRefresh = null
        }
    }

    fun closeDialog() {
        openDialogName = false
    }

    fun messageAck() {
        message = null
    }

    fun changeShipType(ship: ShipType) {
        shipType = ship
    }

    fun changeAlignment(direction: Direction) {
        alignment = direction
    }
    fun cancelAutoRefresh(){
        jobAutoRefresh ?.cancel()
        jobAutoRefresh = null
        autoRefreshEnabled = false
    }

    fun activateAutoRefresh() {
        autoRefreshEnabled = true
        waitForOther()
    }
}