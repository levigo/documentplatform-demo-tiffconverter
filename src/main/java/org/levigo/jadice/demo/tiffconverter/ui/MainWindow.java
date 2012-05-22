package org.levigo.jadice.demo.tiffconverter.ui;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.levigo.jadice.demo.tiffconverter.Main;
import org.levigo.jadice.demo.tiffconverter.controller.Job;
import org.levigo.jadice.demo.tiffconverter.controller.JobAdapter;
import org.levigo.jadice.demo.tiffconverter.controller.JobController;
import org.levigo.jadice.demo.tiffconverter.ui.jobtree.JobOverviewTree;

import com.levigo.jadice.document.BasicDocument;
import com.levigo.jadice.document.UIDocument;
import com.levigo.jadice.swing.dnd.CompositeImportHandler;
import com.levigo.jadice.swing.dnd.ImportHandler;
import com.levigo.jadice.swing.thumbnailview.PageSorter;
import com.levigo.jadice.swing.thumbnailview.ThumbnailView;
import com.levigo.jadice.swing.thumbnailview.dnd.FileImportHandler;
import com.levigo.jadice.swing.thumbnailview.dnd.ImageImportHandler;
import com.levigo.jadice.swing.thumbnailview.dnd.IntraDOCPPageImportHandler;
import com.levigo.jadice.swing.thumbnailview.dnd.IntraJVMPageImportHandler;
import com.levigo.jadice.swing.thumbnailview.dnd.ReorderPageExportHandler;
import com.levigo.jadice.swing.thumbnailview.dnd.ReorderPageImportHandler;
import com.levigo.util.swing.IconManager;
import com.levigo.util.swing.action.Context;
import com.levigo.util.swing.action.Context.Ancestors;
import com.levigo.util.swing.action.Context.Children;
import com.levigo.util.swing.action.DefaultActionFactory;
import com.levigo.util.swing.action.NonUglyActionJToggleButton;

public class MainWindow extends JFrame {

  private static final long serialVersionUID = 655870486677708748L;
  private static final DefaultActionFactory ACTION_FACTORY = DefaultActionFactory.getInstance("/org/levigo/jadice/demo/tiffconverter/resources/actions.properties");

  private final Context context;
  private final PageSorter pageSorter;
  private final JToggleButton smallThumbnailsButton;
  private final JToggleButton normalThumbnailsButton;
  private final JToggleButton largeThumbnailsButton;
  private final JLabel thumbnailSizeLabel;

  private final JButton createTaskButton;

  private final JobOverviewTree jobOverviewTree;

  private final JobController controller;
  private final JPanel contentPanel;

  private final JLabel arrowLabel;
  private final JLabel documentNameLabel;
  private final JTextField documentNameTextField;
  private final JLabel pageSorterHeaderLabel;
  private final JLabel jobOverviewTreeHeaderLabel;
  private final JLabel headerLabel;
  private final JLabel targetDirectoryTitleLabel;
  private final JLabel targetDirectoryLabel;
  private final JLabel legendEnqueuedLabel;
  private final JLabel legendErrorLabel;
  private final JLabel legendFinishedLabel;
  private final JLabel legendRunningLabel;
  private final JLabel legendHeaderLabel;
  private final JButton targetDirectorySelectButton;
  private final JButton targetDirectoryOpenButton;

