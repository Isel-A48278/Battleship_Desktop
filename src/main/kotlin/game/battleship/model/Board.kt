package battleship.model

const val COLUMN_DIM = 10
const val ROW_DIM = 10

/**
 * Represents each player in the game
 */
enum class Player(val symbol: Char) {
    A('A'), B('B');
    fun other() = if (this===A) B else A
}

enum class Direction {
    VERTICAL,HORIZONTAL
}

class Column private constructor (val letter : Char){
    val ordinal get() = values.indexOf(this)
    companion object {
        val values = List(COLUMN_DIM) { Column('A' + it) }
    }
}

fun Int.indexToColumnOrNull() : Column? =
    if (this in 0 until COLUMN_DIM) this.indexToColumn() else null


fun Int.indexToColumn() =
    Column.values[this]

fun Char.toColumnOrNull() : Column? {
    val upperCode = this.uppercase()[0].code
    val firstColumnCode = Column.values.first().letter.code
    return if (upperCode - firstColumnCode in 0 until COLUMN_DIM) Column.values[upperCode-firstColumnCode] else null
}

class Row private constructor (val number : Int){
    val ordinal get() = values.indexOf(this)
    companion object {
        val values = List(ROW_DIM) { Row(it+1) }
    }
}

fun Int.indexToRowOrNull() : Row? =
    if (this in 0 until ROW_DIM) this.indexToRow() else null


fun Int.indexToRow() =
    Row.values[this]

fun Int.toRowOrNull() : Row? =
    if (this in Row.values.first().number..ROW_DIM) Row.values[this-1] else null


class Position private constructor(val column : Column, val row : Row){
    val pos get() = values.indexOf(this)
    companion object{
        val values = List(COLUMN_DIM*ROW_DIM) {
            Position(column = (it%COLUMN_DIM).indexToColumn(),row = (it/ROW_DIM).indexToRow())
        }
        operator fun get(indexColumn : Column, indexRow : Row) =
            values.first {it.column == indexColumn && it.row == indexRow}
        operator fun get(indexColumn : Int, indexRow : Int) = values[indexRow*ROW_DIM+indexColumn]
    }
    override fun toString() : String {
        return "${column.letter}${row.number}"
    }
}

fun String.toPositionOrNull() : Position?{
    val column = first().toColumnOrNull()
    val row = drop(1).toInt().toRowOrNull()
    return if(column != null && row != null) Position[column, row] else null
}

fun String.toPosition() : Position =
    this.toPositionOrNull() ?: throw IllegalStateException("Invalid position $this")

data class Square(val pos : Position, val symbol : ShipPart)

data class Grid(val square: Square, val player: Player)

data class Board(
    val grid : List<Grid> = emptyList(),
    val turn : Player = Player.A,
    val winner : Player? = null
)

fun Board.win(player: Player): Board{
    return if (!grid.any{it.player.other() == player && it.square.symbol == ShipSquare}) copy(winner = turn)
    else this
}

fun Board.place(pos: Position, player: Player): Board {
    check (!grid.any {it.player == player && it.square.pos == pos}) { " Invalid position $pos " }
    return Board(grid + Grid(Square(pos, ShipSquare), player), turn, winner)
}

fun Board.remove(pos: Position, player: Player): Board =
    Board(grid.filter {it.player == player && it.square.pos != pos }, turn, winner)

fun Board.missedShot(pos: Position, player: Player): Board {
    check (turn == player) { " Not your turn " }
    check (grid.count{it.player == player.other()} >= ShipType.values.sumOf{it.squares * it.fleetQuantity}) { "Opponent hasn't started yet" }
    return Board(grid + Grid(Square(pos, MissedShot), player.other()), turn.other(), winner)
}

fun Board.shot(pos: Position, player: Player): Board{
    check (turn == player) { " Not your turn " }
    check (grid.any{it.square.pos == pos && it.square.symbol == ShipSquare && it.player == player.other()}) { "Position has already been shot" }
    check (grid.count{it.player == player.other()} >= ShipType.values.sumOf{it.squares * it.fleetQuantity}) { "Opponent hasn't started yet" }
    val newGrid = grid.map {if (it.square.pos == pos && it.player == player.other()) it.copy(square = Square(it.square.pos, ShipShot)) else it}
    return copy(grid = newGrid).win(player)
}

fun Board.get(pos: Position, player: Player) : Grid? =
    grid.firstOrNull { it.square.pos === pos && player == it.player  }

fun Board.getShipBorder(pos : Position, type: ShipType, align : Direction) : List<Position>{
    var borderPositions = emptyList<Position>()
    val firstColumn = if(pos.column.ordinal == 0) 0 else -1
    val firstRow = if(pos.row.ordinal == 0) 0 else -1
    val cRange : Int
    val rRange : Int
    if (align == Direction.HORIZONTAL){
        cRange = if (pos.column.ordinal + type.squares == COLUMN_DIM) type.squares - 1 else type.squares
        rRange = if (pos.row.ordinal + type.squares == ROW_DIM) 0 else 1
   }
    else{
        cRange = if (pos.column.ordinal + type.squares == COLUMN_DIM ) 0 else 1
        rRange = if (pos.row.ordinal + type.squares == ROW_DIM) type.squares - 1 else type.squares
    }
    for(r in firstRow .. rRange){
        for (c in firstColumn .. cRange){
            borderPositions += Position[pos.column.ordinal + c,pos.row.ordinal + r]
        }
    }
    return borderPositions
}