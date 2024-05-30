package drawing;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DrawingTool
 * @Description TODO
 * @Author Xu
 * @Date 2024/5/4 21:31
 * @Version 1.0
 **/
public class DrawingTool  extends JFrame {

    private DrawingPanel drawingPanel;
    private BufferedImage drawingBuffer;
    private BufferedImage cutBuffer;
    private Graphics2D graphics;
    private Color penColor = Color.WHITE;
    private float penWidth = 2.0f;
    private Rectangle clipRect = null; // 裁剪区域矩形
    private boolean drawingClipRect = false; // 是否正在绘制裁剪区域
    private ArrayList<DrawingPanel.DrawingPath> paths = new ArrayList<>();

    public DrawingTool() {
        setTitle("Drawing Tool");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        drawingPanel = new DrawingPanel();
        drawingPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        drawingPanel.setPreferredSize(new Dimension(700, 500)); // Set preferred size for drawing panel
        add(drawingPanel, BorderLayout.CENTER);

        JPanel controlsPanel = new JPanel();
        JButton penColorButton = new JButton("画笔颜色");
        penColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Pen Color", penColor);
            if (newColor != null) {
                penColor = newColor;
                drawingPanel.setDrawColor(penColor);
            }
        });
        controlsPanel.add(penColorButton);

        JButton penWidthButton = new JButton("画笔粗细");
        penWidthButton.addActionListener(e -> {
            String width = JOptionPane.showInputDialog("Enter pen width:");
            try {
                float newWidth = Float.parseFloat(width);
                if (newWidth > 0) {
                    penWidth = newWidth;
                    drawingPanel.setStrokeWidth(penWidth);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid width");
            }
        });
        controlsPanel.add(penWidthButton);

        JButton clipButton = new JButton("裁剪");
        clipButton.addActionListener(e -> {
            System.out.println(clipRect);
            if (clipRect != null) {
                System.out.println(clipRect.width);

                BufferedImage clippedImage = new BufferedImage(clipRect.width*2, clipRect.height*2, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = clippedImage.createGraphics();
                g2d.setClip(clipRect);
                for (DrawingPanel.DrawingPath dp : paths) {
                    dp.draw(g2d);
                }
                g2d.dispose();
                // 更新 drawingBuffer 为裁剪后的图像
                drawingBuffer = clippedImage;
                cutBuffer =clippedImage;
                System.out.println(cutBuffer);
                repaint();
            }
        });
        controlsPanel.add(clipButton);

        JToggleButton clipModeButton = new JToggleButton("选择裁剪范围");
        clipModeButton.addActionListener(e -> {
            boolean isSelected = clipModeButton.isSelected();
            setDrawingClipRect(isSelected);
        });
        controlsPanel.add(clipModeButton);

        JButton insertImageButton = new JButton("插入图片");
        insertImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage image = ImageIO.read(selectedFile);
                    System.out.println(image);
                    // Insert the image into the drawing buffer
                    drawingPanel.insertImage(image);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        controlsPanel.add(insertImageButton);

        add(controlsPanel, BorderLayout.NORTH);

        pack(); // Ensure the components are laid out before creating the BufferedImage
        updateDrawingBuffer();
        setVisible(true);
    }
    public void setDrawingClipRect(boolean drawingClipRect) {
        this.drawingClipRect = drawingClipRect;
        if (!drawingClipRect) {
            clipRect = null; // 清除裁剪区域
//            cutBuffer =null;
        }
    }
    private void updateDrawingBuffer() {
        drawingBuffer = new BufferedImage(drawingPanel.getWidth(), drawingPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics = drawingBuffer.createGraphics();
        graphics.setColor(penColor);
        graphics.setStroke(new BasicStroke(penWidth));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DrawingTool tool = new DrawingTool();
            tool.setVisible(true);
        });
    }

    class DrawingPanel extends JPanel {

        private Color drawColor = Color.BLACK;
        private float strokeWidth = 2.0f;
        private ArrayList<Point> points = new ArrayList<>();
        private List<Image> images = new ArrayList<>();
        private GeneralPath path = new GeneralPath();
        private Point startPoint;
        public DrawingPanel() {
            setBackground(Color.WHITE); // 设置面板背景色
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint =e.getPoint();
                    System.out.println(drawingClipRect);
                    if (drawingClipRect) {
                        // 开始绘制裁剪区域
//                        clipRect = new Rectangle(e.getPoint());
                    }else {
                        points.add(e.getPoint());
                        path.moveTo(e.getPoint().x, e.getPoint().y);
                        paths.add(new DrawingPath(path, drawColor, strokeWidth));
                    }

                }
                @Override
                public void mouseReleased(MouseEvent e) {
//                    if (drawingClipRect) {
//                         完成裁剪区域的绘制
//                        drawingClipRect = false;
//                        clipRect =null;
//                        cutBuffer =null;
//                    }else {
                        // 当鼠标释放时添加路径
                        addPath(path, drawColor, strokeWidth);
                        path = new GeneralPath(); // 准备下一个路径
//                    }

                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (drawingClipRect) {
                        // 更新裁剪区域的大小
                        int x = Math.min(startPoint.x, e.getPoint().x);
                        int y = Math.min(startPoint.y, e.getPoint().y);
                        int width = Math.abs(e.getX() - startPoint.x);
                        int height = Math.abs(e.getY() - startPoint.y);
                        clipRect = new Rectangle(x, y, width, height);
//                        clipRect.setSize();
                        repaint();
                    } else {
                        points.add(e.getPoint());
                        path.lineTo(e.getPoint().x, e.getPoint().y);
                        repaint(); // 重绘面板
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (drawingClipRect) {
                        // 更新裁剪区域的大小
                        int x = Math.min(startPoint.x, e.getPoint().x);
                        int y = Math.min(startPoint.y, e.getPoint().y);
                        int width = Math.abs(e.getPoint().x - startPoint.x);
                        int height = Math.abs(e.getPoint().y - startPoint.y);
                        clipRect = new Rectangle(x, y, width, height);
                        repaint();
                    } else {
                        points.add(e.getPoint());
                        path.lineTo(e.getPoint().x, e.getPoint().y);
                        repaint(); // 重绘面板
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            if (clipRect != null) {
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(clipRect);
//                g2d.drawImage(drawingBuffer,clipRect.x,clipRect.y,this);
            }

            for (Image image :images){
                g2d.drawImage(image, 0, 0, this);
            }
            if (cutBuffer != null){
                System.out.println(1111111);
                g2d.clearRect(0, 0, getWidth(), getHeight());
                g2d.drawImage(drawingBuffer,0,0,this);
                paths.clear();
                cutBuffer =null;
            }else {
                g2d.drawImage(drawingBuffer,0,0,this);
                for (DrawingPath dp : paths) {
                    dp.draw(g2d);
                }
            }


        }
        public void addPath(GeneralPath path, Color color, float strokeWidth) {
            paths.add(new DrawingPath(path, color, strokeWidth));
            repaint(); // 重绘面板以显示新的路径
        }
        // 设置画笔颜色
        public void setDrawColor(Color color) {
            drawColor = color;
            repaint(); // 重绘面板以显示新的画笔颜色
        }

        // 设置画笔粗细
        public void setStrokeWidth(float width) {
            strokeWidth = width;
            repaint(); // 重绘面板以显示新的画笔宽度
        }

        // 插入图片
        public void insertImage(BufferedImage image) {
            images.add(image);
            Graphics2D g2d = (Graphics2D) this.getGraphics();
            if (g2d != null) {
                g2d.drawImage(image, 0, 0, this);
            }
//            repaint(); // 重绘面板以显示新的图片
        }

        class DrawingPath {
            private GeneralPath path;
            private Color color;
            private float strokeWidth;

            public DrawingPath(GeneralPath path, Color color, float strokeWidth) {
                this.path = path;
                this.color = color;
                this.strokeWidth = strokeWidth;
            }

            public void draw(Graphics2D g2d) {
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(strokeWidth));
                g2d.draw(path);
            }
        }
    }
}
