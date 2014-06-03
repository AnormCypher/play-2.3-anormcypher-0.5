package controllers

import play.api._
import play.api.mvc._
import org.anormcypher._

object Application extends Controller {

  def create = Action {
    var id:Long = -1
    val stmt = Cypher("CREATE (n:Node {play:true, createdAt:timestamp()}) RETURN id(n)")
    stmt().foreach { row =>
      Logger.info("created node with id: " + row[Long]("id(n)"))
      id = row[Long]("id(n)")
    }
    Redirect(routes.Application.index()).flashing(
      "success" -> ("The node has been created with id: " + id),
      "query" -> stmt.query
    )
  }

  def index = Action {implicit request =>
    val query = Cypher("MATCH (n:Node) where has(n.play) \n" +
                       "RETURN id(n), n.play, n.createdAt")

    val results:List[(Long,Boolean,Long)] = query().map(row => (row[Long]("id(n)"), row[Boolean]("n.play"), row[Long]("n.createdAt"))).toList
    Ok(views.html.index(query.query, results))
  }

}