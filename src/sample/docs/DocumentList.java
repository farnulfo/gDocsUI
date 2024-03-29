/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package sample.docs;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Query;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.Link;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An application that serves as a sample to show how the Documents List
 * Service can be used to search your documents and upload files.
 *
 * 
 * 
 */
public class DocumentList {
  public DocsService service;
  public GoogleService spreadsheetsService;

  public static final String DEFAULT_AUTH_PROTOCOL = "https";
  public static final String DEFAULT_AUTH_HOST = "docs.google.com";

  public static final String DEFAULT_PROTOCOL = "http";
  public static final String DEFAULT_HOST = "docs.google.com";

  public static final String SPREADSHEETS_SERVICE_NAME = "wise";
  public static final String SPREADSHEETS_HOST = "spreadsheets.google.com";

  private static final String URL_FEED = "/feeds";
  private static final String URL_DOWNLOAD = "/download";

  private static final String URL_GROUP_DOCUMENTS = "/documents";
  private static final String URL_GROUP_FOLDERS = "/folders";
  private static final String URL_GROUP_MEDIA = "/media";
  private static final String URL_GROUP_ACL = "/acl";

  private static final String URL_PATH = "/private/full";

  private static final String URL_CATEGORY_DOCUMENT = "/-/document";
  private static final String URL_CATEGORY_SPREADSHEET = "/-/spreadsheet";
  private static final String URL_CATEGORY_PDF = "/-/pdf";
  private static final String URL_CATEGORY_PRESENTATION = "/-/presentation";
  private static final String URL_CATEGORY_STARRED = "/-/starred";
  private static final String URL_CATEGORY_TRASHED = "/-/trashed";
  private static final String URL_CATEGORY_FOLDER = "/-/folder";
  private static final String URL_CATEGORY_EXPORT = "/Export";

  private static final String PARAMETER_SHOW_FOLDERS = "showfolders=true";

  private static String applicationName;
  private static String authProtocol;
  private static String authHost;
  private static String protocol;
  private static String host;
  private static String username;
  private static String password;
  private static String authSubToken;

  private static final Map<String, String> DOWNLOAD_DOCUMENT_FORMATS;
  static {
    DOWNLOAD_DOCUMENT_FORMATS = new HashMap<String, String>();
    DOWNLOAD_DOCUMENT_FORMATS.put("doc", "doc");
    DOWNLOAD_DOCUMENT_FORMATS.put("txt", "txt");
    DOWNLOAD_DOCUMENT_FORMATS.put("odt", "odt");
    DOWNLOAD_DOCUMENT_FORMATS.put("pdf", "pdf");
    DOWNLOAD_DOCUMENT_FORMATS.put("png", "png");
    DOWNLOAD_DOCUMENT_FORMATS.put("rtf", "rtf");
    DOWNLOAD_DOCUMENT_FORMATS.put("html", "html");
  }

  private static final Map<String, String> DOWNLOAD_PRESENTATION_FORMATS;
  static {
    DOWNLOAD_PRESENTATION_FORMATS = new HashMap<String, String>();
    DOWNLOAD_PRESENTATION_FORMATS.put("pdf", "pdf");
    DOWNLOAD_PRESENTATION_FORMATS.put("ppt", "ppt");
    DOWNLOAD_PRESENTATION_FORMATS.put("swf", "swf");
    DOWNLOAD_PRESENTATION_FORMATS.put("png", "png");
  }

  private static final Map<String, String> DOWNLOAD_SPREADSHEET_FORMATS;
  static {
    DOWNLOAD_SPREADSHEET_FORMATS = new HashMap<String, String>();
    DOWNLOAD_SPREADSHEET_FORMATS.put("xls", "4");
    DOWNLOAD_SPREADSHEET_FORMATS.put("ods", "13");
    DOWNLOAD_SPREADSHEET_FORMATS.put("pdf", "12");
    DOWNLOAD_SPREADSHEET_FORMATS.put("csv", "5");
    DOWNLOAD_SPREADSHEET_FORMATS.put("tsv", "23");
    DOWNLOAD_SPREADSHEET_FORMATS.put("html", "102");
  }

