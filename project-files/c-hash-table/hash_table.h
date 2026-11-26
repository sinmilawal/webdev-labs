
#ifndef __HASH_TABLE_HEADER__
#define __HASH_TABLE_HEADER__

#include <stdbool.h>

#define HASH_TABLE_SIZE 11
#define MAX_PASSWORD_LEN 40

#define FUNCTION_NOT_IMPLEMENTED 100
#define ERROR 101
#define SUCCESS 102

// a struct to represent a user registered in the
// system and indexed in our hash table
typedef struct user
{
    int user_id;
    char password[MAX_PASSWORD_LEN + 1];
    struct user *next;
} User;

// a struct to represent a hash table of users
// implemented using chained hashing
typedef struct hash_table
{
    User *buckets[HASH_TABLE_SIZE];
} HashTable;

// functions implemented in hash_table.c
void initialise_hash_table(HashTable *ht);
int get_bucket_for_user(int user_id);
User *find_user(const HashTable *ht, int user_id);
int register_user(HashTable *ht, int user_id, char password[]);
int is_valid_password(const HashTable *ht, int user_id, char password[]);
void free_buckets(HashTable *ht);
int load_users_from_file(HashTable *ht, char filename[]);

#endif
