import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

public class View extends JFrame{
    private JButton webCrawlerButton;
    private JTextField TextField2;
    private JButton createButton;
    private JTextField TextField3;
    private JButton readButton;
    private JTextField TextField4;
    private JButton updateButton;
    private JTextField TextField5;
    private JButton deleteButton;
    private JTextField TextField1;
    private JPanel panelMane;
    private JButton bulkuploadButton;
    private JTextField TextField6;
    private JComboBox<String> comboBox1;
    private JButton scriptSearchButton;
    private JTextField TextField10;
    private static final String PLACEHOLDER_TEXT = "Enter filepath for the JSON file with the bulk data";
    private static final String PLACEHOLDER_TEXT2 = "Enter filepath for the JSON file with the post data";
    private static final String PLACEHOLDER_TEXT3 = "Enter document ID";
    private static final String PLACEHOLDER_TEXT4 = "Enter document ID";
    private static final String PLACEHOLDER_TEXT5 = "Enter filepath for the JSON file with the post data";
    private static final String PLACEHOLDER_TEXT6 = "Enter document ID";

    public View() {
        setContentPane(panelMane);
        setTitle("Information Retrieval System");
        setSize(800,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Add placeholder text and focus listener
        TextField6.setText(PLACEHOLDER_TEXT);
        TextField6.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (TextField6.getText().equals(PLACEHOLDER_TEXT)) {
                    TextField6.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (TextField6.getText().isEmpty()) {
                    TextField6.setText(PLACEHOLDER_TEXT);
                }
            }
        });

        // Add placeholder text and focus listener
        TextField10.setText(PLACEHOLDER_TEXT6);
        TextField10.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (TextField10.getText().equals(PLACEHOLDER_TEXT6)) {
                    TextField10.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (TextField10.getText().isEmpty()) {
                    TextField10.setText(PLACEHOLDER_TEXT6);
                }
            }
        });

        // Add placeholder text and focus listener
        TextField4.setText(PLACEHOLDER_TEXT5);
        TextField4.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (TextField4.getText().equals(PLACEHOLDER_TEXT5)) {
                    TextField4.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (TextField4.getText().isEmpty()) {
                    TextField4.setText(PLACEHOLDER_TEXT5);
                }
            }
        });

        // Add placeholder text and focus listener
        TextField2.setText(PLACEHOLDER_TEXT2);
        TextField2.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (TextField2.getText().equals(PLACEHOLDER_TEXT2)) {
                    TextField2.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (TextField2.getText().isEmpty()) {
                    TextField2.setText(PLACEHOLDER_TEXT2);
                }
            }
        });

        // Add placeholder text and focus listener
        TextField3.setText(PLACEHOLDER_TEXT3);
        TextField3.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (TextField3.getText().equals(PLACEHOLDER_TEXT3)) {
                    TextField3.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (TextField3.getText().isEmpty()) {
                    TextField3.setText(PLACEHOLDER_TEXT3);
                }
            }
        });

        // Add placeholder text and focus listener
        TextField5.setText(PLACEHOLDER_TEXT4);
        TextField5.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (TextField5.getText().equals(PLACEHOLDER_TEXT4)) {
                    TextField5.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (TextField5.getText().isEmpty()) {
                    TextField5.setText(PLACEHOLDER_TEXT4);
                }
            }
        });

        webCrawlerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Web Crawler Button Clicked"); // Debugging purposes
                // Call the WebCrawler method
                WebCrawler.WebCrawler();
            }
        });

        comboBox1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String query = TextField1.getText();
                String searchType = (String) comboBox1.getSelectedItem();
                try {
                    performSearch(searchType, query);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get text from textField1 and print it to the terminal
                //System.out.println(TextField2.getText()); // Debugging purposes
                String filepath = TextField2.getText();
                // Index the document to Elasticsearch
                try {
                    //CRUD.indexDocument(CRUD.createClient(), filepath);
                    CRUD.altindexDocument(CRUD.createClient(), filepath);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get text from textField1 and print it to the terminal
                //System.out.println("-"+TextField3.getText()+"-"); // Debugging purposes
                String docID = TextField3.getText();
                // Get the document from Elasticsearch
                try {
                    //CRUD.getDocument(CRUD.createClient(), docID); // This way uses the API to perform the read operation
                    CRUD.altgetDocument(CRUD.createClient(), docID);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get text from textField1 and print it to the terminal
                System.out.println(TextField4.getText() + " and " + TextField10.getText()); // Debugging purposes
                /** update call here */
                String docID = TextField10.getText();
                String filepath = TextField4.getText();
                try {
                    CRUD.altupdateDocument(CRUD.createClient(), docID, filepath);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // System.out.println(TextField5.getText()); // Debugging purposes
                String docID = TextField5.getText();
                // Delete the document from Elasticsearch
                try {
                    CRUD.deleteDocument(CRUD.createClient(), docID);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        bulkuploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bulk button pressed\n"); // Debugging purposes
                // Call the BulkUpload method
                String directoryPath = TextField6.getText();
                //System.out.println(directoryPath); // Debugging purposes
//                try {
//                    BulkUpload.BulkUpload(CRUD.createClient(), directoryPath);
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
                BulkUpload.alternativeBulkUpload();
            }
        });

        setVisible(true);
        scriptSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get text from textField1 and print it to the terminal
                //System.out.println(TextField1.getText()); // Debugging purposes
                String query = TextField1.getText();
                // Search the document from Elasticsearch
                try {
                    Search.altSearchContent(CRUD.createClient(), query);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void performSearch(String searchType, String query) throws Exception {
        switch (searchType) {
            case "Content":
                System.out.println("Searching Content: " + query);
                // Search.altSearchContent(CRUD.createClient(), query);
                Search.altSearchContent(CRUD.createClient(), query);
                break;
            case "Tags":
                System.out.println("Searching Tags: " + query);
                // Implement search by tags
                Search.altSearchTags(CRUD.createClient(), query);
                break;
            case "Title":
                System.out.println("Searching Title: " + query);
                // Implement search by title
                Search.altSearchTitle(CRUD.createClient(), query);
                break;
            default:
                System.out.println("Please select a valid search type.");
                break;
        }
    }

    public static void main(String[] args) {
        View v = new View();
    }
}
