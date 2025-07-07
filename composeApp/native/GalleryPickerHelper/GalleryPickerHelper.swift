import PhotosUI
import UIKit

@objc public class GalleryPickerHelper: NSObject {
    @objc public static func canLoadImageObject(_ provider: NSItemProvider) -> Bool {
        return provider.canLoadObject(ofClass: UIImage.self)
    }

    @objc public static func loadImageObject(_ provider: NSItemProvider, completion: @escaping (UIImage?, Error?) -> Void) {
        provider.loadObject(ofClass: UIImage.self) { obj, error in
            completion(obj as? UIImage, error)
        }
    }
}