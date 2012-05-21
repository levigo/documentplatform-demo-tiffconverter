package org.levigo.jadice.demo.tiffconverter.ui;

import java.awt.Font;

import javax.swing.JLabel;

import com.levigo.util.swing.IconManager;

public class UIUtils {
  public static final IconManager ICONS = IconManager.getInstance("/org/levigo/jadice/demo/tiffconverter/resources/icons");
  public static final IconManager IMAGES = IconManager.getInstance("/org/levigo/jadice/demo/tiffconverter/resources/images");

  public static JLabel createHeader1Label(String text) {
    JLabel label = createLabel(text);
    Font baseFont = label.getFont();
    label.setFont(baseFont.deriveFont(Font.BOLD).deriveFont(baseFont.getSize2D() * 2f));
    return label;
  }
  
  public static JLabel createHeader2Label(String text) {
    JLabel label = createLabel(text);

    Font baseFont = label.getFont();
    label.setFont(baseFont.deriveFont(Font.BOLD).deriveFont(baseFont.getSize2D() * 1.6f));

    return label;
  }

  public static JLabel createLabel(String text) {
    return new JLabel(text);
  }

}
