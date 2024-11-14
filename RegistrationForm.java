import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RegistrationForm extends JFrame {
    JTextField nameField, mobileField;
    JRadioButton maleButton, femaleButton;
    ButtonGroup genderGroup;
    JComboBox<String> dayBox, monthBox, yearBox;
    JTextArea addressField;
    JCheckBox termsCheckBox;
    JButton submitButton, resetButton;
    JTable dataTable;
    DefaultTableModel tableModel;
    Connection connection;

    public RegistrationForm() {
        setTitle("Registration Form");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

    
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(15);  
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        nameField.setPreferredSize(new Dimension(200, 25));  
        formPanel.add(nameField, gbc);

    
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Mobile:"), gbc);
        mobileField = new JTextField(15);  
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mobileField.setPreferredSize(new Dimension(200, 25));  
        formPanel.add(mobileField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Gender:"), gbc);
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(genderPanel, gbc);

    
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("DOB:"), gbc);
        String[] days = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                         "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] years = {"1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005"};
        dayBox = new JComboBox<>(days);
        monthBox = new JComboBox<>(months);
        yearBox = new JComboBox<>(years);
        JPanel dobPanel = new JPanel();
        dobPanel.add(dayBox);
        dobPanel.add(monthBox);
        dobPanel.add(yearBox);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(dobPanel, gbc);

     
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextArea(3, 15);  
        addressField.setPreferredSize(new Dimension(200, 60));  
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JScrollPane(addressField), gbc);

        termsCheckBox = new JCheckBox("Accept Terms and Conditions");
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(termsCheckBox, gbc);

        submitButton = new JButton("Submit");
        resetButton = new JButton("Reset");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        String[] columns = {"ID", "Name", "Gender", "DOB", "Address", "Mobile"};
        tableModel = new DefaultTableModel(columns, 0);
        dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        add(tableScrollPane, BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);

        connectDatabase();
        submitButton.addActionListener(e -> saveData());
        resetButton.addActionListener(e -> resetForm());

        setVisible(true);
    }

    private void connectDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:data.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT, gender TEXT, dob TEXT, address TEXT, mobile TEXT)");
            JOptionPane.showMessageDialog(this, "Connected to Database!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!");
            e.printStackTrace();
        }
    }

    private void saveData() {
        String name = nameField.getText();
        String mobile = mobileField.getText();
        String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : "");
        String dob = dayBox.getSelectedItem() + " " + monthBox.getSelectedItem() + " " + yearBox.getSelectedItem();
        String address = addressField.getText();

        if (name.isEmpty() || mobile.isEmpty() || gender.isEmpty() || address.isEmpty() || !termsCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and accept terms.");
            return;
        }

        try {
            String query = "INSERT INTO users (name, gender, dob, address, mobile) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, gender);
            preparedStatement.setString(3, dob);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, mobile);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Saved Successfully!");
            loadTableData();
            resetForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Saving Data!");
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0); 
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("gender"),
                        resultSet.getString("dob"),
                        resultSet.getString("address"),
                        resultSet.getString("mobile")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetForm() {
        nameField.setText("");
        mobileField.setText("");
        genderGroup.clearSelection();
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        addressField.setText("");
        termsCheckBox.setSelected(false);
    }

    public static void main(String[] args) {
        new RegistrationForm();
    }
}
