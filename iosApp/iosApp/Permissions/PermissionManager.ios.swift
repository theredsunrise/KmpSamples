import Foundation
import UIKit
import ComposeApp

class IOSPermissionManager: PermissionManagerInterface {
    private let systemPermissionHandlerLocator: SystemPermissionHandlerLocatorInterface
    private var onPermissionState: ((PermissionStateType) -> Void)?
    
    init(systemPermissionHandlerLocator: SystemPermissionHandlerLocatorInterface) {
        self.systemPermissionHandlerLocator = systemPermissionHandlerLocator
    }
    
    func onPermissionState(onPermissionState: ((PermissionStateType) -> Void)?) {
        self.onPermissionState = onPermissionState
    }
    
    func createPlatformSpecificPermissions(permissionTypes: [PermissionType]) -> [PlatformPermissionType] {
        let platformPermissionTypes: [PlatformPermissionType] = permissionTypes.map {
            PlatformPermissionTypeCompanion.shared.create(permissionType: $0) }
        let singlePlatformPermissions = PlatformPermissionMapper.toPlatformSinglePermissions(platformPermissionTypes)
        let groupedPermissions = Dictionary(grouping: singlePlatformPermissions
                                            
        ){ key in
            SystemPermissionHandlerWrapper(handler: systemPermissionHandlerLocator.getPermissionHandler(permissionType: key))
        }
        
        let worstCostValue = groupedPermissions.count + 1
        let entries = groupedPermissions.sorted(by: {left, right in
            var leftCostValue = worstCostValue
            if let el = left.value.first, let index = singlePlatformPermissions.firstIndex(of: el) {
                leftCostValue = index
            }
            
            var rightCostValue = worstCostValue
            if let el = right.value.first, let index = singlePlatformPermissions.firstIndex(of: el) {
                rightCostValue = index
            }
            
            return leftCostValue <= rightCostValue
        })
        
        return entries.map { entry in
            PlatformPermissionTypePlatformMultiplePermissions(permissions: Array(entry.value))
        }
    }
    
    func checkPermissions(permissionTypes: [PlatformPermissionType]) -> [PermissionState] {
        return PermissionStateMapper.shared.toPermissionStates(
            permissionStateTypes:
                resolvePermissionStates(
                    permissionTypes: permissionTypes
                )
        )
    }
    
    func requestPermission(permissionType: PlatformPermissionType) {
        guard let onPermissionState = self.onPermissionState else { return }
        let requestedPermissionState = resolvePermissionStates(permissionTypes: [permissionType]).first!
        
        if requestedPermissionState.isGranted == true {
            onPermissionState(requestedPermissionState)
        } else {
            systemPermissionHandlerLocator.getPermissionHandler(permissionType: permissionType)
                .requestPermission { resolvedPermissionState in
                    DispatchQueue.main.async {
                        onPermissionState(resolvedPermissionState)
                    }
                }
        }
    }
    
    func openGrantPermissionsScreen() {
        guard let url = URL(string: UIApplication.openSettingsURLString),
              UIApplication.shared.canOpenURL(url) else { return }
        UIApplication.shared.open(url, options: [:]) { success in
            print("Opened app settings: \(success)")
        }
    }
    
    private func resolvePermissionStates(permissionTypes: [PlatformPermissionType]) -> [PermissionStateType] {
        return permissionTypes.map {
            systemPermissionHandlerLocator.getPermissionHandler(permissionType: $0).resolvePermissionState()
        }
    }
}
