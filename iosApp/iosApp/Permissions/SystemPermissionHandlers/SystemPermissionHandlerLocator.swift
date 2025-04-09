import ComposeApp

protocol SystemPermissionHandlerLocatorInterface {
    func getPermissionHandler(permissionType: PlatformPermissionType) -> SystemPermissionHandler
}

class SystemPermissionHandlerLocator: SystemPermissionHandlerLocatorInterface {
    private let systemPermissionHandlers: [SystemPermissionHandler] = [
        ContactsHandler(),
        RecordAudioHandler(),
        CalendarLegacyHandler()] +
    {
        if #available(iOS 17.0, *) {
            return [
                CalendarWriteAccessIos17Handler(),
                CalendarFullAccessIos17Handler()]
        } else {return []}
    }()
    
    func getPermissionHandler(permissionType: PlatformPermissionType) -> SystemPermissionHandler {
        let singlePermissions = PlatformPermissionMapper.toPlatformSinglePermissions([permissionType])
        
        if let handler = systemPermissionHandlers.first(where: { handler in
            singlePermissions.contains(where: { handler.canHandle(simplePlatformPermission: $0) })
        }) {
            return handler
        } else {
            fatalError("No system permission handler can handle permission type: \(permissionType)")
        }
    }
}
