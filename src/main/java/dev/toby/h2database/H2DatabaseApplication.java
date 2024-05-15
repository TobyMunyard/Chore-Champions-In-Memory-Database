package dev.toby.h2database;

import dev.toby.h2database.model.Chore;
import dev.toby.h2database.repository.ChoreRepository;

import java.io.IOException;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.Border;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class H2DatabaseApplication {

    // Frame to contain GUI components
    private static JFrame frame = new JFrame("Chore Champions!");
    // Main panel to hold buttons
    private static JPanel mainPanel = new JPanel(new BorderLayout());
    // Panel to hold user input into text fields
    private static JPanel inputPanel = new JPanel(new GridLayout(6, 2, 1, 1));
    // Label to display ID input
    private static JLabel idLabel = new JLabel("ID (Used to find other users):");
    // Text field to hold user ID
    private static JTextField idField = createTextFieldWithBorder("");
    // Label to display name input
    private static JLabel nameLabel = new JLabel("Name:");
    // Text field to hold user name
    private static JTextField nameField = createTextFieldWithBorder("");
    // Label to display flat name input
    private static JLabel flatLabel = new JLabel("Flat Name:");
    // Text field to hold user flat name
    private static JTextField flatField = createTextFieldWithBorder("");
    // Label to display chore input
    private static JLabel choreLabel = new JLabel("Most Recent Chore:");
    // Text field to hold user most recent chore
    private static JTextField choreField = createTextFieldWithBorder("");
    // Label to display number of chores input
    private static JLabel numChoresLabel = new JLabel("Number of Chores Completed:");
    // Text field to hold number of user chores completed
    private static JTextField numChoresField = createTextFieldWithBorder("");

    // The repository to store user information
    static ChoreRepository repository;

    /**
     * Constructor to initialize the repository.
     * 
     * @param repository The repository to initialize the ChoreRepository.
     */
    public H2DatabaseApplication(ChoreRepository repositoryToCreate) {
        repository = repositoryToCreate;
    }

    /**
     * Creates and displays the GUI for users to interact with the database.
     */
    public static void createAndShowGUI() {

        // Main menu buttons
        JButton findButton = new JButton("Find Someone");
        JButton createButton = new JButton("Create Account");

        // Add logic to buttons on the GUI
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindPanel();
            }
        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreatePanel();
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Try to get the image for the icon of the frame
        try {
            // <a
            // href="https://www.freepik.com/free-vector/broom-vector-illustration_1294513.htm#query=broom%20png&position=14&from_view=keyword&track=ais&uuid=516f5079-69ae-40f3-a021-6d0f6258bd39">Image
            // by katemangostar</a> on Freepik
            Image image = ImageIO
                    .read(H2DatabaseApplication.class.getClassLoader().getResource("dev/toby/h2database/icon.jpg"));
            frame.setIconImage(image);
        } catch (IOException e) {
            System.out.println("Could not get image");
        }

        // Panel to hold the menu elements
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(1, 2));
        // Add the buttons to the menu
        menuPanel.add(findButton);
        menuPanel.add(createButton);

        // Add the sub-panels to the main panel
        mainPanel.add(menuPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Set frame size, visibility, etc.
        frame.add(mainPanel);
        frame.pack();
        frame.setSize(400, 200);
        frame.setVisible(true);
    }

    /**
     * Create the panel to contain the content to find another user within the
     * database.
     */
    private static void showFindPanel() {
        // Clear the panel
        inputPanel.removeAll();
        // Add the label and text field for ID
        inputPanel.add(idLabel);
        inputPanel.add(idField);

        // Create and add logic to the button for searching for another user
        JButton findButton = new JButton("Find Someone");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer id = getId();
                // Is the id text field empty?
                if (!idField.getText().equals("")) {
                    // Try to connect to the database and get relevant user information
                    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:chores", "sa", "")) {
                        // SQL query to get user information
                        String sql = "SELECT Username, Flat, Chore, Chorescompleted FROM Chore WHERE Id = ?";
                        // Try to create a prepared statement to access the database
                        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                            preparedStatement.setInt(1, id);
                            // Try to set the text fields to the relevant content within the database
                            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                    nameField.setText(resultSet.getString("Username"));
                                    flatField.setText(resultSet.getString("Flat"));
                                    choreField.setText(resultSet.getString("Chore"));
                                    numChoresField.setText(resultSet.getString("Chorescompleted"));
                                } else {
                                    // Couldn't find the ID
                                    JOptionPane.showMessageDialog(frame, "No record found for the provided ID.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "ID number not valid.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Text field is empty, prompt user to enter a ID
                    JOptionPane.showMessageDialog(frame, "Please provide an ID to search for.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Create and add logic to add button to display panel add a new chore to a
        // existing user
        JButton newChoreButton = new JButton("Add Chore Completed");
        newChoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewChorePanel();
            }
        });
        // Add all the other labels and fields to the input panel
        inputPanel.add(findButton);
        inputPanel.add(newChoreButton);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(flatLabel);
        inputPanel.add(flatField);
        inputPanel.add(choreLabel);
        inputPanel.add(choreField);
        inputPanel.add(numChoresLabel);
        inputPanel.add(numChoresField);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    /**
     * Creates the panel to add a new chore to an existing user.
     */
    private static void showNewChorePanel() {
        // Clear the panel
        inputPanel.removeAll();
        // Create chore input
        JLabel newChoreLabel = new JLabel("Chore Completed:");
        JTextField newChoreField = createTextFieldWithBorder("");
        // Create and add logic to button to add a new chore to am existing user
        JButton addChoreButton = new JButton("Add Chore");
        addChoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chore to be entered
                String chore = newChoreField.getText();
                // ID of user to add it to
                Integer id = getId();
                // Does the chore actually exist?
                if (!chore.isEmpty()) {
                    // Try to connect to the database and get relevant user information
                    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:chores", "sa", "")) {
                        // Update chore and number of chores completed
                        String updateSql = "UPDATE Chore SET Chorescompleted = Chorescompleted + 1, Chore = ? WHERE Id = ?";
                        // Try to create a prepared statement to access the database
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                            updateStatement.setString(1, chore);
                            updateStatement.setInt(2, id);
                            updateStatement.executeUpdate();
                        }
                    } catch (SQLException ex) {
                        // Error doing the update statement
                        JOptionPane.showMessageDialog(frame, "Could not create new chore.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Chore field is empty
                    JOptionPane.showMessageDialog(frame, "Please provide an ID to search for.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Add the rest of the panel content
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(newChoreLabel);
        inputPanel.add(newChoreField);
        inputPanel.add(addChoreButton);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private static void showCreatePanel() {
        // Clear the panel
        inputPanel.removeAll();
        // Add all the panel content
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(flatLabel);
        inputPanel.add(flatField);
        inputPanel.add(choreLabel);
        inputPanel.add(choreField);
        inputPanel.add(numChoresLabel);
        inputPanel.add(numChoresField);
        // Create and add logic to button that will create a new user
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the input from text fields
                Integer id = getId();
                String name = getName();
                String flat = getFlat();
                String chore = getChore();
                String numChores = getNumChores();

                // Are any of these fields empty?
                if (!name.isEmpty() && !flat.isEmpty() && !chore.isEmpty() && !numChores.isEmpty()) {
                    // Try to connect to the database and get relevant user information
                    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:chores", "sa", "")) {
                        // SQL to create a new user in the database
                        String sql = "INSERT INTO Chore (Id, Username, Flat, Chore, Chorescompleted) VALUES (?, ?, ?, ?, ?)";
                        // Try to create a prepared statement to access the database
                        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                            preparedStatement.setInt(1, id);
                            preparedStatement.setString(2, name);
                            preparedStatement.setString(3, flat);
                            preparedStatement.setString(4, chore);
                            preparedStatement.setString(5, numChores);
                            preparedStatement.executeUpdate();
                            // Let the user know the entry was successful
                            JOptionPane.showMessageDialog(frame, "Data inserted successfully.", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Empty the text fields in case they want to make another entry
                            idField.setText("");
                            nameField.setText("");
                            flatField.setText("");
                            choreField.setText("");
                            numChoresField.setText("");
                        }
                    } catch (SQLException ex) {
                        // Something went wrong creating the user
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error inserting data into the database.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // One or more of the fields is empty, give a prompt to the user
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields to insert data.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        inputPanel.add(enterButton);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    /**
     * Gets the content entered by the user into the idField.
     * 
     * @return The ID number in the idField.
     */
    public static Integer getId() {
        return Integer.parseInt(idField.getText());
    }

    /**
     * Gets the content entered by the user into the nameField.
     * 
     * @return The name in the nameField.
     */
    public static String getName() {
        System.out.println("Getting name:" + nameField.getText());
        return nameField.getText();
    }

    /**
     * Gets the content entered by the user into the flatField.
     * 
     * @return The flat name in the flatField.
     */
    public static String getFlat() {
        return flatField.getText();
    }

    /**
     * Gets the content entered by the user into the idField.
     * 
     * @return The ID number in the idField.
     */
    public static String getChore() {
        return choreField.getText();
    }

    /**
     * Gets the number of choree entered by the user into the numChoresField.
     * 
     * @return The ID number in the idField.
     */
    public static String getNumChores() {
        return numChoresField.getText();
    }

    /**
     * Sets the ID number in the idField.
     * 
     * @param id The ID to put in the idField.
     */
    public static void setId(String id) {
        idField.setText(id);
    }

    /**
     * Sets the name in the nameField.
     * 
     * @param name The name to put in the nameField.
     */
    public static void setName(String name) {
        nameField.setText(name);
    }

    /**
     * Sets the flat name in the flatField.
     * 
     * @param flat The flat name to put in the flatField.
     */
    public static void setFlat(String flat) {
        flatField.setText(flat);
    }

    /**
     * Sets the most recent chore in the idField.
     * 
     * @param chore The most recent chore to put in the choreField.
     */
    public static void setChore(String chore) {
        choreField.setText(chore);
    }

    /**
     * Sets the number of chores completed in the numChoresField.
     * 
     * @param numChores The number of chores to put in the numChoresField.
     */
    public static void setNumChores(String numChores) {
        numChoresField.setText(numChores);
    }

    /**
     * Private method to reduce duplicated code when adding a border around text fields.
     * 
     * @param text The text to put in the text field.
     * @return A JTextField with a black border containing the input text.
     */
    private static JTextField createTextFieldWithBorder(String text) {
        JTextField textField = new JTextField(text);
        Border border = BorderFactory.createLineBorder(Color.BLACK); 
        textField.setBorder(border); 
        return textField;
    }

    /**
     * Main entry point for the application.
     * 
     * @param args Arguments to pass to the main method.
     */
    public static void main(String[] args) {
        SpringApplication.run(H2DatabaseApplication.class, args);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }

    /**
     * Command line runnner used to test the database and enter a dummy value.
     * 
     * @param repository The repository to run the command in.
     * @return The result of the command.
     */
    @Bean
    static CommandLineRunner commandLineRunner(ChoreRepository repository) {
        return args -> {
            repository.save(new Chore(1, "Steve", "Death Star", "Sweeping", 8));
        };
    }

}
