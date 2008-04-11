/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007-2008, Sualeh Fatehi.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package daylightchart.gui.actions;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import sf.util.ui.Actions;
import sf.util.ui.ExtensionFileFilter;
import sf.util.ui.GuiAction;
import sf.util.ui.Actions.SelectedFile;
import daylightchart.gui.DaylightChartGui;
import daylightchart.gui.Messages;
import daylightchart.options.UserPreferences;

/**
 * Saves locations into a file.
 * 
 * @author sfatehi
 */
public final class SaveLocationsFileAction
  extends GuiAction
{

  private static final class GuiActionListener
    implements ActionListener
  {
    private final DaylightChartGui mainWindow;

    private GuiActionListener(final DaylightChartGui mainWindow)
    {
      this.mainWindow = mainWindow;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(@SuppressWarnings("unused")
    final ActionEvent actionevent)
    {
      final List<FileFilter> fileFilters = new ArrayList<FileFilter>();
      fileFilters.add(new ExtensionFileFilter("Data files", ".data"));
      fileFilters.add(new ExtensionFileFilter("Text files", ".txt"));
      final SelectedFile selectedFile = Actions
        .showSaveDialog(mainWindow,
                        Messages
                          .getString("DaylightChartGui.Menu.File.SaveLocations"),
                        fileFilters,
                        new File(UserPreferences.getOptions()
                          .getWorkingDirectory(), "locations.data"),
                        Messages
                          .getString("DaylightChartGui.Message.Confirm.FileOverwrite")); //$NON-NLS-1$
      if (selectedFile.isSelected())
      {
        try
        {
          UserPreferences.saveLocationsToFile(selectedFile.getFile());
          // Save last selected directory
          UserPreferences.setWorkingDirectory(selectedFile.getDirectory());
        }
        catch (final Exception e)
        {
          LOGGER.log(Level.WARNING, Messages
            .getString("DaylightChartGui.Message.Error.CannotSaveFile"), e); //$NON-NLS-1$
          JOptionPane.showMessageDialog(mainWindow, Messages
            .getString("DaylightChartGui.Message.Error.CannotSaveFile") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                                                    + selectedFile, Messages
            .getString("DaylightChartGui.Menu.File.SaveFile"), //$NON-NLS-1$
                                        JOptionPane.OK_OPTION);
        }
      }
    }
  }

  private static final long serialVersionUID = 1173685118494564955L;

  private static final Logger LOGGER = Logger
    .getLogger(SaveLocationsFileAction.class.getName());

  /**
   * Saves locations into a file.
   * 
   * @param mainWindow
   *        Main window.
   */
  public SaveLocationsFileAction(final DaylightChartGui mainWindow)
  {

    super(Messages.getString("DaylightChartGui.Menu.File.SaveLocations"), //$NON-NLS-1$ 
          "/icons/save_locations.gif" //$NON-NLS-1$
    );
    setShortcutKey(KeyStroke.getKeyStroke("control alt S"));
    addActionListener(new GuiActionListener(mainWindow));
  }

}
