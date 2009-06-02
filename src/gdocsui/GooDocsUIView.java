/*
 * GooDocsUIView.java
 */
package gdocsui;

import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sample.docs.DocumentList;
import sample.docs.DocumentListException;

/**
 * The application's main frame.
 */
public class GooDocsUIView extends FrameView {

    public GooDocsUIView(SingleFrameApplication app) {
        super(app);

        initComponents();

        jTable1.setModel(new DocumentListFeedTableModel(new DocumentListFeed()));
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                jBDownload.setEnabled(!((ListSelectionModel) e.getSource()).isSelectionEmpty());
            }
        });
        jTable1.setColumnModel(createColumnModel());

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = GooDocsUIApp.getApplication().getMainFrame();
            aboutBox = new GooDocsUIAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        GooDocsUIApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jTFLogin = new javax.swing.JTextField();
        jPFPassword = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jBDownload = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(gdocsui.GooDocsUIApp.class).getContext().getResourceMap(GooDocsUIView.class);
        jTFLogin.setText(resourceMap.getString("jTFLogin.text")); // NOI18N
        jTFLogin.setName("jTFLogin"); // NOI18N

        jPFPassword.setText(resourceMap.getString("jPFPassword.text")); // NOI18N
        jPFPassword.setName("jPFPassword"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(gdocsui.GooDocsUIApp.class).getContext().getActionMap(GooDocsUIView.class, this);
        jButton1.setAction(actionMap.get("login")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTable1);

        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jBDownload.setAction(actionMap.get("downloadAs")); // NOI18N
        jBDownload.setText(resourceMap.getString("jBDownload.text")); // NOI18N
        jBDownload.setEnabled(false);
        jBDownload.setName("jBDownload"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPFPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(388, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jPFPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 310, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    @Action()
    public Task login() {
        return new LoginTask(getApplication());
    }

    private class LoginTask extends org.jdesktop.application.Task<Object, Void> {

        private String login;
        private String password;

        LoginTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to LoginTask fields, here.
            super(app);

            login = jTFLogin.getText();
            password = jPFPassword.getText();

            System.out.println("Connect with : " + login + " / " + password);
        }

        @Override
        protected Object doInBackground() throws InterruptedException, DocumentListException, AuthenticationException, IOException, MalformedURLException, ServiceException {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            String authProtocol = DocumentList.DEFAULT_AUTH_PROTOCOL;
            String authHost = DocumentList.DEFAULT_AUTH_HOST;
            String protocol = DocumentList.DEFAULT_PROTOCOL;
            String host = DocumentList.DEFAULT_HOST;

            String APPLICATION_NAME = "Java UI GData Client";
            documentList = new DocumentList(APPLICATION_NAME, authProtocol, authHost, protocol, host);
            documentList.login(login, password);

            feed = documentList.getDocsListFeed("all");

            if (feed != null) {
                for (DocumentListEntry entry : feed.getEntries()) {
                    System.out.println(entry);
                    System.out.println(entry.getTitle());
                    System.out.println(entry.getId());
                }
                ((DocumentListFeedTableModel)jTable1.getModel()).setDocumentListFeed(feed);
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action(block = Task.BlockingScope.ACTION)
    public Task downloadAs() throws DocumentListException {
        return new DownloadAsTask(getApplication());

    }

    private class DownloadAsTask extends org.jdesktop.application.Task<Object, Void> {

        private int selectedRow;
        private File selectedFile = null;
        private String extension = null;
        private boolean download = false;

        DownloadAsTask(org.jdesktop.application.Application app) throws DocumentListException {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DownloadAsTask fields, here.
            super(app);
            selectedRow = jTable1.getSelectedRow();
            if (selectedRow != -1) {
                DocumentListEntry entry = feed.getEntries().get(selectedRow);
                System.out.println(entry);

                String docType = DocumentTools.getObjectIdPrefix(DocumentTools.getShortId(entry));


                String proposedFilename = null;
                File sFile = null;

                final JFileChooserOverwrite chooser = new JFileChooserOverwrite();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                String preferredExtension = null;
                String extensions[];
                if (docType.equals("spreadsheet")) {
                    extensions = new String[]{"xls", "ods", "pdf", "csv", "tsv", "html"};
                    preferredExtension = "xls";
                } else if (docType.equals("document")) {
                    extensions = new String[]{"doc", "txt", "odt", "pdf", "png", "rtf", "html"};
                    preferredExtension = "doc";
                } else if (docType.equals("pdf")) {
                    extensions = new String[]{"pdf"};
                    preferredExtension = "pdf";
                } else if (docType.equals("presentation")) {
                    extensions = new String[]{"pdf", "ppt", "swf", "png"};
                    preferredExtension = "ppt";
                } else {
                    throw new IllegalArgumentException("Unknown docType '" + docType + "'");
                }
                proposedFilename = entry.getTitle().getPlainText() + "." + preferredExtension;
                sFile = new File(proposedFilename);
                prepareJFileChooser(chooser, sFile, extensions, preferredExtension);

                final File proposedFile = sFile;

                // debug
                chooser.addPropertyChangeListener(new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        System.out.println("Property name=" + evt.getPropertyName() + ", oldValue=" + evt.getOldValue() + ", newValue=" + evt.getNewValue());
                        System.out.println("getSelectedFile()=" + chooser.getSelectedFile());
                    }
                });

                chooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        Object o = evt.getNewValue();
                        if (o instanceof FileNameExtensionFilter) {
                            FileNameExtensionFilter filter = (FileNameExtensionFilter) o;

                            String ex = filter.getExtensions()[0];

                            File selectedFile = chooser.getSelectedFile();
                            if (selectedFile == null) {
                                selectedFile = proposedFile;
                            }
                            String path = selectedFile.getName();
                            path = path.substring(0, path.lastIndexOf("."));

                            chooser.setSelectedFile(new File(path + "." + ex));
                        }
                    }
                });


                // Show the dialog; wait until dialog is closed
                int result = chooser.showSaveDialog(getComponent());

                // Determine which button was clicked to close the dialog
                switch (result) {
                    case JFileChooser.APPROVE_OPTION:
                        // Approve (Open or Save) was clicked
                        selectedFile = chooser.getSelectedFile();
                        System.out.println("getSelectedFile() = " + selectedFile);
                        System.out.println("getFileFilter() = " + chooser.getFileFilter());
                        extension = ((FileNameExtensionFilter) chooser.getFileFilter()).getExtensions()[0];

                        download = true;
                        break;
                    case JFileChooser.CANCEL_OPTION:
                        // Cancel or the close-dialog icon was clicked
                        break;
                    case JFileChooser.ERROR_OPTION:
                        // The selection process did not complete successfully
                        break;
                }

            }
        }

        public void prepareJFileChooser(final JFileChooserOverwrite chooser, File sFile, String[] extensions, String preferredExtension) {
            chooser.setSelectedFile(sFile);
            FileFilter ff = null;
            for (int i = 0; i < extensions.length; i++) {
                String ex = extensions[i];
                FileNameExtensionFilter fnef = new FileNameExtensionFilter(ex, ex);
                if (ex.equals(preferredExtension)) {
                    ff = fnef;
                }
                chooser.addChoosableFileFilter(fnef);
            }
            chooser.setFileFilter(ff);
        }

        @Override
        protected Object doInBackground() throws DocumentListException, IOException, MalformedURLException, ServiceException {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            if (download && (selectedRow != -1)) {
                DocumentListEntry entry = feed.getEntries().get(selectedRow);
                //String docType = DocumentTools.getObjectIdPrefix(DocumentTools.getShortId(entry));
                String shortId = DocumentTools.getShortId(entry);
                setMessage("Downloading " + selectedFile.getName() + "...");
                // TODO: fix pdf download
                documentList.downloadFile(shortId, selectedFile.getCanonicalPath(), DocumentList.getDownloadFormat(shortId, extension));
                setMessage("Finished downloading " + selectedFile.getName() + ".");
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }
   protected TableColumnModel createColumnModel() {
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();

        DocumentTitleRenderer documentTitleRenderer = new DocumentTitleRenderer();

        TableColumn column = new TableColumn();
        column.setModelIndex(DocumentListFeedTableModel.TITLE_COLUMN);
        column.setHeaderValue("Title");
        column.setCellRenderer(documentTitleRenderer);
        columnModel.addColumn(column);

        column = new TableColumn();
        column.setModelIndex(DocumentListFeedTableModel.LAST_VIEWED_COLUMN);
        column.setHeaderValue("Last Viewed");
        columnModel.addColumn(column);

        return columnModel;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBDownload;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPasswordField jPFPassword;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTFLogin;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private DocumentList documentList = null;
    private DocumentListFeed feed = null;
}
