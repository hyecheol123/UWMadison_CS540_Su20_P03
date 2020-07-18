# UWMadison_CS540_Su20_P03
Assignment Webpage: http://pages.cs.wisc.edu/~yw/CS540P3S20E.htm  
Repository for the 3rd programming assignment of UW-Madison CS540 Summer 2020 course (Introduction to Artificial Intelligence)


## Goals
We are required to build and simulate a simple **Markov chain model** based on a movie script.
We are going to use the model in order to generate new sentences.
Moreover, we are required to build a **Naive Bayes classifier** to distinguish sentences from the script and sentences from a fake script.  
To mitigate the problem caused by the size of English vocabularies (more than 170,000 words), we will use characters (26 characters) as feature instead of words.


## Dataset
Using a script of a movie: select from *Inception*, *Hotel Rwanda*, *Warrior*. The script can be retrieved from IMSDb: [here](https://www.imsdb.com/).  
I choose [*Inception*](https://www.imsdb.com/scripts/Inception.html).

### Data preprocessing
To retrieve script, need to manually copy and paste the scripts and save it as txt file.  
Also, need to make everything lower case, remove all characters except for letters and space, and substitute consequtive spaces by one single space.


## Tasks
- Data Preprocessing, Unigram Model
  - Related Question: Q1, Q2
- Construct the bigram character (letters + space) transition probability table. Put "space" first then "a", "b", "c", ..., "z". It should be a 27 by 27 matrix.
  - Related Question: Q3, Q4
- Construct the trigram transition probability table. It could be a 27 by 27 by 27 array or a 729 by 27 matrix.
- Generate 26 sentences consists of 1000 characters each using the trigram model starting from "a", "b", "c", ..., "z". You should use the bigram model to generate the second character and switch to the bigram model when the current two-character sequence never appeared in the script.
- Train a Naive Bayes classifier. You should use an uniform prior, compute the likelihood Pr{Letter | Document}, compute the posterior probabilities Pr{Document | Letter} and test your classifier on the 26 random sentences you generated

### Key Ideas
- Using Key-value pair (Use (Hash)Map data structure)
  - Use character(String) as a key, and associated probability as value
- Replace string token(character) to empty string and compare the length of original script and replaced string to count the number of occurance.


## Questions
- **Q1**  
  enter the name of the movie script
- **Q2**  
  (`unigram`) Input the unigram probabilities (27 numbers, comma-separated, rounded to 4 decimal places, "space" first, then "a", "b", ...).
  - Test for data-preprocessing, [counting function](), and [probability calculation]().
- **Q3**  
  (`bigram`) Input the bigram transition probabilities without Laplace smoothing (27 lines, each line containing 27 numbers, comma-separated, rounded to 4 decimal places, "space" first, then "a", "b", ...).
  - Test for [counting function]() and [probability calculation]().
- **Q4**  
  (`bigram_smooth`) Input the bigram transition probabilities with Laplace smoothing (27 lines, each line containing 27 numbers, comma-separated, rounded to 4 decimal places, "space" first, then "a", "b", ...).
  - Test for [probability calculation]().