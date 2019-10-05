let id = (x) => x

@show id
@show id(id)

let zero = (f, x) => x

let succ = (prev, f, x) => f(prev(f, x))

let add = (j, k, f, x) => j(f, k(f, x))

let one = succ(zero)
let two = succ(one)
let three = succ(two)
let four = succ(three)
let five = succ(four)
let six = succ(five)

@show add(one, one)

@assert_eq add(one, one) two

@walk_through add(one, one)
@walk_through add(two, two)
@walk_through add(six, add(six, six))

@walk_through id(id(id(id(id(id(id(id(id))))))))
