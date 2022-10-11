package com.example.myapplication3.tools;

import com.example.myapplication3.fragment_data_models.FragmentParameter;
import com.topjohnwu.superuser.Shell;

import java.util.List;

/**
 * By: Sharan Raj & Tejas Udupa
 * Utils: A utility class for handling file I/0
 * it handles read and write errors internally and only throws read error exception which the
 * implementer can choose to handle in their desired way.
 */
public class Utils {
    final static String TAG = "Utils";
    private final FragmentParameter parameter;
    private ActivityLogger logger;

    public Utils(FragmentParameter parameter) {
        this.parameter = parameter;
    }

    public void initActivityLogger(){
        logger = new ActivityLogger();
        logger.activityLog = parameter.getActivityLog();
    }
    public static List<String> readGetList(String... cmd) throws UtilException{
        Shell.Result result = Shell.cmd(cmd).exec();
        return result.getOut();
    }

    public static String read(int index, String path) throws UtilException{
        Shell.Result result = Shell.cmd("cat " + path).exec();

        if (!isValidIndex(index, result.getOut()))
            throw new UtilException("Read Error");
        else if (!isValaidIO(result.getOut().get(index)))
            throw new UtilException("Read Error");
        else
            return result.getOut().get(index);
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
        if(!isValidIndex(index, result.getOut()) || !isValaidIO(result.getOut().get(index)))
            throw new UtilException("Read Error");
        else
            return result.getOut().get(index);
    }

    public void write(String value, String path){
        String cmd = "echo " + value + " > ";
        Shell.cmd( cmd + path).exec();
        logger.logWriteActivity(cmd, path);
    }

    public static void execCmdString(String... cmd){
        Shell.cmd(cmd).exec();
    }

    public void execWrite(String... cmd){
        StringBuilder finalCmd = new StringBuilder();
        for(String string : cmd)
            finalCmd.append(string).append(" ");
        finalCmd.append(";");
        Shell.cmd(String.valueOf(finalCmd)).exec();
        logger.logExecWriteActivity(cmd[0], String.valueOf(finalCmd));
    }

    public void chmodFile(String perm, String path){
        String cmd = "chmod " + perm + " ";
        Shell.cmd(cmd + path).exec();
        logger.logWriteActivity(cmd, path);
    }

    public static void serviceWrite(String value, String path){
        String cmd = "echo " + value + " > ";
        Shell.cmd( cmd + path).exec();
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
        else return !out.contains("Permission") && !out.contains("No such file or directory");
    }

    public static boolean isValidIndex(int index, List<String> list){
        return index < list.size() && index >= 0;
    }
}
