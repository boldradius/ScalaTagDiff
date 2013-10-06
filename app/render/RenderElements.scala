package render

import models.{UISession}
import render.ScalaTaggedViews._
import tags.Elements._
import tags._


object RenderElements {



  def renderSession(session:UISession):Window = {
    Window(div.id("top").cls("container")(
      uiheader(session),
      content(session),
      uiFooter(session)
    ), renderModal(session),None)
  }

  def renderModal(session:UISession):Option[Modal] =
  if(session.modalVisible){
    val id = "testModal"
    Some(Modal(400,id, modal(id,"Test Modal")))
  } else None




}
