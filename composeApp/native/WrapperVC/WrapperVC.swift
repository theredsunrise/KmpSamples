import UIKit
import PhotosUI

@objc public class WrapperVC: UIViewController {
    private let childVC: UIViewController

    @objc public init(childViewController: UIViewController) {
        self.childVC = childViewController
        super.init(nibName: nil, bundle: nil)

        addChild(childVC)
        view.addSubview(childVC.view)
        childVC.view.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
                                        childVC.view.topAnchor.constraint(equalTo: view.topAnchor),
                                        childVC.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
                                        childVC.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                                        childVC.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
                                    ])

        childVC.didMove(toParent: self)
    }

    @objc required public init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override public func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        detachChild()
    }

    private func detachChild() {
        childVC.willMove(toParent: nil)
        childVC.view.removeFromSuperview()
        childVC.removeFromParent()
    }

    deinit {
        detachChild()
    }
}
