import EventKit
import Foundation
import ComposeApp

@available(iOS 17.0, *)
class CalendarFullAccessIos17Handler: SystemPermissionHandler {
    var id: String = "CalendarFullAccessIos17Handler"
    
    private let eventStore = EKEventStore()
    private var wasPermissionGranted: Bool? = nil
    
    func canHandle(simplePlatformPermission: PlatformPermissionTypePlatformSinglePermission) -> Bool {
        func isSupported(permission: Permission) -> Bool {
            switch permission {
            case is Permission.CalendarReadPermission:
                return isAtLeastIOS(version: 17)
            default:
                return false
            }
        }
        return isSupported(permission: simplePlatformPermission.permission)
    }
    
    private func createSimplePermissionState(isGranted: Bool, shouldShowRationale: Bool) -> PermissionStateTypeSinglePermissionState {
        let permissionState = PermissionState(permission: Permission.CalendarReadPermission(),
                                              isGranted: isGranted,
                                              shouldShowRationale: shouldShowRationale)
        return PermissionStateTypeSinglePermissionState(permissionState: permissionState)
    }
    
    func resolvePermissionState() -> PermissionStateType {
        let status = getStatus()
        switch status {
        case .authorized, .fullAccess:
            return createSimplePermissionState(isGranted: true, shouldShowRationale: false)
        case .denied, .restricted, .writeOnly:
            return createSimplePermissionState(isGranted: false, shouldShowRationale: false)
        case .notDetermined:
            return createSimplePermissionState(isGranted: wasPermissionGranted == true,
                                               shouldShowRationale: wasPermissionGranted == nil)
        @unknown default:
            return createSimplePermissionState(isGranted: false, shouldShowRationale: true)
        }
    }
    
    private func getStatus() -> EKAuthorizationStatus {
        EKEventStore.authorizationStatus(for: .event)
    }
    
    private func isAtLeastIOS(version: Int) -> Bool {
        let majorVersion = ProcessInfo.processInfo.operatingSystemVersion.majorVersion
        return majorVersion >= version
    }
    
    func requestPermission(onRequest: @escaping (PermissionStateType) -> Void) {
        eventStore.requestFullAccessToEvents { isGranted, error in
            DispatchQueue.main.async {
                let status = self.getStatus()
                if status == .notDetermined && error == nil {
                    self.wasPermissionGranted = isGranted
                }
                onRequest(self.resolvePermissionState())
            }
        }
    }
}
