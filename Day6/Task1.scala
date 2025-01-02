import scala.collection.mutable.ListBuffer

class Student(val sno: Int, val name: String, val score: Int) {
  override def toString: String = s"ID: $sno, Name: $name, Score: $score"
}

object Student {
  def apply(sno: Int, name: String, score: Int): Student = new Student(sno, name, score)
}

implicit class EnhancedList(students: ListBuffer[Student]) {
  def %>(value: Int): ListBuffer[Student] = {
    var newlist: ListBuffer[Student] = ListBuffer()
    for(i <- students){
      if(i.score > value){
        newlist += i
      }
    }
    newlist
  }

  def %<(value: Int): ListBuffer[Student] = {
    var newlist2: ListBuffer[Student] = ListBuffer()
    for(i <- students){
      if(i.score < value){
        newlist2 += i
      }
    }
    newlist2
  }
}

implicit def arrayToList[T](array: Array[T]): List[T] = {
  def convert(index: Int, acc: List[T]): List[T] = {
    if (index < 0) acc
    else convert(index - 1, array(index) :: acc)
  }
  convert(array.length - 1, Nil)
}

implicit def tupletostudent(t: (Int, String, Int)): Student = {
  new Student(t._1, t._2, t._3)
}

class StudentOps(var studentlist: ListBuffer[Student]) {

  def add(s: Student): ListBuffer[Student] = {
    studentlist += s
    studentlist
  }

  def add(s: (Int, String, Int)): ListBuffer[Student] = {
    val newStudent: Student = s
    studentlist += newStudent
    studentlist
  }

  def filterStudent: ListBuffer[Student] => Boolean = listbuf => {
    var result: Boolean = false
    for(s <- listbuf){
      if(s.score > 50)
        result = true
      else
        result = false
    }
    result
  }
}

@main def main: Unit = {
  val studentsList = ListBuffer(
    Student(1, "Alice", 85), Student(2, "Bob", 92), Student(3, "Charlie", 78), Student(4, "David", 66), Student(5, "Eve", 90),
    Student(6, "Frank", 73), Student(7, "Grace", 88), Student(8, "Hannah", 91), Student(9, "Isaac", 84), Student(10, "Judy", 76),
    Student(11, "Kevin", 82), Student(12, "Laura", 79), Student(13, "Mike", 95), Student(14, "Nina", 70), Student(15, "Oscar", 89),
    Student(16, "Paul", 80), Student(17, "Quinn", 77), Student(18, "Rachel", 93), Student(19, "Sam", 85), Student(20, "Tina", 74),
    Student(21, "Uma", 69), Student(22, "Victor", 96), Student(23, "Wendy", 87), Student(24, "Xander", 68), Student(25, "Yara", 94),
    Student(26, "Zane", 81), Student(27, "Oliver", 78), Student(28, "Sophia", 85), Student(29, "Liam", 90), Student(30, "Mia", 83),
    Student(31, "Noah", 88), Student(32, "Emma", 75), Student(33, "Ava", 92), Student(34, "William", 86), Student(35, "James", 91),
    Student(36, "Lucas", 72), Student(37, "Amelia", 79), Student(38, "Ella", 89), Student(39, "Mason", 76), Student(40, "Logan", 95),
    Student(41, "Ethan", 84), Student(42, "Charlotte", 82), Student(43, "Benjamin", 80), Student(44, "Alexander", 71),
    Student(45, "Michael", 88), Student(46, "Isabella", 73), Student(47, "Daniel", 86), Student(48, "Elijah", 81),
    Student(49, "Matthew", 79), Student(50, "Jackson", 92)
  )

  println("Students with score less than 50:")
  println(studentsList %< 50)

  println("Students with score greater than 30:")
  println(studentsList %> 30)

  var obj = new StudentOps(studentsList)
  obj.filterStudent(studentsList)
  obj.add(Student(3, "Ninja", 70))
  println(studentsList)
  obj.add((4, "Tuple", 50))
  println(obj.filterStudent(studentsList))
}
