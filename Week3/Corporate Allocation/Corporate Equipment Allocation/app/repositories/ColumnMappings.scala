package repositories

import enums.AllocationStatus.AllocationStatus
import enums.EquipmentCondition.EquipmentCondition
import enums.{AllocationStatus, EquipmentCondition, EquipmentType, MaintenanceStatus}
import enums.EquipmentType.EquipmentType
import enums.MaintenanceStatus.MaintenanceStatus
import slick.jdbc.MySQLProfile.api._

private[repositories] object ColumnMappings {

  // Mapping for EquipmentType enum
  implicit val equipmentTypeColumnType: BaseColumnType[EquipmentType] =
    MappedColumnType.base[EquipmentType, String](
      e => e.toString,
      s => EquipmentType.withName(s)
    )

  // Mapping for EquipmentCondition enum
  implicit val equipmentConditionColumnType: BaseColumnType[EquipmentCondition] =
    MappedColumnType.base[EquipmentCondition, String](
      e => e.toString,
      s => EquipmentCondition.withName(s)
    )

  // Mapping for AllocationStatus enum
  implicit val allocationStatusColumnType: BaseColumnType[AllocationStatus] =
    MappedColumnType.base[AllocationStatus, String](
      e => e.toString,
      s => AllocationStatus.withName(s)
    )

  // Mapping for MaintenanceStatus enum
  implicit val maintenanceStatusColumnType: BaseColumnType[MaintenanceStatus] =
    MappedColumnType.base[MaintenanceStatus, String](
      e => e.toString,
      s => MaintenanceStatus.withName(s)
    )
}
