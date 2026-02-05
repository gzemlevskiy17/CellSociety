# Collections API Lab Discussion
### NAMES: Michael Li, Georgiy Zemlevskiy, Joseph Wang
### TEAM 1


## In your experience using these collections, are they hard or easy to use?
They are easy to use. The most commonly used collections share methods, so you will call the same methods no matter what collection you are using most of the time. It is also very easy to convert between collections like Set and List using the constructors. 

## In your experience using these collections, do you feel mistakes are easy to avoid?
For the collections and their implementations, we feel that mistakes are easy to avoid because they all share common functionalities that intutively work. Also, a compiler error will tell you if a method call is error prone.

## What methods are common to all collections (except Maps)?


## What is the purpose of each interface implemented by LinkedList?
The LinkedList implements: Serializable, Cloneable, Iterable<E>, Collection<E>, Deque<E>, List<E>, Queue<E>, SequencedCollection<E>. Cloneable lets field-for-field clones be created. Iterable lets it be iterated over (for each loop style). Collection lets it implement basic collection methods like add, remove, and size. Serializable means that the object can be turned into bytes and saved/sent to a disk. List interface contains methods like add, get, size, set, and remove. SequencedClec
## How many different implementations are there for a Set?
AbstractSet, ConcurrentHashMap.KeySetView, ConcurrentSkipListSet, CopyOnWriteArraySet, EnumSet, HashSet, JobStateReasons, LinkedHashSet, TreeSet
9 in t

## What is the purpose of each superclass of PriorityQueue?
The 3 super classes of PriorityQueue, Objec
t, AbstractCollections, and Abstract Queue, they add on functionalities and implement all methods to create a narrower and more defined class.

## What is the purpose of the Collections utility class?
The Collections utility class gives access for all the common univerasally implemented methods of the Java Collections Framework. This allows the user to apply methods



## API Characterics applied to Collections API

 * Easy to learn

 * Encourages extension

 * Leads to readable code

 * Hard to misuse
 
 