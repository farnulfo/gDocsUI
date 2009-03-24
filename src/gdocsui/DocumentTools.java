/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gdocsui;

import com.google.gdata.data.docs.DocumentListEntry;
import sample.docs.DocumentListException;

/**
 *
 * @author Franck
 */
public class DocumentTools {

    public static String getObjectIdPrefix(String objectId) throws DocumentListException {
        if (objectId == null || objectId.indexOf("%3A") == 0) {
            throw new DocumentListException("null objectId");
        }

        return objectId.substring(0, objectId.indexOf("%3A"));
    }

    public static String getShortId(DocumentListEntry entry) {
        String shortId = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
        return shortId;
    }
}
