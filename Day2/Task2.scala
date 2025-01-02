object Task2 extends App {

  // Mutable Variables (using var)
  var greeting: String = "Hello, Tinku!"
  println(greeting)
  greeting = "Hi, Yaseen!"
  println(greeting)

  // Immutable Variables (using val)
  val fixedValue: Int = 42
  println(fixedValue)
  // Uncommenting the following line would cause a compile error
  // fixedValue = 50

  // Integer Literals
  val decimalInt: Int = 150  // Decimal literal
  val octalInt: Int = 020    // Octal literal
  val hexInt: Int = 0x1A3    // Hexadecimal literal
  val longInt: Long = 12345L // Long literal
  println(s"Decimal: $decimalInt, Octal: $octalInt, Hexadecimal: $hexInt, Long: $longInt")

  // Float Literals
  val floatNumber: Float = 3.14f
  val largeDouble: Double = 2.7e300
  val scientificFloat: Float = 1.23e3f
  val smallFraction: Double = 0.005
  println(s"Float: $floatNumber, Large Double: $largeDouble, Scientific Float: $scientificFloat, Small Fraction: $smallFraction")

  // Boolean Literals
  val isScalaFun: Boolean = true
  val isProgrammingHard: Boolean = false
  println(s"Is Scala fun? $isScalaFun, Is programming hard? $isProgrammingHard")

  // Character Literals
  val initial: Char = 'R'
  val tabChar: Char = '\t'
  println(s"Initial: $initial, Tab Character: '$tabChar'")

  // String Literals
  val singleLineString: String = "Learning Scala is fun!"
  val multiLineString: String =
    """This is a multi-line string.
      |It spans across multiple lines.
      |Isn't that cool?""".stripMargin
  val escapedString: String = "This string contains a \"quote\" character."
  println(s"Single Line: $singleLineString")
  println(s"Multi Line: $multiLineString")
  println(s"Escaped String: $escapedString")

  // Null Value
  val optionalValue: Null = null
  val defaultValue = null
  println(s"Optional Value: $optionalValue, Default Value: $defaultValue")

}
