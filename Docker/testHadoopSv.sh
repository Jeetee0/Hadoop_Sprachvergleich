#!/bin/bash

# functions
#######################################################################
debug ()
{
  echo "--- $1 ---: $2"
}
#######################################################################
assert ()                 #  If condition false,
{                         #+ exit from script
                          #+ with appropriate error message.
  E_PARAM_ERR=98
  E_ASSERT_FAILED=99
  echo "Asserting..."


  if [ -z "$2" ]          #  Not enough parameters passed
  then                    #+ to assert() function.
    return $E_PARAM_ERR   #  No damage done.
  fi

  lineno=$2

  if [ ! $1 ] 
  then
    echo "Assertion failed:  \"$1\""
    echo "File \"$0\", line $lineno"    # Give name of file and line number.
    exit $E_ASSERT_FAILED
  else
    echo "Assertion successful."
    echo ""
  fi  
}    
#######################################################################


# this script will execute the hadoop_sv.jar and compare the results with known values:
# - it will assert the amount of german words with the input data (9) and also compare the longest word from /hadoop_sv/test_output/Deutsch/part-r-00000
# - also it will assert the two longest words (spanish & german) from /hadoop_sv/test_results/part-r-00000

if [ -z "$1" ]
then
  debug "ERR" "Please specify if you want to just analyze results (0) or also execute test case (1)"
  exit 0
fi

debug "INFO" "Script to test the behaviour of Hadoop_sv.jar (depending on test_textfiles.zip) started..."

if [ $1 = 0 ]; then				# user chose to execute testjob
  # remove destination folders:
  debug "INFO" "Removing destination folders..."
  rm -r /hadoop_sv/test_output/
  rm -r /hadoop_sv/test_results/

  # execute job
  debug "INFO" "Executing job..."
  $HADOOP_PREFIX/bin/hadoop jar /hadoop_sv/hadoop_sv.jar de.berlin.htw.Hadoop_sv /hadoop_sv/test_textfiles /hadoop_sv/test_output/ /hadoop_sv/test_results/

  # copy results from HDFS into container
  debug "INFO" "Copying results from HDFS into container..."
  $HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/test_output /hadoop_sv/
  $HADOOP_PREFIX/bin/hdfs dfs -get /hadoop_sv/test_results /hadoop_sv/
fi

# compare results with expected results file
# german output should have same amount of words as input files & also contain longest word: 'himmelherrgottssakkermentische'

debug "INFO" "Amount of files from folder: /hadoop_sv/test_textfiles/Deutsch/TXT/:"
ls /hadoop_sv/test_textfiles/Deutsch/TXT/ | wc -l
debug "INFO" "Amount of words found through job, located at: /hadoop_sv/test_output/Deutsch/part-r-00000:"
wc -l < /hadoop_sv/test_output/Deutsch/part-r-00000

a=$(ls /hadoop_sv/test_textfiles/Deutsch/TXT/ | wc -l)
b=$(wc -l < /hadoop_sv/test_output/Deutsch/part-r-00000)
condition="$a -eq $b"
assert "$condition" $LINENO

debug "INFO" "Output of german words:"
cat /hadoop_sv/test_output/Deutsch/part-r-00000

# asserting results - should contain two words: 'himmelherrgottssakkermentische' & 'circunstanciadamente'
debug "INFO" "Amount of languages: "
ls /hadoop_sv/test_textfiles/ | wc -l
debug "INFO" "Amount of longest words found for languages: "
wc -l < /hadoop_sv/test_results/part-r-00000

a=$(ls /hadoop_sv/test_textfiles/ | wc -l)
b=$(wc -l < /hadoop_sv/test_results/part-r-00000)
condition="$a -eq $b"
assert "$condition" $LINENO

debug "INFO" "Result file should contain words: 'himmelherrgottssakkermentische' &b 'circunstanciadamente'..."
debug "INFO" "Results:"
cat /hadoop_sv/test_results/part-r-00000

a=$(cat /hadoop_sv/test_results/part-r-00000 | grep -q 'himmelherrgottssakkermentische' ; echo $?)
b=0 	# 0 is returned if grep found word
condition="$a = $b"
assert "$condition" $LINENO

a=$(cat /hadoop_sv/test_results/part-r-00000 | grep -q 'circunstanciadamente' ; echo $?)
b='circunstanciadamente'
assert "$condition" $LINENO