  /**
   * Constructor.
   *
   * @param applicationName name of the application.
   *
   * @throws DocumentListException
   */
  public DocumentList(String applicationName) throws DocumentListException {
    this(applicationName, DEFAULT_AUTH_PROTOCOL, DEFAULT_AUTH_HOST, DEFAULT_PROTOCOL, DEFAULT_HOST);
  }

  /**
   * Constructor
   *
   * @param applicationName name of the application
   * @param authProtocol the protocol to use for authentication
   * @param authHost the host to use for authentication
   * @param protocol the protocol to use for the http calls.
   * @param host the host that contains the feeds
   *
   * @throws DocumentListException
   */
  public DocumentList(String applicationName, String authProtocol, String authHost,
      String protocol, String host) throws DocumentListException {
    if (authProtocol == null || authHost == null || protocol == null || host == null) {
      throw new DocumentListException("null passed in required parameters");
    }

    service = new DocsService(applicationName);

    // Creating a spreadsheets service is necessary for downloading spreadsheets
    spreadsheetsService = new GoogleService(SPREADSHEETS_SERVICE_NAME, applicationName);

    this.applicationName = applicationName;
    this.authProtocol = authProtocol;
    this.authHost = authHost;
    this.protocol = protocol;
    this.host = host;
  }

  /**
   * Set user credentials based on a username and password.
   *
   * @param username to log in with.
   * @param password password for the user logging in.
   *
   * @throws AuthenticationException
   * @throws DocumentListException
   */
  public void login(String username, String password) throws AuthenticationException,
      DocumentListException {
    if (username == null || password == null) {
      throw new DocumentListException("null login credentials");
    }

    this.username = username;
    this.password = password;
    this.authSubToken = "";
    service.setUserCredentials(username, password);
    spreadsheetsService.setUserCredentials(username, password);
  }

  /**
   * Allow a user to login using an AuthSub token.
   *
   * @param token the token to be used when logging in.
   *
   * @throws AuthenticationException
   * @throws DocumentListException
   */
  public void loginWithAuthSubToken(String token) throws AuthenticationException,
      DocumentListException {
    if (token == null) {
      throw new DocumentListException("null login credentials");
    }

    this.authSubToken = token;
    this.username = "";
    this.password = "";

    service.setAuthSubToken(token);
    spreadsheetsService.setAuthSubToken(token);
  }

