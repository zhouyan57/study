package xieyuheng.curry

import scala.annotation.tailrec

object eval {

  def free_variables(exp: Exp): Set[String] = {
    exp match {
      case Var(name) =>
        Set(name)
      case Ap(target, arg) =>
        free_variables(target) ++ free_variables(arg)
      case Fn(arg_name, body) =>
        free_variables(body) - arg_name
    }
  }

  def free_variable_p(name: String, exp: Exp): Boolean = {
    free_variables(exp).contains(name)
  }

  def subst(body: Exp, arg_name: String, arg: Exp): Exp = {
    body match {
      case Var(name: String) =>
        if (arg_name == name) {
          arg
        } else {
          body
        }
      case Ap(target: Exp, arg2: Exp) =>
        Ap(
          subst(target, arg_name, arg),
          subst(arg2, arg_name, arg))
      case Fn(arg_name2: String, body2: Exp) =>
        if (arg_name == arg_name2) {
          body
        } else {
          Fn(
            arg_name2,
            subst(body2, arg_name, arg))
        }
    }
  }

  def beta_step(exp: Exp): Exp = {
    exp match {
      case Var(name: String) =>
        exp
      case Ap(Fn(arg_name: String, body: Exp), arg: Exp) =>
        subst(body, arg_name, arg)
      case Ap(target: Exp, arg: Exp) =>
        val target2 = beta_step(target)
        if (target2 == target) {
          val arg2 = beta_step(arg)
          Ap(target, arg2)
        } else {
          Ap(target2, arg)
        }
      case Fn(arg_name: String, body: Exp) =>
        Fn(arg_name, beta_step(body))
    }
  }

  @tailrec
  def reduction_from_step(step: Exp => Exp, exp: Exp): Exp = {
    val exp2 = step(exp)
    if (exp == exp2) {
      exp
    } else {
      reduction_from_step(step, exp2)
    }
  }

  def beta_reduction(exp: Exp): Exp = {
    reduction_from_step(beta_step, exp)
  }

  def eta_step(exp: Exp): Exp = {
    exp match {
      case Var(name: String) =>
        exp
      case Ap(target: Exp, arg: Exp) =>
        val target2 = eta_step(target)
        if (target2 == target) {
          val arg2 = eta_step(arg)
          Ap(target, arg2)
        } else {
          Ap(target2, arg)
        }
      case Fn(arg_name, Ap(target, Var(name))) =>
        if (arg_name == name && free_variable_p(name, target)) {
          target
        } else {
          Fn(arg_name, Ap(eta_step(target), Var(name)))
        }
      case Fn(arg_name, body) =>
        Fn(arg_name, eta_step(body))
    }
  }

  def eta_reduction(exp: Exp): Exp = {
    reduction_from_step(eta_step, exp)
  }

  def beta_eta_step(exp: Exp): Exp = {
    val exp2 = beta_step(exp)
    if (exp2 == exp) {
      eta_step(exp)
    } else {
      exp2
    }
  }

  def beta_eta_reduction(exp: Exp): Exp = {
    reduction_from_step(beta_eta_step, exp)
  }

  def expend_global_variables(
    exp: Exp,
    global: Map[String, Exp],
    bound_variables: Set[String],
  ): Exp = {
    exp match {
      case Var(name) =>
        if (bound_variables.contains(name)) {
          Var(name)
        } else {
          global.get(name) match {
            case Some(exp) => exp
            case None =>
              println(s"[expend_global_variables fail]")
              println(s"undefined name: ${name}")
              throw new Exception()
          }
        }
      case Ap(target, arg) =>
        Ap(
          expend_global_variables(target, global, bound_variables),
          expend_global_variables(arg, global, bound_variables))
      case Fn(arg_name, body) =>
        Fn(
          arg_name,
          expend_global_variables(body, global, bound_variables + arg_name))
    }
  }

}
