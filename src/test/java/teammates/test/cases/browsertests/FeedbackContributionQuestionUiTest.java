package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

public class FeedbackContributionQuestionUiTest extends FeedbackQuestionUiTest {
    private static InstructorFeedbackEditPage feedbackEditPage;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackContributionQuestionUiTest.json");
        removeAndRestoreDataBundle(testData);
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
    }

    @BeforeClass
    public void classSetup() {
        feedbackEditPage = getFeedbackEditPage(instructorId, courseId, feedbackSessionName);
    }
    
    @Test
    public void allTests() throws Exception {
        testEditPage();
        
        //TODO: move/create other Contribution question related UI tests here.
        //i.e. results page, submit page.
    }
    
    private void testEditPage() throws Exception {
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
        testAddContributionQuestionAsSecondQuestion();
    }

    @Override
    public void testNewQuestionFrame() {
        ______TS("CONTRIB: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONTRIB");
        assertTrue(feedbackEditPage.verifyNewContributionQuestionFormIsDisplayed());
    }
    
    @Override
    public void testInputValidation() {
        
        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);
        
    }

    @Override
    public void testCustomizeOptions() {

        //no question specific options to test
        
        ______TS("CONTRIB: set visibility options");
        
        feedbackEditPage.enableOtherVisibilityOptionsForNewQuestion();
        //TODO: click and ensure can see answer for recipients,
        //giver team members, recipient team members
        //are always the same. (under visibility options)
        
    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("CONTRIB: add question action success");
        
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("contrib qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("CONTRIB: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        
        //Check invalid feedback paths are disabled.
        //Javascript should hide giver/recipient options that are not STUDENTS to OWN_TEAM_MEMBERS_INCLUDING_SELF
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionEdit.html");
        
        feedbackEditPage.fillQuestionTextBox("edited contrib qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        feedbackEditPage.toggleNotSureCheck(1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionEditSuccess.html");
    }
    
    @Override
    public void testDeleteQuestionAction() {
        ______TS("CONTRIB: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONTRIB: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }
    
    /**
     * Tests the case when contribution question is added after another question that
     * has options invalid for contribution questions. This is to prevent invalid options
     * from being copied over to the contribution question.
     */
    private void testAddContributionQuestionAsSecondQuestion() {
        ______TS("CONTRIB: add as second question");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("q1, essay qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONTRIB");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("q2, contribution qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        
    }

}
