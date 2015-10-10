package com.novarto.yaas.erpupload

import java.io.File
import scala.xml.NodeSeq
import scala.xml.Node
import com.novarto.yaas.erpupload.data._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.blocking

object CatalogXmlProcessor {

  def parseCatalog(f: File)(implicit exCtx: ExecutionContext): Future[Result] = Future {
    

    val root = blocking {scala.xml.XML.loadFile(f)}
    val catalogRootNodeSeq = (root \ "Catalog")
    val catalogRootElem = (root \ "Catalog")

    val catalog = catalogRootNodeSeq.map { c =>
      Catalog(c \ "CatalogID" text, c \ "Description" text, c \ "DefaultLanguage" text, c \ "Currency" text, c \ "UOM" text)
    }.head

    val categories = catalogRootElem \ "Category" map { categoryNode =>
      Category(
        id = categoryNode \ "CategoryID" text,
        description = categoryNode \ "Description" text,
        catalogId = catalog.id,
        parentCategoryId = {
          val parentCatId = categoryNode \ "ParentCategoryID"
          if (parentCatId == NodeSeq.Empty) "" else parentCatId text
        })
    }
    
    

    val catalogItems = catalogRootElem \ "Product" map { catalogItem =>
      CatalogItem(
        id = catalogItem \ "CatalogKey" text,
        description = catalogItem \ "Description" text,
        catalogId = catalog.id,
        parentCategoryId = catalogItem \ "ParentCategoryID" text,
        productId = catalogItem \ "ProductID" text,
        uom = catalogItem \ "BasicUnitOfMeasure" text,
        listPrice = (catalogItem \ "VendorDescription" \ "ProductPrice" \ "Price" text).trim(),
        listPriceCurr = catalogItem \ "VendorDescription" \ "ProductPrice" \ "Price" \ "@Currency" text,
        listPriceQuantity = (catalogItem \ "VendorDescription" \ "ProductPrice" \ "Quantity" text).trim())
    }

    val catItemAttributes = catalogRootElem \ "Product" map { catItem =>
      catItem \ "Attribute" map { attr =>
        CatItemAttribute(
          attrId = attr \ "Key" text,
          attrName = attr \ "Name" text,
          catalogId = catalog.id,
          parentCategoryId = catItem \ "ParentCategoryID" text,
          productId = catItem \ "ProductID" text,
          catItemGuid = catItem \ "CatalogKey" text,
          attrValue = attr \ "Value" text,
          attrValueKey = attr \ "ValueKey" text)
      }
    }

    val attributesFlat = catItemAttributes.flatten

    def prod_prodIdSelector = { prod: Node => prod \ "CatalogKey" text }
    def prod_catIdSelector = { prod: Node => prod \ "ParentCategoryID" text }
    def cat_categoryIdSelector = { cat: Node => cat \ "CategoryID" text }

    def collectMultimedia(idSelector: (Node => String), categoryIdSelector: (Node => String), aItemType: MultiMediaOwnerType)(categoryOrPorduct: Node) =
      Seq(Thumbnail, Picture, Attachment) map { elemKind =>
        categoryOrPorduct \ elemKind.toString map { multimediaElem =>
          Multimedia(
            catalogId = catalog.id,
            itemId = idSelector(categoryOrPorduct),
            categoryId = categoryIdSelector(categoryOrPorduct),
            path = {
              val rawPath = multimediaElem \ "URL" text
              val result = rawPath.substring(rawPath.lastIndexOf('/') + 1)
              result
            },
            mime = multimediaElem \ "MimeType" text,
            kind = elemKind,
            itemType = aItemType)
        }
      }

    val itemMedia = catalogRootElem \ "Product" map collectMultimedia(prod_prodIdSelector, prod_catIdSelector, CatItemType)
    val categoryMedia = catalogRootElem \ "Category" map collectMultimedia(cat_categoryIdSelector, cat_categoryIdSelector, CategoryType)

    def collectCategAttr(aCatalogId: String) (category: Node) : Seq[CategoryAttribute] = {
        val CategoryId = cat_categoryIdSelector(category)
        
        category \ "CategoryAttribute" map { attribute =>
        val key = attribute \ "Key" text
        val name = attribute \ "Name" text
        val attrType = attribute \ "FreeText" text
        
        CategoryAttribute(attrId = key, attrName = name, attrType = attrType, catalogId = aCatalogId, categoryId = CategoryId)
      }
    }
    val categoryAttributes = catalogRootElem \ "Category" flatMap collectCategAttr(catalog.id)

    val itemMediaFlat = itemMedia.flatten.flatten
    val categoryMediaFlat = categoryMedia.flatten.flatten
    
    def collectTexts (idSelector: (Node=> String), categoryIdSelector : (Node=> String), aItemType: MultiMediaOwnerType)(textTagName: String) (categoryOrPorduct: Node) = categoryOrPorduct \ textTagName map { textNode =>
      CatalogText(catalogId=catalog.id,categoryId=categoryIdSelector(categoryOrPorduct),itemId=idSelector(categoryOrPorduct),itemType=aItemType,
        textId=textNode \ "@TextID" text, text= textNode text)
    }
    
    //todo collect texts
    val itemTexts = catalogRootElem \ "Product" map collectTexts(prod_prodIdSelector, prod_catIdSelector, CatItemType) ("CatalogText") flatten
    val categoryTexts = catalogRootElem \ "Category" map collectTexts(cat_categoryIdSelector, cat_categoryIdSelector,  CategoryType) ("Text") flatten
 
    Result(catalog, categories, categoryAttributes, catalogItems, attributesFlat, categoryMediaFlat ++ itemMediaFlat, itemTexts++categoryTexts)

  } (exCtx)

}