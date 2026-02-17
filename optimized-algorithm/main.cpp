#include <vector>
#include <cstdint>
#include <bit>

class samples_and_features{
    //contains a vector of samples, whose binary place values correspond to features
    

private: 
    std::vector<uint64_t> samples;
    uint64_t feature_restriction_mask = ~0ULL;

public: 
    
};