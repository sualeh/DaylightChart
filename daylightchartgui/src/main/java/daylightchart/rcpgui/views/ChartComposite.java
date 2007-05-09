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
package daylightchart.rcpgui.views;


import java.awt.Frame;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.jfree.chart.ChartPanel;

import daylightchart.chart.DaylightChart;

public class ChartComposite
  extends Composite
{

  public ChartComposite(Composite parent, DaylightChart daylightChart)
  {
    super(parent, SWT.EMBEDDED);
    setLayoutData(new GridData(GridData.FILL_BOTH));
    final Frame frame = SWT_AWT.new_Frame(this);
    ChartPanel chartPanel = new ChartPanel(daylightChart,
                                           false,
                                           false,
                                           false,
                                           false,
                                           false);
    frame.add(chartPanel);
  }

  public void createChartPrintJob()
  {
    PrintDialog dialog = new PrintDialog(getShell());
    PrinterData printerData = dialog.open();
    if (printerData != null)
    {
      Printer printer = new Printer(printerData);
      try
      {
        Thread.sleep(10);
      }
      catch (InterruptedException e)
      {
        // Ignore and continue
      }

      Point screenDPI = getShell().getDisplay().getDPI();
      org.eclipse.swt.graphics.Point printerDPI = printer.getDPI();
      int scaleFactor = printerDPI.x / screenDPI.x;
      Rectangle trim = printer.computeTrim(0, 0, 0, 0);
      if (printer.startJob("Chart"))
      {
        Image printImage = getChartImage(printer);
        Point size1 = getSize();
        GC gc1 = new GC(printer);
        if (printer.startPage())
        {
          gc1.drawImage(printImage,
                        0,
                        0,
                        size1.x,
                        size1.y,
                        -trim.x,
                        -trim.y,
                        scaleFactor * size1.x,
                        scaleFactor * size1.y);
          printer.endPage();
        }
        gc1.dispose();
        printer.endJob();
      }
      printer.dispose();
    }
  }

  public void doSaveAs()
  {
    FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
    String[] extensions = {
        "*.png", "*.jpg", "*.gif"
    };
    fileDialog.setFilterExtensions(extensions);
    String filename = fileDialog.open();
    if (filename != null)
    {
      int imageFormat = SWT.IMAGE_PNG;
      if (filename.endsWith(".png"))
      {
        imageFormat = SWT.IMAGE_PNG;
      }
      else if (filename.endsWith(".jpg"))
      {
        imageFormat = SWT.IMAGE_JPEG;
      }
      else if (filename.endsWith(".gif"))
      {
        imageFormat = SWT.IMAGE_GIF;
      }
      Image chartImage = getChartImage(getShell().getDisplay());
      ImageLoader imageLoader = new ImageLoader();
      imageLoader.data = new ImageData[] {
        chartImage.getImageData()
      };
      imageLoader.save(filename, imageFormat);
    }
  }

  private Image getChartImage(Device device)
  {
    Point size = getSize();
    Image image = new Image(getShell().getDisplay(), size.x, size.y);
    GC gc = new GC(this);
    gc.copyArea(image, 0, 0);
    ImageData imageData = image.getImageData();
    Image deviceImage = new Image(device, imageData);
    gc.dispose();
    return deviceImage;
  }
}
