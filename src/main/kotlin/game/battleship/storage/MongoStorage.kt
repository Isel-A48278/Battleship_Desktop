package battleship.storage

import battleship.model.Battleship
import battleship.model.*
import mongoDB.*

class MongoStorage(private val driver : MongoDriver) : Storage {
    private data class Doc(
        val _id: String,
        val turn: Player,
        val grid: List<GridStorage> = emptyList(),
        val winner: Player?
        )
    private data class GridStorage(
        val pos : String,
        val symbol : Char,
        val player : Player
    )

    private val col = driver.getCollection<Doc>("games")

    private fun createDoc(name: String, board: Board): Doc{
        var grid : List<GridStorage> = emptyList()
        board.grid.forEach{
           grid = grid + GridStorage(it.square.pos.toString(), it.square.symbol.representation, it.player)
        }
        return Doc(name, board.turn, grid, board.winner)
    }

    override fun start(name: String, board: Board): Player {
        val doc = col.getDocument(name)
        if (doc != null){
            if (!doc.grid.any{it.symbol != '#'}) return Player.B
            col.deleteDocument(name)
        }
        col.insertDocument(createDoc(name, board))
        return Player.A
    }

    override fun save(game: Battleship) {
        checkNotNull(game.name){"No game initiated"}
        col.replaceDocument(createDoc(game.name, game.board))
    }

    override fun load(game: Battleship) : Battleship {
        checkNotNull(game.name){"No game initiated"}
        val doc = col.getDocument(game.name)
        checkNotNull(doc) { "no document in load" }
        var newGrid : List<Grid> = emptyList()
        doc.grid.forEach{
            newGrid = newGrid + Grid(Square(it.pos.toPosition(), it.symbol.toShipPart()), it.player)
        }
        return game.copy(board = game.board.copy(grid = newGrid, winner = doc.winner, turn = doc.turn))
    }
}