package battleship.model

/**
 * All ship types allowed in the game.
 * @property name Ship type name.
 * @property squares Number of squares occupied vertically or horizontally.
 * @property fleetQuantity Number of ships of this type in the starting fleet.
 */
class ShipType private constructor(val name: String, val squares:Int, val fleetQuantity:Int) {
    companion object {
        val values = listOf( ShipType("Carrier",5,1), ShipType("Battleship",4,2),
            ShipType("Cruiser",3,3), ShipType("Submarine",2,4) )
    }
}

fun String.toShipTypeOrNull() : ShipType? =
    when{
        first() in '2'..'5' -> ShipType.values.first {it.squares.toString() == first().toString()}
        first().uppercaseChar() in 'A'..'Z' ->
            ShipType.values.firstOrNull {it.name.take(2).uppercase() == take(2).uppercase()}
        else -> null
    }

fun String.toShipType() : ShipType =
    this.toShipTypeOrNull() ?: throw NoSuchElementException()

data class Ship(val type: ShipType, val positions: List<Position>)

fun Fleet.getShipFromPosition(pos: Position) : Ship? =
    ships.firstOrNull{ it.positions.contains(pos) }

class Fleet(val ships : List<Ship>)

abstract class ShipPart{
    open val representation = ' '
}

object ShipSquare : ShipPart(){
    override val representation = '#'
}

object ShipSunk : ShipPart(){
    override val representation = 'X'
}

object ShipShot : ShipPart(){
    override val representation = '*'
}

object MissedShot : ShipPart(){
    override val representation = 'O'
}

fun Char.toShipPart() : ShipPart =
    when(this){
        '#' -> ShipSquare
        'X' -> ShipSunk
        '*' -> ShipShot
        else -> MissedShot
    }