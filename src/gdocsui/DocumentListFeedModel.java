/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gdocsui;

import com.google.gdata.data.docs.DocumentListFeed;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Franck
 */
public class DocumentListFeedModel extends AbstractTableModel {

    private DocumentListFeed documentListFeed;

    public DocumentListFeedModel(DocumentListFeed documentListFeed) {
        this.documentListFeed = documentListFeed;
    }

    public int getRowCount() {
        return documentListFeed.getEntries().size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        if (columnIndex == 0) {
            result = documentListFeed.getEntries().get(rowIndex).getTitle().getPlainText();
        } else if (columnIndex == 1) {
            result = documentListFeed.getEntries().get(rowIndex).getLastViewed();
        }
        return result;
    }

    @Override
    public String getColumnName(int column) {
        String name = "";
        switch(column) {
            case 0 : name = "Name"; break;
            case 1 : name = "Last Viewed"; break;
        }
        return name;
    }
}
