#!/bin/sh

#post_commit_git_hook_which_calls_java_AutoVersionInc.jar_to_commit_changed_buil.gradle_file
#to_call_jar_file_we_need_full_path_and_for_build_gralde_also_need_full_path
#we need post commit hook to commit changed build.gradle file
echo starting pist commit hook
java -jar F:\\netbeansPorjects\\AutoVersionInc\\someInnerProjectTest\\AutoVersionInc.jar commit F:\\netbeansPorjects\\AutoVersionInc someInnerProjectTest/build.gradle
STATUS="${?}"
echo "${STATUS}"


#echo $result>1
#for (( counter=0; $result>1; counter++ ))
#do echo "waiting java"
#done
#echo "java finished, see results in bash..."
#echo $result
exit 0

#if [ $result -eq 0 ]; 
#then
#exit 0
#else
#  echo "Version increasd failed"
#  echo "Aborting the commit. Run with --no-verify to ignore."
#  exit 1
#fi