  public MainWindow(JobController controller) {
    if (controller == null)
      throw new IllegalArgumentException("controller must not be null");

    setTitle(Main.I18N_RES.getString("application.title"));

    this.controller = controller;

    // our "root" panel that will hold all components.
    contentPanel = new JPanel();
    setContentPane(contentPanel);

    // initialize the context. The context will be attached to our contentPanel.
    context = Context.install(contentPanel, Children.ACTIVE, Ancestors.PARENT);

    // create all components
    pageSorter = new PageSorter();
    thumbnailSizeLabel = UIUtils.createLabel(Main.I18N_RES.getString("label.thumbnailsize"));
    smallThumbnailsButton = createThumbnailSizeToggleButton("SmallThumbnails", "small-icon");
    normalThumbnailsButton = createThumbnailSizeToggleButton("NormalThumbnails", "normal-icon");
    largeThumbnailsButton = createThumbnailSizeToggleButton("LargeThumbnails", "large-icon");
    arrowLabel = new JLabel(UIUtils.IMAGES.getIcon("arrow"));
    documentNameLabel = UIUtils.createLabel(Main.I18N_RES.getString("label.documentname"));
    documentNameTextField = new JTextField();
    createTaskButton = new JButton(ACTION_FACTORY.getAction(context, "CreateTask"));
    jobOverviewTree = new JobOverviewTree(controller);
    headerLabel = UIUtils.createHeader1Label(Main.I18N_RES.getString("application.title"));
    headerLabel.setIcon(UIUtils.IMAGES.getIcon("levigo-logo"));
    headerLabel.setBackground(Color.WHITE);
    headerLabel.setOpaque(true);
    pageSorterHeaderLabel = UIUtils.createHeader2Label(Main.I18N_RES.getString("application.header.doccomposition"));
    jobOverviewTreeHeaderLabel = UIUtils.createHeader2Label(Main.I18N_RES.getString("application.header.jobstatus"));

    targetDirectoryTitleLabel = UIUtils.createLabel(Main.I18N_RES.getString("label.targetdir"));
    targetDirectoryLabel = UIUtils.createLabel("");

    targetDirectorySelectButton = new JButton(ACTION_FACTORY.getAction(context, "SelectTargetDirectory"));
    targetDirectorySelectButton.setHideActionText(true);
    targetDirectoryOpenButton = new JButton(ACTION_FACTORY.getAction(context, "OpenTargetDirectory"));
    targetDirectoryOpenButton.setHideActionText(true);

    legendEnqueuedLabel = UIUtils.createLabel(UIUtils.ICONS.getIcon("icon.job.enqueued"),
        Main.I18N_RES.getString("label.legend.enqueued"));
    legendErrorLabel = UIUtils.createLabel(UIUtils.ICONS.getIcon("icon.job.error"),
        Main.I18N_RES.getString("label.legend.error"));
    legendFinishedLabel = UIUtils.createLabel(UIUtils.ICONS.getIcon("icon.job.finished"),
        Main.I18N_RES.getString("label.legend.finished"));
    legendRunningLabel = UIUtils.createLabel(UIUtils.ICONS.getIcon("icon.job.running"),
        Main.I18N_RES.getString("label.legend.running"));

    legendHeaderLabel = UIUtils.createLabel(Main.I18N_RES.getString("label.legend.header"));

    // add objects used by the commands to the context
    context.add(this);
    context.add(pageSorter);
    context.add(pageSorter.getThumbnailView());
    context.add(jobOverviewTree);
    context.add(controller);

    layoutComponents();


    // register a property change listener on the ThumbnailView to be informed about document
    // changes.
    pageSorter.getThumbnailView().addPropertyChangeListener("document", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        // When the document changes we have to:
        // - update the text of the document name text field
        // - notify the context about a change
        updateDocumentNameTextField();
        context.contextChanged();
      }
    });

    documentNameTextField.getDocument().addDocumentListener(new DocumentListener() {

      private void applyNameToDocument() {
        pageSorter.getThumbnailView().getDocument().setName(documentNameTextField.getText());

        context.contextChanged();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        applyNameToDocument();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        applyNameToDocument();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        applyNameToDocument();
      }
    });

    // to ensure that the root node of the will be expanded once a job has been enqueued, we're
    // forcing the tree to open the root node when we've been informed.
    controller.addJobListener(new JobAdapter() {
      @Override
      protected void jobStateChanged(JobController controller, Job job) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            jobOverviewTree.getFlexibleTree().expandRoot();
          }
        });
      }
    });

    // initially set a document into which the pages will be inserted.
    pageSorter.getThumbnailView().setDocument(new BasicDocument(""));

    configureThumbnailView(pageSorter.getThumbnailView());

    // if the target directory is changed, update the label.
    controller.addPropertyChangeListener("targetDirectory", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            updateTargetDirectoryLabel();
          }
        });
      }
    });
    
    updateTargetDirectoryLabel();
    
    pack();
  }

  protected void updateTargetDirectoryLabel() {

    File targetDirectory = controller.getTargetDirectory();

    if (targetDirectory == null) {
      targetDirectoryLabel.setText(Main.I18N_RES.getString("label.targetdir.tempdir"));
    } else {
      targetDirectoryLabel.setText("<html><b>" + targetDirectory.getName() + "</b> ("
          + targetDirectory.getParentFile().getAbsolutePath() + ")</html>");
    }


  }

  protected void configureThumbnailView(ThumbnailView thumbnailView) {

    // setup the import handlers. These are required. Without them it would not be possible to drop
    // new pages/document or even reorder.
    final List<ImportHandler> importHandlers = new ArrayList<ImportHandler>();

    // Reordering within the same jadice document
    importHandlers.add(new ReorderPageImportHandler());

    // Import pages from different jadice documents within the same JVM
    importHandlers.add(new IntraJVMPageImportHandler());

    // Import pages from different jadice documents from another jadice application running in a
    // different JVM
    importHandlers.add(new IntraDOCPPageImportHandler());

    // Import an image into a jadice document
    importHandlers.add(new ImageImportHandler());

    // Import a local file into an existing jadice document
    importHandlers.add(new FileImportHandler());

    // Create a composite that encompasses the handlers above
    thumbnailView.setImportHandler(new CompositeImportHandler(importHandlers));

    // to enable reordering we have add the reorder page export handler. This export handler will
    // only work within the same ThumbnailView instance.
    thumbnailView.setExportHandler(new ReorderPageExportHandler());

  }

  protected void updateDocumentNameTextField() {
    UIDocument<Component> doc = pageSorter.getThumbnailView().getDocument();
    documentNameTextField.setText(doc != null ? doc.getName() : "");
  }

  protected JToggleButton createThumbnailSizeToggleButton(String actionName, String iconName) {
    final JToggleButton b = new NonUglyActionJToggleButton(ACTION_FACTORY.getAction(context, actionName));
    b.setBorder(null);
    b.setFocusable(false);
    b.setText(null);
    b.setRolloverEnabled(true);
    b.setBorderPainted(false);
    b.setContentAreaFilled(false);

    b.setIcon(UIUtils.ICONS.getIcon(iconName, IconManager.EFFECT_GRAY50P | IconManager.EFFECT_LIGHTER));
    b.setRolloverIcon(UIUtils.ICONS.getIcon(iconName));
    b.setSelectedIcon(UIUtils.ICONS.getIcon(iconName));

    b.setHideActionText(true);

    return b;
  }

  protected void layoutComponents() {

    contentPanel.setLayout(new MigLayout("", "[min:250px, pref:500px, grow, fill][][270px, fill]", "[][][][]"));

    contentPanel.add(headerLabel, "span 3,wrap");
    contentPanel.add(pageSorterHeaderLabel);
    contentPanel.add(jobOverviewTreeHeaderLabel, "skip, wrap");

    contentPanel.add(pageSorter, "spany 11, height 400px, growy");

    contentPanel.add(arrowLabel);
    contentPanel.add(jobOverviewTree, "spany 4, push, growy, wrap");

    contentPanel.add(documentNameLabel, "wrap");
    contentPanel.add(documentNameTextField, "grow, split");
    contentPanel.add(new JLabel("*"), "wrap");
    contentPanel.add(createTaskButton, "wrap, gapbottom 30px");

    contentPanel.add(targetDirectoryTitleLabel, "skip 2, wrap");
    contentPanel.add(targetDirectoryLabel, "skip 2, grow, split 3");
    contentPanel.add(targetDirectoryOpenButton);
    contentPanel.add(targetDirectorySelectButton, "wrap");

    // legend section
    contentPanel.add(legendHeaderLabel, "skip 2, gaptop 10px, wrap");
    contentPanel.add(legendEnqueuedLabel, "skip 2, wrap");
    contentPanel.add(legendRunningLabel, "skip 2, wrap");
    contentPanel.add(legendFinishedLabel, "skip 2, wrap");
    contentPanel.add(legendErrorLabel, "skip 2, wrap");

    contentPanel.add(thumbnailSizeLabel, "split 4, grow");
    contentPanel.add(smallThumbnailsButton, "sg");
    contentPanel.add(normalThumbnailsButton, "sg");
    contentPanel.add(largeThumbnailsButton, "sg");

  }

  public JobController getController() {
    return controller;
  }

}
