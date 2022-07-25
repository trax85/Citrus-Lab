package com.example.myapplication3.tools;

import com.topjohnwu.superuser.Shell;

import java.util.List;

/**
 * added by sharan
 */
public class Utils {
    Shell.Result result;

    public List<String> readGetList(String... cmd) throws UtilException{
        result = Shell.cmd(cmd).exec();
        return result.getOut();
    }

    public String read(int index, String... cmd) throws UtilException{
        result = Shell.cmd(cmd).exec();
        return result.getOut().get(index);
    }

    public String[] readGetArr(String... cmd) throws UtilException{
        result = Shell.cmd(cmd).exec();
        return (String[])result.getOut().toArray();
    }

    public void write(String... cmd){
        Shell.cmd(cmd).exec();
    }
}
