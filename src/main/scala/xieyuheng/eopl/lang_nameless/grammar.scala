package xieyuheng.eopl.lang_nameless

import xieyuheng.partech._
import xieyuheng.partech.ruleDSL._
import xieyuheng.partech.predefined._

object grammar {

  val lexer = Lexer.default

  def preserved: List[String] = List(
    "diff", "zero_p",
    "if",
    "let",
    "sole", "do",
    "assert_eq", "show",
  )

  def identifier = identifier_with_preserved("identifier", preserved)

  def exp: Rule = Rule(
    "exp", Map(
      "var" -> List(identifier),
      "num" -> List(digit),
      "minus_num" -> List("-", digit),
      "diff" -> List("diff", "(", exp, ",", exp, ")"),
      "zero_p" -> List("zero_p", "(", exp, ")"),
      "if" -> List("if", exp, "{", exp, "}", "else", "{", exp, "}"),
      "let" -> List("let", identifier, "=", exp, exp),
      "fn" -> List("(", identifier, ")", "=", ">", exp),
      "ap" -> List(exp, "(", exp, ")"),
      "block_one" -> List("{", exp, "}"),
      "sole" -> List("sole"),
      "do" -> List("do", exp, exp),
      "assert_eq" -> List("assert_eq", "(", exp, ",", exp, ")"),
      "show" -> List("show", "(", exp, ")"),
    ))

  def exp_matcher: Tree => Exp = Tree.matcher[Exp](
    "exp", Map(
      "var" -> { case List(Leaf(name)) =>
        Var(name) },
      "num" -> { case List(Leaf(digit)) =>
        Num(digit.toInt) },
      "minus_num" -> { case List(_, Leaf(digit)) =>
        Num(-digit.toInt) },
      "diff" -> { case List(_, _, exp1, _, exp2, _) =>
        Diff(exp_matcher(exp1), exp_matcher(exp2))},
      "zero_p" -> { case List(_, _, exp1, _) =>
        ZeroP(exp_matcher(exp1)) },
      "if" -> { case List(_, exp1, _, exp2, _, _, _, exp3, _) =>
        If(exp_matcher(exp1), exp_matcher(exp2), exp_matcher(exp3))},
      "let" -> { case List(_, Leaf(name), _, exp1, body) =>
        Let(name, exp_matcher(exp1), exp_matcher(body))},
      "fn" -> { case List(_, Leaf(name), _, _, _, body) =>
        Fn(name, exp_matcher(body))},
      "ap" -> { case List(target, _, arg, _) =>
        Ap(exp_matcher(target), exp_matcher(arg)) },
      "block_one" -> { case List(_, exp, _) =>
        exp_matcher(exp) },
      "sole" -> { case List(_) =>
        Sole() },
      "do" -> { case List(_, exp1, body) =>
        Do(exp_matcher(exp1), exp_matcher(body)) },
      "assert_eq" -> { case List(_, _, exp1, _, exp2, _) =>
        AssertEq(exp_matcher(exp1), exp_matcher(exp2)) },
      "show" -> { case List(_, _, exp1, _) =>
        Show(exp_matcher(exp1)) },
    ))

}
