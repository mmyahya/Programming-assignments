#include "GrayscaleImage.h"
#include <iostream>
//#include <cstring>  // For memcpy
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"
#include <stdexcept>


// Constructor: load from a file
GrayscaleImage::GrayscaleImage(const char* filename) {

    // Image loading code using stbi
    int channels;
    unsigned char* image = stbi_load(filename, &width, &height, &channels, STBI_grey);
    if (image == nullptr) {
        std::cerr << "Error: Could not load image " << filename << std::endl;
        exit(1);
    }
    // TODO: Your code goes here.
    // Dynamically allocate memory for a 2D matrix based on the given dimensions.
    set_data(height, width);

    // Fill the matrix with pixel values from the image
    for(int r = 0; r < height; r++){
        for(int c = 0; c < width; c++){
            data[r][c] = static_cast<int>(image[r*width + c]);
        }
    }
    
    // Free the dynamically allocated memory of stbi image
    stbi_image_free(image);
}

// Constructor: initialize from a pre-existing data matrix
GrayscaleImage::GrayscaleImage(int** inputData, int h, int w) : width(w), height(h) {
    // TODO: Your code goes here.
    // Initialize the image with a pre-existing data matrix by copying the values.
    // Don't forget to dynamically allocate memory for the matrix.
    set_data(h, w);
    for(int i = 0; i < h; i++){
        for(int j = 0; j < w; j++){
            data[i][j] = inputData[i][j];
        }
    }
}

// Constructor to create a blank image of given width and height
GrayscaleImage::GrayscaleImage(int w, int h) : width(w), height(h) {
    // TODO: Your code goes here.
    // Just dynamically allocate the memory for the new matrix.
    set_data(h, w);
}

// Copy constructor
GrayscaleImage::GrayscaleImage(const GrayscaleImage& other) {
    // TODO: Your code goes here.
    // Copy constructor: dynamically allocate memory and 
    // copy pixel values from another image.
    int height = other.get_height();
    int width = other.get_width();
    set_data(height, width);
    for(int r = 0; r < height; r++){
        memcpy(data[r], other.get_data()[r], sizeof(int) * width);
    }
    this->height = height;
    this->width = width;
}

// Destructor
GrayscaleImage::~GrayscaleImage() {
    // TODO: Your code goes here.
    // Destructor: deallocate memory for the matrix.
    if(data){
        for(int i = 0; i < get_height(); i++){
            delete[] data[i];
        }
        delete[] data;
    }
}

// Equality operator
bool GrayscaleImage::operator==(const GrayscaleImage& other) const {
    // TODO: Your code goes here.
    // Check if two images have the same dimensions and pixel values.
    int dim1[2] = {this->get_width(), this->get_height()};
    int dim2[2] = {other.get_width(), other.get_height()};
    if(dim1[0] == dim2[0] && dim1[1] == dim2[1]){
        for(int i = 0; i < dim1[1]; i++){
            for(int j = 0; j < dim1[0]; j++){
                if(this->get_pixel(i,j) != other.get_pixel(i,j)){
                    return false;
                }
            }
        }
        return true;
    }
    // If they do, return true.
    return false;
}

// Addition operator
GrayscaleImage GrayscaleImage::operator+(const GrayscaleImage& other) const {
    // Create a new image for the result
    // TODO: Your code goes here.
    // Add two images' pixel values and return a new image, clamping the results.
    GrayscaleImage result(width, height);
    for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
            int img1_val = this->get_pixel(i,j);
            int img2_val = other.get_pixel(i,j);
            result.set_pixel(i,j,std::min(255, std::max(0,img1_val + img2_val)));
        }
    }
    return result;
}

// Subtraction operator
GrayscaleImage GrayscaleImage::operator-(const GrayscaleImage& other) const {
    // Create a new image for the result
    // TODO: Your code goes here.
    // Subtract pixel values of two images and return a new image, clamping the results.
    GrayscaleImage result(width, height);
    for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
            int img1_val = this->get_pixel(i,j);
            int img2_val = other.get_pixel(i,j);
            result.set_pixel(i,j,std::min(255,std::max(0, img1_val - img2_val)));
            
        }
    }
    return result;
}

// Get a specific pixel value
int GrayscaleImage::get_pixel(int row, int col) const {
    return data[row][col];
}

// Set a specific pixel value
void GrayscaleImage::set_pixel(int row, int col, int value) {
    data[row][col] = value;
}

// Function to save the image to a PNG file
void GrayscaleImage::save_to_file(const char* filename) const {
    // Create a buffer to hold the image data in the format stb_image_write expects
    unsigned char* imageBuffer = new unsigned char[width * height];
    // Fill the buffer with pixel data (convert int to unsigned char)
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            imageBuffer[i * width + j] = static_cast<unsigned char>(data[i][j]);
        }
    }
    // Write the buffer to a PNG file
    if (!stbi_write_png(filename, width, height, 1, imageBuffer, width)) {
        std::cerr << "Error: Could not save image to file " << filename << std::endl;
    }

    // Clean up the allocated buffer
    delete[] imageBuffer;
}

void GrayscaleImage::set_data(int h, int w){
    data = new int*[h];
    for(int row = 0; row < h; row++){
        data[row] = new int[w];
    }
}