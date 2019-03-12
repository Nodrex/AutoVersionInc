package com.nodrex.autoversioninc;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to parse Android's build.grade file and automatically increase build version numbers.
 * @author Nodrex
 */
public class AutoVersionInc {
    public static final String PATH = "build.gradle";//shoule be relative and given from args   //working file and AutoVersionInc should be in one folder
    public static final String VAR_NAME_0 = "versionBuild";//should be given from args
    public static final String VAR_NAME_1 = "aztelekomVersionCode";//shoueld be given from args
    
    public static final String NEW_LINE = "\n";
    public static final String EMPTY_STRING = "";

    public static void main(String args[]) {
        //TODO read values from args
        parsAndIncreasVersion(PATH);
    }

    public static void parsAndIncreasVersion(String fileName) {
        try {
            System.out.println("Git pre commmit hook started...");
            String data = new String(Files.readAllBytes(Paths.get(fileName)));
            if (data == null) {
                System.out.print("unfortunately can not retreiev given file");
                return;
            }

            data = go(data, VAR_NAME_0, VAR_NAME_1);

            FileOutputStream fileOut = new FileOutputStream(new File(PATH));
            fileOut.write(data.getBytes());
            fileOut.close();

            System.out.println("given file was repleced with increasd version!");
            System.out.println("Git pre commmit hook finished :\\)");
            System.out.println("continuing commit...");  
            
            //TODO need to commit from java code withoud hook 
        } catch (Exception e) {
            System.out.println("Unfortunately there was some error while trying to pars build gradle file: " + e.toString());
        }
    }

    private static String go(String data, String... vars) {
        if (vars == null || vars.length <= 0) return null;
        for (String varName : vars) {
            if (varName == null || EMPTY_STRING.equals(varName)) continue;
            System.out.println("Trying to increase " + varName + " number ...");
            int index = data.indexOf(varName);
            String var = data.substring(index);
            var = var.substring(0, var.indexOf(NEW_LINE));

            int lastIndex = var.length();
            int value = 0;
            for (int i = var.length() - 4; i < lastIndex; i++) {
                try {
                    String sub = var.substring(i, lastIndex - 1);
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
