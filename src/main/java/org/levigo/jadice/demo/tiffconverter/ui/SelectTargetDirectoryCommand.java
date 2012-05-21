package org.levigo.jadice.demo.tiffconverter.ui;

import javax.swing.JFileChooser;

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

    if (chooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
      
      
      
      System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
      System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
    }


  }

}
