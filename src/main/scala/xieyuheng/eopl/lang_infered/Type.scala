package xieyuheng.eopl.lang_infered

sealed trait Type
final case class TypeVar(serial: Int, aka: Option[String]) extends Type
final case class TypeInt() extends Type
final case class TypeBool() extends Type
final case class TypeSole() extends Type
final case class TypeArrow(arg_t: Type, ret_t: Type) extends Type
