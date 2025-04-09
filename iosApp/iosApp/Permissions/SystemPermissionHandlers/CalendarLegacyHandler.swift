import EventKit
import Foundation
import ComposeApp

class CalendarLegacyHandler: SystemPermissionHandler {
    var id: String = "CalendarLegacyHandler"
    
    private let eventStore = EKEventStore()
    
    func canHandle(simplePlatformPermission: PlatformPermissionTypePlatformSinglePermission) -> Bool {
        func isSupported(permission: Permission) -> Bool {
            switch permission {
            case is Permission.CalendarReadPermission, is Permission.CalendarWritePermission:
                return !isAtLeastIOS(version: 17)
            default:
                return false
            }
        }
        return isSupported(permission: simplePlatformPermission.permission)
    }
    
    private func createMultiplePermissionState(isGranted: Bool, shouldShowRationale: Bool) -> PermissionStateTypeMultiplePermissionState {
        let permissionStates = [Permission.CalendarReadPermission(), Permission.CalendarWritePermission()].map {
            let permissionState = PermissionState(permission: $0, isGranted: isGranted, shouldShowRationale: shouldShowRationale)
            return PermissionStateTypeSinglePermissionState(permissionState: permissionState)
        }
        return PermissionStateTypeMultiplePermissionState(permissionStates: permissionStates)
    }
    
    func resolvePermissionState() -> PermissionStateType {
        let status = EKEventStore.authorizationStatus(for: .event)
        switch status {
        case .authorized, .writeOnly, .fullAccess:
            return createMultiplePermissionState(isGranted: true, shouldShowRationale: false)
        case .denied, .restricted:
            return createMultiplePermissionState(isGranted: false, shouldShowRationale: false)
        case .notDetermined:
            return createMultiplePermissionState(isGranted: false, shouldShowRationale: true)
        @unknown default:
            return createMultiplePermissionState(isGranted: false, shouldShowRationale: true)
        }
    }
    
    private func isAtLeastIOS(version: Int) -> Bool {
        let majorVersion = ProcessInfo.processInfo.operatingSystemVersion.majorVersion
        return majorVersion >= version
    }
    
    func requestPermission(onRequest: @escaping (PermissionStateType) -> Void) {
        eventStore.requestAccess(to: .event) { isGranted, error in
            DispatchQueue.main.async {
                onRequest(self.resolvePermissionState())
            }
        }
    }
}
