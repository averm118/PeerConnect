package guiEvaluateDiscussions;

import java.util.List;

import entityClasses.Feedback;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ControllerEvaluateDiscussions Class. </p>
 * 
 * <p> Description: This controller handles the evaluation of student discussions.
 * It allows staff to:
 *
 * evaluate a student's discussion participation,
 * save discussion evaluation feedback,
 * 
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */

public class ControllerEvaluateDiscussions {

	public ControllerEvaluateDiscussions() {}
	
	/**
	 * This method evaluates a student's participation in a given thread.
	 *
	 * It retrieves the student's posts and replies in the selected thread,
	 * checks whether they meet the required criteria, calculates a suggested grade,
	 * and displays the evaluation results in the user interface.
	 */

	protected static void performEvaluate() {
		String username = ViewEvaluateDiscussions.text_Student.getText().trim();
		String thread = ViewEvaluateDiscussions.text_Thread.getText().trim();

		if (username.isEmpty() || thread.isEmpty()) {
			ViewEvaluateDiscussions.label_Status.setText("Enter student username and thread.");
			return;
		}

		int minPostWords;
		int minReplyWords;
		int minReplies;
		// Read the evaluation criteria entered by staff.
		try {
			minPostWords = Integer.parseInt(ViewEvaluateDiscussions.text_MinPostWords.getText().trim());
			minReplyWords = Integer.parseInt(ViewEvaluateDiscussions.text_MinReplyWords.getText().trim());
			minReplies = Integer.parseInt(ViewEvaluateDiscussions.text_MinReplies.getText().trim());
			//Stop if criteria is not number
		} catch (Exception e) {
			ViewEvaluateDiscussions.label_Status.setText("Criteria must be numbers.");
			return;
		}

		List<String> postList = ViewEvaluateDiscussions.thePostDatabase.getPostTitlesByThread(thread, username);
		//used to track total work done by the student by counting
		int postCount = 0;
		int qualifiedPostCount = 0;
		int replyCount = 0;
		int qualifiedReplyCount = 0;

		StringBuilder postsText = new StringBuilder();
		StringBuilder repliesText = new StringBuilder();

		for (String item : postList) {
			try {
				String[] lines = item.split("\n");
				String second = lines[1];
				int start = second.indexOf('[') + 1;
				int end = second.indexOf(']');
				int postId = Integer.parseInt(second.substring(start, end));

				Post p = ViewEvaluateDiscussions.thePostDatabase.getPostById(postId);
				// Only count posts that belong to the selected student and thread.
				if (p != null && p.getAuthor().equals(username) && p.getThread().equals(thread)) {
					postCount++;

					postsText.append("Post ").append(postCount).append(":\n");
					postsText.append("Title: ").append(p.getTitle()).append("\n");
					postsText.append(p.getBody()).append("\n\n");

					if (countWords(p.getBody()) >= minPostWords) {
						qualifiedPostCount++;
					}
				}
			} catch (Exception e) {
			}
		}

		for (String item : postList) {
			try {
				String[] lines = item.split("\n");
				String second = lines[1];
				int start = second.indexOf('[') + 1;
				int end = second.indexOf(']');
				int postId = Integer.parseInt(second.substring(start, end));

				List<Reply> replies = ViewEvaluateDiscussions.theReplyDatabase.getReplyList(postId);

				for (Reply r : replies) {
					if (r.getAuthor().equals(username)) {
						replyCount++;

						repliesText.append("Reply ").append(replyCount).append(":\n");
						repliesText.append(r.getBody()).append("\n\n");

						if (countWords(r.getBody()) >= minReplyWords) {
							qualifiedReplyCount++;
						}
					}
				}
			} catch (Exception e) {
			}
		}

		if (postsText.length() == 0) {
			postsText.append("No posts found for this student in this thread.");
		}

		if (repliesText.length() == 0) {
			repliesText.append("No replies found for this student in this thread.");
		}
		// Determine whether each rule has been satisfied.
		boolean passesPostRule = qualifiedPostCount > 0;
		boolean passesReplyCountRule = replyCount >= minReplies;
		boolean passesReplyWordRule = (replyCount > 0 && qualifiedReplyCount == replyCount);

		int grade = 0;
		// Give 40 marks if at least one post met the post requirement
		if (passesPostRule) grade += 40;
		// Give 30 marks if the student made enough replies
		if (passesReplyCountRule) grade += 30;
		// Give up to 30 marks based on how many replies met the word requirement.
		if (replyCount > 0) {
			grade += (int)Math.round((30.0 * qualifiedReplyCount) / replyCount);
		}

		String result =
				"Student: " + username + "\n" +
				"Thread: " + thread + "\n" +
				"Posts in thread: " + postCount + "\n" +
				"Posts meeting word requirement: " + qualifiedPostCount + "\n" +
				"Replies in thread: " + replyCount + "\n" +
				"Replies meeting word requirement: " + qualifiedReplyCount + "\n" +
				"Minimum replies required: " + minReplies + "\n" +
				"Post requirement met: " + (passesPostRule ? "Yes" : "No") + "\n" +
				"Reply count requirement met: " + (passesReplyCountRule ? "Yes" : "No") + "\n" +
				"Reply word requirement met: " + (passesReplyWordRule ? "Yes" : "No") + "\n" +
				"Suggested Grade: " + grade;

		ViewEvaluateDiscussions.area_Result.setText(result);
		ViewEvaluateDiscussions.area_PostsMade.setText(postsText.toString());
		ViewEvaluateDiscussions.area_RepliesMade.setText(repliesText.toString());
		ViewEvaluateDiscussions.text_Grade.setText(String.valueOf(grade));
		ViewEvaluateDiscussions.label_Status.setText("Evaluation complete.");
	}

