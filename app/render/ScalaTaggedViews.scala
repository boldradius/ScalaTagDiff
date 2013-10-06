package render

import controllers.{Get, BrowserGetEvent, routes}
import models._
import play.api.libs.json.Json
import controllers.BrowserEvents._
import controllers.BrowserEventTypes._
import models.UISession
import controllers.Get
import tags._
import models.UI._
import tools.Logging._
import xml.Unparsed

object ScalaTaggedViews {

  implicit def xmlToString(t: HtmlTag): String = t.toXML.toString

  lazy val container = div.cls("container").id("container")

  def scalaLinkTag(route: Get, content: STag, buttonClass: String = "") = {
    a.href("#").cls("clickable "+buttonClass).attr("data-getaction" -> Json.toJson(route))(content)
  }


  def main: HtmlTag =
    html(
      head(
        title("ScalaTagDiff Demo"),
        link.rel("stylesheet").attr("media" -> "screen").href(routes.Assets.at("bootstrap/css/bootstrap.min.css")),
        link.rel("stylesheet").attr("media" -> "screen").href(routes.Assets.at("bootstrap/css/jumbotron-narrow.css")),
        link.rel("stylesheet").attr("media" -> "screen").href(routes.Assets.at("stylesheets/main.css")),
        script.ctype("text/javascript").src(routes.Assets.at("javascripts/require.js")),
        script(
          "require(['" + routes.Assets.at("javascripts/common.js") + "'], function (common) {require(['app/main']);});"
        ),
        body.id("body")(
          div.id("main").css("width" -> "100%", "height" -> "100%"),
          div.id("modal").cls("modal fade")()
        )
      )
    )

  def listItem(content: STag): HtmlTag = li(content)

  lazy val headerListItems: List[(Tab, HtmlTag)] =
    List(
    (TabLeafReplace,
        listItem(scalaLinkTag(Get(LeafReplace), "Simple Diff"))),
    (DeepDomDiff,
      listItem(scalaLinkTag(Get(DOMReplace), "Full Diff"))))


  def uiheader(ui: UISession): HtmlTag = {
    div.cls("header")(
      ul.cls("nav nav-pills pull-right")(
        headerListItems.map {
          case ((id, listItem)) => if (ui.activeTab == id)
            listItem.cls("active")
          else
            listItem
        }
      ),
      h3.cls("text-muted")("ScalaTagDiff Demo")
    )
  }

  def titleDescription(ui: UISession): HtmlTag =
    ui.activeTab match {
      case TabLeafReplace => div.L(h1("Leaf Replace"),div("Just Replace a chunck of DOM"),leafReplace(ui))
      case DeepDomDiff => div(h1("Full Diff"),div("Here we'll traverse deeper into dom tree, and render Diffs"),deepReplace(ui))
    }


  def leafReplace(ui: UISession):HtmlTag = div(
    div(
      span(scalaLinkTag(Get(FirstLeaf),"First Leaf")).css("padding-right"->"40px"),
      span(scalaLinkTag(Get(SecondLeaf),"Second Leaf"))
    ),
  {
      div.cls("row")(
        div(img.src(routes.Assets.at("/images/logo.gif"))),
        table.cls("table table-condensed")(
          thead(
            tr(
              th("COL1"),th("COL2"),th("COL3"),th("COL4"),th("COL5")
            )
          ),
        {
          val r = (1 until 10).map(i =>
            if(ui.leafDemo == FirstL)
              tr( td("This"),td("whole"),td("Table"),td(img.src(routes.Assets.at("/images/logo.gif")).width(30).height(30)),td("Replaced") )
            else if(ui.leafDemo == SecondL){
              if( i % 2 == 0)
                tr( td("This"),td("whole").css("background-color" -> "gray", "color" -> "white"),td("Table"),td(img.src(routes.Assets.at("/images/logo.gif")).width(30).height(30)),td("Replaced") )
              else
                tr( td("This").css("background-color" -> "gray", "color" -> "white"),
                  td("whole"),td("Table"),td(img.src(routes.Assets.at("/images/logo.gif")).width(30).height(30)),td("Replaced") )
            }

            else
              tr(td(),td(),td(),td(),td())
          )
          tbody(r.toList)

        }
        )
      )
  }

  )

