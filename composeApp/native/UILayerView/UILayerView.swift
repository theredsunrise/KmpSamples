import AVFoundation
import Foundation
import UIKit

@objc public protocol NSKeyValueObserving: NSObjectProtocol {
    @objc(observeValueForKeyPath:ofObject:change:context:) optional func observeValue(
        forKeyPath keyPath: String?,
        of object: Any?,
        change: [NSKeyValueChangeKey: Any]?,
        context: UnsafeMutableRawPointer?)
}

@objc public class UILayerView: UIView {

    public override init(frame: CGRect) {
        super.init(frame: frame)
    }

    public required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public override var frame: CGRect {
        didSet {
            self.layer.sublayers?.compactMap { $0 as? AVPlayerLayer }.forEach {
                $0.frame = self.bounds
            }
        }
    }
    public override var bounds: CGRect {
        didSet {
            self.layer.sublayers?.compactMap { $0 as? AVPlayerLayer }.forEach {
                $0.frame = self.bounds
            }
        }
    }
}
