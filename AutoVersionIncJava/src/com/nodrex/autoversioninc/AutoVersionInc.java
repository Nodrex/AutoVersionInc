package com.nodrex.autoversioninc;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;

/**
 * This class is used to parse Android's build.grade file and automatically increase build version numbers.
 * @author Nodrex
 * @version 1.0
 * @since 2019
 */
public class AutoVersionInc {
    
    public static final String NEW_LINE = "\n";
    public static final String EMPTY_STRING = "";

    public static void main(String args[]) {
        //args = new String[]{"F:\\netbeansPorjects\\AutoVersionInc\\someInnerProjectTest\\build.gradle", "versionBuild", "aztelekomVersionCode"};
        //abouv code is for testing purposes only and shoudl be commented on prod version
        
        //System.exit(1);
        
        /*
        try{
            Git git = Git.open(new File("F:\\netbeansPorjects\\AutoVersionInc"));
            CommitCommand commit = git.commit();
            
            commit.setOnly("someInnerProjectTest/build.gradle")  ;
            commit.setNoVerify(true);
            commit.setMessage("some commit message from java himself, trying 4");
            commit.call();
            System.out.println("commit from java finished!");
            System.exit(0);
        }catch(Exception e){
            System.out.println("E: " + e.toString());
        }
        */
        
        
        if(args == null || args.length < 2) {
            System.out.println("given args are empty or it is not enough: args shoudl be at list 2. first always should be file name(maby full path) which should be parsed, other args shouldl be variable names like(versionBuild or aztelekomVersionCode or both) which value shoudl be increasd (they should be represented in format key == value), so keep in mind that and try again with correct or enaf args!!!");
            System.exit(1);
        }
        parsAndIncreasVersion(args);
    }

    public static void parsAndIncreasVersion(String args[]) {
        try {
            System.out.println("Git pre commmit hook started...");
            String fileName = args[0];
            String data = new String(Files.readAllBytes(Paths.get(fileName)));
            
            data = go(data, args);

            FileOutputStream fileOut = new FileOutputStream(new File(fileName));
            fileOut.write(data.getBytes());
            fileOut.close();

            System.out.println("given file was repleced with increasd version!");
            System.out.println("Git pre commmit hook finished :)");
            System.out.println("continuing commit...");  
            
            
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("strat delay");  
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException ex) {}
//                    System.out.println("delay ended");
//                    commitChangedFile();
//                }
//            }).start();
            
            //commitChangedFile();
        } catch (Exception e) {
            System.out.println("Unfortunately there was some error while trying to pars build gradle file: " + e.toString());
            System.exit(1);
        }
    }
    
    private static void commitChangedFile(){
        //TODO misamartebi args indan unda waikitxos
        try {
            Git git = Git.open(new File("F:\\netbeansPorjects\\AutoVersionInc"));
            CommitCommand commit = git.commit();
            System.out.println("get commit...");  
            commit.setOnly("someInnerProjectTest/build.gradle"); //aucileblad gayofit unda gadaeces da ara sleshit!
            System.out.println("set file to commit");  
            commit.setNoVerify(true);
            System.out.println("disable pre commit hook");
            commit.setMessage("increasted build version");
            System.out.println("commit message set");
            commit.call();
            System.out.println("commit finished!");
        } catch (Exception e) {
            System.out.println("Unfortunately problem in commit from java: " + e.toString());
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

            System.out.println("current vallue which will be commited: " + value);
            int newValue = value + 1;
            System.out.println("new vallue which will be commited on next commit: " + newValue);
            String newVar = var.replace(String.valueOf(value), String.valueOf(newValue));
            data = data.replace(var, newVar);
        }
        return data;
    }
    
}
