package org.levigo.jadice.demo.tiffconverter.ui.jobtree;

import javax.swing.JScrollPane;

import org.levigo.jadice.demo.tiffconverter.controller.JobController;

import com.levigo.jadice.swing.infotree.PageListModule;
import com.levigo.jadice.swing.infotree.PageModule;
import com.levigo.jadice.swing.infotree.PageSegmentModule;
import com.levigo.util.swing.flextree.FlexibleTree;

public class JobOverviewTree extends JScrollPane {

  private static final long serialVersionUID = 1L;

  private final FlexibleTree flexibleTree;

  public JobOverviewTree(JobController controller) {

    if (controller == null)
      throw new IllegalArgumentException("controller must not be null");

    flexibleTree = new FlexibleTree();
    flexibleTree.setInput(controller);
    flexibleTree.setShowRootHandles(true);
    flexibleTree.setRootVisible(false);
    flexibleTree.expandRoot();
    flexibleTree.addModule(new JobControllerTreeModule());
    flexibleTree.addModule(new JobModule());
    flexibleTree.addModule(new JobContentModule());
    flexibleTree.addModule(new PageListModule());
    flexibleTree.addModule(new PageModule());
    flexibleTree.addModule(new PageSegmentModule());

    setViewportView(flexibleTree);
  }

  public FlexibleTree getFlexibleTree() {
    return flexibleTree;
  }

}
