package controllers

object BrowserEventTypes extends Enumeration {
  type BrowserEventType = Value
  val INIT,
  LeafReplace,
  DOMReplace,
  About,
  Contact,
  OpenModal,
  CloseModal,
  FirstLeaf,
  SecondLeaf,
  FirstDeepDom,
  SecondDeepDom,
  ThirdDeepDom
  = Value
}
