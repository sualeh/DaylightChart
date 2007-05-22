/////////////////////////////////////////////////////////
//Bare Bones Browser Launch                            //
//Version 1.5                                          //
//December 10, 2005                                    //
//Supports: Mac OS X, GNU/Linux, Unix, Windows XP      //
//Example Usage:                                       //
// String url = "http://www.centerkey.com/";           //
// BareBonesBrowserLaunch.openURL(url);                //
//Public Domain Software -- Free to Use as You Like    //
/////////////////////////////////////////////////////////

package daylightchart.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The Bare Bones Browser Launch solution, is intended for those with
 * much simpler requirements or those just looking for an educational
 * "Hello, World" type tutorial on the subject. This solution is
 * appropriate when a compact lightweight method to open a web page is
 * needed. Bare Bones is free and works on Mac OS X, GNU/Linux, Unix
 * (Solaris), and Windows XP.
 * 
 * @see <a href="http://www.centerkey.com/java/browser/">Bare Bones
 *      Browser Launch for Java</a>
 */
public class BareBonesBrowserLaunch
{

  private static final String errMsg = "Error attempting to launch web browser";

  /**
   * Quick test for the Bare Bones Browser Launch.
   * 
   * @param args
   *        None.
   */
  public static void main(final String[] args)
  {
    final JFrame frame = new JFrame();
    final JPanel panel = new JPanel();
    final JTextField urlField = new JTextField("http://www.centerkey.com");
    final JButton webButton = new JButton("Web Trip");
    webButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(@SuppressWarnings("unused")
      final ActionEvent e)
      {
        BareBonesBrowserLaunch.openURL(urlField.getText().trim());
      }
    });
    frame.setTitle("Bare Bones Browser Launch");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    panel.add(new JLabel("URL:"));
    panel.add(urlField);
    panel.add(webButton);
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * This is a fire and forget method -- no further communication or
   * confirmation is provided. However, a pop-up error message will be
   * displayed to the user in most cases if a failure is encountered.
   * 
   * @param url
   *        URL to launch in a browser
   */
  @SuppressWarnings("unchecked")
  public static void openURL(final String url)
  {
    final String osName = System.getProperty("os.name");
    try
    {
      if (osName.startsWith("Mac OS"))
      {
        final Class fileMgr = Class.forName("com.apple.eio.FileManager");
        final Method openURL = fileMgr.getDeclaredMethod("openURL",
                                                         new Class[] {
                                                           String.class
                                                         });
        openURL.invoke(null, new Object[] {
          url
        });
      }
      else if (osName.startsWith("Windows"))
      {
        Runtime.getRuntime()
          .exec("rundll32 url.dll,FileProtocolHandler " + url);
      }
      else
      { // assume Unix or Linux
        final String[] browsers = {
            "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"
        };
        String browser = null;
        for (int count = 0; count < browsers.length && browser == null; count++)
        {
          if (Runtime.getRuntime().exec(new String[] {
              "which", browsers[count]
          }).waitFor() == 0)
          {
            browser = browsers[count];
          }
        }
        if (browser == null)
        {
          throw new Exception("Could not find web browser");
        }
        else
        {
          Runtime.getRuntime().exec(new String[] {
              browser, url
          });
        }
      }
    }
    catch (final Exception e)
    {
      JOptionPane.showMessageDialog(null, errMsg + ":\n"
                                          + e.getLocalizedMessage());
    }
  }

}
