# DB-Team API Reference Guide

## Setup
The db-team JAR has been added to `/lib/hrms-database.jar`

**Add to your Java classpath:**
```bash
javac -cp lib/hrms-database.jar YourFile.java
java -cp .:lib/hrms-database.jar YourClass
```

---

## Main API Entry Point

### HRMSDataAccessFacade
**Package:** `com.hrms.db.facade`

Main facade class to access all database operations. This is your entry point for all DB interactions.

```java
import com.hrms.db.facade.HRMSDataAccessFacade;

HRMSDataAccessFacade dbApi = new HRMSDataAccessFacade();
```

---

## Available Repository APIs by Subsystem

### 1. **Recruitment Management**
**Package:** `com.hrms.db.repositories.recruitment_management`

**Interface:** `IRecruitmentRepository`

**Key Methods:**
- `createJobPosting(jobPostingDTO)` - Post new job
- `searchCandidates(criteria)` - Search candidates
- `createApplication(applicationDTO)` - Record job application
- `scheduleInterview(interviewScheduleDTO)` - Schedule interview
- `createOffer(offerDTO)` - Create job offer
- `getApplicationsByStatus(status)` - Get applications by status

**DTOs Used:**
- `JobPostingDTO`
- `CandidateDTO`
- `ApplicationDTO`
- `InterviewScheduleDTO`
- `OfferDTO`

---

### 2. **Onboarding & Exit Management**
**Package:** `com.hrms.db.repositories.onboarding`

**Interface:** `IOnboardingRepository`

**Key Methods:**
- `createOnboardingTask(taskDTO)` - Create onboarding task
- `getOnboardingProgress(employeeId)` - Track onboarding progress
- `recordExitRequest(exitRequestDTO)` - Record exit request
- `scheduleExitInterview(interviewDTO)` - Schedule exit interview
- `recordClearance(clearanceDTO)` - Record clearance completion
- `getExitChecklistStatus(employeeId)` - Get exit status

**DTOs Used:**
- `OnboardingTaskDTO`
- `ProgressDTO`
- `ExitRequestDTO`
- `ExitInterviewDTO`
- `ClearanceDTO`

---

### 3. **Leave Management**
**Package:** `com.hrms.db.repositories.leave` or `Leave_Management_Subsytem`

**Interface:** `ILeaveRecordRepository`, `ILeavePolicyRepository`

**Key Methods:**
- `submitLeaveRequest(leaveRequestDTO)` - Submit leave request
- `approveLeaveRequest(requestId)` - Approve leave
- `rejectLeaveRequest(requestId)` - Reject leave
- `getLeavePolicies()` - Get all leave policies
- `getLeaveBalance(employeeId)` - Get employee leave balance
- `getLeaveHistory(employeeId)` - Get leave history

**DTOs Used:**
- `LeaveRequestDTO`
- `LeaveSummaryDTO`
- `LeavePolicyDTO`

---

### 4. **Payroll Management**
**Package:** `com.hrms.db.repositories.payroll`

**Interface:** `IPayrollRepository`

**Key Methods:**
- `processPayroll(payrollDataPackage)` - Process monthly payroll
- `calculatePayroll(employeeId, month)` - Calculate salary
- `getPayrollResult(employeeId, month)` - Get payroll details
- `recordAttendance(attendanceDTO)` - Record attendance
- `getTaxContext(employeeId)` - Get tax information

**DTOs Used:**
- `PayrollDataPackage`
- `EmployeeDTO`
- `AttendanceDTO`
- `PayrollResultDTO`
- `TaxContextDTO`

---

### 5. **Performance Management**
**Package:** `com.hrms.db.repositories.performance`

**Interfaces:** `IAppraisalRepository`, `IGoalRepository`, `IKPIRepository`, `IFeedbackRepository`

**Key Methods:**
- `createAppraisal(appraisalDTO)` - Create appraisal
- `submitAppraisal(appraisalId)` - Submit appraisal
- `setGoals(employeeId, goalsList)` - Set employee goals
- `recordKPI(kpiRecordDTO)` - Record KPI metrics
- `submitFeedback(feedbackDTO)` - Submit 360 feedback
- `generateProgressReport(employeeId)` - Generate progress report

**DTOs Used:**
- `Appraisal`
- `Goal`
- `KPI`
- `Feedback`
- `SkillGap`

---

### 6. **Expense Management**
**Package:** `com.hrms.db.repositories.Expense_Management`

**Interfaces:** `ClaimRepository`, `ReceiptRepository`, `BudgetRepository`

