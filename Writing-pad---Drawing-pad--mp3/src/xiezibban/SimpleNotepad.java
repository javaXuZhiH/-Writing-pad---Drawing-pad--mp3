package xiezibban;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @ClassName SimpleNotepad
 * @Description TODO
 * @Author Xu
 * @Date 2024/5/4 20:49
 * @Version 1.0
 **/
public class SimpleNotepad extends JFrame {
    private JTextPane textPane;
    private JButton findButton;
    private JButton replaceButton;
    private JButton fontSizeButton;
    private JButton colorButton;

    public SimpleNotepad() {
        setTitle("写字板");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        findButton = new JButton("查找");
        replaceButton = new JButton("替换");
        fontSizeButton = new JButton("修改大小");
        colorButton = new JButton("修改颜色");
        buttonPanel.add(findButton);
        buttonPanel.add(replaceButton);
        buttonPanel.add(fontSizeButton);
        buttonPanel.add(colorButton);
        add(buttonPanel, BorderLayout.NORTH);

        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String findText = JOptionPane.showInputDialog("输入要查找的字符:");
                if (findText != null && !findText.isEmpty()) {
                    highlightText(findText);
                }

            }
        });

        replaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String findText = JOptionPane.showInputDialog("输入要替换的字符:");
                String replaceText = JOptionPane.showInputDialog("输入替换后的内容:");
                if (findText != null && !findText.isEmpty() && replaceText != null) {
                    String text = textPane.getText();
                    text = text.replace(findText, replaceText);
                    textPane.setText(text);
//                    replaceText(findText, replaceText);
                }
            }
        });

        fontSizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fontSize = JOptionPane.showInputDialog("输入大小:");
                try {
                    int size = Integer.parseInt(fontSize);
//                    textPane.setFont(new Font(textPane.getFont().getFamily(), Font.PLAIN, size));
                    changeFontSize(size);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "未知大小");
                }
            }
        });

        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(null, "选择颜色", textPane.getForeground());
                if (newColor != null) {
//                    textPane.setForeground(newColor);
                    changeFontColor(newColor);
                }
            }
        });
    }
    private void highlightText(String findText) {
        Highlighter highlighter = textPane.getHighlighter();
        highlighter.removeAllHighlights();

        String text = textPane.getText();
        int index = text.indexOf(findText);
        while (index >= 0) {
            try {
                highlighter.addHighlight(index, index + findText.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            index = text.indexOf(findText, index + findText.length());
        }
    }



    private void changeFontSize(int size) {
        StyledDocument doc = (StyledDocument) textPane.getDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontSize(attrs, size);
            doc.setCharacterAttributes(start, end - start, attrs, false);
        }
    }

    private void changeFontColor(Color color) {
        StyledDocument doc = (StyledDocument) textPane.getDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            doc.setCharacterAttributes(start, end - start, attrs, false);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SimpleNotepad().setVisible(true);
            }
        });
    }
}