  /**
   * Create a document.
   *
   * @param title the title of the document to be created.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public DocumentEntry createDocument(String title) throws IOException, MalformedURLException,
      ServiceException, DocumentListException {
    if (title == null) {
      throw new DocumentListException("null title");
    }

    URL url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH);
    DocumentEntry entry = new DocumentEntry();
    entry.setTitle(new PlainTextConstruct(title));

    return service.insert(url, entry);
  }

  /**
   * Create a spreadsheet.
   *
   * @param title the title of the spreadsheet to be created.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public SpreadsheetEntry createSpreadsheet(String title) throws IOException, MalformedURLException,
      ServiceException, DocumentListException {
    if (title == null) {
      throw new DocumentListException("null title");
    }

    URL url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH);
    SpreadsheetEntry entry = new SpreadsheetEntry();
    entry.setTitle(new PlainTextConstruct(title));

    return service.insert(url, entry);
  }

  /**
   * Create a folder.
   *
   * @param title the title of the folder to be created.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public FolderEntry createFolder(String title) throws IOException, MalformedURLException,
      ServiceException, DocumentListException {
    if (title == null) {
      throw new DocumentListException("null title");
    }

    URL url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH);
    FolderEntry entry = new FolderEntry();
    entry.setTitle(new PlainTextConstruct(title));

    return service.insert(url, entry);
  }

  /**
   * Gets a feed containing the documents.
   *
   * @param category what types of documents to list:
   *     "all": lists all the doc objects (documents, spreadsheets, presentations)
   *     "folders": lists all doc objects including folders.
   *     "documents": lists only documents.
   *     "spreadsheets": lists only spreadsheets.
   *     "pdfs": lists only pdfs.
   *     "presentations": lists only presentations.
   *     "starred": lists only starred objects.
   *     "trashed": lists trashed objects.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public DocumentListFeed getDocsListFeed(String category) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    if (category == null) {
      throw new DocumentListException("null category");
    }

    URL url;

    if (category.equals("all")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH);
    } else if (category.equals("folders")) {
      String[] parameters = {PARAMETER_SHOW_FOLDERS};
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_FOLDER, parameters);
    } else if (category.equals("documents")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_DOCUMENT);
    } else if (category.equals("spreadsheets")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_SPREADSHEET);
    } else if (category.equals("pdfs")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_PDF);
    } else if (category.equals("presentations")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_PRESENTATION);
    } else if (category.equals("starred")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_STARRED);
    } else if (category.equals("trashed")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_TRASHED);
    } else {
      return null;
    }

    return service.getFeed(url, DocumentListFeed.class);
  }

  /**
   * Gets the entry for the provided object id.
   *
   * @param objectId the id of the object to return the entry for.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public DocumentListEntry getDocsListEntry(String objectId) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    if (objectId == null) {
      throw new DocumentListException("null objectId");
    }

    URL url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + "/" + objectId);

    return service.getEntry(url, DocumentListEntry.class);
  }

  /**
   * Gets the feed for all the objects contained in a folder.
   *
   * @param folderId the id of the folder to return the feed for the contents.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public DocumentListFeed getFolderDocsListFeed(String folderId) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    if (folderId == null) {
      throw new DocumentListException("null folderId");
    }
    URL url = buildUrl(URL_GROUP_FOLDERS + URL_PATH + "/" + folderId);
    return service.getFeed(url, DocumentListFeed.class);
  }

  /**
   * Search the documents, and return a feed of docs that match.
   *
   * @param searchParameters parameters to be used in searching criteria.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public DocumentListFeed search(Map<String, String> searchParameters) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    return search(searchParameters, null);
  }

  /**
   * Search the documents, and return a feed of docs that match.
   *
   * @param searchParameters parameters to be used in searching criteria.
   *    accepted parameters are:
   *    "q": Typical search query
   *    "alt":
   *    "author":
   *    "start-index":
   *    "max-results":
   *    "updated-min":
   *    "updated-max":
   *    "title": Specifies the search terms for the title of a document.
   *        This parameter used without title-exact will only submit partial queries, not exact
   *        queries.
   *    "title-exact": Specifies whether the title query should be taken as an exact string.
   *        Meaningless without title. Possible values are true and false.
   *    "opened-min": Bounds on the last time a document was opened by the current user.
   *        Use the RFC 3339 timestamp format. For example: 2005-08-09T10:57:00-08:00
   *    "opened-max": Bounds on the last time a document was opened by the current user.
   *        Use the RFC 3339 timestamp format. For example: 2005-08-09T10:57:00-08:00
   *    "owner": Searches for documents with a specific owner.
   *        Use the email address of the owner.
   *    "writer": Searches for documents which can be written to by specific users.
   *        Use a single email address or a comma separated list of email addresses.
   *    "reader": Searches for documents which can be read by specific users.
   *        Use a single email address or a comma separated list of email addresses.
   *    "showfolders": Specifies whether the query should return folders as well as documents.
   *        Possible values are true and false.
   * @param category define the category to search. (documents, spreadsheets, presentations,
   *     starred, trashed, folders)
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public DocumentListFeed search(Map<String, String> searchParameters, String category)
      throws IOException, MalformedURLException, ServiceException, DocumentListException {
    if (searchParameters == null) {
      throw new DocumentListException("searchParameters null");
    }

    URL url;

    if (category == null || category.equals("")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH);
    } else if (category.equals("documents")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_DOCUMENT);
    } else if (category.equals("spreadsheets")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_SPREADSHEET);
    } else if (category.equals("presentations")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_PRESENTATION);
    } else if (category.equals("starred")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_STARRED);
    } else if (category.equals("trashed")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_TRASHED);
    } else if (category.equals("folders")) {
      url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + URL_CATEGORY_FOLDER);
    } else {
      throw new DocumentListException("invaild category");
    }

    Query qry = new Query(url);

    for (String key : searchParameters.keySet()) {
      qry.setStringCustomParameter(key, searchParameters.get(key));
    }

    return service.query(qry, DocumentListFeed.class);
  }

  /**
   * Upload a file.
   *
   * @param filepath path to uploaded file.
   * @param title title to use for uploaded file.
   *
   * @throws ServiceException when the request causes an error in the Doclist
   *         service.
   * @throws IOException when an error occurs in communication with the Doclist
   *         service.
   * @throws DocumentListException
   */
  public DocumentListEntry uploadFile(String filepath, String title) throws IOException,
      ServiceException, DocumentListException {
    if (filepath == null || title == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_GROUP_DOCUMENTS + URL_PATH);
    DocumentEntry newDocument = new DocumentEntry();
    File documentFile = new File(filepath);
    newDocument.setFile(documentFile);
    newDocument.setTitle(new PlainTextConstruct(title));

    return service.insert(url, newDocument);
  }

