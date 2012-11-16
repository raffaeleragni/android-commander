/*
 *   commander - android app for remote and batch command execution
 *   Copyright (C) 2012 Raffaele Ragni
 *   https://github.com/raffaeleragni/android-wardrive4
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ki.commander.data;

import java.io.File;

/**
 *
 * @author Raffaele Ragni <raffaele.ragni@gmail.com>
 */
public class Command
{
    public enum Type {ssh}
    public enum Auth {login, privatekey}
    public enum Risk {none, confirm, dangerous}
    
    private Type type;
    private Auth auth;
    private Risk risk;
    
    private String name;
    private String description;
    private String target;
    private String commandString;
    private File commandScriptLocation;
    private File iconLocation;
    private File privateKeyLocation;
    private String privateKeyPassphrase;
    private String loginUsername;
    private String loginPassword;

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Auth getAuth()
    {
        return auth;
    }

    public void setAuth(Auth auth)
    {
        this.auth = auth;
    }

    public Risk getRisk()
    {
        return risk;
    }

    public void setRisk(Risk risk)
    {
        this.risk = risk;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getCommandString()
    {
        return commandString;
    }

    public void setCommandString(String commandString)
    {
        this.commandString = commandString;
    }

    public File getCommandScriptLocation()
    {
        return commandScriptLocation;
    }

    public void setCommandScriptLocation(File commandScriptLocation)
    {
        this.commandScriptLocation = commandScriptLocation;
    }

    public File getIconLocation()
    {
        return iconLocation;
    }

    public void setIconLocation(File iconLocation)
    {
        this.iconLocation = iconLocation;
    }

    public File getPrivateKeyLocation()
    {
        return privateKeyLocation;
    }

    public void setPrivateKeyLocation(File privateKeyLocation)
    {
        this.privateKeyLocation = privateKeyLocation;
    }

    public String getPrivateKeyPassphrase()
    {
        return privateKeyPassphrase;
    }

    public void setPrivateKeyPassphrase(String privateKeyPassphrase)
    {
        this.privateKeyPassphrase = privateKeyPassphrase;
    }

    public String getLoginUsername()
    {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername)
    {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword()
    {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword)
    {
        this.loginPassword = loginPassword;
    }
}
