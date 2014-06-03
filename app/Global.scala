import play.api._
import org.anormcypher._
import java.net.URL

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("configuring Neo4j for " + System.getenv("GRAPHENEDB_URL"))
    val url = new URL(System.getenv("GRAPHENEDB_URL"))
    // testing locally don't have user/pass
    if(url.getUserInfo != null) {
      val Array(user:String, pass:String) = url.getUserInfo.split(":")
      Neo4jREST.setServer(url.getHost, url.getPort, "/db/data/", user, pass)
    } else {
      Neo4jREST.setServer(url.getHost, url.getPort, "/db/data/")
    }
  }

}