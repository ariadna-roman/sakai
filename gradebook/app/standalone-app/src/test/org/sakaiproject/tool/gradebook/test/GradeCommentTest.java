/*******************************************************************************
 * Copyright (c) 2006 The Regents of the University of California, The MIT Corporation
 *
 *  Licensed under the Educational Community License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ecl1.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/

package org.sakaiproject.tool.gradebook.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.gradebook.*;

import junit.framework.Assert;


import java.util.*;

/**
 * Author:Louis Majanja <louis@media.berkeley.edu>
 * Date: Oct 24, 2006
 * Time: 2:20:55 PM
 */
public class GradeCommentTest extends GradebookTestBase  {

    protected Gradebook gradebook;

    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        // Create a gradebook to work with
        String className = this.getClass().getName();
        String gradebookName = className + (new Date()).getTime();
        gradebookService.addGradebook(gradebookName, gradebookName);

        // Set up a holder for enrollments, teaching assignments, and sections.
        integrationSupport.createCourse(gradebookName, gradebookName, false, false, false);

        // Grab the gradebook for use in the tests
        gradebook = gradebookManager.getGradebook(gradebookName);
    }



    public void testCreateComment() throws Exception{

        Set students = new HashSet();
        students.add("entered1");
        //create assignment
        Long asgId = gradebookManager.createAssignment(gradebook.getId(), "Scores Entered Test", new Double(10), new Date(), Boolean.FALSE,Boolean.FALSE
        );
        Assignment asn = (Assignment)gradebookManager.getAssignmentsWithStats(gradebook.getId(), Assignment.DEFAULT_SORT, true).get(0);

        Assert.assertTrue(!gradebookManager.isEnteredAssignmentScores(asgId));

        // add grade records
        List gradeRecords = new ArrayList();
        gradeRecords.add(new AssignmentGradeRecord(asn, "entered1", new Double(9)));

        gradebookManager.updateAssignmentGradeRecords(asn, gradeRecords);
        Assert.assertTrue(gradebookManager.isEnteredAssignmentScores(asgId));

        List persistentGradeRecords = gradebookManager.getPointsEarnedSortedGradeRecords(asn, students);

        gradeRecords = new ArrayList();
        AssignmentGradeRecord gradeRecord = (AssignmentGradeRecord)persistentGradeRecords.get(0);
        gradeRecord.setPointsEarned(null);
        gradeRecords.add(gradeRecord);

        gradebookManager.updateAssignmentGradeRecords(asn, gradeRecords);
        Assert.assertTrue(!gradebookManager.isEnteredAssignmentScores(asgId));

        // add comments
        Long commentId = gradebookManager.createComment(asn,"entered1","grade commentText test");
        logger.debug("new commentText entered with id " +commentId);
        //get the eneterd commentText
        Comment comment = gradebookManager.getComment(asn,"entered1");
        logger.debug(comment.getCommentText());
        Assert.assertTrue(comment.getCommentText().equals("grade commentText test"));

    }


    public void testUpdateComment() throws Exception {

        Set students = new HashSet();
        students.add("entered1");
        //create assignment
        Long asgId = gradebookManager.createAssignment(gradebook.getId(), "Scores Entered Test", new Double(10), new Date(), Boolean.FALSE,Boolean.FALSE
        );
        Assignment asn = (Assignment)gradebookManager.getAssignmentsWithStats(gradebook.getId(), Assignment.DEFAULT_SORT, true).get(0);

        Assert.assertTrue(!gradebookManager.isEnteredAssignmentScores(asgId));

        // add grade records
        List gradeRecords = new ArrayList();
        gradeRecords.add(new AssignmentGradeRecord(asn, "entered1", new Double(9)));

        gradebookManager.updateAssignmentGradeRecords(asn, gradeRecords);
        Assert.assertTrue(gradebookManager.isEnteredAssignmentScores(asgId));

        List persistentGradeRecords = gradebookManager.getPointsEarnedSortedGradeRecords(asn, students);

        gradeRecords = new ArrayList();
        AssignmentGradeRecord gradeRecord = (AssignmentGradeRecord)persistentGradeRecords.get(0);
        gradeRecord.setPointsEarned(null);
        gradeRecords.add(gradeRecord);

        gradebookManager.updateAssignmentGradeRecords(asn, gradeRecords);
        Assert.assertTrue(!gradebookManager.isEnteredAssignmentScores(asgId));

        // add comments
        Long commentId = gradebookManager.createComment(asn,"entered1","grade commentText test");
        logger.debug("new commentText entered with id " +commentId);
        //get the entered commentText
        Comment comment = gradebookManager.getComment(asn,"entered1");
        logger.debug(comment.getCommentText());
        Assert.assertTrue(comment.getCommentText().equals("grade commentText test"));
        //change the commentText text and save it
        comment.setCommentText("grade commentText changed");
        gradebookManager.updateComment(comment);
        //retrieve the cahnged and updated commment
        comment = gradebookManager.getComment(asn,"entered1");
        //verify that that the saved commentText did actually change
        logger.debug("updated test is:" + comment.getCommentText());
        Assert.assertTrue(comment.getCommentText().equals("grade commentText changed"));

    }

    public void testGetAssignmentCommentSet()throws Exception{

        List studentUidsList = Arrays.asList(new String[] {
			"testStudentUserUid1",
			"testStudentUserUid2",
			"testStudentUserUid3",
		});
		addUsersEnrollments(gradebook, studentUidsList);

        //create a asssignment
        gradebookManager.createAssignment(gradebook.getId(), "Scores Entered Test", new Double(10), new Date(), Boolean.FALSE,Boolean.FALSE);

        Assignment asn = (Assignment)gradebookManager.getAssignmentsWithStats(gradebook.getId(), Assignment.DEFAULT_SORT, true).get(0);

        // add comments to students
        Long commentId = gradebookManager.createComment(asn,"testStudentUserUid1","grade commentText test 1");
        logger.debug("new commentText entered with id " +commentId);
        commentId = gradebookManager.createComment(asn,"testStudentUserUid2","grade commentText test 2");
        logger.debug("new commentText entered with id " +commentId);
        commentId = gradebookManager.createComment(asn,"testStudentUserUid3","grade commentText test 3");
        logger.debug("new commentText entered with id " +commentId);
        // retrieve comments set

        AssignmentCommentSet assignmentCommentSet = gradebookManager.getAssignmentComments(asn);

        Map commentMap = assignmentCommentSet.getCommentMap();

        Assert.assertTrue(commentMap.size() > 0);
        logger.debug("print out the commentText set contents");
        Iterator it = commentMap.keySet().iterator();
        while(it.hasNext()){
            Comment comment = (Comment)commentMap.get(it.next());
            logger.debug("student id "+ comment.getStudentId() + " commentText text:"+ comment.getCommentText());
        }
        Comment comment = (Comment)commentMap.get("testStudentUserUid1");
        Assert.assertTrue(comment.getCommentText().equals("grade commentText test 1"));
    }


    public void testUpdateAssignmentCommentSet()throws Exception {

           List studentUidsList = Arrays.asList(new String[] {
			"testStudentUserUid1",
			"testStudentUserUid2",
			"testStudentUserUid3",
		});
		addUsersEnrollments(gradebook, studentUidsList);

        //create a asssignment
        gradebookManager.createAssignment(gradebook.getId(), "Scores Entered Test", new Double(10), new Date(), Boolean.FALSE,Boolean.FALSE);

        Assignment asn = (Assignment)gradebookManager.getAssignmentsWithStats(gradebook.getId(), Assignment.DEFAULT_SORT, true).get(0);

        // add comments to students
        Long commentId = gradebookManager.createComment(asn,"testStudentUserUid1","grade commentText test 1");
        logger.debug("new commentText entered with id " +commentId);
        commentId = gradebookManager.createComment(asn,"testStudentUserUid2","grade commentText test 2");
        logger.debug("new commentText entered with id " +commentId);
        commentId = gradebookManager.createComment(asn,"testStudentUserUid3","grade commentText test 3");
        logger.debug("new commentText entered with id " +commentId);
        // retrieve comments set
        AssignmentCommentSet assignmentCommentSet = gradebookManager.getAssignmentComments(asn);
        //test updates without any changes
        logger.debug("test assignmentCommentSet updates");
        gradebookManager.updateAssignmentComments(assignmentCommentSet);

        logger.debug("now make changes and update");

        Map commentMap = assignmentCommentSet.getCommentMap();
        Iterator it = commentMap.keySet().iterator();
        while(it.hasNext()){
            Comment comment = (Comment)commentMap.get(it.next());
            comment.setCommentText("grade update test");
        }
        //verify the update to the map
        Iterator iter = commentMap.keySet().iterator();
        while(iter.hasNext()){
            Comment comment = (Comment)commentMap.get(iter.next());
            logger.debug("student id "+ comment.getStudentId() + " commentText text:"+ comment.getCommentText());
        }
        //now update the database
        gradebookManager.updateAssignmentComments(assignmentCommentSet);
        // retrieve the grade set and see if the comments were updated

        logger.debug("retrieve updated commentSet -----");
        assignmentCommentSet = gradebookManager.getAssignmentComments(asn);

        commentMap = assignmentCommentSet.getCommentMap();

        Assert.assertTrue(commentMap.size() > 0);
        logger.debug("print out the commentText set contents");
        Iterator i = commentMap.keySet().iterator();
        while(i.hasNext()){
            Comment comment = (Comment)commentMap.get(i.next());
            logger.debug("student id "+ comment.getStudentId() + " commentText text:"+ comment.getCommentText());
        }
        Comment comment = (Comment)commentMap.get("testStudentUserUid1");
        Assert.assertTrue(comment.getCommentText().equals("grade update test"));

    }
}
