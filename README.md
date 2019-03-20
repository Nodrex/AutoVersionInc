# AutoVersionInc
Create and test git pre hook comands

Ok so this repo is created to test git hooks and create custom code for some manipulations on with java

what is git hooks and what it does ?  
shortly:
Git hooks are scripts that Git executes before or after events such as: commit, push, and receive. Git hooks are a built-in feature - no need to download anything. Git hooks are run locally.

for more: https://githooks.com/

What i did?

I created jar file, which parses given file, which has some values in format "key = value" and increments value. This may be nedeed to automaticaly increas aplcation version, 
like after each commit increas version of app. 


Usage

So i am using 2 hooks pre-commit and post-commit hooks. this hooks arre already in .git /hook folder when git repo is created, but that files are local,
so you can not save them in cloud repo., that is why i saved that files in AutoVersionInc/someInnerProjectTest, to copy for future use.

in pre-commit hook bash screept calls java with args: firs is jar file path which jar should be called, in this case for proper work both AutoVersionInc/someInnerProjectTest/AutoVersionInc.jar and AutoVersionInc/someInnerProjectTest/lib/ folder should be copied in your project, where is file, which shoudl be parsed.
second argument is file, which should be parsed.
other arguments can be keys, which values should be increased

after commit command pre-commit hook calls java, with args and increases version, but after version increas newly changed file is not commited and can not be commited from this call, because pre-commit hook already holds git instance and JGit can not commit any additional changes, here comes post-commit hook

in post-commit hook, same java file is called, but with other args: first should be commit command to identify what should do.
second is repo path.
third is file, which should be commited.

and that is it 

just you get 2 commits instead of one!