  /**
   * Trash an object.
   *
   * @param objectId id of object to be trashed.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public void trashObject(String objectId) throws IOException, MalformedURLException,
      ServiceException, DocumentListException {
    if (objectId == null) {
      throw new DocumentListException("null objectId");
    }

    URL url = buildUrl(URL_GROUP_MEDIA + URL_PATH + "/" + objectId);
    service.delete(url, getObjectEtag(objectId));
  }

  /**
   * Remove an object from a folder.
   *
   * @param objectId id of an object to be removed from the folder.
   * @param folderId id of the folder to remove the object from.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public void removeFromFolder(String objectId, String folderId)
      throws IOException, MalformedURLException, ServiceException, DocumentListException {
    if (objectId == null || folderId == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_GROUP_FOLDERS + URL_PATH + "/" + folderId + "/" + objectId);
    service.delete(url, getObjectEtag(objectId));
  }

  /**
   * Download a file.
   *
   * @param filepath path and name of the object to be saved as.
   * @param objectId id of the object to be downloaded.
   * @param format format to download the file to.
   *     The following file types are supported:
   *     documents: "doc", "txt", "odt", "png", "pdf", "rtf", "html"
   *     spreadsheets: "4", "13", "12", "5", "23", "102"
   *     presentations: "pdf", "ppt", "swf"
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public void downloadFile(String objectId, String filepath, String format) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    if (objectId == null || filepath == null || format == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    URL url;
    GoogleService service;
    String id = getObjectIdSuffix(objectId);
    String docType = getObjectIdPrefix(objectId);

    if (docType.equals("spreadsheet")) {
      service = spreadsheetsService;

      HashMap<String, String> parameters = new HashMap<String, String>();
      parameters.put("key", id);
      parameters.put("fmcmd", format);
      if (format.equals(DOWNLOAD_SPREADSHEET_FORMATS.get("csv")) ||
          format.equals(DOWNLOAD_SPREADSHEET_FORMATS.get("tsv"))) {
        parameters.put("gid", "0");  // download only the first sheet
      }

      //String[] parameters = {"key=" + id, "fmcmd=" + format};
      url = buildUrl(SPREADSHEETS_HOST, URL_DOWNLOAD + "/" + docType + "s" +
                     URL_CATEGORY_EXPORT, parameters);

    } else {
      service = this.service;

      String[] parameters = {"docID=" + id, "exportFormat=" + format};
      url = buildUrl(URL_DOWNLOAD + "/" + docType + "s" +
                     URL_CATEGORY_EXPORT, parameters);
    }

    InputStream inStream = null;
    FileOutputStream outStream = null;

    try {
      Link link = new Link();
      link.setHref(url.toString());

      inStream = service.getStreamFromLink(link);
      outStream = new FileOutputStream(filepath);

      int c;
      while ((c = inStream.read()) != -1) {
        outStream.write(c);
      }
    } finally {
      if (inStream != null) {
        inStream.close();
      }

      if (outStream != null) {
        outStream.flush();
        outStream.close();
      }
    }
  }

  /**
   * Moves a object to a folder.
   *
   * @param objectId id of the object to be moved to the folder.
   * @param folderId id of the folder to move the object to.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public DocumentListEntry moveObjectToFolder(String objectId, String folderId) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    if (objectId == null || folderId == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_GROUP_FOLDERS + URL_PATH + "/" + folderId);
    DocumentListEntry doc = new DocumentListEntry();
    doc.setId(buildUrl(URL_GROUP_DOCUMENTS + URL_PATH + "/" + objectId).toString());

    return  service.insert(url, doc);
  }

  /**
   * Gets the access control list for a object.
   *
   * @param objectId id of the object to retrieve the ACL for.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public AclFeed getAclFeed(String objectId) throws IOException, MalformedURLException,
      ServiceException, DocumentListException {
    if (objectId == null) {
      throw new DocumentListException("null objectId");
    }

    URL url = buildUrl(URL_GROUP_ACL + URL_PATH + "/" + objectId);

    return service.getFeed(url, AclFeed.class);
  }

  /**
   * Add an ACL role to an object.
   *
   * @param role the role of the ACL to be added to the object.
   * @param scope the scope for the ACL.
   * @param objectId id of the object to set the ACL for.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public AclEntry addAclRole(AclRole role, AclScope scope, String objectId) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    if (role == null || scope == null || objectId == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_GROUP_ACL + URL_PATH + "/" + objectId);
    AclEntry entry = new AclEntry();
    entry.setRole(role);
    entry.setScope(scope);

    return service.insert(url, entry);
  }

  /**
   * Change the ACL role of a file.
   *
   * @param role the new role of the ACL to be updated.
   * @param scope the new scope for the ACL.
   * @param objectId id of the object to be updated.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public AclEntry changeAclRole(AclRole role, AclScope scope, String objectId)
      throws IOException, MalformedURLException, ServiceException, DocumentListException {
    if (role == null || scope == null || objectId == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_GROUP_ACL + URL_PATH + "/" + objectId);

    return service.update(url, scope, role);
  }

  /**
   * Remove an ACL role from a object.
   *
   * @param scope scope of the ACL to be removed.
   * @param email email address to remove the role of.
   * @param objectId id of the object to remove the role from.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  public void removeAclRole(String scope, String email, String objectId) throws IOException,
      MalformedURLException, ServiceException, DocumentListException {
    if (scope == null || email == null || objectId == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    URL url = buildUrl(URL_GROUP_ACL + URL_PATH + "/" + objectId + "/" + scope + "%3A" + email);

    service.delete(url);
  }

  /**
   * Returns the format code based on a file extension, and object id.
   *
   * @param objectId id of the object you want the format for.
   * @param ext extension of the file you want the format for.
   *
   * @throws DocumentListException
   */
  public static String getDownloadFormat(String objectId, String ext) throws DocumentListException {
    if (objectId == null || ext == null) {
      throw new DocumentListException("null passed in for required parameters");
    }

    if (objectId.indexOf("document") == 0) {
      if (DOWNLOAD_DOCUMENT_FORMATS.containsKey(ext)) {
        return DOWNLOAD_DOCUMENT_FORMATS.get(ext);
      }
    } else if (objectId.indexOf("presentation") == 0) {
      if (DOWNLOAD_PRESENTATION_FORMATS.containsKey(ext)) {
        return DOWNLOAD_PRESENTATION_FORMATS.get(ext);
      }
    } else if (objectId.indexOf("spreadsheet") == 0) {
      if (DOWNLOAD_SPREADSHEET_FORMATS.containsKey(ext)) {
        return DOWNLOAD_SPREADSHEET_FORMATS.get(ext);
      }
    }
    throw new DocumentListException("invalid document type");
  }

