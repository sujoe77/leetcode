# Classes
## Collections
* UnmodifiedXXX

    Return unmodified Collections, list, Map, Set, etc

* EMPTY_XXX / emptyXXXX

    when you need to return some empty collections, not support add, get, 
    some methods are empty like sort etc.

* SynchronizedXXX

    return a Synchronized collections, like List, Map etc
    
* CheckedXXXX

    return a collection with check type functions

    https://stackoverflow.com/questions/4662628/why-arent-collections-checkedmap-and-friends-used-more-commonly

* SingletonXXX

    return a singleton collection

* Sort related methods 
    
    reverse / sort / shuffle / swap / rotate

* Search methods

    binarySearch / indexOf / frequency

* Modifications

    fill / copy / replaceAll / addAll 

* Conversion

    ncopies / enumeration / list / newSetFromMap / asLifoQueue

* Set operation

    disjoint

## CollectionUtils

* boolean

    isSubCollection / isProperSubCollection / isEqualCollection / exists / ifFull

* addAll / addIgnoreNull

* collate

    merge sorted collections

* collect / transform

    Transforms all elements from input collection with the given transformer and adds them to the output collection
    
* containsAll / containsAny / countMatches

* union / intersection / disjunction / subtract

    disjunction

    Returns a Collection containing the exclusive disjunction (symmetric difference) of the given Iterables.

* emptyCollection / emptyIfNull

* extractSingleton

* filter / filterInverse / select / selectRejected 

* get*

* getCardinalityMap

* Collection operation

    intersection / union / retainAll / removeAll / containsAll

* empty

    isNotEmpty / isEmpty / EMPTY_COLLECTION / emptyIfNull / sizeIsEmpty 

* forAll
    
    forAll / forAllButLastDo
    
* Collection as result 

    synchronizedCollection / unmodifiableCollection / predicatedCollection / transformingCollection
    
* size / maxSize

* find

* reverseArray / permutations

