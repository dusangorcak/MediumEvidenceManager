package mediamanager.web;

import java.io.IOException;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import manager1.*;

/**
 *
 * @author Tomas
 */
@WebServlet(name = "ActionServlet",
urlPatterns = {ActionServlet.ACTION_LIST_STORAGES, ActionServlet.ACTION_ADD_STORAGE})
public class ActionServlet extends HttpServlet {

    static final String ACTION_ADD_STORAGE = "/Index";
    static final String ACTION_LIST_STORAGES = "/List";
    static final String ATTRIBUTE_STORAGES = "storages";
    static final String ATTRIBUTE_STORAGE_FORM = "storageForm";
    static final String ATTRIBUTE_ERROR = "error";
    static final String JSP_ADD_STORAGE = "/WEB-INF/jsp/index.jsp";
    static final String JSP_LIST_STORAGES = "/WEB-INF/jsp/list.jsp";
    private StorageManagerImpl storageManager = new StorageManagerImpl();
    private EvidenceManagerImpl manager = new EvidenceManagerImpl();

    @Resource(name = "jdbc/EvidenceManager")
    private void setDataSource(DataSource ds) throws SQLException {
        Utils.tryCreateTables(ds, EvidenceManager.class.getResource("CreateTables.sql"));
        manager.setDataSource(ds);
        storageManager.setDataSource(ds);
    }

    private void listStorages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ATTRIBUTE_STORAGES, manager.getAllStorages());
        request.getRequestDispatcher(JSP_LIST_STORAGES).forward(request, response);
    }

    private void addStorage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("POST")) {

            if (request.getParameter("cancel") != null) {
                response.sendRedirect(request.getContextPath());
                return;
            }

            StorageForm storageForm = StorageForm.extractFromRequest(request);
            StringBuilder errors = new StringBuilder();
            Storage storage = storageForm.validateAndToStorage(errors);

            if (storage == null) {
                request.setAttribute(ATTRIBUTE_ERROR, errors.toString());
                request.setAttribute(ATTRIBUTE_STORAGE_FORM, storageForm);
                request.getRequestDispatcher(JSP_ADD_STORAGE).forward(request, response);
            } else {
                storageManager.createStorage(storage);
                response.sendRedirect(request.getContextPath());
            }

        } else {
            request.setAttribute(ATTRIBUTE_STORAGE_FORM, new StorageForm());
            request.getRequestDispatcher(JSP_ADD_STORAGE).forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        if (request.getServletPath().equals(ACTION_LIST_STORAGES)) {
            listStorages(request, response);
        } else if (request.getServletPath().equals(ACTION_ADD_STORAGE)) {
            addStorage(request, response);
        } else {
            throw new RuntimeException("Unknown operation: " + request.getServletPath());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
