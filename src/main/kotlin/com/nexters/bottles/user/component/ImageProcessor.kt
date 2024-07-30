package com.nexters.bottles.user.component

import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sqrt

@Component
class ImageProcessor {

    fun blurImage(file: File): BufferedImage {
        val image = ImageIO.read(file)
        val blurredImage = applyGaussianBlur(image)
        return blurredImage
    }

    private fun applyGaussianBlur(image: BufferedImage): BufferedImage {
        val matrix = FloatArray(225) { 1 / 225f }
        val size = sqrt(matrix.size.toDouble()).toInt()
        val kernel = Kernel(size, size, matrix)
        val convolveOp = ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null)

        val blurredImage = BufferedImage(image.width, image.height, image.type)
        convolveOp.filter(image, blurredImage)
        return blurredImage
    }
}
