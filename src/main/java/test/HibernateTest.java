package test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.auca.librarymanagement.model.*;
import util.HibernateUtil;
import java.util.*;
import java.text.SimpleDateFormat;

public class HibernateTest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            // Test Location Hierarchy
            testLocationHierarchy(session);

            // Test User Creation and Roles
            testUserCreation(session);

            // Test Membership System
            testMembershipSystem(session);

            // Test Library Operations
            testLibraryOperations(session);

            transaction.commit();
            System.out.println("All tests completed successfully");

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession(session);
            HibernateUtil.shutdown();
        }
    }

    private static void testLocationHierarchy(Session session) throws Exception {
        System.out.println("\nTesting Location Hierarchy...");

        // Create Province
        Location province = new Location("PRV001", "Kigali City", LocationType.PROVINCE, null);
        session.persist(province);

        // Create District
        Location district = new Location("DST001", "Gasabo", LocationType.DISTRICT, province);
        session.persist(district);

        // Create Sector
        Location sector = new Location("SEC001", "Remera", LocationType.SECTOR, district);
        session.persist(sector);

        // Create Cell
        Location cell = new Location("CEL001", "Rukiri", LocationType.CELL, sector);
        session.persist(cell);

        // Create Village
        Location village = new Location("VIL001", "Rukiri I", LocationType.VILLAGE, cell);
        session.persist(village);

        System.out.println("Location hierarchy created successfully");
    }

    private static void testUserCreation(Session session) throws Exception {
        System.out.println("\nTesting User Creation...");

        Location village = session.createQuery("FROM Location WHERE locationType = :type", Location.class)
            .setParameter("type", LocationType.VILLAGE)
            .getSingleResult();

        // Create Librarian
        User librarian = createUser(
            UUID.randomUUID(),
            "John",
            "Doe",
            Gender.MALE,
            "0788123456",
            "librarian1",
            "Password123!",
            RoleType.LIBRARIAN,
            village
        );
        session.persist(librarian);

        // Create Student
        User student = createUser(
            UUID.randomUUID(),
            "Alice",
            "Smith",
            Gender.FEMALE,
            "0788987654",
            "student1",
            "Password123!",
            RoleType.STUDENT,
            village
        );
        session.persist(student);

        System.out.println("Users created successfully");
    }

    private static void testMembershipSystem(Session session) throws Exception {
        System.out.println("\nTesting Membership System...");

        // Create Membership Types
        MembershipType gold = new MembershipType("GOLD", 5, 50);
        MembershipType silver = new MembershipType("SILVER", 3, 30);
        MembershipType striver = new MembershipType("STRIVER", 2, 10);

        session.persist(gold);
        session.persist(silver);
        session.persist(striver);

        // Create Membership for a Student
        User student = session.createQuery("FROM User WHERE role = :role", User.class)
            .setParameter("role", RoleType.STUDENT)
            .setMaxResults(1)
            .getSingleResult();

        Calendar cal = Calendar.getInstance();
        Date registrationDate = cal.getTime();
        cal.add(Calendar.MONTH, 6);
        Date expiringTime = cal.getTime();

        Membership membership = new Membership(
            "MEM000001",
            silver,
            student,
            registrationDate,
            expiringTime
        );
        session.persist(membership);

        System.out.println("Membership system tested successfully");
    }

    private static void testLibraryOperations(Session session) throws Exception {
        System.out.println("\nTesting Library Operations...");

        // Create Room
        Room room = new Room("R001");
        session.persist(room);

        // Create Shelf
        Shelf shelf = new Shelf("Computer Science", room, 100);
        session.persist(shelf);

        // Create Book
        Book book = new Book(
            "Java Programming",
            1,
            "ISBN-13: 978-0-13-447644-4",
            DATE_FORMAT.parse("2023-01-01"),
            "Publisher Name",
            shelf
        );
        session.persist(book);

        // Test Borrowing
        User student = session.createQuery("FROM User WHERE role = :role", User.class)
            .setParameter("role", RoleType.STUDENT)
            .setMaxResults(1)
            .getSingleResult();

        // Check if student has any memberships
        if (!student.getMemberships().isEmpty()) {
            Membership membership = student.getMemberships().get(0);

            Calendar cal = Calendar.getInstance();
            Date pickupDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 14);
            Date dueDate = cal.getTime();

            Borrower borrower = new Borrower(
                book,
                student,
                membership,
                pickupDate,
                dueDate
            );
            session.persist(borrower);

            System.out.println("Book borrowed successfully by student.");
        } else {
            System.err.println("No memberships found for the student. Borrowing operation skipped.");
        }
    }

    private static User createUser(UUID personId, String firstName, String lastName,
                                 Gender gender, String phone, String username,
                                 String password, RoleType role, Location village) {
        User user = new User(personId, firstName, lastName, gender, phone,
                           username, password, role, village);
        System.out.println("Created user: " + user);
        return user;
    }
}