  /**
   * Gets the suffix of the objectId.  If the objectId is "document%3Adh3bw3j_0f7xmjhd8",
   *     "dh3bw3j_0f7xmjhd8" will be returned.
   *
   * @param objectId id to extract the suffix from.
   *
   * @throws DocumentListException
   */
  private String getObjectIdSuffix(String objectId) throws DocumentListException {
    if (objectId == null || objectId.indexOf("%3A") == 0) {
      throw new DocumentListException("null objectId");
    }

    return objectId.substring(objectId.lastIndexOf("%3A") + 3);
  }

  /**
   * Gets the prefix of the objectId.  If the objectId is "document%3Adh3bw3j_0f7xmjhd8",
   *     "document" will be returned.
   *
   * @param objectId id to extract the suffix from.
   *
   * @throws DocumentListException
   */
  private String getObjectIdPrefix(String objectId) throws DocumentListException {
    if (objectId == null || objectId.indexOf("%3A") == 0) {
      throw new DocumentListException("null objectId");
    }

    return objectId.substring(0, objectId.indexOf("%3A"));
  }

  /**
   * Gets the Etag for the given object id.
   *
   * @param objectId id of the object to get the etag for.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws DocumentListException
   */
  private String getObjectEtag(String objectId) throws IOException, MalformedURLException,
      ServiceException, DocumentListException {
    if (objectId == null) {
      throw new DocumentListException("null objectId");
    }

    DocumentListEntry doc = getDocsListEntry(objectId);

    return doc.getEtag();
  }

