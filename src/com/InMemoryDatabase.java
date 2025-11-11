import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryDatabase {
    private static InMemoryDatabase instance;
    private Map<Integer, Student> students;
    private AtomicInteger idCounter;
    
    // Private constructor for Singleton pattern
    private InMemoryDatabase() {
        students = new HashMap<>();
        idCounter = new AtomicInteger(1);
        initializeSampleData();
    }
    
    // Singleton instance
    public static synchronized InMemoryDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }
    
    // Initialize with sample data
    private void initializeSampleData() {
        try {
            addStudent(new Student(0, "Aarav Sharma", "aarav.sharma@email.com", 20, "Computer Science"));
            addStudent(new Student(0, "Priya Patel", "priya.patel@email.com", 21, "Electrical Engineering"));
            addStudent(new Student(0, "Rohan Singh", "rohan.singh@email.com", 22, "Mechanical Engineering"));
            addStudent(new Student(0, "Neha Gupta", "neha.gupta@email.com", 19, "Business Administration"));
            addStudent(new Student(0, "Vikram Joshi", "vikram.joshi@email.com", 23, "Civil Engineering"));
        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }
    
    // Add student
    public boolean addStudent(Student student) {
        try {
            if (student == null) {
                throw new IllegalArgumentException("Student cannot be null");
            }
            
            // Validate required fields
            if (student.getName() == null || student.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Student name is required");
            }
            
            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Student email is required");
            }
            
            // Check for duplicate email
            for (Student existingStudent : students.values()) {
                if (existingStudent.getEmail().equalsIgnoreCase(student.getEmail())) {
                    throw new IllegalArgumentException("Student with this email already exists");
                }
            }
            
            // Set auto-generated ID
            student.setId(idCounter.getAndIncrement());
            students.put(student.getId(), student);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error adding student: " + e.getMessage());
            throw e; // Re-throw to handle in GUI
        }
    }
    
    // Get all students
    public List<Student> getAllStudents() {
        try {
            return new ArrayList<>(students.values());
        } catch (Exception e) {
            System.err.println("Error retrieving students: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Get student by ID
    public Student getStudentById(int id) {
        try {
            return students.get(id);
        } catch (Exception e) {
            System.err.println("Error getting student by ID: " + e.getMessage());
            return null;
        }
    }
    
    // Update student
    public boolean updateStudent(Student student) {
        try {
            if (student == null) {
                throw new IllegalArgumentException("Student cannot be null");
            }
            
            if (!students.containsKey(student.getId())) {
                throw new IllegalArgumentException("Student not found with ID: " + student.getId());
            }
            
            // Validate required fields
            if (student.getName() == null || student.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Student name is required");
            }
            
            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Student email is required");
            }
            
            // Check for duplicate email (excluding current student)
            for (Student existingStudent : students.values()) {
                if (existingStudent.getId() != student.getId() && 
                    existingStudent.getEmail().equalsIgnoreCase(student.getEmail())) {
                    throw new IllegalArgumentException("Another student with this email already exists");
                }
            }
            
            students.put(student.getId(), student);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error updating student: " + e.getMessage());
            throw e; // Re-throw to handle in GUI
        }
    }
    
    // Delete student
    public boolean deleteStudent(int id) {
        try {
            if (!students.containsKey(id)) {
                throw new IllegalArgumentException("Student not found with ID: " + id);
            }
            
            students.remove(id);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error deleting student: " + e.getMessage());
            throw e; // Re-throw to handle in GUI
        }
    }
    
    // Search students by name
    public List<Student> searchStudentsByName(String name) {
        try {
            List<Student> result = new ArrayList<>();
            String searchTerm = name.toLowerCase();
            
            for (Student student : students.values()) {
                if (student.getName().toLowerCase().contains(searchTerm)) {
                    result.add(student);
                }
            }
            return result;
            
        } catch (Exception e) {
            System.err.println("Error searching students: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Get students by course
    public List<Student> getStudentsByCourse(String course) {
        try {
            List<Student> result = new ArrayList<>();
            String searchCourse = course.toLowerCase();
            
            for (Student student : students.values()) {
                if (student.getCourse().toLowerCase().contains(searchCourse)) {
                    result.add(student);
                }
            }
            return result;
            
        } catch (Exception e) {
            System.err.println("Error filtering students by course: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Get statistics
    public Map<String, Object> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            List<Student> allStudents = getAllStudents();
            
            stats.put("totalStudents", allStudents.size());
            
            // Count by course
            Map<String, Integer> courseCount = new HashMap<>();
            for (Student student : allStudents) {
                courseCount.merge(student.getCourse(), 1, Integer::sum);
            }
            stats.put("courseDistribution", courseCount);
            
            // Average age
            double averageAge = allStudents.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
            stats.put("averageAge", averageAge);
            
            return stats;
            
        } catch (Exception e) {
            System.err.println("Error calculating statistics: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    // Clear all data (for testing)
    public void clearAllData() {
        try {
            students.clear();
            idCounter.set(1);
        } catch (Exception e) {
            System.err.println("Error clearing data: " + e.getMessage());
        }
    }
    
    // Get database size
    public int getSize() {
        return students.size();
    }
}