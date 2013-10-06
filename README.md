ScalaTagDiff
============


ScalaTagDiff is a Play Project set up to demo an HTML Diff approach to rendering DOM
in single page Javascript apps.

It uses a fork of scalatags (https://github.com/lihaoyi/scalatags), an amazing, small library
used for XML/HTML construction.


This project also demos client state maintained on the server with a scalaz MVar.

ScalaTagDiff is as much a thought experiment as it is a possible approach to html rendering.

Requires Play 2.1.1 (Scala)


The skinny:


On each request, the new GUI is calculated (Event => State => UISession) :

```scala
def processBrowserGetEvent(event: Get): State => UISession = {
    s => event match {
      case Get(et, id) => et match {
        case LeafReplace => s.ui.copy(activeTab = TabLeafReplace)
        case DOMReplace => s.ui.copy(activeTab = DeepDomDiff)
        ...
      }
    }
  }
```

Then this state is rendered to an HTMLTag, eg.

```scala
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
      h3.cls("text-muted")("TagDiff Demo")
    )
  }
```

THis HTMLTag is compared to a rendering of the previous tag, and a TagDiff is
sent to browser to be rendered with a javascript function.

```scala

case class Window(main: HtmlTag, modal: Option[Modal], focus: Option[String])
  case class WindowDiff(main: Diff, modal: Either[Option[Modal], ModalDiff], focus: Option[String])
  case class Modal(width: Int, id:String, contents: HtmlTag)
  case class ModalDiff(contents: Option[Diff])
  
type Diff = Either[STag, Option[TagDiff]]

case class TagDiff(
                      attrDiff: Option[List[(String,String)]] = None,
                      classesDiff: Option[List[String]] = None,
                      cssDiff: Option[List[(String, String)]] = None,
                      diffChildren: List[Diff] = Nil)
  
  
```

The idea is that the GUI dev gets to create and maintain html with scala (and scalatags instead
of Play Templates), and never has to write anymore javascript to do fancypants DOM rendering (eg.
change a class in a div deep in the DOM tree without re-rendering the whole tree)


The project also demos a way to pass JSON to a route in a structured way using Play's PathBindable:

```scala
implicit def getRoutePathGetEventBinder(implicit stringBinder: PathBindable[String]) = new PathBindable[BrowserGetEvent] {
    override def bind(key: String, value: String): Either[String, BrowserGetEvent] =
       stringBinder.bind(key, value).right.map(s => J.fromJson[BrowserGetEvent](J.parse(decode(s, "UTF-8"))).asEither.left.map(_.toString)).joinRight
    override def unbind(key: String, value: BrowserGetEvent): String =
     stringBinder.unbind(key, encode(J.stringify(J.toJson(value)), "UTF-8"))
  }
```



Created by Dave Sugden and Patrick Premont
dave@tindr.ca
patrick@tindr.ca




