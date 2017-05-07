package forms

import views.html

object UIKitFieldConstructor {
  import views.html.helper.FieldConstructor
  implicit val fields = FieldConstructor(html.uikitFieldConstructor.f)
}
