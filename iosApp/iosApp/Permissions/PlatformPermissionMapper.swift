import Foundation
import ComposeApp

struct PlatformPermissionMapper {
    static func toPlatformSinglePermissions(_ platformPermissionTypes: [PlatformPermissionType]) -> [PlatformPermissionTypePlatformSinglePermission] {
        
        return platformPermissionTypes.flatMap { permissionType in
            switch permissionType {
            case let singlePermission as PlatformPermissionTypePlatformSinglePermission:
                return [singlePermission]
            case let multiplePermissions as PlatformPermissionTypePlatformMultiplePermissions:
                return multiplePermissions.permissions
            default:
                fatalError("Not supported platform permission type: \(permissionType)")
            }
        }
    }
}
