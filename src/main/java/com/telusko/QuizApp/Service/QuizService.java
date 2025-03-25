package com.telusko.QuizApp.Service;

import com.telusko.QuizApp.Dao.QuestionDao;
import com.telusko.QuizApp.Dao.QuizDao;
import com.telusko.QuizApp.Model.Question;
import com.telusko.QuizApp.Model.QuestionWrapper;
import com.telusko.QuizApp.Model.Quiz;
import com.telusko.QuizApp.Model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<Question> questions = questionDao.findRandomQuestionsByCategory(category,numQ);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz = quizDao.findById(id);
        List<Question> questionsFromDb =quiz.get().getQuestions();
        List<QuestionWrapper> questionsForUser = new ArrayList<>();
        for (Question question : questionsFromDb){
            QuestionWrapper questionWrapper = new QuestionWrapper(
                    question.getId(),
                    question.getQuestionTitle(),
                    question.getOption1(),
                    question.getOption2(),
                    question.getOption3(),
                    question.getOption4()
                    );
            questionsForUser.add(questionWrapper);
        }

        return new ResponseEntity<>(questionsForUser,HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Quiz quiz = quizDao.findById(id).get();
        List<Question> questions = quiz.getQuestions();
        int rightAnswer = 0;
        int i = 0;
        for (Response response : responses){
            if(response.getResponse().equals(questions.get(i).getRightAnswer())){
                rightAnswer++;
            }
            i++;
        }
        return new ResponseEntity<>(rightAnswer,HttpStatus.OK);
    }
}
