package xieyuheng.unificationless

import scala.collection.immutable.ListMap

sealed trait Exp

final case class Var(name: String) extends Exp

final case class Type() extends Exp

final case class Pi(arg: Exp, ret: Exp) extends Exp
final case class Fn(arg: Exp, ret: Exp, body: Exp) extends Exp
final case class Ap(target: Exp, arg: Exp) extends Exp

final case class Club(
  name: String,
  fileds: ListMap[String, (Exp, Option[Exp])],
  members: List[Member],
) extends Exp
final case class Choice(target: Exp, map: Map[String, Exp]) extends Exp

final case class Member(
  name: String,
  fileds: ListMap[String, (Exp, Option[Exp])],
  clubName: String,
) extends Exp

final case class GetField(target: Exp, fieldName: String) extends Exp
final case class GetFieldType(target: Exp, fieldName: String) extends Exp

final case class RefineField(target: Exp, fieldName: String, arg: Exp) extends Exp
final case class FillField(target: Exp, fieldName: String, arg: Exp) extends Exp
final case class Refine(target: Exp, arg: Exp) extends Exp