  def deepReplace(ui:UISession):HtmlTag =  div(
  div(
    span(scalaLinkTag(Get(FirstDeepDom),"First ")).css("padding-right"->"40px"),
    span(scalaLinkTag(Get(SecondDeepDom),"Second"))
  ),
  {
    div.cls("col-lg-12")(
      div(img.src(routes.Assets.at("/images/logo.gif"))),
      table.cls("table table-condensed").width("auto")(
        thead(
          tr(
            th("COL1").width(150),th("COL2").width(150),th("COL3").width(150),th("COL4").width(100),th("COL5").width(100)
          )
        ),
        {
          val nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;"
          val r = (1 until 10).map(i =>
            if(ui.domDemo == FirstD)
              tr( td("Dom Dif", Unparsed(nbsp)),td("DOM"),td("Diff"),td(img.src(routes.Assets.at("/images/logo.gif")).width(30).height(30)),td("Result") )
            else if(ui.domDemo == SecondD)
              if( i % 2 == 0)
                tr( td("Dom Differ"),td("DOM").css("background-color" -> "gray", "color" -> "white"),
                  td("Diff"),td(img.src(routes.Assets.at("/images/logo.gif")).width(30).height(30)),td("Result") )
              else
                tr( td("Dom Diffest").css("background-color" -> "gray", "color" -> "white"),
                  td("DOM"),td("Diff"),td(img.src(routes.Assets.at("/images/logo.gif")).width(30).height(30).css("border"->"1px solid black")),td("Result") )
            else
              tr(td(),td(),td(),td(),td())
          )
          tbody(r.toList)

        }

      )
    )
  }

  )




  def content(ui: UISession): HtmlTag =
    div.cls("jumbotron")(titleDescription(ui))
//      div.cls("row")(
//        {
//          if(ui.activeTab == TabContact){
//            List(
//            div.cls("col-md-4 ")(img.src(routes.Assets.at("/images/logo.gif"))),
//            div.cls("col-md-4 bordered")(scalaLinkTag(Get(OpenModal),"Open modal")),
//            div.cls("col-md-4 bordered")(if (ui.activeTab == DeepDomDiff) "r3" else "r5"))
//          }else{
//            List(
//            div.cls("col-md-4 bordered")(img.src(routes.Assets.at("/images/logo.gif"))),
//            div.cls("col-md-4 bordered")(if (ui.activeTab == DeepDomDiff) "r3" else "r5"),
//            div.cls("col-md-4 bordered")(scalaLinkTag(Get(OpenModal),"Open modal")))
//          }
//        }
//    ),
//    div.cls("col-lg-12")(
//      form.id("testForm")(
//        div(
//          input.ctype("text").cls("form-control")()
//        )
//      )
//    ),
//    div.cls("col-lg-12")(
//    {
//      var contents =
//      if(ui.activeTab == TabContact){
//        div("CCCCCCCC",div("RRR"))
//      }else{
//        div("OTHER")
//      }
//    }



  def uiFooter(ui: UISession): HtmlTag = {
    val d = div("Footer").id("Footer")
    ui.activeTab match {
      case TabLeafReplace => d.css("color" -> "black")
      case DeepDomDiff => d.css("color" -> "blue")
    }

  }

  lazy val closeModal = "&times;"

  def modal(id:String, title:String):HtmlTag =
      div.id("modalDialog").cls("modal-dialog")(
        div.id("modalContent").cls("modal-content")(
          div.cls("modal-header")(
            button.ctype("button").cls("close")(scalaLinkTag(Get(CloseModal),Unparsed(closeModal))),
            h4.cls("modal-title")(title)
          )
        )
      )



//          <div class="modal-body">
//            <p>One fine body&hellip;</p>
//          </div>
//          <div class="modal-footer">
//            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
//            <button type="button" class="btn btn-primary">Save changes</button>
//          </div>
//        </div><!-- /.modal-content -->
//      </div><!-- /.modal-dialog -->
//    </div><!-- /.modal -->


}
