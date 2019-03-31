package com.nodrex.autoversioninc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * This class is used to parse Android's build.grade file and automatically increase build version numbers.
 * @author Nodrex
 * @version 1.0
 * @since 2019
 */
public class AutoVersionInc {
   
    public static final String NEW_LINE = "\n";
    public static final String EMPTY_STRING = "";
    public static final String COMMIT_STR_COMMAND = "commit";
    public static final String SKIP_CI = "[skip ci]";
    public static final String QUOTATION = "\"";
    
    public static void main(String args[]) {
        //args = new String[]{".git/COMMIT_EDITMSG", "F:\\netbeansPorjects\\AutoVersionInc\\someInnerProjectTest\\build.gradle", "versionBuild", "aztelekomVersionCode"}; //to test parsing and version inc
        //args = new String[]{"commit", "F:\\netbeansPorjects\\AutoVersionInc", "someInnerProjectTest/build.gradle"}; //to test commit from git
        //abouv code is for testing purposes only and shoudl be commented on prod version
        
        if(args == null || args.length <= 3){
            System.out.println(ConsoleColors.RED +
                    "Given args are empty or it is not enough: args should be at list 3. first always should be $1 to get file location where last commit message is. Second always should be file name(can be full path) which should be parsed, other args shouldl be variable names like(versionBuild or aztelekomVersionCode or both) which value should be increasd (they should be represented in format key == value), so keep in mind that and try again with correct or enough args!!!\n"
                    + "or args should be at list 5. first commit command to detect that commit is needed, second repo path and third should be file path , which should be parsed to read version code and name and committed, 4 should be version code key and 5 should be version name to read!!!\n" + ConsoleColors.RESET);
            System.exit(1);
        }
        if(args[0].equalsIgnoreCase(COMMIT_STR_COMMAND)){
            if(args.length < 5){
                System.out.println(ConsoleColors.RED + "Given args should be at list 5. first commit command to detect that commit is needed, second repo path and third should be file path , which should be parsed to read version code and name and committed, 4 should be version code key and 5 should be version name to read!!!\n" + ConsoleColors.RESET);
                System.exit(1);
            }
            commitChangedFile(args);
        }else parsAndIncreasVersion(args);
    }
   
    public static void parsAndIncreasVersion(String args[]) {
        try {
            System.out.println("Git commit-msg hook started...");
            String fileName = args[1];
            System.out.println("file to pars: " + fileName);
            String data = new String(Files.readAllBytes(Paths.get(fileName)));
            
            data = increasVersion(data, args);

            FileOutputStream fileOut = new FileOutputStream(new File(fileName));
            fileOut.write(data.getBytes());
            fileOut.close();

            System.out.println(ConsoleColors.GREEN + "given file was repleced with increasd version!" + ConsoleColors.RESET);
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Unfortunately there was some error while trying to parse build gradle file: " + e.toString() + ConsoleColors.RESET);
            System.exit(1);
        }
    }
    
    private static String increasVersion(String data, String args[]) throws IOException, GitAPIException {
        String lastCommitMessage = new String(Files.readAllBytes(Paths.get(args[0])));
        boolean skipCi = lastCommitMessage.contains(SKIP_CI);
        for (int i= 2; i< args.length; i++) {
            String varName = args[i];
            if(varName.contains("[") && varName.contains("]")){
                if(skipCi) {
                    //if commit message contains skip ci, igonore this key and continue with ather key
                    System.out.println("commit message has [skip si], so ignoring " + varName);
                    continue;
                }else {
                    varName = varName.substring(1, varName.length()-1);
                    System.err.println("last commit message does not contains [skip ci], so removing square brases to: " + varName);
                }
            }
            System.out.println("Trying to increase " + varName + " number ...");
            
            String var = getStrVariable(data, varName);
            if(var == null) continue;
            int value = getVariable(var);
            if(value < 0) continue;

            System.out.println("current value: " + value);
            int newValue = value + 1;
            System.out.println("new value which will be commited: " + newValue);
            String newVar = var.replace(String.valueOf(value), String.valueOf(newValue));
            data = data.replace(var, newVar);
        }
        return data;
    }
    
