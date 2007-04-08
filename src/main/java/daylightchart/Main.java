package daylightchart;


import daylightchart.gui.DaylightChartGui;

/**
 * Main window.
 * 
 * @author Sualeh Fatehi
 */
public class Main
{

  /**
   * Main window.
   * 
   * @param args
   *        Arguments
   * @throws Exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    new DaylightChartGui().setVisible(true);
  }

}
