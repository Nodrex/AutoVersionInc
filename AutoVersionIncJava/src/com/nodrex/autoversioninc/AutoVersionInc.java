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
    
    public static void main(String args[]) {
        //args = new String[]{"F:\\netbeansPorjects\\AutoVersionInc\\someInnerProjectTest\\build.gradle", "versionBuild", "aztelekomVersionCode"}; //to test parsing and version inc
        //args = new String[]{"commit", "F:\\netbeansPorjects\\AutoVersionInc", "someInnerProjectTest/build.gradle"}; //to test commit from git
        //abouv code is for testing purposes only and shoudl be commented on prod version
        
        if(args == null || args.length <= 0){
            System.out.println(
                    "Given args are empty or it is not enough: args should be at list 2. first always should be file name(can be full path) which should be parsed, other args shouldl be variable names like(versionBuild or aztelekomVersionCode or both) which value should be increasd (they should be represented in format key == value), so keep in mind that and try again with correct or enough args!!!\n"
                    + "or args should be at list 3. first commit command to detect that commit is needed, second repo path and third should be file path , which should be committed!!!\n");
            System.exit(1);
        }
        if(args[0].equalsIgnoreCase(COMMIT_STR_COMMAND)){
            if(args.length < 3){
                System.out.println("Given args should be at list 3. first commit command to detect that commit is needed, second repo path and third should be file path , which should be committed!!!\n");
                System.exit(1);
            }
            commitChangedFile(args);
        }else parsAndIncreasVersion(args);
    }
   
    public static void parsAndIncreasVersion(String args[]) {
        try {
            System.out.println("Git prepare-commit-msg hook started...");
            String fileName = args[1];
            System.out.println("file to pars: " + fileName);
            String data = new String(Files.readAllBytes(Paths.get(fileName)));
            
            data = go(data, args);

            FileOutputStream fileOut = new FileOutputStream(new File(fileName));
            fileOut.write(data.getBytes());
            fileOut.close();

            System.out.println("given file was repleced with increasd version!");
            System.out.println("Git prepare-commit-msg hook finished :)");
            System.out.println("continuing commit...");  
        } catch (Exception e) {
            System.out.println("Unfortunately there was some error while trying to parse build gradle file: " + e.toString());
            System.exit(1);
        }
    }
    
    private static String go(String data, String args[]) throws IOException, GitAPIException {
        Git git = Git.open(new File(args[0]));
        boolean skipCi = hasSkipCiInCommitMessage(git);
        for (int i= 2; i< args.length; i++) {
            String varName = args[i];
            if(varName.contains("[") && varName.contains("]")){
                if(skipCi) {
                    //if commit message contains skip ci, igonore this key and continue with ather key
                    System.out.println("commit message has [skip si], so ignoring " + varName);
                    continue;
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
    
    private static int getVersion(String fileName, String varName) throws IOException{
        String data = new String(Files.readAllBytes(Paths.get(fileName)));
        return getVariable(getStrVariable(data, varName));
    }
    
    private static void commitChangedFile(String[] args){
        String repo = args[1];
        String file = args[2];
        try {
            Git git = Git.open(new File(repo));
            String lastCommitMessage = getLastCommitMessage(git);
            System.out.println("post commit started");
            System.out.println("trying to commit file...");
            printData(repo, file);
            System.out.println("branch: " + git.getRepository().getBranch());
            CommitCommand commit = git.commit();
            commit.setOnly(file); //aucileblad gayofit unda gadaeces da ara sleshit!
            commit.setNoVerify(true);
            System.out.println("disable pre commit hook");
            String commitMessage = "App version " + getVersion(args[3], args[4]) + " " + lastCommitMessage;
            commit.setMessage(commitMessage);
            commit.call();
            System.out.println("commit finished!");
        } catch (JGitInternalException jgie){
            System.out.println("Unfortunately problem in commit from java: " + jgie.toString());
            printData(repo, file);
            Throwable cause = jgie.getCause();
            if(cause != null) System.out.println(cause.getMessage());
            System.out.println(jgie.getMessage());
        } catch (Exception e) {
            System.out.println("Unfortunately problem in commit from java: " + e.toString());
            printData(repo, file);
        }
    }
    
    private static boolean hasSkipCiInCommitMessage(Git git) throws GitAPIException{
        String lastCommitMessage = getLastCommitMessage(git);
        if(lastCommitMessage == null) return false;
        return lastCommitMessage.contains(SKIP_CI);
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
            System.err.println("Last commit message: " + commitMessage);
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
