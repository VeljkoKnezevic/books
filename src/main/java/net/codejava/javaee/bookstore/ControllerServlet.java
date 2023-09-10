package net.codejava.javaee.bookstore;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ControllerServlet extends HttpServlet {
    private BookDAO bookDAO;

    public void init() {
        String jdbcURL = getServletContext().getInitParameter("jdbcURL");
        String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
        String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");

        bookDAO = new BookDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getServletPath();

        try {
            switch (action) {
                case "/new":
                    showNewForm(req,resp);
                    break;
                case "/insert":
                    insertBook(req,resp);
                    break;
                case "/delete":
                    deleteBook(req,resp);
                    break;
                case "/edit":
                    showEditForm(req,resp);
                    break;
                case "/update":
                    updateBook(req,resp);
                    break;
                default:
                    listBook(req,resp);
                    break;
            }
        } catch(SQLException ex) {
            throw new ServletException(ex);
        }
    }


    private void listBook(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException, ServletException  {
        List<Book> bookList = bookDAO.listAllBooks();

        req.setAttribute("bookList", bookList);
        RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
        dispatcher.forward(req,resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("BookForm.jsp");
        dispatcher.forward(req,resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(req.getParameter("id"));
        Book existingBook = bookDAO.getBook(id);
        RequestDispatcher dispatcher = req.getRequestDispatcher("BookForm.jsp");
        req.setAttribute("book", existingBook);
        dispatcher.forward(req,resp);
    }

    private void insertBook(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        float price = Float.parseFloat(req.getParameter("price"));

        Book newBook = new Book(title,author,price);
        bookDAO.insertBook(newBook);
        resp.sendRedirect("list");
    }

    private void updateBook(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        float price = Float.parseFloat(req.getParameter("price"));

        Book book = new Book(id,title,author,price);

        bookDAO.updateBook(book);
        resp.sendRedirect("list");
    }

    private void deleteBook(HttpServletRequest req, HttpServletResponse resp)
        throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));

        Book book = new Book(id);
        bookDAO.deleteBook(book);
        resp.sendRedirect("list");
    }
}
