/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gdocsui;

import com.google.gdata.data.docs.DocumentListFeed;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import sample.docs.DocumentListException;

/**
 *
 * @author Franck
 */
public class DocumentListFeedTableModel extends AbstractTableModel {

    public static final int TYPE_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int LAST_VIEWED_COLUMN = 2;
    public static final int COLUMN_COUNT = 3;

    private DocumentListFeed documentListFeed;

    public DocumentListFeedTableModel(DocumentListFeed documentListFeed) {
        this.documentListFeed = documentListFeed;
        fireTableDataChanged();
    }

    public void setDocumentListFeed(DocumentListFeed documentListFeed) {
        this.documentListFeed = documentListFeed;
        fireTableDataChanged();
    }

    public DocumentListFeed getDocumentListFeed() {
        return documentListFeed;
    }

    public int getRowCount() {
        return documentListFeed.getEntries().size();
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        if (columnIndex == TYPE_COLUMN) {
            try {
                result = DocumentTools.getObjectIdPrefix(DocumentTools.getShortId(documentListFeed.getEntries().get(rowIndex)));
            } catch (DocumentListException ex) {
                Logger.getLogger(DocumentListFeedTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (columnIndex == TITLE_COLUMN) {
            result = documentListFeed.getEntries().get(rowIndex).getTitle().getPlainText();
        } else if (columnIndex == LAST_VIEWED_COLUMN) {
            result = documentListFeed.getEntries().get(rowIndex).getLastViewed();
        }
        return result;
    }

//    @Override
//    public String getColumnName(int column) {
//        String name = "";
//        switch (column) {
//            case 0:
//                name = "Name";
//                break;
//            case 1:
//                name = "Last Viewed";
//                break;
//        }
//        return name;
//    }
}
