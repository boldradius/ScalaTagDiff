package models

import tags.STag
import tools.Lenses._
import controllers.Application.ClientSession

sealed trait Tab
case object TabLeafReplace extends Tab
case object DeepDomDiff extends Tab

sealed trait LeafDemo
case object FirstL extends LeafDemo
case object SecondL extends LeafDemo

sealed trait DOMDemo
case object FirstD extends DOMDemo
case object SecondD extends DOMDemo


case class UISession(activeTab:Tab = TabLeafReplace,
                     modalVisible:Boolean = false,
                     leafDemo:LeafDemo = FirstL,
                      domDemo:DOMDemo  = FirstD)


object UI {

  implicit def tabToSTag(t:Tab):STag = t.toString
  implicit def tabToString(t:Tab):String = t.toString


  lazy val uiLens:shapeless.Lens[ClientSession,UISession] =
    lens[ClientSession,UISession](_.ui, b => _.copy(ui = b))

  lazy val activeTabLens:shapeless.Lens[UISession,Tab] =
    lens[UISession,Tab](_.activeTab, b => _.copy(activeTab = b))

  lazy val uiActiveTabLens:shapeless.Lens[ClientSession,Tab] = activeTabLens compose uiLens



//  val activeTabLens:shapeless

}
