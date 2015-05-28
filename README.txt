Name: Kiran Bhat Gopalakrishna
NetId: kxb140230

*************************************************************************************************
To Compile:

javac Information_Retrieval_Hw2.java

*************************************************************************************************

To Run:

java Information_Retrieval_Hw2.java <path to cranfield directory> <path to file containing stopwords>

Note: Stopwords file is provided along with the source code

*************************************************************************************************

Programm Description:

I/p: Path to cranfield collection and stopwords
o/p: 4 files are generated as per the program requirement.

Description:

Building Uncompressed Index version 1 & 2:
Do not store stop-words in any version of your index. The files are parsed from the Cranfield collection and the dictionary is built for each of the words. The version 1 is built by removing the stopwords only where as Version 2 is built by removing the stop words plus stemming the words present in the cranfield collection. Path to the file containing stopwords is given as input to the program.The code for the Porter stemmer is  taken from online resource.

Building Compressed Files.
Compressed files are build based on 4 compression techniques namely Gamma compression, delta compression, front coding and blocked compression.
While compressing the version 1 file  blocked compression and gamma compression are used . Blocked compression is done using block value k=8 and for the posting file gamma encoding for the gaps between document-ids is done as shown in the program requirement example.
While compressing the  version 2 file , compression of the dictionary with front-coding and for the posting files delta codes to encode the gaps between document-ids have been used.



Writing the contents into the File
All the 4 versions i.e 2 uncompressed and 2 compressed files are written into the file using object output stream. Uncomressed files are written as the characters whereas compressed files are written as  bytes wherever necessary.

************************************************************************************************************************************

1. The program gets all the text characteristics in about  4 seconds

2. Program handling:
Programm replaces all the NON alpha numeric characters with " "(space) and seperates words based on the " "(space). However if it encounters . it replaces the word with ""(nothing).
A. The program handles upper and lower case letters to be the same. Ex: People, PEOPLE, People, PEoplE are all same. 
B.  Words with "-" are seperated into two words based on hyphen. Ex:middle-class is treated as 2 words 'middle' and 'class'.
C. Possessives divide the word into two halves. Ex: university's is treated as two words 'university' and 's'
D. The acronyms are treated as 1 word. Ex: U.S is treated as 'us'

3. Datastructures  & Algorithms used: 

A) Hash Map is used to store all the tokens and stemmed tokens.
B) An ArraList is used for computing distinct words in each file of cranfield directory. 
C) Stemmer algorithm to stem the words for 2nd vesion.
d) Stopword List is used to remove stopword (path to file containing stopwords shouls be provided by user)

****************************************************************************************************