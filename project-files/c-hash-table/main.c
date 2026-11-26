
#include <stdio.h>
#include "hash_table.h"

int main()
{
    // e.g., create a hash table
    HashTable ht;

    // call function to initialise it
    initialise_hash_table(&ht);

    // call other functions
    int bucket = get_bucket_for_user(12);

    // do any tests / checks required
    printf("Bucket is: %d\n", bucket);

    return 0;
}
