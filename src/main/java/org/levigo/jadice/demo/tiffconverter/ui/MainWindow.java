package org.levigo.jadice.demo.tiffconverter.ui;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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
import com.levigo.jadice.document.util.I18N;
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

    pack();
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

    contentPanel.setLayout(new MigLayout("", "[min:250px, pref:500px, grow, fill][][270px, fill]", "[][][grow,fill][]"));

    contentPanel.add(headerLabel, "span 3,wrap");
    contentPanel.add(pageSorterHeaderLabel);
    contentPanel.add(jobOverviewTreeHeaderLabel, "skip, wrap");

    contentPanel.add(pageSorter, "spany 4, height 400px");

    contentPanel.add(arrowLabel);
    contentPanel.add(jobOverviewTree, "spany 4, wrap");

    contentPanel.add(documentNameLabel, "wrap");
    contentPanel.add(documentNameTextField, "grow, split");
    contentPanel.add(new JLabel("*"), "wrap");
    contentPanel.add(createTaskButton, "wrap, gapbottom 30px");

    contentPanel.add(thumbnailSizeLabel, "split 4, grow");
    contentPanel.add(smallThumbnailsButton, "sg");
    contentPanel.add(normalThumbnailsButton, "sg");
    contentPanel.add(largeThumbnailsButton, "sg");

  }

  public JobController getController() {
    return controller;
  }

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


    // try {
    // genImage(16, 16, 5, 5, 6, 6, new File("small-icon.png"));
    // genImage(16, 16, 4, 4, 8, 8, new File("normal-icon.png"));
    // genImage(16, 16, 2, 2, 12, 12, new File("large-icon.png"));
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

  }
  //
  // private static void genImage(int w, int h, int x, int y, int rectW, int rectH, File f) throws
  // IOException {
  // BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
  // Graphics2D g2d = bi.createGraphics();
  // g2d.setColor(Color.GRAY);
  // g2d.fillRect(x, y, rectW, rectH);
  // g2d.dispose();
  //
  // ImageIO.write(bi, "PNG", f);
  // }
}
