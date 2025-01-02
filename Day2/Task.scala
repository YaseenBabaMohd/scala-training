object Task extends App {

  // Arithmetic Operations
  val a = 10
  val b = 3
  println("Arithmetic Operations:")
  println(s"a + b = ${a + b}")
  println(s"a - b = ${a - b}")
  println(s"a * b = ${a * b}")
  println(s"a / b = ${a / b}")
  println(s"a % b = ${a % b}")

  // Relational Operations
  val x = 5
  val y = 10
  println("\nRelational Operations:")
  println(s"x == y: ${x == y}")
  println(s"x != y: ${x != y}")
  println(s"x > y: ${x > y}")
  println(s"x < y: ${x < y}")
  println(s"x >= y: ${x >= y}")
  println(s"x <= y: ${x <= y}")

  // Logical Operations
  val p = true
  val q = false
  println("\nLogical Operations:")
  println(s"p && q: ${p && q}")
  println(s"p || q: ${p || q}")
  println(s"!p: ${!p}")

  // Bitwise Operations
  val m = 5 // 0101 in binary
  val n = 3 // 0011 in binary
  println("\nBitwise Operations:")
  println(s"m & n: ${m & n}")  // 0001
  println(s"m | n: ${m | n}")  // 0111
  println(s"m ^ n: ${m ^ n}")  // 0110
  println(s"~m: ${~m}")        // Inverts all bits
  println(s"m << 1: ${m << 1}") // Left shift
  println(s"m >> 1: ${m >> 1}") // Right shift

  // Assignment Operations
  var z = 5
  println("\nAssignment Operations:")
  z += 3
  println(s"z += 3: $z")
  z -= 2
  println(s"z -= 2: $z")
  z *= 2
  println(s"z *= 2: $z")
  z /= 3
  println(s"z /= 3: $z")

  // Unary Operations
  val u = 5
  println("\nUnary Operations:")
  val unaryMinus = -u
  println(s"Unary minus of u: $unaryMinus")
  val unaryPlus = +u
  println(s"Unary plus of u: $unaryPlus (no change)")

}
