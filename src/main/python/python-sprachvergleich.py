import os
import io
import re
import time
import sys


def find_longest_word_for_language(list):
    length = 0
    longest_word = {}
    for word in list:
        if word['length'] > length:
            length = word['length']
            longest_word = word

            path = word['language']
            array = path.split("/")
            longest_word['language'] = array[len(array)-3]

    return longest_word


# loop through folder
if len(sys.argv) != 2:
    print("Please give the root folder of your txt-files as an argument.")
    exit(1)

rootdir = sys.argv[1]
print("Counting words for all txt-files from root-folder: %s" % rootdir)
longest_words = []
start = time.time()
for subdir, dirs, files in os.walk(rootdir):
    long_words = []
    for file in files:
        current_txt_file_path = os.path.join(subdir, file)

        # find longest word of file

        long_word = {}
        max_length = 0
        regex = r'\b\w+\b'
        with io.open(current_txt_file_path, 'r', encoding='utf8') as f:
            for line in f:
                for word in re.findall(regex, line):
                    if len(word) > max_length:
                        long_word['word'] = word
                        long_word['length'] = len(word)
                        long_word['language'] = current_txt_file_path
                        max_length = len(word)

        long_words.append(long_word)
    if len(long_words) > 0:
        longest_words.append(find_longest_word_for_language(long_words))


end = time.time()
print("Gesamte Laufzeit: %s secs" % str(end-start))
# print("Anzahl der Elemente: %s" % len(os.walk(rootdir)))

print("Longest word for each language:")
for long_word in longest_words:
    print("%s - %s - %s" % (long_word['language'], long_word['length'], long_word['word']))




# aggregate into results file