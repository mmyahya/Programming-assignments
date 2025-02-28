#include "Crypto.h"
#include "GrayscaleImage.h"

// Extract the least significant bits (LSBs) from SecretImage, calculating x, y based on message length
std::vector<int> Crypto::extract_LSBits(SecretImage& secret_image, int message_length) {
    std::vector<int> LSB_array;
    // TODO: Your code goes here.

    // 1. Reconstruct the SecretImage to a GrayscaleImage.
    GrayscaleImage image = secret_image.reconstruct();
    
    // 2. Calculate the image dimensions.
    int width = image.get_width();
    int height = image.get_height();

    // 3. Determine the total bits required based on message length.
    int total_bits = message_length * 7;

    // 4. Ensure the image has enough pixels; if not, throw an error.
    if(width * height < total_bits) throw std::runtime_error("Check the Image Dimensions");

    // 5. Calculate the starting pixel from the message_length knowing that  
    //    the last LSB to extract is in the last pixel of the image.
    int starting_1d_position = (width * height - total_bits);
    int starting_row = (starting_1d_position + 1) / image.get_height();
    int starting_column = starting_1d_position - image.get_width() * starting_row;

    // 6. Extract LSBs from the image pixels and return the result.
    for(int i = starting_row; i < image.get_height(); i++){
        if(i > starting_row){
            starting_column = 0; // to restart the column back to zero after the passing the starting_row
        }
        for(int j = starting_column; j < image.get_width(); j++){
            std::bitset<8> image_pixel_binary(image.get_pixel(i,j));
            LSB_array.push_back(image_pixel_binary[0]);
        }
    }
    return LSB_array;
}


// Decrypt message by converting LSB array into ASCII characters
std::string Crypto::decrypt_message(const std::vector<int>& LSB_array) {
    std::string message;
    // TODO: Your code goes here.

    // 1. Verify that the LSB array size is a multiple of 7, else throw an error.
    if(LSB_array.size() % 7 != 0) throw std::runtime_error("LSB array size is not a multiple of 7");

    // 2. Convert each group of 7 bits into an ASCII character.
    // 3. Collect the characters to form the decrypted message.
    int multiplier_checker = 1;
    std::string charachters;
    for(int bit : LSB_array){
        charachters += std::to_string(bit);
        if(multiplier_checker % 7 == 0){
            std::bitset<7> binary_to_char(charachters);
            message+=char(static_cast<int>(binary_to_char.to_ulong())); //converts the bitset to unsigned int and back to integer and back to charachter.
            charachters = "";
        }
        multiplier_checker++;
    }
    // 4. Return the resulting message.
    return message;
}

// Encrypt message by converting ASCII characters into LSBs
std::vector<int> Crypto::encrypt_message(const std::string& message) {
    std::vector<int> LSB_array;
    std::vector<char> split_msg(message.begin(), message.end());
    // TODO: Your code goes here.
    
    for(int i = 0; i < split_msg.size(); i++){
        // 1. Convert each character of the message into a 7-bit binary representation.
        //    You can use std::bitset.
        int ascii_value = int(split_msg[i]);
        std::bitset<7> binary_pixel_value(ascii_value);
        // 2. Collect the bits into the LSB array.
        for(auto elem : binary_pixel_value.to_string()){
            int num = elem - '0';
            LSB_array.push_back(num);
        }
    }
    // 3. Return the array of bits.
    return LSB_array;
}

// Embed LSB array into GrayscaleImage starting from the last bit of the image
SecretImage Crypto::embed_LSBits(GrayscaleImage& image, const std::vector<int>& LSB_array) {
    // TODO: Your code goes here.

    // 1. Ensure the image has enough pixels to store the LSB array, else throw an error.
    if(image.get_width() * image.get_height() < LSB_array.size()) throw std::runtime_error("Not enough space to store your message.");
    
    // 2. Find the starting pixel based on the message length knowing that  
    //    the last LSB to embed should end up in the last pixel of the image.
    int starting_1d_position = (image.get_width() * image.get_height() - LSB_array.size());
    int starting_row = (starting_1d_position + 1) / image.get_height();
    int starting_column = starting_1d_position - image.get_width() * starting_row;
    int current_bit = 0;

    //3. Iterate over the image pixels, embedding LSBs from the array.
    for(int i = starting_row; i < image.get_height(); i++){
        if(i > starting_row){
            starting_column = 0;
        }
        for(int j = starting_column; j < image.get_width(); j++){
            if(current_bit >= LSB_array.size()){
                break;
            }
            std::bitset<8> image_pixel_binary(image.get_pixel(i,j));
            int LSB_val = LSB_array[current_bit];
            current_bit++;
            image_pixel_binary[0] = LSB_val;
            image.set_pixel(i,j,image_pixel_binary.to_ulong());
        }
    }
    
    // 4. Return a SecretImage object constructed from the given GrayscaleImag
    //    with the embedded message.
    SecretImage secret_image(image);
    return secret_image;
}