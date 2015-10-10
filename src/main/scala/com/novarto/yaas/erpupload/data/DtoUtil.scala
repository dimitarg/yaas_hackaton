package com.novarto.yaas.erpupload.data

object DtoUtil {

  def makeCategoryTree[T](input: Seq[Category]) : Seq[Category] = {
    
    val byId = input.map(cat => (cat.id, cat)).toMap

    val addedChildren = collection.mutable.Set[Category]()
    byId.foreach { tuple =>
      val cat = tuple._2
      byId.get(cat.parentCategoryId) match {
        case Some(parentCat) =>
          parentCat.nodes += cat
          addedChildren += cat
        case _ =>
      }
    }

    val withChildrenRemoved = byId.filter(tuple => !addedChildren.contains(tuple._2))

    val seq = withChildrenRemoved.map(x => x._2)
    seq.toSeq.sortBy(_.id)
  }
  
  
  type CategoryOrItem = WithMedia with Describable
  
  def appendChildren [A<:CategoryOrItem,B] (items: Seq[A], children: Seq[B], add: (A,B)=>Unit, getParentId: B=>String) : Unit = {
    val itemsById = items.groupBy(_.id)
    children.foreach { child =>
      itemsById(getParentId(child)).foreach { item =>
        add(item, child)
      }
    }
  }

  private def addMedia(item: CategoryOrItem, m: Multimedia) : Unit = item.media += m
  private def addText(item: Category, t: CatalogText) : Unit = item.texts += t
  private def mediaParentId(m: Multimedia) : String  = m.itemId
  private def textParentId(t: CatalogText) : String = t.itemId

  def appendMedia(items: Seq[CategoryOrItem], medias: Seq[Multimedia]): Unit = appendChildren(items, medias, addMedia, mediaParentId)
  
  def appendTexts(items: Seq[Category], texts: Seq[CatalogText]) : Unit = appendChildren(items, texts, addText, textParentId)

}