    private static String getStrVariable(String data, String varName){
        int index = data.indexOf(varName);
        if(index < 0) {
            System.out.println("Can not find " + varName + " in file, so skipping" );
            return null;
        }
        String var = data.substring(index);
        return var.substring(0, var.indexOf(NEW_LINE));
    }
    
    private static int getVariable(String var){
        if(var == null) return -1;
        int lastIndex = var.length();
        for (int j = var.length() - 4; j < lastIndex; j++) {
            try {
                String sub = var.substring(j, lastIndex - 1);
                sub = sub.trim();
                return Integer.valueOf(sub);
            } catch (NumberFormatException e) {}
        }
        return -1;
    }
    
    private static String getVersion(String fileName, String versionKey, String versionNameKey) throws IOException{
        String data = new String(Files.readAllBytes(Paths.get(fileName)));
        int version = getVariable(getStrVariable(data, versionKey));
        String versionName = getStrVariable(data, versionNameKey);
        int startIndex = versionName.indexOf(QUOTATION);
        int lastIndex = versionName.lastIndexOf(QUOTATION);
        System.err.println("versionName: " + versionName + " , " + startIndex + " , " + lastIndex);
        versionName = versionName.substring(startIndex, lastIndex);
        return versionName + version;
    }
    
    private static void commitChangedFile(String[] args){
        System.out.println(NEW_LINE);
        String repo = args[1];
        String file = args[2];
        try {
            Git git = Git.open(new File(repo));
            String lastCommitMessage = getLastCommitMessage(git);
            System.out.println("post commit started");
            System.out.println("trying to commit file...");
            printData(repo, file);
            System.out.println("branch: " + git.getRepository().getBranch());
            lastCommitMessage = lastCommitMessage.replace(NEW_LINE, "");
            System.err.println("Last commit message: " + lastCommitMessage);
            CommitCommand commit = git.commit();
            commit.setOnly(file); //aucileblad gayofit unda gadaeces da ara sleshit!
            commit.setNoVerify(true);
            System.out.println("disable pre commit hook");
            String commitMessage = "App version: " + getVersion(file, args[3], args[4]) + " " + lastCommitMessage;
            commit.setMessage(commitMessage);
            commit.call();
            System.out.println(ConsoleColors.GREEN + "commit finished!" + ConsoleColors.RESET);
        } catch (JGitInternalException jgie){
            System.out.println(ConsoleColors.RED + "Unfortunately problem in commit from java: " + jgie.toString());
            printData(repo, file);
            Throwable cause = jgie.getCause();
            if(cause != null) System.out.println(cause.getMessage());
            System.out.println(jgie.getMessage() + ConsoleColors.RESET);
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Unfortunately problem in commit from java: " + e.toString() + ConsoleColors.RESET);
            printData(repo, file);
        }
    }
    
    private static String getLastCommitMessage(Git git) throws GitAPIException{
        //check if is enithing to commit
        Status status = git.status().call();
        Set<String> changes = status.getUncommittedChanges();
        if(changes.size() <= 0) System.exit(0); //there is no files to commit and probably this code was called from second post commit hook, which shouldb ignored!

        LogCommand logCommand = git.log();
        Iterable<RevCommit> commits = logCommand.call();

        for (RevCommit rc : commits) {
            if(rc == null) continue;
            String commitMessage = rc.getFullMessage();
            if(commitMessage == null) continue;
            return commitMessage;
        }
        return null;
    }
    
    private static void printData(String repo, String file){
        System.out.println("Given params to commit:");
        System.out.println("repo: " + repo);
        System.out.println("file: " + file);
    }
    
}
