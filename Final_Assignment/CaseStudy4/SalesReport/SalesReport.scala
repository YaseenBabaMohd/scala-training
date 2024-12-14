package SalesReport


import _root_.scalapb.UnknownFieldSet
import _root_.scalapb.GeneratedMessage
import _root_.scalapb.lenses.Updatable
import _root_.com.google.protobuf.{CodedInputStream, CodedOutputStream, UnknownFieldSet}
import _root_.scalapb.descriptors.{FieldDescriptor, PMessage, PValue}
import com.google.protobuf.Descriptors.FieldDescriptor
import spire.macros.Auto.java.eq

/** Represents a sales record */
@SerialVersionUID(0L)
final case class SalesRecord(
                              store: _root_.scala.Predef.String = "",
                              dept: _root_.scala.Predef.String = "",
                              date: _root_.scala.Predef.String = "",
                              weeklySales: _root_.scala.Float = 0.0f,
                              isHoliday: _root_.scala.Boolean = false,
                              unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty) extends GeneratedMessage
  with Updatable[SalesRecord] {

  @transient
  private[this] var __serializedSizeMemoized: Int = 0

  private[this] def __computeSerializedSize(): Int = {
    var __size = 0

    if (store.nonEmpty) {
      __size += CodedOutputStream.computeStringSize(1, store)
      case _ => PValue.Empty
    }

    if (dept.nonEmpty) {
      __size += CodedOutputStream.computeStringSize(2, dept)
    }

    if (timestamp.nonEmpty) {
      __size += CodedOutputStream.computeStringSize(3, timestamp)
    }

    if (weeklySales != 0.0f) {
      __size += CodedOutputStream.computeFloatSize(4, weeklySales)
    }

    if (isHoliday) {
      __size += CodedOutputStream.computeBoolSize(5, isHoliday)
    }

    __size += unknownFields.serializedSize
    __size
  }

  override def serializedSize: Int = {
    if (__serializedSizeMemoized == 0) {
      __serializedSizeMemoized = __computeSerializedSize() + 1
    }
    __serializedSizeMemoized - 1
  }

  def writeTo(output: CodedOutputStream): Unit = {
    if (store.nonEmpty) {
      output.writeString(1, store)
    }
    if (dept.nonEmpty) {
      output.writeString(2, dept)
    }
    if (timestamp.nonEmpty) {
      output.writeString(3, timestamp)
    }
    if (weeklySales != 0.0f) {
      output.writeFloat(4, weeklySales)
    }
    if (isHoliday) {
      output.writeBool(5, isHoliday)
    }
    unknownFields.writeTo(output)
  }

  def withStore(value: String): SalesRecord = copy(store = value)
  def withDept(value: String): SalesRecord = copy(dept = value)
  def withTimestamp(value: String): SalesRecord = copy(timestamp = value)
  def withWeeklySales(value: Float): SalesRecord = copy(weeklySales = value)
  def withIsHoliday(value: Boolean): SalesRecord = copy(isHoliday = value)
  def withUnknownFields(value: UnknownFieldSet): SalesRecord = copy(unknownFields = value)

  def discardUnknownFields: SalesRecord = copy(unknownFields = UnknownFieldSet.empty)

  def getFieldByNumber(fieldNumber: Int): Any = {
    fieldNumber match {
      case 1 => if (store.nonEmpty) store else null
      case 2 => if (dept.nonEmpty) dept else null
      case 3 => if (timestamp.nonEmpty) timestamp else null
      case 4 => if (weeklySales != 0.0f) weeklySales else null
      case 5 => if (isHoliday) isHoliday else null
      case _ => null
    }
  }

  def getField(field: FieldDescriptor): PValue = {
    require(field.containingMessage eq companion.scalaDescriptor)
    field.number match {
      case 1 => PValue.PString(store)
      case 2 => PValue.PString(dept)
      case 3 => PValue.PString(timestamp)
      case 4 => PValue.PFloat(weeklySales)
      case 5 => PValue.PBoolean(isHoliday)
    }
  }

  def toProtoString: String = scalapb.TextFormat.printToUnicodeString(this)

  def companion: SalesRecord.type = SalesRecord
}

object SalesRecord extends scalapb.GeneratedMessageCompanion[SalesRecord] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[SalesRecord] = this

  def parseFrom(input: CodedInputStream): SalesRecord = {
    var store = ""
    var dept = ""
    var timestamp = ""
    var weeklySales = 0.0f
    var isHoliday = false
    var unknownFields: UnknownFieldSet.Builder = null
    var done = false

    while (!done) {
      val tag = input.readTag()
      tag match {
        case 0 => done = true
        case 10 => store = input.readStringRequireUtf8()
        case 18 => dept = input.readStringRequireUtf8()
        case 26 => timestamp = input.readStringRequireUtf8()
        case 37 => weeklySales = input.readFloat()
        case 40 => isHoliday = input.readBool()
        case _ =>
          if (unknownFields == null) {
            unknownFields = UnknownFieldSet.newBuilder()
          }
          unknownFields.parseField(tag, input)
      }
    }

    SalesRecord(
      store = store,
      dept = dept,
      timestamp = timestamp,
      weeklySales = weeklySales,
      isHoliday = isHoliday,
      unknownFields = if (unknownFields == null) UnknownFieldSet.empty else unknownFields.result()
    )
  }

  lazy val defaultInstance: SalesRecord = SalesRecord()

  final val STORE_FIELD_NUMBER = 1
  final val DEPT_FIELD_NUMBER = 2
  final val TIMESTAMP_FIELD_NUMBER = 3
  final val WEEKLY_SALES_FIELD_NUMBER = 4
  final val IS_HOLIDAY_FIELD_NUMBER = 5

  lazy val scalaDescriptor: scalapb.descriptors.Descriptor = SalesReport.SalesRecordProto
}
