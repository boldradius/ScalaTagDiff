package tools

import shapeless.Lens

/**
 * Created by dave on 13-12-09.
 */
object Lenses {


  def lens[A,B](getter:A => B, setter: B => A => A ):shapeless.Lens[A,B] = {
    new Lens[A,B] {
      def get(a : A) = getter(a)
      def set(a : A)(b : B) : A = setter(b)(a)
    }
  }


}
