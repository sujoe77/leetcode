struct __attribute__((__packed__)) sdshdr8
{
    uint8_t len;         /* used */
    uint8_t alloc;       /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};

// double linked list
typedef struct listNode
{
    struct listNode *prev;
    struct listNode *next;
    void *value;
} listNode;

typedef struct list
{
    listNode *head;
    listNode *tail;
    unsigned long len;

    void *(*dup)(void *ptr);
    void (*free)(void *ptr);
    int (*match)(void *ptr, void *key);
} list;

typedef struct listIter
{
    listNode *next;
    int direction;
} listIter;