  /**
   *
   * @param path the path to add to the protocol/host
   *
   * @throws MalformedURLException
   * @throws DocumentListException
   */
  private URL buildUrl(String path) throws MalformedURLException, DocumentListException {
    if (path == null) {
      throw new DocumentListException("null path");
    }

    return buildUrl(path, null);
  }

  /**
   * Builds a URL with parameters.
   *
   * @param path the path to add to the protocol/host
   * @param parameters parameters to be added to the URL.
   *
   * @throws MalformedURLException
   * @throws DocumentListException
   */
  private URL buildUrl(String path, String[] parameters)
      throws MalformedURLException, DocumentListException {
    if (path == null) {
      throw new DocumentListException("null path");
    }

    return buildUrl(DEFAULT_HOST, path, parameters);
  }

  /**
   * Builds a URL with parameters.
   *
   * @param host the domain of the server
   * @param path the path to add to the protocol/host
   * @param parameters parameters to be added to the URL.
   *
   * @throws MalformedURLException
   * @throws DocumentListException
   */
  private URL buildUrl(String host, String path,  String[] parameters)
      throws MalformedURLException, DocumentListException {
    if (path == null) {
      throw new DocumentListException("null path");
    }

    StringBuffer url = new StringBuffer();
    url.append(protocol + "://" + host + URL_FEED + path);

    if (parameters != null && parameters.length > 0) {
      url.append("?");
      for (int i = 0; i < parameters.length; i++) {
        url.append(parameters[i]);
        if (i != (parameters.length - 1)) {
          url.append("&");
        }
      }
    }

    return new URL(url.toString());
  }

  /**
   * Builds a URL with parameters.
   *
   * @param host the domain of the server
   * @param path the path to add to the protocol/host
   * @param parameters parameters to be added to the URL as key value pairs.
   *
   * @throws MalformedURLException
   * @throws DocumentListException
   */
  private URL buildUrl(String host, String path, Map<String, String> parameters)
  throws MalformedURLException, DocumentListException {
    if (path == null) {
      throw new DocumentListException("null path");
    }

    StringBuffer url = new StringBuffer();
    url.append(protocol + "://" + host + URL_FEED + path);

    if (parameters != null && parameters.size() > 0) {
      Set<Map.Entry<String, String>> params = parameters.entrySet();
      Iterator<Map.Entry<String, String>> itr = params.iterator();

      url.append("?");
      while (itr.hasNext()) {
        Map.Entry<String, String> entry = itr.next();
        url.append(entry.getKey() + "=" + entry.getValue());
        if (itr.hasNext()) {
          url.append("&");
        }
      }
    }

    return new URL(url.toString());
  }
}
