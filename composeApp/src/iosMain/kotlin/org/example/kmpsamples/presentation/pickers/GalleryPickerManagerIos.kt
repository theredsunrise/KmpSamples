package org.example.kmpsamples.presentation.pickers

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.kmpsamples.ui.galleryimagepicker.GalleryPickerHelper
import com.example.kmpsamples.ui.wrappervc.WrapperVC
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.example.kmpsamples.shared.ResultState
import org.example.kmpsamples.shared.ResultState.Failure
import org.example.kmpsamples.shared.ResultState.None
import org.example.kmpsamples.shared.ResultState.Success
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGContextRotateCTM
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.Foundation.NSItemProvider
import platform.Foundation.NSThread
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationAssetRepresentationModeCurrent
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIImageOrientation
import platform.UIKit.UINavigationController
import platform.UIKit.UIPresentationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.modalInPresentation
import platform.UIKit.presentationController
import platform.UIKit.transitioningDelegate
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.PI

class GalleryPickerManagerIos(private val coroutineScope: CoroutineScope) : NSObject(),
    PHPickerViewControllerDelegateProtocol, UIAdaptivePresentationControllerDelegateProtocol {

    private var _state: MutableStateFlow<ResultState<PlatformImage>> =
        MutableStateFlow(None)
    val state = _state.asStateFlow()

    @OptIn(ExperimentalForeignApi::class)
    fun presentPicker() {
        val controller = UIApplication.sharedApplication.getTopMostVisibleController() ?: return
        if (controller.presentedViewController != null) return

        _state.tryEmit(ResultState.Progress)
        val config = PHPickerConfiguration().apply {
            selectionLimit = 1L
            preferredAssetRepresentationMode = PHPickerConfigurationAssetRepresentationModeCurrent
            selection = PHPickerConfigurationSelectionOrdered
            filter = PHPickerFilter.imagesFilter()
        }
        val picker = PHPickerViewController(configuration = config)
        picker.transitioningDelegate = null
        picker.delegate = this
        val wrapperVC = WrapperVC(picker)
        wrapperVC.modalInPresentation = true
        wrapperVC.presentationController?.delegate = this
        wrapperVC.view.backgroundColor = UIColor.clearColor
        controller.presentViewController(wrapperVC, true) {
        }
    }

    override fun presentationControllerWillDismiss(presentationController: UIPresentationController) {
        check(NSThread.isMainThread)
        presentationController.delegate = null
        _state.tryEmit(None)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.delegate = null
        check(NSThread.isMainThread)
        coroutineScope.launch {
            supervisorScope {
                val deferred = async(Dispatchers.IO) {
                    didFinishPicking.firstOrNull()?.let { (it as? PHPickerResult)?.itemProvider }
                        ?.takeIf {
                            GalleryPickerHelper.canLoadImageObject(it)
                        }?.apply {
                            val result = loadImageObject(this)
                            _state.tryEmit(result)
                        } ?: run { _state.tryEmit(None) }
                }
                try {
                    deferred.await()
                } catch (e: Exception) {
                    _state.tryEmit(Failure(e))
                }
                picker.dismissModalViewControllerAnimated(true)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    suspend fun loadImageObject(itemProvider: NSItemProvider) =
        suspendCoroutine { context ->
            GalleryPickerHelper.loadImageObject(itemProvider) { data, error ->
                data?.let {
                    (it as? UIImage)?.toImageBitmap()
                }?.also {
                    context.resume(Success(it))
                }
                    ?: error?.also { context.resume(Failure(Exception(it.localizedDescription))) }
                    ?: { context.resume(Failure(NoSuchElementException())) }
            }
        }

    private fun ImageBitmap.toPlatformImage(): PlatformImage {
        return PlatformImageIos(this)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun UIImage.toImageBitmap(): PlatformImage? {
        val cgImage = CGImage ?: return null

        val width = CGImageGetWidth(cgImage).toInt()
        val height = CGImageGetHeight(cgImage).toInt()

        val (finalWidth, finalHeight) = when (imageOrientation) {
            UIImageOrientation.UIImageOrientationLeft,
            UIImageOrientation.UIImageOrientationRight,
            UIImageOrientation.UIImageOrientationLeftMirrored,
            UIImageOrientation.UIImageOrientationRightMirrored -> height to width

            else -> width to height
        }

        val bitsPerComponent = 8
        val bytesPerPixel = 4
        val bitmapInfo =
            CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value or kCGBitmapByteOrder32Big
        val colorSpace = CGColorSpaceCreateDeviceRGB()
        val bytesPerRow = finalWidth * bytesPerPixel
        val byteArray = ByteArray(finalHeight * bytesPerRow)

        byteArray.usePinned { pinned ->
            val context = CGBitmapContextCreate(
                data = pinned.addressOf(0),
                width = finalWidth.convert(),
                height = finalHeight.convert(),
                bitsPerComponent = bitsPerComponent.convert(),
                bytesPerRow = bytesPerRow.convert(),
                space = colorSpace,
                bitmapInfo = bitmapInfo
            ) ?: return null

            when (imageOrientation) {
                UIImageOrientation.UIImageOrientationDown -> {
                    println("**** UIImageOrientationDown")
                    CGContextTranslateCTM(context, finalWidth.toDouble(), finalHeight.toDouble())
                    CGContextRotateCTM(context, PI)
                }

                UIImageOrientation.UIImageOrientationLeft -> {
                    println("**** UIImageOrientationLeft")
                    CGContextTranslateCTM(context, finalWidth.toDouble(), 0.0)
                    CGContextRotateCTM(context, PI / 2)
                }

                UIImageOrientation.UIImageOrientationRight -> {
                    println("**** UIImageOrientationRight")
                    CGContextTranslateCTM(context, 0.0, finalHeight.toDouble())
                    CGContextRotateCTM(context, -PI / 2)
                }

                UIImageOrientation.UIImageOrientationUpMirrored -> {
                    println("**** UIImageOrientationUpMirrored")
                    CGContextTranslateCTM(context, finalWidth.toDouble(), 0.0)
                    CGContextScaleCTM(context, -1.0, 1.0)
                }

                UIImageOrientation.UIImageOrientationDownMirrored -> {
                    println("**** UIImageOrientationDownMirrored")
                    CGContextTranslateCTM(context, 0.0, finalHeight.toDouble())
                    CGContextScaleCTM(context, 1.0, -1.0)
                }

                UIImageOrientation.UIImageOrientationLeftMirrored -> {
                    println("**** UIImageOrientationLeftMirrored")
                    CGContextTranslateCTM(context, finalWidth.toDouble(), 0.0)
                    CGContextRotateCTM(context, PI / 2)
                    CGContextTranslateCTM(context, finalWidth.toDouble(), 0.0)
                    CGContextScaleCTM(context, -1.0, 1.0)
                }

                UIImageOrientation.UIImageOrientationRightMirrored -> {
                    println("**** UIImageOrientationRightMirrored")
                    CGContextTranslateCTM(context, 0.0, finalHeight.toDouble())
                    CGContextRotateCTM(context, -PI / 2)
                    CGContextTranslateCTM(context, finalWidth.toDouble(), 0.0)
                    CGContextScaleCTM(context, -1.0, 1.0)
                }

                else -> {
                }
            }
            CGContextDrawImage(
                context,
                CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()),
                cgImage
            )

            val imageInfo =
                ImageInfo(finalWidth, finalHeight, ColorType.RGBA_8888, ColorAlphaType.UNPREMUL)
            return Image.makeRaster(imageInfo, byteArray, bytesPerRow)
                .toComposeImageBitmap()
                .toPlatformImage()
        }
    }

    private fun UIApplication.getTopMostVisibleController(): UIViewController? {
        return connectedScenes.filterIsInstance<UIWindowScene>()
            .flatMap { scenes -> scenes.windows.map { it as UIWindow } }
            .filter { it.isKeyWindow() }
            .firstNotNullOfOrNull { it.rootViewController }?.getTopMostVisibleController()
    }

    private fun UIViewController.getTopMostVisibleController(): UIViewController {
        return when (this) {
            is UINavigationController -> visibleViewController
            is UITabBarController -> selectedViewController
            else -> presentedViewController
        }?.getTopMostVisibleController() ?: this
    }
}
