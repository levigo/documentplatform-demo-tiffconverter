package org.levigo.jadice.demo.tiffconverter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.levigo.jadice.demo.tiffconverter.controller.JobController;
import org.levigo.jadice.demo.tiffconverter.ui.MainWindow;

import com.levigo.jadice.document.util.ExtResourceBundle;
import com.levigo.jadice.document.util.I18N;

public class Main {
  public static final ExtResourceBundle I18N_RES = I18N.getBundle(Main.class);

  public static void main(String[] args) {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      // should not happen. Ignoring if it does.
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        MainWindow mw = new MainWindow(new JobController());
        mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mw.setVisible(true);
      }
    });
  }

}
