#include "SecretImage.h"
#include <fstream>
#include <vector>

// Constructor: split image into upper and lower triangular arrays
SecretImage::SecretImage(const GrayscaleImage& image) {
    // TODO: Your code goes here.
    // 1. Dynamically allocate the memory for the upper and lower triangular matrices.
    int size_upper = image.get_width() * (image.get_width() + 1) / 2;
    int size_lower = image.get_width() * (image.get_width() - 1) / 2;
    width = image.get_width();
    height = image.get_height();
    upper_triangular = new int[size_upper];
    lower_triangular= new int[size_lower];
    int index_upper = 0;
    int index_lower = 0;
    for(int i = 0; i < image.get_height(); i++){
        //Triggerig row change here
        int count = i;
        for(int j_upper = 0; j_upper < image.get_width(); j_upper++){
            if(count == 0){
                upper_triangular[index_upper] = image.get_pixel(i,j_upper);
                index_upper++;
            }else{
                count--;
            }
        }
        if(i > 0){
            for(int j_lower = 0; j_lower < i; j_lower++){
                lower_triangular[index_lower] = image.get_pixel(i,j_lower);
                index_lower++;
            }
        }
    }
    // 2. Fill both matrices with the pixels from the GrayscaleImage.
}

// Constructor: instantiate based on data read from file
SecretImage::SecretImage(int w, int h, int * upper, int * lower) {
    // TODO: Your code goes here.
    // Since file reading part should dynamically allocate upper and lower matrices.
    // You should simply copy the parameters to instance variables.
    width = w;
    height = h;
    upper_triangular = upper;
    lower_triangular = lower;
}


// Destructor: free the arrays
SecretImage::~SecretImage() {
    // TODO: Your code goes here.
    delete[] upper_triangular;
    delete[] lower_triangular;
    // Simply free the dynamically allocated memory
    // for the upper and lower triangular matrices.
}

// Reconstructs and returns the full image from upper and lower triangular matrices.
GrayscaleImage SecretImage::reconstruct() const {
    GrayscaleImage image(width, height);
    int upper_index = 0;
    int lower_index = 0;
    // TODO: Your code goes here.
    for(int i = 0; i < image.get_height(); i++){
        int count = i;
        for(int j = 0; j < image.get_width(); j++){
            if(count == 0){
                image.set_pixel(i,j,this->get_upper_triangular()[upper_index]);
                upper_index++;
            }else{
                image.set_pixel(i,j,this->get_lower_triangular()[lower_index]);
                lower_index++;
                count--;
            }
        }
    }
    return image;
}

// Save the filtered image back to the triangular arrays
void SecretImage::save_back(const GrayscaleImage& image) {
    // TODO: Your code goes here.
    // Update the lower and upper triangular matrices
    *this = SecretImage(image);
    // based on the GrayscaleImage given as the parameter.
}

// Save the upper and lower triangular arrays to a file
void SecretImage::save_to_file(const std::string& filename) {
    // TODO: Your code goes here.
    std::ofstream fileSaved(filename);
    if(!fileSaved.is_open()) throw std::runtime_error("File couldn't be created. Check permissions");
    int upper_size = get_width() * (get_width() + 1) / 2;
    int lower_size = get_width() * (get_width() - 1) / 2;
    int* upper_triangular_matrix = get_upper_triangular();
    int* lower_triangular_matrix = get_lower_triangular();

    // 1. Write width and height on the first line, separated by a single space.
    fileSaved << get_width() << " " << get_height() << std::endl;    

    // 2. Write the upper_triangular array to the second line.
    // Ensure that the elements are space-separated. 
    // If there are 15 elements, write them as: "element1 element2 ... element15"
    for(int i = 0; i < upper_size - 1; i++){
        fileSaved << upper_triangular_matrix[i] << " ";
    }
    fileSaved << upper_triangular_matrix[upper_size - 1];

    // 3. Write the lower_triangular array to the third line in a similar manner
    // as the second line.
    fileSaved << std::endl;
    for(int i = 0; i < lower_size - 1; i++){
        fileSaved << lower_triangular_matrix[i] << " ";
    }
    fileSaved << lower_triangular_matrix[lower_size - 1];
    if(fileSaved.fail()) throw std::runtime_error("Failed to write data to the created file");
    fileSaved.close();
}

void SecretImage::process_file(std::string line, int* data){
    std::string temp;
    std::istringstream stream(line);
    int index = 0;
    while(getline(stream, temp, ' ')){
        if(temp != " ") data[index++] = std::stoi(temp);
    }
}

// Static function to load a SecretImage from a file
SecretImage SecretImage::load_from_file(const std::string& filename) {
    // TODO: Your code goes here.
    // 1. Open the file and read width and height from the first line, separated by a space.
    std::ifstream secret_file(filename);
    if(!secret_file.is_open()) throw std::runtime_error("Error: File was not opened successfully. Try again");
    std::string line;
    getline(secret_file, line);
    std::vector<int> dimensions;
    std::string stored_string;
    std::istringstream stream(line);
    while(getline(stream, stored_string, ' ')){
        dimensions.push_back(std::stoi(stored_string));
    }
    int width = dimensions[0];
    int height = dimensions[1];

    // // 2. Calculate the sizes of the upper and lower triangular arrays.
    int upper_triangular_size = width * (width + 1) / 2;
    int lower_triangular_size = width * (width - 1) / 2;
    // // 3. Allocate memory for both arrays.
    int* upper_triangular = new int[upper_triangular_size];
    int* lower_triangular = new int[lower_triangular_size];
    // // 4. Read the upper_triangular array from the second line, space-separated.
    getline(secret_file, line);
    process_file(line, upper_triangular);
    // // 5. Read the lower_triangular array from the third line, space-separated.
    getline(secret_file, line);
    process_file(line, lower_triangular);
    // // 6. Close the file and return a SecretImage object initialized with the
    // //    width, height, and triangular arrays.
    secret_file.close();
    SecretImage secret_image(width,height,upper_triangular,lower_triangular);
    return secret_image;
}
// Returns a pointer to the upper triangular part of the secret image.
int * SecretImage::get_upper_triangular() const {
    return upper_triangular;
}

// Returns a pointer to the lower triangular part of the secret image.
int * SecretImage::get_lower_triangular() const {
    return lower_triangular;
}

// Returns the width of the secret image.
int SecretImage::get_width() const {
    return width;
}

// Returns the height of the secret image.
int SecretImage::get_height() const {
    return height;
}
