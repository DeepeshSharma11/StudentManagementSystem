import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class StudentManagementGUI extends JFrame {
    private InMemoryDatabase database;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, emailField, ageField, courseField, searchField;
    private JButton addButton, updateButton, deleteButton, clearButton, refreshButton, searchButton;
    private JComboBox<String> filterComboBox;
    private JLabel statusLabel;
    
    public StudentManagementGUI() {
        database = InMemoryDatabase.getInstance();
        initializeGUI();
        loadStudentData();
    }
    
    private void initializeGUI() {
        setTitle("Student Management System - In-Memory Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("STUDENT MANAGEMENT SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search and Filter"));
        
        searchField = new JTextField(20);
        searchButton = new JButton("Search by Name");
        
        String[] filters = {"All Courses", "Computer Science", "Electrical Engineering", 
                           "Mechanical Engineering", "Civil Engineering", "Business Administration"};
        filterComboBox = new JComboBox<>(filters);
        
        JButton statsButton = new JButton("Show Statistics");
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Filter by Course:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(statsButton);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        searchButton.addActionListener(e -> searchStudents());
        filterComboBox.addActionListener(e -> filterByCourse());
        statsButton.addActionListener(e -> showStatistics());
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        
        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        ageField = new JTextField();
        courseField = new JTextField();
        
        idField.setEditable(false);
        idField.setBackground(Color.LIGHT_GRAY);
        
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:*"));
        panel.add(nameField);
        panel.add(new JLabel("Email:*"));
        panel.add(emailField);
        panel.add(new JLabel("Age:*"));
        panel.add(ageField);
        panel.add(new JLabel("Course:*"));
        panel.add(courseField);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Students List"));
        
        String[] columns = {"ID", "Name", "Email", "Age", "Course"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 || columnIndex == 3 ? Integer.class : String.class;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedStudent();
            }
        });
        
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        addButton = createStyledButton("Add Student", new Color(0, 150, 0));
        updateButton = createStyledButton("Update Student", new Color(0, 100, 200));
        deleteButton = createStyledButton("Delete Student", new Color(200, 0, 0));
        clearButton = createStyledButton("Clear Form", new Color(150, 150, 0));
        refreshButton = createStyledButton("Refresh List", new Color(100, 100, 100));
        
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> loadStudentData());
        
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }
    
    private void addStudent() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String ageText = ageField.getText().trim();
            String course = courseField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty() || ageText.isEmpty() || course.isEmpty()) {
                showError("Please fill all required fields!");
                return;
            }
            
            int age = Integer.parseInt(ageText);
            if (age <= 0 || age > 150) {
                showError("Please enter a valid age (1-150)!");
                return;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                showError("Please enter a valid email address!");
                return;
            }
            
            Student student = new Student(0, name, email, age, course);
            database.addStudent(student);
            
            showSuccess("Student added successfully!");
            clearForm();
            loadStudentData();
            updateStatus("Student added: " + name);
            
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for age!");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    
    private void updateStudent() {
        try {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a student to update!");
                return;
            }
            
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String ageText = ageField.getText().trim();
            String course = courseField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty() || ageText.isEmpty() || course.isEmpty()) {
                showError("Please fill all required fields!");
                return;
            }
            
            int age = Integer.parseInt(ageText);
            if (age <= 0 || age > 150) {
                showError("Please enter a valid age (1-150)!");
                return;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                showError("Please enter a valid email address!");
                return;
            }
            
            Student student = new Student(id, name, email, age, course);
            database.updateStudent(student);
            
            showSuccess("Student updated successfully!");
            clearForm();
            loadStudentData();
            updateStatus("Student updated: " + name);
            
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for age!");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    
    private void deleteStudent() {
        try {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a student to delete!");
                return;
            }
            
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete student:\n" + name + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                database.deleteStudent(id);
                showSuccess("Student deleted successfully!");
                clearForm();
                loadStudentData();
                updateStatus("Student deleted: " + name);
            }
            
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    
    private void searchStudents() {
        try {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                loadStudentData();
                updateStatus("Showing all students");
                return;
            }
            
            List<Student> students = database.searchStudentsByName(searchTerm);
            displayStudents(students);
            updateStatus("Found " + students.size() + " students matching: " + searchTerm);
            
        } catch (Exception e) {
            showError("Error during search: " + e.getMessage());
        }
    }
    
    private void filterByCourse() {
        try {
            String selectedCourse = (String) filterComboBox.getSelectedItem();
            if ("All Courses".equals(selectedCourse)) {
                loadStudentData();
                updateStatus("Showing all students");
                return;
            }
            
            List<Student> students = database.getStudentsByCourse(selectedCourse);
            displayStudents(students);
            updateStatus("Showing " + students.size() + " students in: " + selectedCourse);
            
        } catch (Exception e) {
            showError("Error during filtering: " + e.getMessage());
        }
    }
    
    private void showStatistics() {
        try {
            Map<String, Object> stats = database.getStatistics();
            StringBuilder statsText = new StringBuilder();
            statsText.append("=== STUDENT STATISTICS ===\n\n");
            statsText.append("Total Students: ").append(stats.get("totalStudents")).append("\n\n");
            
            statsText.append("Average Age: ").append(String.format("%.1f", stats.get("averageAge"))).append("\n\n");
            
            @SuppressWarnings("unchecked")
            Map<String, Integer> courseDist = (Map<String, Integer>) stats.get("courseDistribution");
            statsText.append("Course Distribution:\n");
            for (Map.Entry<String, Integer> entry : courseDist.entrySet()) {
                statsText.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" students\n");
            }
            
            JOptionPane.showMessageDialog(this, statsText.toString(), 
                "Statistics", JOptionPane.INFORMATION_MESSAGE);
            updateStatus("Statistics displayed");
            
        } catch (Exception e) {
            showError("Error generating statistics: " + e.getMessage());
        }
    }
    
    private void displaySelectedStudent() {
        try {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);
                String email = (String) tableModel.getValueAt(selectedRow, 2);
                int age = (int) tableModel.getValueAt(selectedRow, 3);
                String course = (String) tableModel.getValueAt(selectedRow, 4);
                
                idField.setText(String.valueOf(id));
                nameField.setText(name);
                emailField.setText(email);
                ageField.setText(String.valueOf(age));
                courseField.setText(course);
                
                updateStatus("Selected student: " + name);
            }
        } catch (Exception e) {
            System.err.println("Error displaying student: " + e.getMessage());
        }
    }
    
    private void loadStudentData() {
        try {
            List<Student> students = database.getAllStudents();
            displayStudents(students);
            updateStatus("Loaded " + students.size() + " students");
            
        } catch (Exception e) {
            showError("Error loading student data: " + e.getMessage());
        }
    }
    
    private void displayStudents(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getAge(),
                student.getCourse()
            });
        }
    }
    
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        ageField.setText("");
        courseField.setText("");
        studentTable.clearSelection();
        updateStatus("Form cleared");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        updateStatus("Error: " + message);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText("Status: " + message);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StudentManagementGUI();
            }
        });
    }
}