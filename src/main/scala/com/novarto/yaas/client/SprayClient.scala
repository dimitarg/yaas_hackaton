package com.novarto.yaas.client

/**
 * Created by fmap on 09.10.15.
 */

import akka.actor.ActorSystem
import spray.http.HttpHeaders.Authorization
import spray.http.Uri.{Path, Host, Authority, Query}
import spray.http._
import spray.client.pipelining._

import scala.concurrent.Future
import com.novarto.yaas.client.Codecs._
import spray.json._

class SprayClient(implicit val actorSystem: ActorSystem) {

  val baseUri = "https://api.yaas.io/hybris/"

  import actorSystem.dispatcher

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  def token(clientId: String, clientSecret: String) : Future[Token] = {

    val request = Post(baseUri+"oauth2/b1/token", FormData(Seq(
      ("grant_type","client_credentials") , ("client_id",clientId) , ("client_secret", clientSecret),
        ("scope", "hybris.customer_read hybris.customer_edit_profile hybris.product_create hybris.customer_create")
    )))
    pipeline(request).map(resp=>resp.entity.data.asString.parseJson.convertTo[Token]);

  }

  def getExistingProductIds(token: Token, tenant: String)  : Future[Seq[String]] = {

    val query = Query(("fields","code"))
    val uri =  Uri("https",Authority(Host("api.yaas.io")),
      Path(s"/hybris/product/b1/${tenant}/products"), query)
    println(uri.toString())
    val request = Get(uri).withHeaders(Authorization(OAuth2BearerToken(token.access_token)))
    pipeline(request).map(resp=>resp.entity.data.asString).map(str => {
      str.parseJson match {
        case arr: JsArray => {
          arr.elements.map( jsVal => jsVal match {
            case obj: JsObject => obj.fields("id").toString()
          })
        }
      }
    })
  }

  def createCustomer(token: Token, tenant: String): Unit = {
    s"customer/b1/${tenant}"
  }


}
