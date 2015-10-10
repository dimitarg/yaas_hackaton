package com.novarto.yaas.erpupload.data

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

import scala.collection.mutable.{ListBuffer, Map}

case class Result(catalog: Catalog, categories: Seq[Category], categoryAttributes: Seq[CategoryAttribute], catItems: Seq[CatalogItem], attributes: Seq[CatItemAttribute], media: Seq[Multimedia],
  texts: Seq[CatalogText])

case class Catalog(val id: String, val description: String, val language: String, val currency: String, val unitOfMeasure: String, 
		val multimediaRootUrl: String = "", val status: Option[String] = None) extends Describable


case class Category(val id: String, val description: String, val catalogId: String,
  val parentCategoryId: String = "") extends Describable with CatalogTreeItem with WithMedia {

  val nodes = new ListBuffer[Category]
  val media = new ListBuffer[Multimedia]
  val texts = new ListBuffer[CatalogText]

}

case class CategoryAttribute(val attrId: String, val attrName: String, val attrType: String, val catalogId: String, val categoryId: String) {
}

case class CatalogItem(val id: String, val description: String, val catalogId: String, val parentCategoryId: String, val productId: String,
						val uom: String, val listPrice: String, val listPriceCurr: String, val listPriceQuantity: String)
  extends Describable with CatalogTreeItem with WithMedia {

  val media = new ListBuffer[Multimedia]
  val texts = new ListBuffer[CatalogText]
}

case class CatItemAttribute(val attrId: String, val attrName: String, val catalogId: String,
  val parentCategoryId: String, val productId: String, val catItemGuid: String, val attrValue: String, val attrValueKey: String) extends CatalogTreeItem {
}

sealed trait MultiMediaOwnerType
case object CategoryType extends MultiMediaOwnerType
case object CatItemType extends MultiMediaOwnerType

sealed trait MultiMediaKind
case object Thumbnail extends MultiMediaKind
case object Picture extends MultiMediaKind
case object Attachment extends MultiMediaKind


case class Multimedia(val catalogId: String, val itemId: String, val categoryId: String, val path: String, val mime: String,
                      val kind: MultiMediaKind, val itemType: MultiMediaOwnerType)


case class CatalogText(val catalogId: String, val categoryId: String, val itemId: String, val itemType: MultiMediaOwnerType, val textId: String, val text: String)


trait Describable {
  val id: String
  val description: String
}

trait CatalogTreeItem {
  val catalogId: String
  val parentCategoryId: String
}

trait WithMedia {
  val media: ListBuffer[Multimedia]
  val texts: ListBuffer[CatalogText]
}