**Key Methods:**
- `submitExpenseClaim(claimDTO)` - Submit expense claim
- `uploadReceipt(receiptDTO)` - Upload receipt
- `approveExpenseClaim(claimId)` - Approve claim
- `rejectExpenseClaim(claimId)` - Reject claim
- `getClaimHistory(employeeId)` - Get employee claims
- `getBudgetStatus(departmentId)` - Check department budget

---

### 7. **Customization**
**Package:** `com.hrms.db.repositories.Customization_team`

**Interfaces:** `IFormRepository`, `IFlexfieldRepository`, `IWorkflowRepository`

**Key Methods:**
- `createCustomForm(formDTO)` - Create custom form
- `createFlexField(flexFieldDTO)` - Add flexible field
- `defineWorkflow(workflowDTO)` - Define business workflow
- `getFormById(formId)` - Retrieve form

---

### 8. **Benefits Management**
**Package:** `com.hrms.db.repositories.benefits`

**Interfaces:** `BenefitPlanDAO`, `EnrollmentDAO`, `BenefitPolicyDAO`

**Key Methods:**
- `getBenefitPlans()` - Get available benefit plans
- `enrollInBenefit(enrollmentDTO)` - Enroll employee in benefit
- `getEmployeeBenefits(employeeId)` - Get employee benefits
- `getBenefitPolicy(policyId)` - Get policy details

---

### 9. **Multi-Country/Multi-Currency**
**Package:** `com.hrms.db.repositories.multicountry`

**Interface:** `IMultiCountryRepository`

**Key Methods:**
- `getLocaleConfig(countryCode)` - Get country configuration
- `getExchangeRate(fromCurrency, toCurrency)` - Get exchange rate
- `getCompliancePolicy(countryCode)` - Get compliance rules
- `getTaxRegime(countryCode)` - Get tax configuration
- `getWorkingHours(countryCode)` - Get working hours policy

---

### 10. **Document Management**
**Package:** `com.hrms.db.repositories.docu_management`

**Interfaces:** `DocumentRepository`, `AuditRepository`

**Key Methods:**
- `storeDocument(documentDTO)` - Store document
- `retrieveDocument(documentId)` - Get document
- `deleteDocument(documentId)` - Delete document
- `getAuditLog(documentId)` - Get document audit trail

---

### 11. **Attrition Analysis**
**Package:** `com.hrms.db.repositories.attrition`

**Interface:** `IAttritionRepository`

**Key Methods:**
- `getAttritionAnalysis()` - Get attrition metrics
- `getRiskAssessment(employeeId)` - Assess exit risk
- `getCohortAnalysis()` - Cohort-based analysis
- `generateHeatMap()` - Visualization data
- `getExitReasons()` - Get exit statistics

---

### 12. **Security & Audit**
**Package:** `com.hrms.db.repositories.security`

**Interfaces:** `IAuthenticationService`, `IAuthorizationService`, `IAuditService`

**Key Methods:**
- `authenticateUser(username, password)` - User login
- `authorizeUser(userId, action)` - Check permissions
- `recordAuditLog(auditEntry)` - Log user actions
- `getUserSession(userId)` - Get user session

---

## Entity/Model Classes

All entities are in `com.hrms.db.entities`:

```
Employee, Asset, Attendance, Leave, Payroll
Candidate, Application, Offer, JobPosting
Document, Notification, Training
Goal, KPI, Appraisal, Feedback
And 60+ more entity classes...
```

---

## Common Usage Pattern

```java
import com.hrms.db.facade.HRMSDataAccessFacade;
import com.hrms.db.repositories.recruitment_management.*;

public class MyService {
    private HRMSDataAccessFacade dbApi;
    
    public MyService() {
        dbApi = new HRMSDataAccessFacade();
    }
    
    public void example() {
        // Example: Create job posting
        JobPostingDTO job = new JobPostingDTO();
        job.setTitle("Senior Developer");
        job.setDepartment("Engineering");
        
        // Call through facade
        // IRecruitmentRepository repo = dbApi.getRecruitmentRepository();
        // repo.createJobPosting(job);
    }
}
```

---

## Notes

⚠️ **Important:**
- The original JAR file has **missing dependencies** - you'll need to ask db-team to rebuild with `maven-shade-plugin`
- Until then, you can use the extracted classes but may encounter runtime errors for certain operations
- All access must go through the provided APIs/facades

---

**For more details, refer to the extracted JAR files in `/db-team/extracted_jar/`**
