import AVFAudio
import ComposeApp

class RecordAudioHandler: SystemPermissionHandler {
    var id: String = "RecordAudioHandler"
    
    private let mainScope = DispatchQueue.main
    
    func canHandle(simplePlatformPermission: PlatformPermissionTypePlatformSinglePermission) -> Bool {
        return simplePlatformPermission.permission is Permission.RecordAudioPermission
    }
    
    private func createSimplePermissionState(
        isGranted: Bool,
        shouldShowRationale: Bool
    ) -> PermissionStateTypeSinglePermissionState {
        let permissionState = PermissionState(permission: Permission.RecordAudioPermission(), isGranted: isGranted, shouldShowRationale: shouldShowRationale)
        return PermissionStateTypeSinglePermissionState(permissionState: permissionState)
    }
    
    func resolvePermissionState() -> PermissionStateType {
        let status = AVAudioSession.sharedInstance().recordPermission
        switch status {
        case .granted:
            return createSimplePermissionState(isGranted: true, shouldShowRationale: false)
        case .denied:
            return createSimplePermissionState(isGranted: false, shouldShowRationale: false)
        case .undetermined:
            return createSimplePermissionState(isGranted: false, shouldShowRationale: true)
        @unknown default:
            return createSimplePermissionState(isGranted: false, shouldShowRationale: true)
        }
    }
    
    func requestPermission(onRequest: @escaping (PermissionStateType) -> Void) {
        AVAudioSession.sharedInstance().requestRecordPermission { isGranted in
            self.mainScope.async {
                onRequest(self.resolvePermissionState())
            }
        }
    }
}
