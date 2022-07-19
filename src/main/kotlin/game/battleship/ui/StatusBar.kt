package game.battleship.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import battleship.model.ROW_DIM
import battleship.model.gameNotStarted
import battleship.model.isOver
import battleship.model.isYourTurn
import battleship.ui.CELL_SIZE
import battleship.ui.GameState


@Composable
fun StatusView(state: GameState) {
    with(state.game) {//state.game,apply
        val text = if (gameNotStarted) "Edit Fleet" else "Game: $name"
        Spacer(modifier = Modifier.width(CELL_SIZE.dp))
        Row(
            Modifier.width((CELL_SIZE * ROW_DIM * 2.5).dp).border(width = 2.dp, color = Color.Black)
                .height(CELL_SIZE.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text)
            Spacer(modifier = Modifier.width(CELL_SIZE.dp))

            if (!isOver)
                Text(if (isYourTurn) "Its your turn" else "Wait for your turn")
            else
                Text("You ${if (board.winner == player) "Win" else "Lose"}")
            Spacer(modifier = Modifier.width(CELL_SIZE.dp))
            if (name != null) Text(player.toString())

            if (state.jobAutoRefresh != null) LinearProgressIndicator()
        }
    }
}