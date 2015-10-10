package com.novarto.yaas.client

import spray.json.DefaultJsonProtocol

/**
 * Created by fmap on 09.10.15.
 */
object Codecs extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(Token)
}
