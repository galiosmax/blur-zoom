package panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {

    private MyPanel panel;
    private JLabel status;

    private JToolBar toolBar;
    private JPanel statusBar;

    private final String dataPath = System.getProperty("user.dir");
    private String lastPath = dataPath;
    private final String resources = "/";
    private File currentFile = null;
    private BufferedImage imageToSave = null;

    public MainFrame() {
        super("GaussZoom");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitFileItemListener();
            }
        });

        panel = new MyPanel(this);
        createMenuAndToolBar();
        createStatusBar();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1600, 800));

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText("X: " + e.getX() + "; Y: " + e.getY());
            }
        });
        add(scrollPane);
        pack();
        setVisible(true);
    }

    private void createMenuAndToolBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("GaussZoom Toolbar");
        add(toolBar, BorderLayout.PAGE_START);

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");
        JMenu aboutMenu = new JMenu("About");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(aboutMenu);

        createFileMenuItems(fileMenu);
        createEditMenuItems(editMenu);
        createViewMenuItems(viewMenu);
        createAboutMenuItems(aboutMenu);
    }

    private void createStatusBar() {
        statusBar = new JPanel();
        status = new JLabel("Ready");
        statusBar.add(status);
        add(statusBar, BorderLayout.PAGE_END);
    }

    private void createFileMenuItems(JMenu fileMenu) {

        createMenuAndToolbarItem("New", "Create new file", "new.png", e -> newFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Load...", "Open image", "open.png", e -> loadFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Save", "Save image", "save.png", e -> saveFileItemListener(), fileMenu);
        createMenuAndToolbarItem("Save as...", "Save image as...", "save-as.png", e -> saveAsFileItemListener(), fileMenu);
        fileMenu.addSeparator();
        toolBar.addSeparator();
        createMenuAndToolbarItem("Exit", "Close the window", "exit.png", e -> exitFileItemListener(), fileMenu);
        toolBar.addSeparator();
    }

    private void newFileItemListener() {
        askSave();
        panel.clear();
        currentFile = null;
        imageToSave = null;
        lastPath = dataPath;
    }

    private void loadFileItemListener() {
        askSave();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "bmp", "PNG", "BMP");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(lastPath));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (!fileChooser.getCurrentDirectory().getAbsolutePath().equals(lastPath)) {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.setFileFilter(filter);
        if (fileChooser.showDialog(null, "Открыть файл") == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            File file = new File(fileName);
            String extension = getExtension(fileName);
            if (extension == null || extension.equals("txt")) {
                cantReadFile();
                return;
            }

            try {
                panel.setImage(ImageIO.read(file));
                imageToSave = null;
                lastPath = file.getParent();
            } catch (IOException e) {
                cantReadFile();
            }
        }

    }

    private void cantReadFile() {
        JOptionPane.showMessageDialog(null, "Can't read the file");
        currentFile = null;
    }

    private void cantSaveFile() {
        JOptionPane.showMessageDialog(null, "Can't save the file");
        currentFile = null;
    }

    private int askSave() {
        if (imageToSave != null) {
            int response = JOptionPane.showConfirmDialog(null, "Would you like to save the picture?");
            if (response == JOptionPane.OK_OPTION) {
                saveFileItemListener();
            }
            return response;
        }
        return JOptionPane.NO_OPTION;
    }

    private void saveFileItemListener() {
        if (imageToSave != null) {
            if (currentFile == null) {
                saveAsFileItemListener();
            } else {
                save();
            }
        }
    }

    private String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        if (!extension.equals("png") && !extension.equals("bmp") && !extension.equals("PNG") && !extension.equals("BMP") && !extension.equals("txt")) {
            return null;
        }
        return extension;
    }

    private void saveAsFileItemListener() {

        if (imageToSave != null) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "bmp", "PNG", "BMP");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showSaveDialog(null);
            currentFile = fileChooser.getSelectedFile();
            if (currentFile != null) {
                save();
            }
        }
    }

    private void save() {

        String extension = getExtension(currentFile.getName());
        if (extension == null) {
            cantSaveFile();
            saveAsFileItemListener();
            return;
        }

        try {
            ImageIO.write(imageToSave, extension, currentFile);
        } catch (IOException e) {
            cantSaveFile();
        }
    }

    private void exitFileItemListener() {

        int response = askSave();
        if (response == JOptionPane.OK_OPTION || response == JOptionPane.NO_OPTION) {
            setVisible(false);
            dispose();
            System.exit(0);
        }
    }

    private void createEditMenuItems(JMenu editMenu) {

        createMenuAndToolbarItem("A to B", "Copy image from zone A to B", "right.png", e -> aToBEditItemListener(), editMenu);
        createMenuAndToolbarItem("B to A", "Copy image from zone B to A", "left.png", e -> bToAEditItemListener(), editMenu);
        editMenu.addSeparator();
        toolBar.addSeparator();
        createMenuAndToolbarItem("Zoom", "Get zoomed image", "zoom.png", e -> zoomEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("Gauss", "Get Gaussian blur image", "gauss.png", e -> gaussEditMenuItemListener(), editMenu);
        createMenuAndToolbarItem("GaussZoom", "Make final pic", "roberts.png", e -> gaussZoomEditMenuItemListener(), editMenu);
        toolBar.addSeparator();
        editMenu.addSeparator();
    }

    private void aToBEditItemListener() {
        panel.copyAToB();
    }

    private void bToAEditItemListener() {
        panel.copyBToA();
    }

    private void zoomEditMenuItemListener() {
        panel.zoom();
    }

    private void gaussEditMenuItemListener() {
        panel.blurFilter();
    }

    private void gaussZoomEditMenuItemListener() {
        panel.gaussZoom();
    }

    private void createViewMenuItems(JMenu viewMenu) {

        createCheckBoxMenuItem("Toolbar", "Remove toolbar", "toolbar.png", e -> toolbarViewItemListener(), viewMenu);
        createCheckBoxMenuItem("Status bar", "Remove status bar", "status.png", e -> statusViewItemListener(), viewMenu);
    }

    private void toolbarViewItemListener() {
        if (toolBar.isVisible()) {
            toolBar.setVisible(false);
        } else {
            toolBar.setVisible(true);
        }
    }

    private void statusViewItemListener() {
        if (statusBar.isVisible()) {
            statusBar.setVisible(false);
        } else {
            statusBar.setVisible(true);
        }
    }

    private void createAboutMenuItems(JMenu aboutMenu) {
        createMenuAndToolbarItem("About", "Open about window", "about.png", e -> aboutItemListener(), aboutMenu);
    }

    private void aboutItemListener() {

        JDialog aboutDialog = new JDialog();
        JPanel panel = new JPanel();
        JPanel info = new JPanel(new GridLayout(4, 1, 10, 10));
        aboutDialog.setTitle("About \"GaussZoom\" by Galios Maxim");

        ImageIcon me = new ImageIcon(getClass().getResource(resources + "me.jpg"));
        JLabel photo = new JLabel(me);
        JLabel progInfo = new JLabel("Program: Filter v1.0");
        JLabel authorInfo = new JLabel("Author: Galios Maxim");
        JLabel groupInfo = new JLabel("Group: FIT 16203");
        JLabel yearInfo = new JLabel("Year: 2019");
        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.setPreferredSize(new Dimension(80, 30));

        okButton.addActionListener(e -> {
            aboutDialog.setVisible(false);
            aboutDialog.dispose();
        });

        panel.add(photo);
        info.add(progInfo);
        info.add(authorInfo);
        info.add(groupInfo);
        info.add(yearInfo);
        panel.add(info);
        panel.add(okButton);
        aboutDialog.add(panel);
        aboutDialog.pack();
        aboutDialog.setVisible(true);
    }

    void setImageToSave(BufferedImage image) {
        this.imageToSave = image;
    }

    private void createMenuAndToolbarItem(String name, String tipText, String imageName, ActionListener e, JMenu menu) {

        JMenuItem item = new JMenuItem(name);
        menu.add(item);
        item.setIcon(new ImageIcon(getClass().getResource(resources + imageName)));

        JButton button = new JButton(item.getIcon());
        item.setToolTipText(tipText);

        item.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(item.getToolTipText());
            }
        });

        item.addActionListener(e);
        button.setToolTipText(item.getToolTipText());

        for (MouseMotionListener i : item.getMouseMotionListeners()) {
            button.addMouseMotionListener(i);
        }

        for (ActionListener i : item.getActionListeners()) {
            button.addActionListener(i);
        }
        toolBar.add(button);
    }

    private void createCheckBoxMenuItem(String name, String tipText, String imageName, ActionListener e, JMenu menu) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
        menu.add(item);
        item.setIcon(new ImageIcon(getClass().getResource(resources + imageName)));
        item.setToolTipText(tipText);

        item.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                status.setText(item.getToolTipText());
            }
        });

        item.addActionListener(e);
        item.setSelected(true);
    }

}
