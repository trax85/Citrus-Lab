package com.example.myapplication3.tools;

import android.util.Log;

import com.topjohnwu.superuser.Shell;

import java.util.List;

/**
 * added by sharan
 */
public class Utils {
    final static String TAG = "Utils";

    public static List<String> readGetList(String... cmd) throws UtilException{
        Shell.Result result = Shell.cmd(cmd).exec();
        return result.getOut();
    }

    public static String read(int index, String path) throws UtilException{
        Shell.Result result = Shell.cmd("cat " + path).exec();
        if(isValaidIO(result.getOut().get(index)))
            return result.getOut().get(index);
        else
            throw new UtilException("Read Error");
    }

    public static String[] readGetArr(String... cmd) throws UtilException{
        Shell.Result result = Shell.cmd(cmd).exec();
        String[] str = result.getOut().toArray(new String[0]);
        if(isValaidIO(str[0]))
            return str;
        else
            throw new UtilException("Read Error");
    }

    public static String execCmdRead(int index, String... cmd) throws UtilException{
        Shell.Result result = Shell.cmd(cmd).exec();
        if(isValaidIO(result.getOut().get(index)))
            return result.getOut().get(index);
        else
            throw new UtilException("Read Error");
    }

    public static void write(String value, String path){
        Shell.cmd("echo " + value + " > " + path).exec();
    }

    public static void execCmdWrite(String... cmd){
        Shell.cmd(cmd).exec();
    }

    public static String[] splitStrings(String cmd, String regex){
        try {
            String[] arrStr = Utils.readGetArr("cat " + cmd);
            return arrStr[0].split(regex);
        }catch (UtilException e){
            return null;
        }
    }

    public static boolean isValaidIO(String out){
        if(out.contains("cat"))
            return false;
        else return !out.contains("Permission");
    }
}
