package tools

import play.api.Logger


/**
 * @author patrick@tindr.ca
 */
case class Logging[V](v : V) {
  def logInfo(f: V => String) : V = {Logger.info(f(v)); v}
  def logDebug(f: V => String) : V = {Logger.debug(f(v)); v}
  def logError(f: V => String) : V = {Logger.error(f(v)); v}
}


object Logging {
  def withValue[V](v : V)(f: V => Unit) = {f(v); v}
  implicit def toLogging[V](v: V) : Logging[V] = Logging(v)
  
  def time[A](f: => A) : (A, Long) = {val s = System.currentTimeMillis; val v : A = f; (v, System.currentTimeMillis - s)}
  def logTime[A](show: Long => String)(f: => A) : A = time(f).logInfo{case (_, t) => show(t)}._1
}