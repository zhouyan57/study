package xieyuheng.adventure.untyped

import xieyuheng.adventure.util._

import scala.annotation.tailrec

case class Ds(list: List[Val] = List()) {

  def empty_p(): Boolean = {
    list.length == 0
  }

  def toc(): Option[Val] = {
    if (list.length == 0) {
      None
    } else {
      Some(list(0))
    }
  }

  def drop(): Ds = {
    Ds(list.tail)
  }

  def push(value: Val): Ds = {
    Ds(value :: list)
  }

}

case class Frame(index: Int, list: List[Jo], env: Env)

case class Rs(list: List[Frame] = List()) {

  def empty_p(): Boolean = {
    list.length == 0
  }

  def toc(): Option[Frame] = {
    if (list.length == 0) {
      None
    } else {
      Some(list(0))
    }
  }

  def drop(): Rs = {
    Rs(list.tail)
  }

  def push(frame: Frame): Rs = {
    Rs(frame :: list)
  }

  def toc_ext(name: String, value: Val): Rs = {
    val frame = list.head
    val new_env = frame.env.ext_let(name, value)
    Rs(frame.copy(env = new_env) :: list.tail)
  }

  def next(): Rs = {
    val frame = list.head
    this.drop().push(frame.copy(index = frame.index + 1))
    // if (frame.index == frame.list.length - 1) {
    //   // tail call
    //   drop()
    // } else {
    //   drop().push(frame.copy(index = frame.index + 1))
    // }
  }

}

object exe {

  @tailrec
  def run(ds: Ds, rs: Rs): Either[Err, Ds] = {
    if (rs.empty_p()) {
      Right(ds)
    } else {
      step(ds, rs) match {
        case Right((ds, rs)) => run(ds, rs)
        case Left(err) => Left(err)
      }
    }
  }

  def step(ds: Ds, rs: Rs): Either[Err, (Ds, Rs)] = {
    rs.toc() match {
      case Some(frame) =>
        if (frame.index == frame.list.length) {
          Right(ds, rs.drop())
        } else {
          val jo = frame.list(frame.index)
          exe(ds, rs.next(), frame.env, jo)
        }
      case None =>
        Left(Err(
          s"[step fail]\n" ++
            s"stack underflow\n"
        ))
    }
  }

  def exe(ds: Ds, rs: Rs, env: Env, jo: Jo): Either[Err, (Ds, Rs)] = {
    jo match {
      case Var(name: String) =>
        env.lookup_val(name) match {
          case Some(jojo: ValJoJo) =>
            Right(ds, rs.push(Frame(0, jojo.list, jojo.env)))
          case None =>
            Left(Err(
              s"[exe fail]\n" ++
                s"undefined name: ${name}\n"
            ))
        }
      case Let(name: String) =>
        ds.toc() match {
          case Some(value) =>
            Right(ds.drop(), rs.toc_ext(name, value))
          case None =>
            Left(Err(
              s"[exe fail]\n" ++
                s"stack underflow\n"
            ))
        }
      case JoJo(list: List[Jo]) =>
        Right(ds.push(ValJoJo(list, env)), rs)
      case Define(name: String, jojo: JoJo) =>
        Right(ds, rs.toc_ext(name, ValJoJo(jojo.list, env)))
    }
  }

}
