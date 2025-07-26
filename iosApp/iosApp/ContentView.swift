import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.all)
                .onOpenURL { incomingURL in
                    print("App was opened via URL: \(incomingURL)")
                    handle(url: incomingURL)
                }
    }
    
    func handle(url: URL) {
        guard url.scheme == "example" else {
            return
        }
        guard url.host == "kmp_samples.com" else {
            print("Invalid host")
            return
        }
        let pathComponents = url.pathComponents
        guard pathComponents.count == 3 else {
            print("Invalid path structure")
            return
        }
        guard pathComponents[1] == "test" else {
            print("Expected 'test' in path")
            return
        }
        IntentHandler.shared.emitData(uri: url.absoluteString)
    }
}



