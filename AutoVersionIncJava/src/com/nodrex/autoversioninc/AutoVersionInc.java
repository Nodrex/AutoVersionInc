package com.nodrex.autoversioninc;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;

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
            System.out.println("Git pre commit hook started...");
            String fileName = args[0];
            System.out.println("file to pars: " + fileName);
            String data = new String(Files.readAllBytes(Paths.get(fileName)));
            
            data = go(data, args);

            FileOutputStream fileOut = new FileOutputStream(new File(fileName));
            fileOut.write(data.getBytes());
            fileOut.close();

            System.out.println("given file was repleced with increasd version!");
            System.out.println("Git pre commit hook finished :)");
            System.out.println("continuing commit...");  
            
        } catch (Exception e) {
            System.out.println("Unfortunately there was some error while trying to parse build gradle file: " + e.toString());
            System.exit(1);
        }
    }
    
    private static String go(String data, String args[]) {
        for (int i= 1; i< args.length; i++) {
            String varName = args[i]; 
            System.out.println("Trying to increase " + varName + " number ...");
            int index = data.indexOf(varName);
            if(index < 0) {
                System.out.println("Can not find " + varName + " in file, so skipping" );
                continue;
            }
            String var = data.substring(index);
            var = var.substring(0, var.indexOf(NEW_LINE));

            int lastIndex = var.length();
            int value = 0;
            for (int j = var.length() - 4; j < lastIndex; j++) {
                try {
                    String sub = var.substring(j, lastIndex - 1);
                    sub = sub.trim();
                    value = Integer.valueOf(sub);
                    break;
                } catch (NumberFormatException e) {}
            }

            System.out.println("current value: " + value);
            int newValue = value + 1;
            System.out.println("new value which will be commited: " + newValue);
            String newVar = var.replace(String.valueOf(value), String.valueOf(newValue));
            data = data.replace(var, newVar);
        }
        return data;
    }
    
    private static void commitChangedFile(String[] args){
        try {
            System.out.println("trying to commit file...");
            String repo = args[1];
            System.out.println("repo: " + repo);
            String file = args[2];
            System.out.println("file: " + file);
            Git git = Git.open(new File(repo));
            System.out.println("branch: " + git.branchList().call().get(0).getName());
            CommitCommand commit = git.commit();
            commit.setOnly(file); //aucileblad gayofit unda gadaeces da ara sleshit!
            commit.setNoVerify(true);
            System.out.println("disable pre commit hook");
            commit.setMessage("increased build version");
            commit.call();
            System.out.println("commit finished!");
            System.exit(0);
        }catch (JGitInternalException jgie){
            System.out.println("Unfortunately problem in commit from java: " + jgie.toString());
            System.out.println(jgie.getCause().getMessage());
            System.out.println(jgie.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unfortunately problem in commit from java: " + e.toString());
            System.exit(1);
        }
    }
    
}
