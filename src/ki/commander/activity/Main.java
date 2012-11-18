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
package ki.commander.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Random;
import ki.commander.C;
import ki.commander.R;
import ki.commander.activity.tools.SSHManager;
import ki.commander.data.Command;
import ki.commander.data.CommandManager;

public class Main extends ListActivity
{
    List<Command> commands;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        reloadCommands();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        launchCommand(commands.get(position));
    }
    
    void reloadCommands()
    {
        CommandManager.reload();
        commands = CommandManager.getCommands();
        ListAdapter adapter = new ArrayAdapter<Command>(this, R.layout.main_item, commands);
        setListAdapter(adapter);
    }
    
    void launchCommand(final Command command)
    {
        final CommandExecutor executorTask = new CommandExecutor();
        
        switch (command.getRisk())
        {
            case confirm:
                new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_confirmation_title)
                    .setMessage(getResources().getString(R.string.dialog_confirmation_text, command.getName()))
                    .setNegativeButton(R.string.Cancel, null)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface di, int i)
                        {
                            executorTask.execute(command);
                        }
                    })
                    .create().show();
                break;
            case dangerous:
                // Generate random numbers
                Random random = new Random(System.currentTimeMillis());
                final int total = random.nextInt(90) + 10;
                int v1 = random.nextInt(total - 10) + 10;
                int v2 = total - v1;
                // Build the widgets
                View view = getLayoutInflater().inflate(R.layout.sum_dialog, null);
                final EditText result = (EditText) view.findViewById(R.id_sum_dialog.text);
                TextView message = (TextView) view.findViewById(R.id_sum_dialog.message);
                message.setText(getResources().getString(R.string.dialog_confirmation_sum_text, v1, v2));
                // Show the dialog
                new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_confirmation_title)
                    .setView(view)
                    .setNegativeButton(R.string.Cancel, null)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface di, int i)
                        {
                            Integer resultInt = null;
                            try{resultInt = Integer.parseInt(result.getText().toString());} catch (Exception e){}
                            if (resultInt != null && resultInt == total)
                                executorTask.execute(command);
                        }
                    })
                    .create().show();
                break;
            case none:
                executorTask.execute(command);
                break;
        }
    }
    
    public class CommandExecutor extends AsyncTask<Command, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(Command... paramss)
        {
            Command command = paramss[0];
            
            switch (command.getType())
            {
                case ssh:
                    SSHManager instance = null;
                    switch (command.getAuth())
                    {
                        case login:
                            instance = new SSHManager(command.getLoginUsername(), command.getLoginPassword(), command.getTarget(), "");
                            break;
                        case privatekey:
                            instance = new SSHManager(command.getLoginUsername(), command.getPrivateKeyLocation().getAbsolutePath(), command.getPrivateKeyPassphrase(), command.getTarget(), "");
                            break;
                    }
                    
                    if (instance == null)
                        return false; 
                    
                    String errorMessage = instance.connect();
                    if (errorMessage != null)
                    {
                        Log.e(C.PACKAGE, errorMessage);
                        return false;
                    }

                    Log.i(C.PACKAGE, "Command execution: "+command.getCommandString());
                    String result = instance.sendCommand(command.getCommandString());
                    instance.close();
                    Log.i(C.PACKAGE, "Command response: "+result);
                    break;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if (result)
                Toast.makeText(Main.this, R.string.command_executed, Toast.LENGTH_LONG);
        }
    }
}
