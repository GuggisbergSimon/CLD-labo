#!/usr/bin/env fish

set dict /usr/share/dict/words
set num_words (wc -l < $dict)

# Loop 10 times
for i in (seq 1 10)
        set random_word_title (shuf -n 1 $dict)
        set encoded_word_title (echo $random_word_title | sed 's/ /%20/g')
        set random_word_author (shuf -n 1 $dict)
        set encoded_word_author (echo $random_word_author | sed 's/ /%20/g')
        if test $i -lt 10
                http "http://localhost:8080/datastorewrite?_kind=book&author=$encoded_word_author&title=$encoded_word_title" > /dev/null
            else
                http "http://localhost:8080/datastorewrite?_kind=book&author=$encoded_word_author&title=$encoded_word_title"
            end
end