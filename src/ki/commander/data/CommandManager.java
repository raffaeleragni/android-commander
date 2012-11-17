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

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import ki.commander.C;

/**
 *
 * @author Raffaele Ragni <raffaele.ragni@gmail.com>
 */
public class CommandManager
{
    public static final Object COMMANDS_LOCK = new Object();
    
    private static List<Command> commands = null;
    
    /**
     * After calling reload, a new getCommands() must be issued.
     * The old array won't be cleared unless by GC.
     */
    public static void reload()
    {
        File directory = new File(Environment.getExternalStorageDirectory(), "commander");
        if (!directory.exists())
            directory.mkdirs();
        
        synchronized(COMMANDS_LOCK)
        {
            File file = new File(directory, "commander.xml");
            try
            {
                commands = CommandXMLReader.reload(new FileInputStream(file), directory);
            }
            catch (FileNotFoundException ex)
            {
                Log.e(C.PACKAGE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Make a copy when returning the list.
     * So that when loading a new set the application will continue to use the
     * old one, until it will call getCommands() again.
     */
    public static List<Command> getCommands()
    {
        if (commands == null)
            reload();
        synchronized(COMMANDS_LOCK)
        {
            return commands == null ? null : new ArrayList<Command>(commands);
        }
    }
}
