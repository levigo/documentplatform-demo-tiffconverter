package org.levigo.jadice.demo.tiffconverter.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.levigo.jadice.demo.tiffconverter.Main;
import org.levigo.jadice.demo.tiffconverter.controller.JobController;

import com.levigo.util.swing.action.injection.Argument;
import com.levigo.util.swing.action.injection.InjectedCommand;

public class SelectTargetDirectoryCommand extends InjectedCommand {

  private JobController controller;
  private MainWindow mainWindow;

  public JobController getController() {
    return controller;
  }

  public MainWindow getMainWindow() {
    return mainWindow;
  }

  /**
   * Direct calls to this method are deprecated. It is used for the argument injection process of
   * {@link InjectedCommand}.
   * 
   * @param controller
   */
  @Deprecated
  @Argument
  public void setController(JobController controller) {
    this.controller = controller;
  }

  /**
   * Direct calls to this method are deprecated. It is used for the argument injection process of
   * {@link InjectedCommand}.
   * 
   * @param mainWindow
   */
  @Deprecated
  @Argument
  public void setMainWindow(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
  }

  @Override
  protected void execute() {


    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(controller.getTargetDirectory());
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);

    File targetDirectory = null;

    while (targetDirectory == null) {

      int selectedOption = chooser.showOpenDialog(mainWindow);
      if (selectedOption == JFileChooser.APPROVE_OPTION) {

        File f = chooser.getSelectedFile();

        // check if the specified target exists.
        if (!f.exists()) {
          int res = JOptionPane.showConfirmDialog(mainWindow,
              Main.I18N_RES.getString("message.targetdir.notexist", f.getAbsolutePath()),
              Main.I18N_RES.getString("message.targetdir.notexist.title", f.getAbsolutePath()),
              JOptionPane.YES_NO_CANCEL_OPTION);

          if (res == JOptionPane.YES_OPTION) {
            if (!f.mkdirs()) {
              JOptionPane.showMessageDialog(mainWindow, Main.I18N_RES.getString("message.targetdir.createfail"),
                  Main.I18N_RES.getString("message.targetdir.createfail.title", f.getAbsolutePath()),
                  JOptionPane.ERROR_MESSAGE);
              return;
            }
          } else if (res == JOptionPane.NO_OPTION) {
            continue;
          } else if (res == JOptionPane.CANCEL_OPTION) {
            return;
          }


        } else {
          // target exists. Check if it is a directory
          if (!f.isDirectory()) {
            JOptionPane.showMessageDialog(mainWindow, Main.I18N_RES.getString("message.targetdir.notadir"),
                Main.I18N_RES.getString("message.targetdir.notadir.title", f.getAbsolutePath()),
                JOptionPane.ERROR_MESSAGE);
            // try again
            continue;
          }
        }
        
        targetDirectory = f;
      } else {
        // cancel clicked.
        return;
      }
    }

    // targetDirectory must be non null here.

    controller.setTargetDirectory(targetDirectory);

  }

}
