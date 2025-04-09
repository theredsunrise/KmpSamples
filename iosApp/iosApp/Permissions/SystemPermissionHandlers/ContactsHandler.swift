import Contacts
import ComposeApp

class ContactsHandler: SystemPermissionHandler {
    var id: String = "ContactsHandler"
    
    private let mainScope = DispatchQueue.main
    private let contactStore = CNContactStore()
    
    func canHandle(simplePlatformPermission: PlatformPermissionTypePlatformSinglePermission) -> Bool {
        switch simplePlatformPermission.permission {
        case is Permission.ContactsReadPermission, is Permission.ContactsWritePermission:
            return true
        default:
            return false
        }
    }
    
    private func createMultiplePermissionState(
        isGranted: Bool,
        shouldShowRationale: Bool
    ) -> PermissionStateTypeMultiplePermissionState {
        let permissionStates = [Permission.ContactsReadPermission(), Permission.ContactsWritePermission()].map { permission in
            let permissionState = PermissionState(permission: permission, isGranted: isGranted, shouldShowRationale: shouldShowRationale)
            return PermissionStateTypeSinglePermissionState(permissionState: permissionState)
        }
        return PermissionStateTypeMultiplePermissionState(permissionStates: permissionStates)
    }
    
    func resolvePermissionState() -> PermissionStateType {
        let status = CNContactStore.authorizationStatus(for: .contacts)
        switch status {
        case .limited:
            return createMultiplePermissionState(isGranted: true, shouldShowRationale: false)
        case .authorized:
            return createMultiplePermissionState(isGranted: true, shouldShowRationale: false)
        case .denied:
            return createMultiplePermissionState(isGranted: false, shouldShowRationale: false)
        case .restricted:
            return createMultiplePermissionState(isGranted: false, shouldShowRationale: false)
        case .notDetermined:
            return createMultiplePermissionState(isGranted: false, shouldShowRationale: true)
        @unknown default:
            return createMultiplePermissionState(isGranted: false, shouldShowRationale: true)
        }
    }
    
    func requestPermission(onRequest: @escaping (PermissionStateType) -> Void) {
        contactStore.requestAccess(for: .contacts) { isGranted, error in
            self.mainScope.async {
                onRequest(self.resolvePermissionState())
            }
        }
    }
}
