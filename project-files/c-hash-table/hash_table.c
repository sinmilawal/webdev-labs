
#include "hash_table.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
 * set every bucket to NULL so the table starts empty
 */
void initialise_hash_table(HashTable *ht)
{
    int i;

    // check that the table pointer is valid
    if (ht == NULL)
    {
        return;
    }

    // go through every bucket and mark it as empty
    for (i = 0; i < HASH_TABLE_SIZE; i++)
    {
        ht->buckets[i] = NULL;
    }
}

/*
 * calculate which bucket a user ID belongs in
 * return -1 if the ID is outside the allowed range
 */
int get_bucket_for_user(int user_id)
{
    // check that the ID is within the allowed range
    if (user_id < 1 || user_id > 2139)
    {
        return -1;
    }

    // use the remainder to calculate the bucket number
    return user_id % HASH_TABLE_SIZE;
}

/*
 * find a user by their ID
 * search the linked list because users can share a bucket
 */
User *find_user(const HashTable *ht, int user_id)
{
    int bucket;
    User *current;

    // check that the table pointer is valid
    if (ht == NULL)
    {
        return NULL;
    }

    // work out which bucket should contain this user
    bucket = get_bucket_for_user(user_id);

    if (bucket == -1)
    {
        return NULL;
    }

    // start at the first user in the bucket
    current = ht->buckets[bucket];

    // check each user until we find the right ID
    while (current != NULL)
    {
        if (current->user_id == user_id)
        {
            // return a pointer to the user we found
            return current;
        }

        // move to the next user in the list
        current = current->next;
    }

    // the user was not found
    return NULL;
}

/*
 * add a new user or update an existing user's password
 */
int register_user(HashTable *ht, int user_id, char password[])
{
    int bucket;
    User *user;
    User *new_user;

    // check that the table and password pointers are valid
    if (ht == NULL || password == NULL)
    {
        return ERROR;
    }

    // find the correct bucket for this ID
    bucket = get_bucket_for_user(user_id);

    if (bucket == -1)
    {
        return ERROR;
    }

    // make sure the password fits in the user structure
    if (strlen(password) > MAX_PASSWORD_LEN)
    {
        return ERROR;
    }

    // check whether this user is already registered
    user = find_user(ht, user_id);

    if (user != NULL)
    {
        // update the password instead of adding a duplicate
        strcpy(user->password, password);
        return SUCCESS;
    }

    // make space for the new user
    new_user = malloc(sizeof(User));

    if (new_user == NULL)
    {
        return ERROR;
    }

    // store the new user's details
    new_user->user_id = user_id;
    strcpy(new_user->password, password);

    // put the new user at the start of the linked list
    new_user->next = ht->buckets[bucket];
    ht->buckets[bucket] = new_user;

    return SUCCESS;
}

/*
 * check whether the supplied password matches the stored one
 */
int is_valid_password(const HashTable *ht, int user_id, char password[])
{
    User *user;

    // check that the table and password pointers are valid
    if (ht == NULL || password == NULL)
    {
        return ERROR;
    }

    // make sure the supplied password is within the length limit
    if (strlen(password) > MAX_PASSWORD_LEN)
    {
        return ERROR;
    }

    // find the user with this ID
    user = find_user(ht, user_id);

    if (user == NULL)
    {
        // a user who is not registered cannot log in
        return ERROR;
    }

    // compare the supplied password with the stored password
    if (strcmp(user->password, password) == 0)
    {
        return SUCCESS;
    }

    // the passwords do not match
    return ERROR;
}

/*
 * free all users in the hash table and empty every bucket
 */
void free_buckets(HashTable *ht)
{
    int i;
    User *current;
    User *next_user;

    // check that the table pointer is valid
    if (ht == NULL)
    {
        return;
    }

    // go through every bucket
    for (i = 0; i < HASH_TABLE_SIZE; i++)
    {
        current = ht->buckets[i];

        // free each user in this bucket's linked list
        while (current != NULL)
        {
            // save the next user before freeing the current one
            next_user = current->next;

            free(current);

            // move on to the next user
            current = next_user;
        }

        // mark the bucket as empty again
        ht->buckets[i] = NULL;
    }
}

/*
 * load user IDs and passwords from a text file
 */
int load_users_from_file(HashTable *ht, char filename[])
{
    FILE *file;
    char line[256];
    char password[MAX_PASSWORD_LEN + 1];
    char extra;
    char *position;
    int user_id;
    int fields;

    // check that the table and filename are valid
    if (ht == NULL || filename == NULL || filename[0] == '\0')
    {
        return ERROR;
    }

    // open the file so we can read its contents
    file = fopen(filename, "r");

    if (file == NULL)
    {
        // return an error if the file could not be opened
        return ERROR;
    }

    // read the file one line at a time
    while (fgets(line, sizeof(line), file) != NULL)
    {
        // skip any spaces at the start of the line
        position = line;

        while (*position == ' ' || *position == '\t' ||
               *position == '\n' || *position == '\r')
        {
            position++;
        }

        // ignore empty lines
        if (*position == '\0')
        {
            continue;
        }

        // read the user ID and password
        // the extra field helps detect unexpected extra data
        fields = sscanf(position, "%d %40s %c",
                        &user_id, password, &extra);

        // check that the line contains exactly two fields
        if (fields != 2)
        {
            fclose(file);
            return ERROR;
        }

        // add the user to the hash table
        if (register_user(ht, user_id, password) != SUCCESS)
        {
            fclose(file);
            return ERROR;
        }
    }

    // check whether the file could not be read properly
    if (ferror(file))
    {
        fclose(file);
        return ERROR;
    }

    // close the file when we are finished
    fclose(file);

    return SUCCESS;
}
