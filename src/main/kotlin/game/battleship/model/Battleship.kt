package battleship.model

import battleship.storage.Storage

data class Battleship (
    val board : Board = Board(),
    val player : Player = Player.A,
    val name : String?,
    val fleet : Fleet = Fleet(emptyList())
)
fun Battleship.startGame(name: String, st: Storage) : Battleship{
    val player = st.start(name, board)
    val game = copy(player = player, name = name)
    return if (player === Player.B) {
        val savedGame = st.load(game)
        var newBoard = board.copy(grid = board.grid.map {if (it.player == Player.A) it.copy(player =  Player.B) else it})
        newBoard = newBoard.copy(grid = newBoard.grid + savedGame.board.grid)
        return copy(board = newBoard, player = player, name = name).also{st.save(it)}
    }
    else game
}

fun Battleship.put(shipType: ShipType, position: Position, align: Direction, st: Storage) : Battleship{
    val indexPosition = Position.values.indexOf(position)
    var shipPositions: List<Position> = emptyList()
    var newBoard : Board = board
    for(i in 0 until shipType.squares){
        if (align == Direction.HORIZONTAL){
            shipPositions = shipPositions + Position.values[indexPosition + i]
            newBoard = newBoard.place(Position.values[indexPosition + i], player)
        }
        else{
            shipPositions = shipPositions + Position.values[indexPosition + i * COLUMN_DIM]
            newBoard = newBoard.place(Position.values[indexPosition + i * COLUMN_DIM], player)
        }
    }
    val newShips = fleet.ships + Ship(shipType, shipPositions)
    return copy(board = newBoard, fleet = Fleet(newShips))
}

fun Battleship.remove(ship: Ship, st: Storage) : Battleship{
    var newBoard = board
    for(i in 0 until ship.type.squares){
        newBoard = newBoard.remove(ship.positions[i], player)
    }
    val newShips = fleet.ships - ship
    return copy(board = newBoard, fleet = Fleet(newShips))
}

fun Battleship.shot(pos: Position, st: Storage) : Battleship{
    val newBoard = if(board.grid.any {it.player == player.other() && it.square.pos == pos}) board.shot(pos, player)
    else board.missedShot(pos, player)
    val ship = fleet.getShipFromPosition(pos)
    newBoard.grid.map{grid ->
        if(!newBoard.grid.any{ ship?.positions?.contains(grid.square.pos) == true && grid.square.symbol == ShipSquare})
            grid.copy(square = Square(pos, ShipSunk))
        else grid}
    return copy(board = newBoard).also {st.save(it)}
}

val Battleship.allShipsPlaced get() = fleet.ships.size >= ShipType.values.sumOf{it.fleetQuantity}
val Battleship.gameNotStarted get() = name == null
val Battleship.isYourTurn get() = player == board.turn
val Battleship.isOver get() = board.winner != null