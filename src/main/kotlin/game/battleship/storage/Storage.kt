package battleship.storage
import battleship.model.*

interface Storage {
    fun start(name: String, board: Board) : Player
    fun save(game: Battleship)
    fun load(game: Battleship) : Battleship
}