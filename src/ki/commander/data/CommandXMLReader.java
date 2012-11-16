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

import android.util.Log;
import android.util.Xml;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import ki.commander.C;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Stuff for reading structure-dependant information of the XML.
 * 
 * @author Raffaele Ragni <raffaele.ragni@gmail.com>
 */
public class CommandXMLReader
{
    private static final String ns = null;
    
    public static List<Command> reload(InputStream is, File xmlDirectory)
    {
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();
            return CommandXMLReader.readCommands(parser, xmlDirectory);
        }
        catch (IOException ex)
        {
            Log.e(C.PACKAGE, ex.getMessage(), ex);
        }
        catch (XmlPullParserException ex)
        {
            Log.e(C.PACKAGE, ex.getMessage(), ex);
        }
        
        return null;
    }
    
    private static List<Command> readCommands(XmlPullParser parser, File xmlDirectory) throws XmlPullParserException, IOException
    {
        List<Command> result = new ArrayList<Command>();
        
        parser.require(XmlPullParser.START_TAG, ns, "commander");
        
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
        
            String tagName = parser.getName();
            if ("command".equals(tagName))
                result.add(readCommand(parser, xmlDirectory));
            else
                skip(parser);
        }
        
        return result;
    }
    
    private static Command readCommand(XmlPullParser parser, File xmlDirectory) throws XmlPullParserException, IOException
    {
        Command result = new Command();
        
        parser.require(XmlPullParser.START_TAG, ns, "command");

        for (int i= 0; i < parser.getAttributeCount(); i++)
        {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if ("risk".equals(name))
                result.setRisk(Command.Risk.valueOf(value));
            else if ("type".equals(name))
                result.setType(Command.Type.valueOf(value));
            else if ("auth".equals(name))
                result.setAuth(Command.Auth.valueOf(value));
        }
        
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String tagName = parser.getName();
            if ("name".equals(tagName))
                result.setName(parser.getText());
            else if ("description".equals(tagName))
                result.setDescription(parser.getText());
            else if ("target".equals(tagName))
                result.setTarget(parser.getText());
            else if ("command-string".equals(tagName))
                result.setCommandString(parser.getText());   
            else if ("command-script-location".equals(tagName))
                result.setCommandScriptLocation(new File(xmlDirectory, parser.getText()));
            else if ("icon-location".equals(tagName))
                result.setIconLocation(new File(xmlDirectory, parser.getText()));
            else if ("private-key-location".equals(tagName))
                result.setPrivateKeyLocation(new File(xmlDirectory, parser.getText()));
            else if ("private-key-passphrase".equals(tagName))
                result.setPrivateKeyPassphrase(parser.getText());
            else if ("login-username".equals(tagName))
                result.setLoginUsername(parser.getText());
            else if ("login-password".equals(tagName))
                result.setLoginPassword(parser.getText());
            
            // Every tag here can only have a text node, no childs.
            skip(parser);
        }
        
        return result;
    }
    
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        if (parser.getEventType() != XmlPullParser.START_TAG)
            throw new IllegalStateException();
        int depth = 1;
        while (depth != 0)
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
    }
}
