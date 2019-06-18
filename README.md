# Information Retrieval

Practices of the course Information Retrieval

* RITika is the first practice, it uses Apache Tika 1.18, a toolkit that detects and extracts metadata and text from over a thousand different file types, you can download it [here](http://tika.apache.org). 
This program takes a document (PDF, HTML, TXT...) and returns its codification, type of document, language and all of its links. It also creates a file with all the words that appear in the document and its occurrences, to be later analyzed and build a graph showing that it complies Zipf's law.


* RIAnalyzer is the second one, it uses Apache Lucene 7.5.0, a high-performance text search engine library, you can download it [here](https://lucene.apache.org/core/).
This program uses part of the previous practice, it runs analyzers on diferent documents with the purpose of making an statistical study about the different tokens that are obtained when performing different types of analysis.

* P3 is an Information Retrieval System with a GUI, it uses both Apache Tika and Apache Lucene. 
This system indexes all questions and answers of the programming language R in StackOverfow. Data can be downloaded [here](https://www.kaggle.com/stackoverflow/rquestions). 
This systems implements a GUI that can be used to search questions and answers making use of AND, OR and NOT operations and filtering by facets. 
Facets have been implemented with problems because the professor didn't expect anyone to get that far in the practice, so we spent some hours trying to fix errors and left it the way it is implemented now.
