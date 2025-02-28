#ifndef FILTER_H
#define FILTER_H

#include "GrayscaleImage.h"

class Filter {
public:
    // Apply the Mean Filter
    static void apply_mean_filter(GrayscaleImage& image, int kernelSize = 3);

    // Apply Gaussian Smoothing Filter
    static void apply_gaussian_smoothing(GrayscaleImage& image, int kernelSize = 3, double sigma = 1.0);

    // Apply Unsharp Masking Filter
    static void apply_unsharp_mask(GrayscaleImage& image, int kernelSize = 3, double amount = 1.5);

    // Fill a kernel cell with a value: One or a Gaussian value;
    static double fill_kernel(bool is_gaussian, int x, int y, double sigma);

    // Create a kernel;
    static double** makeKernel(int kernelSize, double sigma, bool is_gaussian);

    //Convolution Operation for both mean and gaussian; Same logic
    static void convolution(GrayscaleImage& image, double** kernel, int kernelSize);
};

#endif // FILTER_H
