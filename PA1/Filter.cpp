#include "Filter.h"
#include <algorithm>
#include <cmath>
#include <vector>
#include <numeric>
#include <math.h>
#include <iostream>


double Filter::fill_kernel(bool is_gaussian, int x, int y, double sigma){
    return is_gaussian ? (1/(2*M_PI*sigma*sigma))*std::exp(-(x*x+y*y)/(2*sigma*sigma)) : 1;
}

double** Filter::makeKernel(int kernel_size, double sigma, bool is_gaussian){
    double** kernel = new double*[kernel_size];
    int kernel_offset = kernel_size / 2;//to loop the kernel in an x-y coordinates system; the range is [-1,1] for both x and y.
    for(int x = 0; x < kernel_size; x++){
        kernel[x] = new double[kernel_size];
    }
    //Wanted to construct the kernel in an x-y coordinates system logic
    for(int y =  -kernel_offset; y < kernel_offset + 1; y++){ 
        for(int x =  -kernel_offset; x < kernel_offset + 1; x++){
            kernel[y + kernel_offset][x + kernel_offset] = fill_kernel(is_gaussian, x,y,sigma);
        }
    }
    return kernel;
}

//Though of operation overload as well, but prefered, for this assignment, to include the Filter logic here.
void Filter::convolution(GrayscaleImage& image, double** kernel, int kernel_size){
    GrayscaleImage copy_image(image);
    double sum_values = 0;
    double sum_kernel = 0;
    int kernel_offset = kernel_size / 2;
    for(int i = 0; i < image.get_height(); i++){ //row
        for(int j = 0; j < image.get_width(); j++){ // column
            sum_values = 0;
            sum_kernel = 0;
            //Wanted to use the kernel it in an x-y coordinates system logic
            for(int y =  -kernel_offset; y < kernel_offset + 1; y++){
                for(int x =  -kernel_offset; x < kernel_offset + 1; x++){
                    int pixel_intensity = 0;
                    double kernel_value = kernel[y + kernel_offset][x + kernel_offset];
                    sum_kernel += kernel_value;
                    if(i + y < 0 || j + x < 0 || i + y > copy_image.get_height() - 1 || j + x > copy_image.get_width() - 1){ //Check the edges and handle them by ignoring them
                        kernel_value = 0;
                    }else{
                        pixel_intensity = copy_image.get_pixel(i + y, j + x);
                    }
                    sum_values += pixel_intensity * kernel_value;

                }
            }
            int val = std::floor(sum_values / sum_kernel);
            image.set_pixel(i,j, std::min(255,std::max(0,val)));
        }
    }
}

// Mean Filter
void Filter::apply_mean_filter(GrayscaleImage& image, int kernel_size) {
    // TODO: Your code goes here.
    // 1. Copy the original image for reference.
    // 2. For each pixel, calculate the mean value of its neighbors using a kernel.
    // 3. Update each pixel with the computed mean.
    double** kernel = makeKernel(kernel_size, 0, false);
    convolution(image, kernel, kernel_size);

    //Memory cleanup for the kernel
    for(int i = 0; i < kernel_size; i++){
        delete[] kernel[i];
    }
    delete[] kernel;
}

// Gaussian Smoothing Filter
void Filter::apply_gaussian_smoothing(GrayscaleImage& image, int kernel_size, double sigma) {
    // TODO: Your code goes here.
    // 1. Create a Gaussian kernel based on the given sigma value.
    // 2. Normalize the kernel to ensure it sums to 1.
    // 3. For each pixel, compute the weighted sum using the kernel.
    // 4. Update the pixel values with the smoothed results.
    double** kernel = makeKernel(kernel_size, sigma, true);
    convolution(image, kernel, kernel_size);

    //Memory cleanup for the kernel
    for(int i = 0; i < kernel_size; i++){
        delete[] kernel[i];
    }
    delete[] kernel;
}

// Unsharp Masking Filter
void Filter::apply_unsharp_mask(GrayscaleImage& image, int kernel_size, double amount) {
    // TODO: Your code goes here.
    // 1. Blur the image using Gaussian smoothing, use the default sigma given in the header.
    GrayscaleImage original(image);
    apply_gaussian_smoothing(image, kernel_size, 1.0);
    // 2. For each pixel, apply the unsharp mask formula: original + amount * (original - blurred).
    for(int i = 0; i < original.get_height(); i++){
        for(int j = 0; j < original.get_width(); j++){
            int val = (original.get_pixel(i,j) - image.get_pixel(i,j)) * amount + original.get_pixel(i,j);
            // 3. Clip values to ensure they are within a valid range [0-255].
            image.set_pixel(i,j,std::max(0,std::min(255, val)));
        }
    }
}