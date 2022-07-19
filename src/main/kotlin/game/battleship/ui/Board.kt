package battleship.ui

import battleship.model.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import battleship.model.Direction.*

import java.util.ResourceBundle

const val CELL_SIZE = 30

@Composable
fun CellView(grid: Grid?, player: Player?, onClick: () -> Unit) {
    val backgroundColor = when(grid?.square?.symbol){
        ShipShot -> Color.Red
        ShipSunk -> Color.Black
        else -> if (grid?.player == player) Color.Blue else Color.Gray
    }
    val modifier = Modifier.size(CELL_SIZE.dp).clickable(onClick = onClick)
    Box(modifier){
        if (grid?.square?.symbol == ShipSunk || grid?.square?.symbol == ShipShot){
            val imageName = "flame"
            Image(
                painterResource("$imageName.svg"), imageName,
                Modifier.fillMaxSize()
            )
        }
        if (grid?.square?.symbol == MissedShot){
            val imageName = "point"
            Image(
                painterResource("$imageName.png"), imageName,
                Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun UserBoard(player: Player, board: Board, onClick: ((Position) -> Unit)?) {
    repeat(ROW_DIM + 1) { line ->
        if (line != 0) Spacer(Modifier.height(2.dp))
        Row {
            if (line == 0) Column.values.forEach {
                if (it.ordinal == 0) Spacer(Modifier.width(CELL_SIZE.dp))
                Text(
                    it.letter.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(CELL_SIZE.dp).padding(vertical = 2.dp)
                )
                Spacer(Modifier.width(2.dp))
            }
            else {
                Text(
                    "$line",
                    Modifier.size(CELL_SIZE.dp).align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                repeat(COLUMN_DIM) { col ->
                    if (col != 0) Spacer(Modifier.width(2.dp))
                    val pos = Position[col, line-1 ]
                    with(board.get(pos,player)) {
                        CellView(this, player) {
                            if (onClick != null) {
                                onClick(pos)
                            }
                        }
                    }
                }

            }
        }
    }
    Spacer(Modifier.height(2.dp))
}

@Composable
fun BoardView(state: GameState, onClick: (Position) -> Unit) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UserBoard(
                state.game.player,
                state.game.board,
                if (state.game.gameNotStarted)onClick else null
            )

        }
        Column(Modifier.padding(horizontal = 2.dp)) {
            if (!state.game.gameNotStarted) {
                UserBoard(state.game.player.other(), state.game.board, onClick)
            } else {
                Spacer(Modifier.size(CELL_SIZE.dp))
                Box(Modifier.border(2.dp, color = Color.Blue).size(Dp.Unspecified)) {
                    Column {
                        SelectShip(state)
                    }
                }
                Spacer(Modifier.size(CELL_SIZE.dp * 3))
                Box(Modifier.border(2.dp, color = Color.Blue).size(Dp.Unspecified)) {
                    Column {
                        SelectDirection(state)
                    }
                }

            }
        }
    }
}

@Composable
fun SelectShip(state: GameState) {
    ShipType.values.forEach { ship ->
        Row(Modifier.size(Dp.Unspecified).padding(2.dp)) {
            val nbrShipsPlaced = state.game.fleet.ships.count() { it.type == ship }
            Checkbox(
                checked =state.shipType == ship || nbrShipsPlaced == ship.fleetQuantity,
                onCheckedChange = {
                    state.changeShipType(ship)
                }, Modifier.size(CELL_SIZE.dp),
                enabled = nbrShipsPlaced < ship.fleetQuantity
            )
            Text("$nbrShipsPlaced of ${ship.fleetQuantity}", modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(Modifier.width(2.dp))
            repeat(ship.squares) {
                Box(Modifier.size(CELL_SIZE.dp).padding(2.dp).background(Color.Blue))
            }
            Spacer(Modifier.width(2.dp))
        }
    }

}

@Composable
fun SelectDirection(state: GameState) {
    values().forEach { direction ->
        Row(Modifier.size(Dp.Unspecified).padding(2.dp)) {
            Checkbox(
                state.alignment == direction,
                onCheckedChange = { state.changeAlignment(direction) }, Modifier.size(CELL_SIZE.dp)
            )
            Text(direction.name, modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(Modifier.width(2.dp))
            Spacer(Modifier.width(2.dp))
        }
    }
}