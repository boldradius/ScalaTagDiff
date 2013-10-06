package models

import tags.STag

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

}
