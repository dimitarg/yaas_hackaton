package com.novarto.yaas

import akka.actor.ActorSystem
import com.novarto.yaas.client.SprayClient
import com.novarto.yaas.erpupload.CatalogXmlProcessor
import com.novarto.yaas.erpupload.data._
import java.io.File
import spray.http.Uri
import spray.http.Uri.{Path, Host, Authority, Query}
import spray.routing.SimpleRoutingApp

import scala.concurrent.duration._
import com.novarto.yaas.client._

import scala.concurrent.{Future, Await, ExecutionContext}

/**
 * Created by fmap on 09.10.15.
 */
object Main  extends App with SimpleRoutingApp {
//  def main(args: Array[String]) : Unit = {
//
//
//    val clientId = "DuMblkWl5foFpteAWIrrSmaQRdQqKTdg"
//    val clientSecret = "a8hMEAwGM8di6nMo"
//
//    implicit val system = ActorSystem()
//    implicit val exCtx = system.dispatcher
//    val file: File = new File(args(0))
//    val fut: Future[Result] = CatalogXmlProcessor.parseCatalog(file)
//
//    val result = Await.result(fut, 10 seconds)
//    println(result)
//
//    val sc: SprayClient = new SprayClient()
//    val token : Token = Await.result(sc.token(clientId,clientSecret), 10 seconds)
//
//    val pr = Await.result(sc.getExistingProductIds(token,"novartohelloyaas"), 10 seconds)
//
//
//    println(pr)
//
//    println(token)
//  }

    implicit val system = ActorSystem("my-system")

    startServer(interface = "localhost", port = 8080) {
      path("hello") {
        get {
          complete {
            <h1>Say hello to spray</h1>
          }
        }
      }
    }

  sys.addShutdownHook({Await.result(system.terminate(), Duration.Inf)})



}
