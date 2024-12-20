package SalesReport

object SalesRecordProto extends _root_.scalapb.GeneratedFileObject {
  lazy val dependencies: Seq[_root_.scalapb.GeneratedFileObject] = Seq.empty
  lazy val messagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] =
    Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]](
        SalesReport.SalesRecord
    )
  private lazy val ProtoBytes: _root_.scala.Array[Byte] =
    scalapb.Encoding.fromBase64(scala.collection.immutable.Seq(
      """ChFTYWxlc1JlY29yZC5wcm90bxIYY2FzZV9zdHVkeV80LkR5bmFtaWNEYXRhItEBCgtTYWxlc1JlY29yZBIgCgVzdG9yZRgBI
  AEoCUIK4j8HEgVzdG9yZVIFc3RvcmUSHQoEZGVwdBgCIAEoCUIJ4j8GEgRkZXB0UgRkZXB0Eh0KBGRhdGUYAyABKAlCCeI/BhIEZ
  GF0ZVIEZGF0ZRIzCgx3ZWVrbHlfc2FsZXMYBCABKAJCEOI/DRILd2Vla2x5U2FsZXNSC3dlZWtseVNhbGVzEi0KCmlzX2hvbGlkY
  XkYBSABKAhCDuI/CxIJaXNIb2xpZGF5Uglpc0hvbGlkYXliBnByb3RvMw=="""
    ).mkString)
  lazy val scalaDescriptor: _root_.scalapb.descriptors.FileDescriptor = {
    val scalaProto = com.google.protobuf.descriptor.FileDescriptorProto.parseFrom(ProtoBytes)
    _root_.scalapb.descriptors.FileDescriptor.buildFrom(scalaProto, dependencies.map(_.scalaDescriptor))
  }
  lazy val javaDescriptor: com.google.protobuf.Descriptors.FileDescriptor = {
    val javaProto = com.google.protobuf.DescriptorProtos.FileDescriptorProto.parseFrom(ProtoBytes)
    com.google.protobuf.Descriptors.FileDescriptor.buildFrom(javaProto, _root_.scala.Array(
    ))
  }
  @deprecated("Use javaDescriptor instead. In a future version this will refer to scalaDescriptor.", "ScalaPB 0.5.47")
  def descriptor: com.google.protobuf.Descriptors.FileDescriptor = javaDescriptor
}