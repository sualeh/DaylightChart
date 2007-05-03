/* 
 * 
 * Daylight Chart
 * http://sourceforge.net/projects/daylightchart
 * Copyright (c) 2007, Sualeh Fatehi.
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
package daylightchart.rcpgui;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import daylightchart.rcpgui.views.DaylightChartView;
import daylightchart.rcpgui.views.NavigationView;

public class Perspective
  implements IPerspectiveFactory
{

  public static final String DAYLIGHT_CHARTS_FOLDER = "daylight_charts";

  public void createInitialLayout(final IPageLayout layout)
  {
    final String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible(false);

    layout.addStandaloneView(NavigationView.ID,
                             false,
                             IPageLayout.LEFT,
                             0.25f,
                             editorArea);
    final IFolderLayout folder = layout.createFolder(DAYLIGHT_CHARTS_FOLDER,
                                                     IPageLayout.TOP,
                                                     0.5f,
                                                     editorArea);
    folder.addPlaceholder(DaylightChartView.ID + ":*");

    layout.getViewLayout(NavigationView.ID).setCloseable(false);
  }
  
}
