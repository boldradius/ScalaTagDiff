package utility

import play.api.{Play, LoggerLike, Logger}
import org.slf4j.LoggerFactory
import org.slf4j.impl.SimpleLoggerFactory

object TestLogger extends LoggerLike {

  val factory = if (Play.isTest(Play.current)) {
    new SimpleLoggerFactory()
  } else {
    LoggerFactory.getILoggerFactory
  }

  val redirectDebugToInfo = factory.isInstanceOf[SimpleLoggerFactory]

  val logger = factory.getLogger("application")

  def apply(name: String): Logger = new Logger(factory.getLogger(name))

  def apply[T](clazz: Class[T]): Logger = new Logger(factory.getLogger(clazz.getCanonicalName))

  // this method is to make debug statements to show up in test mode
  override def debug(m: => String) = {
    if (redirectDebugToInfo) {
      info(m)
    } else {
      super.debug(m)
    }
  }
}