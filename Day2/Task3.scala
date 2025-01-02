object Task3 extends App {

  // Byte - 8-bit signed value. Range: -128 to 127
  val byteValue1: Byte = 10
  val byteValue2: Byte = 127
  println(s"Byte values: $byteValue1, $byteValue2")

  // Short - 16-bit signed value. Range: -32,768 to 32,767
  val shortValue1: Short = 32000
  val shortValue2: Short = -100
  println(s"Short values: $shortValue1, $shortValue2")

  // Int - 32-bit signed value. Range: -2,147,483,648 to 2,147,483,647
  val intValue1: Int = 12345
  val intValue2: Int = -67890
  println(s"Int values: $intValue1, $intValue2")

  // Long - 64-bit signed value. Range: -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
  val longValue1: Long = 9876543210L
  val longValue2: Long = -1234567890L
  println(s"Long values: $longValue1, $longValue2")

  // Float - 32-bit IEEE 754 single-precision float
  val floatValue1: Float = 3.14f
  val floatValue2: Float = -0.98f
  println(s"Float values: $floatValue1, $floatValue2")

  // Double - 64-bit IEEE 754 double-precision float
  val doubleValue1: Double = 1.23456789
  val doubleValue2: Double = -9.87654321
  println(s"Double values: $doubleValue1, $doubleValue2")

  // Char - 16-bit unsigned Unicode character
  val charValue1: Char = 'R'
  val charValue2: Char = '\u0041' // Unicode for 'A'
  println(s"Char values: $charValue1, $charValue2")

  // String - Sequence of characters
  val stringValue1: String = "Scala"
  val stringValue2: String = "Programming"
  println(s"String values: $stringValue1, $stringValue2")

  // Boolean - true or false
  val isScalaFun: Boolean = true
  val isCodeEasy: Boolean = false
  println(s"Boolean values: Is Scala fun? $isScalaFun, Is code easy? $isCodeEasy")

  // Unit - Corresponds to no value
  val unitResult: Unit = {
    println("This block has no meaningful return value")
  }
  println(s"Unit result: $unitResult")

  // Null - Null or empty reference
  val nullString: String = null
  val nullValue: Null = null
  println(s"Null values: String = $nullString, Null = $nullValue")

  // Nothing - Subtype of every other type, includes no values
  def terminateWithError(message: String): Nothing = {
    throw new RuntimeException(message)
  }
  // Uncommenting the next line will cause a runtime exception
  // terminateWithError("This is a fatal error!")

  // Any - The supertype of all types
  val anyValue1: Any = 42
  val anyValue2: Any = "Hello, World!"
  println(s"Any values: $anyValue1, $anyValue2")

  // AnyRef - Supertype of all reference types
  val anyRefValue: AnyRef = new String("Reference type example")
  println(s"AnyRef value: $anyRefValue")
}
