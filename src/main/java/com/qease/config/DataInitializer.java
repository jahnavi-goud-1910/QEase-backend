package com.qease.config;

import com.qease.entity.*;
import com.qease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${qease.seed-demo-data:true}")
    private boolean seedDemoData;

    // Optional first admin for real deployments (used only when demo seeding is off and no users exist yet)
    @Value("${qease.bootstrap-admin.email:}")
    private String bootstrapAdminEmail;

    @Value("${qease.bootstrap-admin.password:}")
    private String bootstrapAdminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!seedDemoData) {
            // Production: no demo accounts. Optionally create the first administrator from environment variables.
            if (userRepository.count() == 0 && !bootstrapAdminEmail.isBlank() && bootstrapAdminPassword.length() >= 8) {
                userRepository.save(User.builder()
                        .fullName("System Administrator")
                        .email(bootstrapAdminEmail.trim())
                        .password(passwordEncoder.encode(bootstrapAdminPassword))
                        .phone("0000000000")
                        .role(Role.ADMIN)
                        .active(true)
                        .build());
                System.out.println(">>> [QEase] Bootstrap administrator created: " + bootstrapAdminEmail.trim());
            }
            return;
        }
        if (userRepository.count() == 0) {
            seedInitialData();
        }
        // Runs on EVERY start (also for databases created earlier): makes sure every service has a
        // staff login + counter. It only fills gaps and never overwrites existing assignments.
        ensureStaffCoverage();
    }

    /** One demo staff account + counter per college service: {service code, email, full name, counter no., counter name}. */
    private static final String[][] STAFF_PER_SERVICE = {
            {"EXAM", "staff.exam@college.edu", "Dr. Robert Vance", "C-101", "Exam Cell Counter 1"},
            {"ACCT", "staff.fees@college.edu", "Sarah Jenkins", "C-102", "Accounts Desk A"},
            {"CERT", "staff.cert@college.edu", "Michael Chen", "C-103", "Certificates Counter"},
            {"STUD", "staff.stud@college.edu", "Anita Rao", "C-104", "Student Helpline"},
            {"ADMN", "staff.admn@college.edu", "Rahul Verma", "C-105", "Administration Desk"},
            {"LIBR", "staff.libr@college.edu", "Meera Iyer", "C-106", "Library Help Desk"},
    };

    private void ensureStaffCoverage() {
        String hash = null;

        for (String[] row : STAFF_PER_SERVICE) {
            ServiceEntity service = serviceRepository.findByCode(row[0]).orElse(null);
            if (service == null) {
                continue; // service was removed/renamed by an admin: nothing to cover
            }

            User staff = userRepository.findByEmail(row[1]).orElse(null);
            if (staff == null) {
                if (hash == null) hash = passwordEncoder.encode("password123");
                staff = userRepository.save(User.builder()
                        .fullName(row[2]).email(row[1]).password(hash)
                        .phone("98765432" + (10 + row[3].charAt(row[3].length() - 1) % 10))
                        .role(Role.STAFF).active(true).build());
            }

            if (counterRepository.findFirstByStaffIdOrderByIdAsc(staff.getId()).isPresent()) {
                continue; // already has a counter
            }

            // Prefer an existing counter of this service that has nobody assigned
            Counter free = counterRepository.findByService(service).stream()
                    .filter(c -> c.getStaff() == null)
                    .findFirst().orElse(null);
            if (free != null) {
                free.setStaff(staff);
                counterRepository.save(free);
                continue;
            }

            // Otherwise create a new counter (unique number)
            String number = row[3];
            int n = 200;
            while (counterRepository.existsByCounterNumber(number)) {
                number = "C-" + (++n);
            }
            counterRepository.save(Counter.builder()
                    .counterNumber(number).name(row[4]).service(service).staff(staff).active(true).build());
        }

        // Staff account that deliberately has no counter (to demo the "No counter assigned" screen)
        if (!userRepository.existsByEmail("staff.none@college.edu")) {
            userRepository.save(User.builder()
                    .fullName("Priya Nair").email("staff.none@college.edu")
                    .password(passwordEncoder.encode("password123"))
                    .phone("9876543214").role(Role.STAFF).active(true).build());
        }
    }

    private void seedInitialData() {
        System.out.println(">>> [QEase] Seeding initial database records...");

        String defaultPass = passwordEncoder.encode("password123");

        // 1. Create Users
        User admin = User.builder()
                .fullName("System Administrator")
                .email("admin@college.edu")
                .password(defaultPass)
                .phone("9876543210")
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);

        User staffExam = User.builder()
                .fullName("Dr. Robert Vance")
                .email("staff.exam@college.edu")
                .password(defaultPass)
                .phone("9876543211")
                .role(Role.STAFF)
                .active(true)
                .build();
        userRepository.save(staffExam);

        User staffFees = User.builder()
                .fullName("Sarah Jenkins")
                .email("staff.fees@college.edu")
                .password(defaultPass)
                .phone("9876543212")
                .role(Role.STAFF)
                .active(true)
                .build();
        userRepository.save(staffFees);

        User staffCert = User.builder()
                .fullName("Michael Chen")
                .email("staff.cert@college.edu")
                .password(defaultPass)
                .phone("9876543213")
                .role(Role.STAFF)
                .active(true)
                .build();
        userRepository.save(staffCert);

        // Staff account intentionally WITHOUT a counter (demonstrates the "No counter assigned" state)
        User staffUnassigned = User.builder()
                .fullName("Priya Nair")
                .email("staff.none@college.edu")
                .password(defaultPass)
                .phone("9876543214")
                .role(Role.STAFF)
                .active(true)
                .build();
        userRepository.save(staffUnassigned);

        User student1 = User.builder()
                .fullName("Rahul Sharma")
                .email("student1@college.edu")
                .password(defaultPass)
                .phone("9123456780")
                .role(Role.STUDENT)
                .active(true)
                .build();
        userRepository.save(student1);

        User student2 = User.builder()
                .fullName("Priya Patel")
                .email("student2@college.edu")
                .password(defaultPass)
                .phone("9123456781")
                .role(Role.STUDENT)
                .active(true)
                .build();
        userRepository.save(student2);

        User student3 = User.builder()
                .fullName("Alex Mercer")
                .email("student3@college.edu")
                .password(defaultPass)
                .phone("9123456782")
                .role(Role.STUDENT)
                .active(true)
                .build();
        userRepository.save(student3);

        // 2. Create Services
        ServiceEntity examService = ServiceEntity.builder()
                .name("Examination Cell")
                .code("EXAM")
                .description("Hall tickets, re-evaluation requests, grade card collection, and exam registration inquiries.")
                .estimatedTimeMinutes(8)
                .active(true)
                .build();
        serviceRepository.save(examService);

        ServiceEntity feesService = ServiceEntity.builder()
                .name("Accounts / Fee Payment")
                .code("ACCT")
                .description("Tuition fee receipts, scholarship claims, fine payments, and refund status.")
                .estimatedTimeMinutes(10)
                .active(true)
                .build();
        serviceRepository.save(feesService);

        ServiceEntity studentSec = ServiceEntity.builder()
                .name("Student Section")
                .code("STUD")
                .description("Bonafide certificates, ID card reissue, change of details, and general queries.")
                .estimatedTimeMinutes(6)
                .active(true)
                .build();
        serviceRepository.save(studentSec);

        ServiceEntity adminOffice = ServiceEntity.builder()
                .name("Administration Office")
                .code("ADMN")
                .description("Official letters, NOC requests, hostel approvals, and grievance registration.")
                .estimatedTimeMinutes(12)
                .active(true)
                .build();
        serviceRepository.save(adminOffice);

        ServiceEntity library = ServiceEntity.builder()
                .name("Library Services")
                .code("LIBR")
                .description("Book clearance, library fine clearance, digital access card activation.")
                .estimatedTimeMinutes(5)
                .active(true)
                .build();
        serviceRepository.save(library);

        ServiceEntity certService = ServiceEntity.builder()
                .name("Certificates / Documents")
                .code("CERT")
                .description("Degree certificate issue, provisional certificates, transcript verification.")
                .estimatedTimeMinutes(15)
                .active(true)
                .build();
        serviceRepository.save(certService);

        // 3. Create Counters
        Counter counterExam = Counter.builder()
                .counterNumber("C-101")
                .name("Exam Cell Counter 1")
                .service(examService)
                .staff(staffExam)
                .active(true)
                .build();
        counterRepository.save(counterExam);

        Counter counterFees = Counter.builder()
                .counterNumber("C-102")
                .name("Accounts Desk A")
                .service(feesService)
                .staff(staffFees)
                .active(true)
                .build();
        counterRepository.save(counterFees);

        Counter counterCert = Counter.builder()
                .counterNumber("C-103")
                .name("Certificates Counter")
                .service(certService)
                .staff(staffCert)
                .active(true)
                .build();
        counterRepository.save(counterCert);

        Counter counterHelp = Counter.builder()
                .counterNumber("C-104")
                .name("Student Helpline")
                .service(studentSec)
                .staff(null)
                .active(true)
                .build();
        counterRepository.save(counterHelp);

        // 4. Create Historical & Active Queue Tokens for rich demo
        LocalDateTime now = LocalDateTime.now();

        // Completed historical token
        QueueToken hist1 = QueueToken.builder()
                .tokenNumber("EXAM-001")
                .sequenceNumber(1)
                .student(student1)
                .service(examService)
                .counter(counterExam)
                .status(TokenStatus.COMPLETED)
                .issueTime(now.minusHours(2))
                .calledTime(now.minusHours(2).plusMinutes(5))
                .servedTime(now.minusHours(2).plusMinutes(6))
                .completedTime(now.minusHours(2).plusMinutes(15))
                .notes("Hall ticket issued.")
                .build();
        queueTokenRepository.save(hist1);

        QueueToken hist2 = QueueToken.builder()
                .tokenNumber("ACCT-001")
                .sequenceNumber(1)
                .student(student2)
                .service(feesService)
                .counter(counterFees)
                .status(TokenStatus.COMPLETED)
                .issueTime(now.minusHours(1))
                .calledTime(now.minusHours(1).plusMinutes(3))
                .servedTime(now.minusHours(1).plusMinutes(4))
                .completedTime(now.minusHours(1).plusMinutes(14))
                .notes("Fee receipt stamped.")
                .build();
        queueTokenRepository.save(hist2);

        // Active tokens
        QueueToken servingToken = QueueToken.builder()
                .tokenNumber("ACCT-002")
                .sequenceNumber(2)
                .student(student1)
                .service(feesService)
                .counter(counterFees)
                .status(TokenStatus.SERVING)
                .issueTime(now.minusMinutes(20))
                .calledTime(now.minusMinutes(5))
                .servedTime(now.minusMinutes(4))
                .notes("Processing scholarship voucher.")
                .build();
        queueTokenRepository.save(servingToken);

        QueueToken waiting1 = QueueToken.builder()
                .tokenNumber("ACCT-003")
                .sequenceNumber(3)
                .student(student2)
                .service(feesService)
                .status(TokenStatus.WAITING)
                .issueTime(now.minusMinutes(10))
                .build();
        queueTokenRepository.save(waiting1);

        QueueToken waiting2 = QueueToken.builder()
                .tokenNumber("EXAM-002")
                .sequenceNumber(2)
                .student(student3)
                .service(examService)
                .status(TokenStatus.WAITING)
                .issueTime(now.minusMinutes(5))
                .build();
        queueTokenRepository.save(waiting2);

        System.out.println(">>> [QEase] Initial records created successfully!");
    }
}
