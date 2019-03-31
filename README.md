# AutoVersionInc
Create and test git hook commands

Ok so this repo is created to test git hooks and create custom code for some manipulations on with java

what is git hooks and what it does ?  
shortly:
Git hooks are scripts that Git executes before or after events such as: commit, push, and receive. Git hooks are a built-in feature - no need to download anything. Git hooks are run locally.

for more: https://githooks.com/

What i did?

I created jar file, which parses given file, which has some values in format "key = value" and increments value. This may be nedeed to automaticaly increas aplication version, 
like after each commit increas version of app. 


Usage

So i am using 2 hooks commit-msg and post-commit hooks. this hooks are already in .git/hook folder when git repo is created, but that files are local,
so you can not save them in cloud repo, that is why i saved that files in AutoVersionInc/someInnerProjectTest, to copy for future use.

in commit-msg hook bash script calls java with args: firs is jar file path which jar should be called, in this case for proper work both: AutoVersionInc/someInnerProjectTest/AutoVersionInc.jar and AutoVersionInc/someInnerProjectTest/lib/ folder should be copied in your project, where is file, which shoudl be parsed.
second argument is $1 which is file location where last commit is written this argument should not be changed and should be written as it is where it is(mean should be second after file location)
third is path to file, which should be parsed.
other arguments can be keys, which values should be increased.
square braces for key like aztelekomVersionCode(can be allowed on any key) means that check will be provided if last commit message contains [skip ci] befor version increase for this key, so version will be increased if and only if [skip ci] will be included in last commit, otherwise if [skip ci] will not be included in last commit message, version will not be increased!


after commit command commit-msg hook calls java, with args and increases version, but after version increas newly changed file is not commited and can not be commited from this call, because commit-msg hook already holds git instance and JGit can not commit any additional changes, here comes post-commit hook

in post-commit hook, same java file is called, but with other args: first should be commit command to identify what should do.
second is repo path.
third is file, which should be commited.
4) path to file, which should be parsed to get app version and then commited.
5) build version key
6) build version name key


and that is it 

just you get 2 commits instead of one!



