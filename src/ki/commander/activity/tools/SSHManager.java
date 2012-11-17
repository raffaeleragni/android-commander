/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ki.commander.activity.tools;

import android.util.Log;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import ki.commander.C;

/**
 *
 * @author Raffaele Ragni <raffaele.ragni@gmail.com>
 */
public class SSHManager
{
    private JSch jschSSHChannel;
    private String strUserName;
    private String strConnectionIP;
    private int intConnectionPort;
    private String strPassword;
    private Session sesConnection;
    private int intTimeOut;
    private String privkey;

    private void doCommonConstructorActions(String userName,
        String password, String connectionIP, String knownHostsFileName)
    {
        jschSSHChannel = new JSch();

        try
        {
            jschSSHChannel.setKnownHosts(knownHostsFileName);
        }
        catch (JSchException jschX)
        {
            logError(jschX.getMessage());
        }

        strUserName = userName;
        strPassword = password;
        strConnectionIP = connectionIP;
    }

    public SSHManager(String userName, String privkey, String passphrase,
        String connectionIP, String knownHostsFileName)
    {
        doCommonConstructorActions(userName, passphrase,
            connectionIP, knownHostsFileName);
        intConnectionPort = 22;
        intTimeOut = 60000;
        this.privkey = privkey;
    }

    public SSHManager(String userName, String password,
        String connectionIP, String knownHostsFileName)
    {
        doCommonConstructorActions(userName, password,
            connectionIP, knownHostsFileName);
        intConnectionPort = 22;
        intTimeOut = 60000;
    }

    public SSHManager(String userName, String password, String connectionIP,
        String knownHostsFileName, int connectionPort)
    {
        doCommonConstructorActions(userName, password, connectionIP,
            knownHostsFileName);
        intConnectionPort = connectionPort;
        intTimeOut = 60000;
    }

    public SSHManager(String userName, String password, String connectionIP,
        String knownHostsFileName, int connectionPort, int timeOutMilliseconds)
    {
        doCommonConstructorActions(userName, password, connectionIP,
            knownHostsFileName);
        intConnectionPort = connectionPort;
        intTimeOut = timeOutMilliseconds;
    }

    public String connect()
    {
        String errorMessage = null;

        try
        {
            if (privkey != null)
                jschSSHChannel.addIdentity(privkey, strPassword);
            
            sesConnection = jschSSHChannel.getSession(strUserName,
                strConnectionIP, intConnectionPort);
            
            if (privkey == null)
                sesConnection.setPassword(strPassword);
            // UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
            sesConnection.setConfig("StrictHostKeyChecking", "no");
            sesConnection.connect(intTimeOut);
        }
        catch (JSchException jschX)
        {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }

    private String logError(String errorMessage)
    {
        if (errorMessage != null)
            Log.e(C.PACKAGE, strConnectionIP+":"+intConnectionPort+" - "+errorMessage);

        return errorMessage;
    }

    private String logWarning(String warnMessage)
    {
        if (warnMessage != null)
            Log.w(C.PACKAGE, strConnectionIP+":"+intConnectionPort+" - "+warnMessage);

        return warnMessage;
    }

    public String sendCommand(String command)
    {
        StringBuilder outputBuffer = new StringBuilder();

        try
        {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.connect();
            InputStream commandOutput = channel.getInputStream();
            int readByte = commandOutput.read();

            while (readByte != 0xffffffff)
            {
                outputBuffer.append((char) readByte);
                readByte = commandOutput.read();
            }

            channel.disconnect();
        }
        catch (IOException ioX)
        {
            logWarning(ioX.getMessage());
            return null;
        }
        catch (JSchException jschX)
        {
            logWarning(jschX.getMessage());
            return null;
        }

        return outputBuffer.toString();
    }

    public void close()
    {
        sesConnection.disconnect();
    }
}