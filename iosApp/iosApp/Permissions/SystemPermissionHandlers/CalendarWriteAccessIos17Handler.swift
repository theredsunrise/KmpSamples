import EventKit
import ComposeApp

@available(iOS 17.0, *)
class CalendarWriteAccessIos17Handler: SystemPermissionHandler {
    var id: String = "CalendarWriteAccessIos17Handler"
    
    private let eventStore = EKEventStore()
    private var wasPermissionGranted: Bool? = nil
    
    func canHandle(simplePlatformPermission: PlatformPermissionTypePlatformSinglePermission) -> Bool {
        func isSupported(permission: Permission) -> Bool {
            switch permission {
            case is Permission.CalendarWritePermission:
                return isAtLeastIOS(version: 17)
            default:
                return false
            }
        }
        return isSupported(permission: simplePlatformPermission.permission)
    }
    
    private func createSimplePermissionState(
        isGranted: Bool,
        shouldShowRationale: Bool
    ) -> PermissionStateTypeSinglePermissionState {
        let permissionState = PermissionState(
            permission: Permission.CalendarWritePermission(),
            isGranted: isGranted,
            shouldShowRationale: shouldShowRationale
        )
        return PermissionStateTypeSinglePermissionState(permissionState: permissionState)
    }
    
    func resolvePermissionState() -> PermissionStateType {
        let status = getStatus()
        switch status {
        case .authorized, .writeOnly:
            return createSimplePermissionState(isGranted: true, shouldShowRationale: false)
        case .denied, .restricted, .fullAccess:
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
        eventStore.requestWriteOnlyAccessToEvents { isGranted, error in
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
