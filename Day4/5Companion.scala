trait Atom {
  def atomicNumber: Int
  def symbol: String
}

class Nucleus(protons: Int, neutrons: Int) {
  def massNumber: Int = protons + neutrons
}

case class Element(name: String, atomicNumber: Int, symbol: String) // Case class

object Element {  // Companion object
  def output(element: Element): String = s"Name: ${element.name}, Atomic Number: ${element.atomicNumber}"
}

@main def main: Unit = {
  val hydrogen = Element("Hydrogen", 1, "H")
  println(Element.output(hydrogen))

  val hydrogenNucleus = new Nucleus(1, 0)

  // Anonymous class to implement Atom trait
  val hydrogenAtom = new Atom {
    def atomicNumber: Int = hydrogen.atomicNumber
    def symbol: String = hydrogen.symbol
  }

  println(s"Atom: ${hydrogenAtom.symbol}, Atomic Number: ${hydrogenAtom.atomicNumber}")
  println(s"Nucleus Mass Number: ${hydrogenNucleus.massNumber}")
}
