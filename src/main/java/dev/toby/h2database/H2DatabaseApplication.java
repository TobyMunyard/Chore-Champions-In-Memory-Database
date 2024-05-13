package dev.toby.h2database;

import dev.toby.h2database.model.Chore;
import dev.toby.h2database.repository.ChoreRepository;

import java.io.IOException;

import javax.imageio.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.Border;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class H2DatabaseApplication {

    private static JFrame frame = new JFrame("Chore Champions!");
    private static JPanel mainPanel = new JPanel(new BorderLayout());
    private static JPanel inputPanel = new JPanel(new GridLayout(6, 2, 1, 1));

    private static JLabel idLabel = new JLabel("ID (Used to find other users):");
    private static JTextField idField = createTextFieldWithBorder("");
    private static JLabel nameLabel = new JLabel("Name:");
    private static JTextField nameField = createTextFieldWithBorder("");
    private static JLabel flatLabel = new JLabel("Flat Name:");
    private static JTextField flatField = createTextFieldWithBorder("");
    private static JLabel choreLabel = new JLabel("Most Recent Chore:");
    private static JTextField choreField = createTextFieldWithBorder("");
    private static JLabel numChoresLabel = new JLabel("Number of Chores Completed:");
    private static JTextField numChoresField = createTextFieldWithBorder("");

    static ChoreRepository repository;

    // Constructor for initializing the repository
    public H2DatabaseApplication(ChoreRepository repository) {
        this.repository = repository;
    }

    public static void createAndShowGUI() {

        // Main menu buttons
        JButton findButton = new JButton("Find Someone");
        JButton createButton = new JButton("Create Account");

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

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(1, 2));
        menuPanel.add(findButton);
        menuPanel.add(createButton);

        mainPanel.add(menuPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.pack();
        frame.setSize(400, 200);
        frame.setVisible(true);
    }

    private static void showFindPanel() {
        inputPanel.removeAll();
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        JButton findButton = new JButton("Find Someone");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer id = getId();
                if (!idField.getText().equals("")) {
                    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:chores", "sa", "")) {
                        String sql = "SELECT Username, Flat, Chore, Chorescompleted FROM Chore WHERE Id = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                            preparedStatement.setInt(1, id);
                            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                    nameField.setText(resultSet.getString("Username"));
                                    flatField.setText(resultSet.getString("Flat"));
                                    choreField.setText(resultSet.getString("Chore"));
                                    numChoresField.setText(resultSet.getString("Chorescompleted"));
                                } else {
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
                    JOptionPane.showMessageDialog(frame, "Please provide an ID to search for.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton newChoreButton = new JButton("Add Chore Completed");
        newChoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewChorePanel();
            }
        });
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

    private static void showNewChorePanel() {
        inputPanel.removeAll();
        JLabel newChoreLabel = new JLabel("Chore Completed:");
        JTextField newChoreField = createTextFieldWithBorder("");
        JButton addChoreButton = new JButton("Add Chore");
        addChoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chore = newChoreField.getText();
                Integer id = getId();
                if (!chore.isEmpty()) {
                    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:chores", "sa", "")) {
                        // Update Chorescompleted and set the chore to a question mark
                        String updateSql = "UPDATE Chore SET Chorescompleted = Chorescompleted + 1, Chore = ? WHERE Id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                            updateStatement.setString(1, chore);
                            updateStatement.setInt(2, id);
                            updateStatement.executeUpdate();
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Could not create new chore.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please provide an ID to search for.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(newChoreLabel);
        inputPanel.add(newChoreField);
        inputPanel.add(addChoreButton);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private static void showCreatePanel() {
        inputPanel.removeAll();
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
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer id = getId();
                String name = getName();
                String flat = getFlat();
                String chore = getChore();
                String numChores = getNumChores();

                if (!name.isEmpty() && !flat.isEmpty() && !chore.isEmpty() && !numChores.isEmpty()) {
                    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:chores", "sa", "")) {
                        String sql = "INSERT INTO Chore (Id, Username, Flat, Chore, Chorescompleted) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                            preparedStatement.setInt(1, id);
                            preparedStatement.setString(2, name);
                            preparedStatement.setString(3, flat);
                            preparedStatement.setString(4, chore);
                            preparedStatement.setString(5, numChores);
                            preparedStatement.executeUpdate();
                            JOptionPane.showMessageDialog(frame, "Data inserted successfully.", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            idField.setText("");
                            nameField.setText("");
                            flatField.setText("");
                            choreField.setText("");
                            numChoresField.setText("");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error inserting data into the database.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields to insert data.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        inputPanel.add(enterButton);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    public static Integer getId() {
        return Integer.parseInt(idField.getText());
    }

    public static void setId(String id) {
        idField.setText(id);
    }

    public static String getName() {
        System.out.println("Getting name:" + nameField.getText());
        return nameField.getText();
    }

    public static String getFlat() {
        return flatField.getText();
    }

    public static String getChore() {
        return choreField.getText();
    }

    public static String getNumChores() {
        return numChoresField.getText();
    }

    public static void setName(String name) {
        nameField.setText(name);
    }

    public static void setFlat(String flat) {
        flatField.setText(flat);
    }

    public static void setChore(String chore) {
        choreField.setText(chore);
    }

    public static void setNumChores(String numChores) {
        numChoresField.setText(numChores);
    }

    private static JTextField createTextFieldWithBorder(String text) {
        JTextField textField = new JTextField(text);
        Border border = BorderFactory.createLineBorder(Color.BLACK); // Create a black line border
        textField.setBorder(border); // Set the border to the text field
        return textField;
    }

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

    @Bean
    static CommandLineRunner commandLineRunner(ChoreRepository repository) {
        return args -> {
            System.out.println(getName());
            repository.save(new Chore(1, "Steve", "Death Star", "Sweeping", 8));
        };
    }

}
