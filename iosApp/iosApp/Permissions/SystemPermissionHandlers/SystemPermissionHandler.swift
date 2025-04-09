import ComposeApp

protocol SystemPermissionHandler {
    var id: String {get}
    func resolvePermissionState() -> PermissionStateType
    func canHandle(simplePlatformPermission: PlatformPermissionTypePlatformSinglePermission) -> Bool
    func requestPermission(onRequest: @escaping (_ permissionState: PermissionStateType) -> Void)
}

final class SystemPermissionHandlerWrapper: Hashable {
    let handler: any SystemPermissionHandler
    
    init(handler: any SystemPermissionHandler) {
        self.handler = handler
    }
    static func == (lhs: SystemPermissionHandlerWrapper, rhs: SystemPermissionHandlerWrapper) -> Bool {
        return lhs.handler.id == rhs.handler.id
    }
        
    func hash(into hasher: inout Hasher) {
        hasher.combine(handler.id)
    }
}
