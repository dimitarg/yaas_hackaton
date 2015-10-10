package com.novarto.yaas.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

object ProductTransformer {

  case class Id(val id:String) extends AnyVal

  implicit val idReader:Reads[Id] = (__ \ "id").read[String].map { name => Id(name) }


}
