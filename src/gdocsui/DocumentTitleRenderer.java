package gdocsui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Franck
 */
public class DocumentTitleRenderer extends DefaultTableCellRenderer {

    private final ImageIcon spreadsheetIcon;
    private final ImageIcon documentIcon;
    private final ImageIcon presentationIcon;
    private final ImageIcon pdfIcon;

    public DocumentTitleRenderer() {
        //icon_4_doc.gif
        //icon_4_folder.gif
        //icon_4_form.gif
        //icon_4_pdf.gif
        //icon_4_pres.gif
        //icon_4_spread.gif

        spreadsheetIcon = new ImageIcon(
                getClass().getResource("resources/images/icon_4_spread.gif"));
        documentIcon = new ImageIcon(
                getClass().getResource("resources/images/icon_4_doc.gif"));
        presentationIcon = new ImageIcon(
                getClass().getResource("resources/images/icon_4_pres.gif"));
        pdfIcon = new ImageIcon(
                getClass().getResource("resources/images/icon_4_pdf.gif"));

        setHorizontalTextPosition(JLabel.TRAILING);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        TableModel model = table.getModel();
        String type = (String) model.getValueAt(table.convertRowIndexToModel(row), DocumentListFeedTableModel.TYPE_COLUMN);
        ImageIcon icon = null;
        if ("document".equals(type)) {
            icon = documentIcon;
        } else if ("spreadsheet".equals(type)) {
            icon = spreadsheetIcon;
        } else if ("presentation".equals(type)) {
            icon = presentationIcon;
        }else if ("pdf".equals(type)) {
            icon = pdfIcon;
        }

        setIcon(icon);

        return this;
    }
}
