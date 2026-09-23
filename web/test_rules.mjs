import { spawn } from 'child_process';
import fs from 'fs';
import { initializeTestEnvironment, assertFails, assertSucceeds } from '@firebase/rules-unit-testing';
import { doc, getDoc, setDoc, updateDoc, deleteDoc, addDoc, collection } from 'firebase/firestore';

const EMULATOR_PORT = 8089;
const PROJECT_ID = 'crescentconnect-test';

console.log("=== FIRESTORE RULES VALIDATION SUITE ===");

// 1. Start Firestore Emulator
console.log(`Starting Firestore emulator on port ${EMULATOR_PORT}...`);
const emulator = spawn('java', [
  '-jar',
  '/opt/firebase-emulators/cloud-firestore-emulator-v1.22.0.jar',
  '--port', String(EMULATOR_PORT),
  '--rules', '/app/applet/firestore.rules'
], { stdio: 'pipe' });

emulator.stdout.on('data', (d) => {
  // console.log(`[EMULATOR] ${d}`);
});
emulator.stderr.on('data', (d) => {
  // console.error(`[EMULATOR ERR] ${d}`);
});

// Wait for emulator to start
async function waitForPort(port, timeoutMs = 15000) {
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    try {
      const res = await fetch(`http://127.0.0.1:${port}/`);
      return true;
    } catch {
      await new Promise(r => setTimeout(r, 300));
    }
  }
  throw new Error(`Timeout waiting for emulator on port ${port}`);
}

