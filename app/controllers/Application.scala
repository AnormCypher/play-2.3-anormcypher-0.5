package controllers

import play.api._
import play.api.mvc._
import org.anormcypher._

object Application extends Controller {

  def create = Action { request =>
    var id:Long = -1
    val remote = request.remoteAddress.toString
    val stmt = Cypher("CREATE (n:Node {play:true, createdAt:timestamp(), ip:{ip}}) RETURN id(n)").on("ip" -> remote)
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
    val query = Cypher("MATCH (n:Node) \n" +
                       "WHERE n.createdAt > timestamp() - 1000 * 60 * 60 \n" +
                       "RETURN id(n), n.play, n.createdAt, n.ip")

    val results:List[(Long,Boolean,Long,String)] = query().map(row => (row[Long]("id(n)"), row[Boolean]("n.play"), row[Long]("n.createdAt"), row[String]("n.ip"))).toList
    Ok(views.html.index(query.query, results))
  }

}