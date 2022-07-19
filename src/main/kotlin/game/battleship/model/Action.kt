package battleship.ui
import battleship.model.*
import battleship.storage.Storage

fun startAction(game: Battleship, name: String, st: Storage) : Battleship{
    check(!game.allShipsPlaced) {"Not all ships have been placed"}
    check(game.gameNotStarted) {" Game Has Already Started "}
    return game.startGame(name, st)
}

fun putShipAction(shipType: ShipType, position: Position, align: Direction, game: Battleship, st: Storage) : Battleship{
    check(position.column.ordinal+shipType.squares <= COLUMN_DIM && align == Direction.HORIZONTAL ||
        position.row.ordinal+shipType.squares <= ROW_DIM && align == Direction.VERTICAL) {"In that position ${shipType.name} surpasses the grid"}
    check(!game.board.grid.any{it.square.pos in game.board.getShipBorder(position, shipType, align) && it.player == game.player})
        { " ${shipType.name} is to close to another ship, choose other position" }
    check(game.fleet.ships.count { it.type === shipType } < shipType.fleetQuantity) { " Maximum number of ${shipType.name} placed " }
    return game.put(shipType,position,align, st)
}


fun Battleship.putShipRandom() : Battleship{
    return this
}

fun Battleship.putAll() : Battleship{
    return this
}

fun removeAction(game: Battleship, position: Position, st: Storage) : Battleship{
    check(game.gameNotStarted) {" Game Has Already Started"}
    val ship = game.fleet.getShipFromPosition(position) ?: throw IllegalArgumentException("No ship in such Position $position")
    return game.remove(ship, st)
}

fun shotAction(game: Battleship, position: Position, st: Storage) : Battleship{
    check(!game.gameNotStarted) {" Game has not started yet "}
    return game.shot(position, st)
}

fun refreshAction(game: Battleship, st: Storage) : Battleship{
    check(!game.gameNotStarted) {" Game has not started yet "}
    return st.load(game)
}