async function runTests() {
  try {
    await waitForPort(EMULATOR_PORT);
    console.log("Firestore emulator is live and responsive!\n");

    const rules = fs.readFileSync('/app/applet/firestore.rules', 'utf8');
    const testEnv = await initializeTestEnvironment({
      projectId: PROJECT_ID,
      firestore: {
        rules,
        host: '127.0.0.1',
        port: EMULATOR_PORT
      }
    });

    const results = [];
    async function test(name, fn) {
      try {
        await fn();
        results.push({ name, status: 'PASS' });
        console.log(`  ✓ PASS: ${name}`);
      } catch (err) {
        results.push({ name, status: 'FAIL', error: err.message });
        console.error(`  ✗ FAIL: ${name} -> ${err.message}`);
      }
    }

    // --- SEED BASE DATA (bypass rules) ---
    await testEnv.withSecurityRulesDisabled(async (context) => {
      const adminDb = context.firestore();
      // Seed user profiles in Firestore (used by fallback hasFirestoreRole)
      await setDoc(doc(adminDb, 'users', 'student-alice'), {
        name: 'Alice Student',
        email: 'alice@crescent.edu',
        role: 'STUDENT',
        registerNumber: '220071601001'
      });
      await setDoc(doc(adminDb, 'users', 'student-bob'), {
        name: 'Bob Student',
        email: 'bob@crescent.edu',
        role: 'STUDENT',
        registerNumber: '220071601002'
      });
      await setDoc(doc(adminDb, 'users', 'staff-prof-dr-khan'), {
        name: 'Dr. Khan',
        email: 'drkhan@crescent.edu',
        role: 'STAFF',
        department: 'CA'
      });
      await setDoc(doc(adminDb, 'users', 'adviser-mary'), {
        name: 'Mary Adviser',
        email: 'mary@crescent.edu',
        role: 'CLASS_ADVISER'
      });
      await setDoc(doc(adminDb, 'users', 'admin-super'), {
        name: 'Super Admin',
        email: 'admin@crescent.edu',
        role: 'ADMIN'
      });
      await setDoc(doc(adminDb, 'users', 'cr-charlie'), {
        name: 'Charlie CR',
        email: 'charlie@crescent.edu',
        role: 'CR'
      });

      // Seed an attendance session with self-attendance enabled
      await setDoc(doc(adminDb, 'attendanceSessions', 'session-open'), {
        courseId: 'MCA401',
        date: '2026-09-20',
        periodNumber: 1,
        staffId: 'staff-prof-dr-khan',
        isSelfAttendanceEnabled: true,
        createdAt: Date.now()
      });

      // Seed an attendance session with self-attendance disabled
      await setDoc(doc(adminDb, 'attendanceSessions', 'session-closed'), {
        courseId: 'MCA402',
        date: '2026-09-20',
        periodNumber: 2,
        staffId: 'staff-prof-dr-khan',
        isSelfAttendanceEnabled: false,
        createdAt: Date.now()
      });
    });

    console.log("--- 1. UNAUTHENTICATED TESTS ---");
    const unauthDb = testEnv.unauthenticatedContext().firestore();

    await test("Unauthenticated cannot read /users/student-alice", async () => {
      await assertFails(getDoc(doc(unauthDb, 'users', 'student-alice')));
    });

    await test("Unauthenticated cannot read /announcements", async () => {
      await assertFails(getDoc(doc(unauthDb, 'announcements', 'ann-dummy')));
    });

    await test("Unauthenticated cannot write /announcements", async () => {
      await assertFails(setDoc(doc(unauthDb, 'announcements', 'ann-fake'), {
        title: 'Fake',
        authorId: 'anon'
      }));
    });

    await test("Unauthenticated cannot write /study_materials", async () => {
      await assertFails(setDoc(doc(unauthDb, 'study_materials', 'mat-fake'), {
        title: 'Hacked',
        staffId: 'anon'
      }));
    });

    await test("Unauthenticated cannot write /attendanceSessions", async () => {
      await assertFails(setDoc(doc(unauthDb, 'attendanceSessions', 'sess-fake'), {
        courseId: 'MCA401',
        staffId: 'anon'
      }));
    });

    await test("Unauthenticated cannot write attendance record", async () => {
      await assertFails(setDoc(doc(unauthDb, 'attendanceSessions', 'session-open', 'records', 'student-alice'), {
        studentId: 'student-alice',
        status: 'PRESENT'
      }));
    });

    console.log("\n--- 2. STUDENT ROLE TESTS ---");
    const aliceDb = testEnv.authenticatedContext('student-alice', { role: 'STUDENT' }).firestore();

    await test("Student can read their OWN user profile (/users/student-alice)", async () => {
      await assertSucceeds(getDoc(doc(aliceDb, 'users', 'student-alice')));
    });

    await test("Student CAN read announcements", async () => {
      await assertSucceeds(getDoc(doc(aliceDb, 'announcements', 'ann-dummy')));
    });

    await test("Student CANNOT read another student's user profile (/users/student-bob)", async () => {
      await assertFails(getDoc(doc(aliceDb, 'users', 'student-bob')));
    });

    await test("Student CANNOT create an ADMIN user profile (privilege escalation prevention)", async () => {
      await assertFails(setDoc(doc(aliceDb, 'users', 'student-alice'), {
        name: 'Alice Hacked',
        role: 'ADMIN'
      }));
    });

    await test("Student CANNOT modify their role field from STUDENT to ADMIN on update", async () => {
      await assertFails(updateDoc(doc(aliceDb, 'users', 'student-alice'), {
        role: 'ADMIN'
      }));
    });

    await test("Student CANNOT publish announcements", async () => {
      await assertFails(setDoc(doc(aliceDb, 'announcements', 'ann-student-attempt'), {
        title: 'Class Cancelled',
        content: 'No class today',
        authorId: 'student-alice'
      }));
    });

    await test("Student CANNOT upload study materials", async () => {
      await assertFails(setDoc(doc(aliceDb, 'study_materials', 'mat-student-attempt'), {
        title: 'Cheatsheet',
        staffId: 'student-alice'
      }));
    });

    await test("Student CANNOT create attendance sessions", async () => {
      await assertFails(setDoc(doc(aliceDb, 'attendanceSessions', 'session-by-student'), {
        courseId: 'MCA401',
        staffId: 'student-alice'
      }));
    });

    await test("Student CANNOT mark another student's attendance (student-bob)", async () => {
      await assertFails(setDoc(doc(aliceDb, 'attendanceSessions', 'session-open', 'records', 'student-bob'), {
        studentId: 'student-bob',
        status: 'PRESENT'
      }));
    });

    await test("Student CANNOT self-mark when self-attendance is disabled (session-closed)", async () => {
      await assertFails(setDoc(doc(aliceDb, 'attendanceSessions', 'session-closed', 'records', 'student-alice'), {
        studentId: 'student-alice',
        status: 'PRESENT'
      }));
    });

    await test("Student CAN self-mark when self-attendance IS enabled (session-open)", async () => {
      await assertSucceeds(setDoc(doc(aliceDb, 'attendanceSessions', 'session-open', 'records', 'student-alice'), {
        studentId: 'student-alice',
        status: 'PRESENT'
      }));
    });

    await test("Student CANNOT read another student's attendance record (student-bob)", async () => {
      await assertFails(getDoc(doc(aliceDb, 'attendanceSessions', 'session-open', 'records', 'student-bob')));
    });

    console.log("\n--- 3. STAFF ROLE TESTS ---");
    const staffDb = testEnv.authenticatedContext('staff-prof-dr-khan', { role: 'STAFF' }).firestore();

    await test("Staff can read student profiles (e.g. /users/student-alice)", async () => {
      await assertSucceeds(getDoc(doc(staffDb, 'users', 'student-alice')));
    });

    await test("Staff can publish announcements", async () => {
      await assertSucceeds(setDoc(doc(staffDb, 'announcements', 'ann-by-staff'), {
        title: 'Midterm Exam Notice',
        content: 'Midterm exams begin next Monday',
        authorId: 'staff-prof-dr-khan'
      }));
    });

    await test("Staff can upload study materials", async () => {
      await assertSucceeds(setDoc(doc(staffDb, 'study_materials', 'mat-by-staff'), {
        title: 'MCA401 Lecture 1 Notes',
        staffId: 'staff-prof-dr-khan'
      }));
    });

    await test("Staff can create attendance sessions", async () => {
      await assertSucceeds(setDoc(doc(staffDb, 'attendanceSessions', 'session-by-staff-1'), {
        courseId: 'MCA401',
        staffId: 'staff-prof-dr-khan',
        date: '2026-09-20',
        periodNumber: 3
      }));
    });

    await test("Staff can mark any student attendance record", async () => {
      await assertSucceeds(setDoc(doc(staffDb, 'attendanceSessions', 'session-open', 'records', 'student-bob'), {
        studentId: 'student-bob',
        status: 'PRESENT'
      }));
    });

    await test("Staff can delete attendance records", async () => {
      await assertSucceeds(deleteDoc(doc(staffDb, 'attendanceSessions', 'session-open', 'records', 'student-bob')));
    });

    console.log("\n--- 4. CLASS ADVISER & CR TESTS ---");
    const adviserDb = testEnv.authenticatedContext('adviser-mary', { role: 'CLASS_ADVISER' }).firestore();
    const crDb = testEnv.authenticatedContext('cr-charlie', { role: 'CR' }).firestore();

    await test("Class Adviser can read student profiles", async () => {
      await assertSucceeds(getDoc(doc(adviserDb, 'users', 'student-alice')));
    });

    await test("Class Adviser can create attendance sessions", async () => {
      await assertSucceeds(setDoc(doc(adviserDb, 'attendanceSessions', 'session-by-adviser'), {
        courseId: 'MCA403',
        staffId: 'adviser-mary'
      }));
    });

    await test("CR can read student profiles in class batch", async () => {
      await assertSucceeds(getDoc(doc(crDb, 'users', 'student-alice')));
    });

    await test("CR can post batch announcements", async () => {
      await assertSucceeds(setDoc(doc(crDb, 'announcements', 'ann-by-cr'), {
        title: 'Class Meeting at 4 PM',
        content: 'Regarding symposium preparations',
        authorId: 'cr-charlie'
      }));
    });

    console.log("\n--- 5. ADMIN ROLE TESTS ---");
    const adminDb = testEnv.authenticatedContext('admin-super', { role: 'ADMIN' }).firestore();

    await test("Admin can read any user profile", async () => {
      await assertSucceeds(getDoc(doc(adminDb, 'users', 'student-alice')));
    });

    await test("Admin can promote a user or update roles", async () => {
      await assertSucceeds(updateDoc(doc(adminDb, 'users', 'student-bob'), {
        role: 'CR'
      }));
    });

    await test("Admin can delete any session", async () => {
      await assertSucceeds(deleteDoc(doc(adminDb, 'attendanceSessions', 'session-closed')));
    });

    console.log("\n==========================================");
    const passCount = results.filter(r => r.status === 'PASS').length;
    const failCount = results.filter(r => r.status === 'FAIL').length;
    console.log(`TOTAL TESTS: ${results.length}`);
    console.log(`PASSED: ${passCount}`);
    console.log(`FAILED: ${failCount}`);
    console.log("==========================================");

    await testEnv.cleanup();
  } finally {
    emulator.kill();
  }
}

runTests().catch(e => {
  console.error("Test execution fatal error:", e);
  emulator.kill();
  process.exit(1);
});
