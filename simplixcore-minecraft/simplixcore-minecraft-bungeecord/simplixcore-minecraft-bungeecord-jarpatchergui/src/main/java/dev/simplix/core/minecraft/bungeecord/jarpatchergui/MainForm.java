package dev.simplix.core.minecraft.bungeecord.jarpatchergui;

import dev.simplix.core.minecraft.bungeecord.slf4j.ServiceProviderPatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainForm {

  private JTextField filePathTextField;
  private JButton searchButton;
  private JButton patchButton;
  private JPanel panel;

  private File file;

  public MainForm(JFrame frame) {
    this.searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java archive", "jar"));
        fileChooser.showOpenDialog(frame);
        MainForm.this.file = fileChooser.getSelectedFile();
        if (MainForm.this.file == null) {
          MainForm.this.patchButton.setEnabled(false);
          MainForm.this.filePathTextField.setText("");
          return;
        }
        MainForm.this.patchButton.setEnabled(true);
        MainForm.this.filePathTextField.setText(MainForm.this.file.getAbsolutePath());
      }
    });
    this.patchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ServiceProviderPatcher.patchJarUnix(MainForm.this.file);
        JOptionPane.showMessageDialog(frame, "The file has been patched succesfully.");
      }
    });
  }

  public static void main(String[] args)
      throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    JFrame frame = new JFrame("BungeeCord Slf4j Patcher");
    frame.setResizable(false);
    frame.setContentPane(new MainForm(frame).panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
