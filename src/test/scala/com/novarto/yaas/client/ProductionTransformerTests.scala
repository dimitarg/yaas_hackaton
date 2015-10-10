package com.novarto.yaas.client

import org.scalatest.{FunSuite, FunSpec}
import ProductTransformer._
import play.api.libs.json._
import play.api.libs.functional.syntax._

class ProductionTransformerTests extends FunSuite {

  val js = Json.parse(
    """
      |[
      |    {
      |        "id": "56094ccff5ce7b36a5256ffe",
      |        "sku": "001001",
      |        "name": "A Shrubbery",
      |        "description": "A shrubbery. One that looks nice. And not too expensive",
      |        "published": true,
      |        "metadata": {
      |            "createdAt": "2015-09-28T14:21:03.449+0000",
      |            "modifiedAt": "2015-09-28T14:23:46.546+0000",
      |            "version": 4,
      |            "schema": "https://api.yaas.io/hybris/schema/b2/hybriscommerce/Product.json",
      |            "mixins": {
      |                "inventory": "https://api.yaas.io/hybris/schema/b2/hybriscommerce/inventorySchema-v1",
      |                "taxCodes": "https://api.yaas.io/hybris/schema/b2/hybriscommerce/productTaxCodeMixin-1.0.0"
      |            }
      |        },
      |        "media": [
      |            {
      |                "id": "56094cd1bfae28df9db660dc",
      |                "url": "https://api.yaas.io/hybris/media/b2/novartohelloyaas/hybris.product/media/56094cd1bfae28df9db660dc",
      |                "stored": true,
      |                "customAttributes": {
      |                    "main": true
      |                }
      |            }
      |        ],
      |        "mixins": {
      |            "inventory": {
      |                "inStock": true
      |            }
      |        },
      |        "customAttributes": []
      |    },
      |    {
      |        "id": "testytesty",
      |        "sku": "001001",
      |        "name": "A Shrubbery",
      |        "description": "A shrubbery. One that looks nice. And not too expensive",
      |        "published": true,
      |        "metadata": {
      |            "createdAt": "2015-09-28T14:21:03.449+0000",
      |            "modifiedAt": "2015-09-28T14:23:46.546+0000",
      |            "version": 4,
      |            "schema": "https://api.yaas.io/hybris/schema/b2/hybriscommerce/Product.json",
      |            "mixins": {
      |                "inventory": "https://api.yaas.io/hybris/schema/b2/hybriscommerce/inventorySchema-v1",
      |                "taxCodes": "https://api.yaas.io/hybris/schema/b2/hybriscommerce/productTaxCodeMixin-1.0.0"
      |            }
      |        },
      |        "media": [
      |            {
      |                "id": "56094cd1bfae28df9db660dc",
      |                "url": "https://api.yaas.io/hybris/media/b2/novartohelloyaas/hybris.product/media/56094cd1bfae28df9db660dc",
      |                "stored": true,
      |                "customAttributes": {
      |                    "main": true
      |                }
      |            }
      |        ],
      |        "mixins": {
      |            "inventory": {
      |                "inStock": true
      |            }
      |        },
      |        "customAttributes": []
      |    }
      |]
    """.stripMargin)

  test("can parse numbers") {
    assert(js.validate[List[Id]] == JsSuccess(List(Id("56094ccff5ce7b36a5256ffe"), Id("testytesty"))))

  }

}
