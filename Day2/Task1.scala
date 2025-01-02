object Task1 extends App {

  // String Formatting
  val name: String = "Rajesh"
  val formattedName = String.format("Name using format: %s", name)
  println(s"Name using Interpolator: $name")
  printf("Name using printf: %s\n", name)
  println(formattedName)

  // Integer Formatting
  val number: Int = 7
  println(f"Integer using Interpolator: $number%d")
  printf("Integer using printf: %d\n", number)
  printf("Integer with leading zeros: %02d\n", number)

  // Formatting for Large Numbers
  val largeNumber = 1234567
  println(f"Formatted large number with commas: $largeNumber%,d")
  printf("Large number with commas: %,d\n", largeNumber)

  // Formatting for Double Variables
  val decimalNumber: Double = 456.789
  println(f"Formatted double with two decimals: $decimalNumber%.2f")
  printf("Formatted double using printf: %.2f\n", decimalNumber)
  printf("Double in scientific notation: %.2e\n", decimalNumber)
  println(f"Double in scientific notation using interpolator: $decimalNumber%.2e")
  println(f"Double with leading zeros: $decimalNumber%08.2f")
  val formattedDouble = String.format("Double using format: %.2f", decimalNumber)
  println(formattedDouble)

  // Boolean Formatting
  val flag: Boolean = false
  printf("Boolean value: %b\n", flag)
  printf("Boolean value (uppercase): %B\n", flag)
  println(f"Formatted Boolean using interpolator: $flag")
  val formattedFlag = if (flag) "Active" else "Inactive"
  println(s"Boolean as descriptive text: $formattedFlag")
  val formattedBooleanString = String.format("Boolean using format: %b", flag)
  println(formattedBooleanString)
}