	/*******
	 * <p> Method: countWords </p>
	 * 
	 * <p> Counts the number of words in a string.</p>
	 * 
	 * @param text inpuT
	 * @return number of words, or 0 if empty/null
	 */

	protected static int countWords(String text) {
		if (text == null || text.trim().isEmpty()) return 0;
		return text.trim().split("\\s+").length;
	}

	/*******
	 * <p> Method: performSave </p>
	 * 
	 * <p> Saves the evaluation and feedback into the database
	 * as a thread-level evaluation.</p>
	 */

	protected static void performSave() {
		String username = ViewEvaluateDiscussions.text_Student.getText().trim();
		String thread = ViewEvaluateDiscussions.text_Thread.getText().trim();
		String feedbackBody = ViewEvaluateDiscussions.area_Feedback.getText().trim();
		String result = ViewEvaluateDiscussions.area_Result.getText().trim();
		String grade = ViewEvaluateDiscussions.text_Grade.getText().trim();

		if (username.isEmpty()) {
			ViewEvaluateDiscussions.label_Status.setText("Enter student username.");
			return;
		}

		if (thread.isEmpty()) {
			ViewEvaluateDiscussions.label_Status.setText("Enter thread.");
			return;
		}

		if (result.isEmpty()) {
			ViewEvaluateDiscussions.label_Status.setText("Evaluate first.");
			return;
		}

		String fullFeedback =
				"Discussion Evaluation\n\n" +
				"Thread: " + thread + "\n\n" +
				result + "\n\n" +
				"Staff Feedback:\n" + feedbackBody + "\n\n" +
				"Final Grade: " + grade;

		Feedback feedback = new Feedback(
				"THREAD_EVALUATION",
				0,
				ViewEvaluateDiscussions.theUser.getUserName(),
				username,
				fullFeedback
		);

		ViewEvaluateDiscussions.theFeedbackDatabase.createFeedback(feedback);
		ViewEvaluateDiscussions.label_Status.setText("Evaluation saved.");
	}
	
	
	protected static void performReturn() {
		guiStaffHome.ViewStaffHome.displayStaffHome(
				ViewEvaluateDiscussions.theStage,
				ViewEvaluateDiscussions.theUser
		);
	}
	
	
	protected static void performQuit() {
		System.exit(0);
	}
}