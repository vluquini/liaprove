package com.lia.liaprove.infrastructure.configs;

import com.lia.liaprove.application.gateways.algorithms.bayesian.BayesianGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.assessment.CertificateGateway;
import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.algorithms.bayesian.BayesianNetworkUseCaseImpl;
import com.lia.liaprove.application.services.assessment.*;
import com.lia.liaprove.application.services.metrics.*;
import com.lia.liaprove.application.services.question.*;
import com.lia.liaprove.application.services.user.*;
import com.lia.liaprove.core.algorithms.bayesian.BayesianConfig;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;
import com.lia.liaprove.core.usecases.assessments.*;
import com.lia.liaprove.core.usecases.metrics.*;
import com.lia.liaprove.core.usecases.question.*;
import com.lia.liaprove.core.usecases.user.admin.UserModerationUseCase;
import com.lia.liaprove.core.usecases.user.users.*;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.VoteMapper;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.VoteJpaRepository;
import com.lia.liaprove.infrastructure.services.metrics.FeedbackGatewayImpl;
import com.lia.liaprove.infrastructure.services.user.PasswordHasherImpl;
import com.lia.liaprove.infrastructure.services.user.UserGatewayImpl;
import com.lia.liaprove.infrastructure.services.metrics.VoteGatewayImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableScheduling
public class AppConfig {

    // Auth
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordHasher passwordHasher(PasswordEncoder passwordEncoder) {
        return new PasswordHasherImpl(passwordEncoder);
    }

    // User Domain
    @Bean
    public UserGateway userGateway(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        return new UserGatewayImpl(userJpaRepository, userMapper);
    }

    @Bean
    public UserFactory userFactory() {
        return new DefaultUserFactory();
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordHasher passwordHasher,
                                               UserFactory userFactory) {
        return new CreateUserUseCaseImpl(userGateway, passwordHasher, userFactory);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(UserGateway userGateway) {
        return new GetUserByIdUseCaseImpl(userGateway);
    }

    @Bean
    public FindUsersUseCase findUsersUseCase(UserGateway userGateway) {
        return new FindUsersUseCaseImpl(userGateway);
    }

    @Bean
    public UpdateUserProfileUseCase updateUserProfileUseCase(UserGateway userGateway) {
        return new UpdateUserProfileUseCaseImpl(userGateway);
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(UserGateway userGateway, PasswordHasher passwordHasher) {
        return new ChangePasswordUseCaseImpl(userGateway, passwordHasher);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserGateway userGateway) {
        return new DeleteUserUseCaseImpl(userGateway);
    }

    @Bean
    public UserModerationUseCase userModerationUseCase(UserGateway userGateway) {
        return new UserModerationUseCaseImpl(userGateway);
    }

    // Question Domain
    @Bean
    public QuestionFactory questionFactory() {
        return new DefaultQuestionFactory();
    }

    @Bean
    public SubmitQuestionUseCase submitQuestionUseCase(QuestionGateway questionGateway, QuestionFactory factory) {
        return new SubmitQuestionUseCaseImpl(questionGateway, factory);
    }

    @Bean
    public PreAnalyzeQuestionUseCase preAnalyzeQuestionUseCase(QuestionPreAnalysisGateway questionPreAnalysisGateway) {
        return new PreAnalyzeQuestionUseCaseImpl(questionPreAnalysisGateway);
    }

    @Bean
    public PrepareQuestionSubmissionUseCase prepareQuestionSubmissionUseCase(QuestionPreAnalysisGateway questionPreAnalysisGateway) {
        return new PrepareQuestionSubmissionUseCaseImpl(questionPreAnalysisGateway);
    }

    @Bean
    public EvaluateVotingResultUseCase evaluateVotingResultUseCase(QuestionGateway questionGateway) {
        return new MockEvaluateVotingResultUseCaseImpl(questionGateway);
    }

    @Bean
    public UpdateQuestionUseCase updateQuestionUseCase(QuestionGateway questionGateway, UserGateway userGateway) {
        return new UpdateQuestionUseCaseImpl(questionGateway, userGateway);
    }

    @Bean
    public ListQuestionsUseCase listQuestionsUseCase(QuestionGateway questionGateway) {
        return new ListQuestionsUseCaseImpl(questionGateway);
    }

    @Bean
    public GetQuestionByIdUseCase getQuestionByIdUseCase(QuestionGateway questionGateway) {
        return new GetQuestionByIdUseCaseImpl(questionGateway);
    }

    @Bean
    public ModerateQuestionUseCase moderateQuestionUseCase(QuestionGateway questionGateway) {
        return new ModerateQuestionUseCaseImpl(questionGateway);
    }

    @Bean
    public GetQuestionVotingDetailsUseCase getQuestionVotingDetailsUseCase(QuestionGateway questionGateway,
                                                                         VoteGateway voteGateway,
                                                                         FeedbackGateway feedbackGateway,
                                                                         UserGateway userGateway) {
        return new GetQuestionVotingDetailsUseCaseImpl(questionGateway, voteGateway, feedbackGateway, userGateway);
    }

    // Metrics Domain - Feedback
    @Bean
    public FeedbackGateway feedbackGateway(FeedbackQuestionJpaRepository feedbackQuestionJpaRepository,
                                           FeedbackQuestionMapper feedbackQuestionMapper,
                                           QuestionMapper questionMapper) {
        return new FeedbackGatewayImpl(feedbackQuestionJpaRepository, feedbackQuestionMapper, questionMapper);
    }

    @Bean
    public SubmitFeedbackOnQuestionUseCase submitFeedbackOnQuestionUseCase(FeedbackGateway feedbackGateway,
                                                                           UserGateway userGateway,
                                                                           QuestionGateway questionGateway) {
        return new SubmitFeedbackOnQuestionUseCaseImpl(feedbackGateway, userGateway, questionGateway);
    }

    @Bean
    public ListFeedbacksForQuestionUseCase listFeedbacksForQuestionUseCase(FeedbackGateway feedbackGateway,
                                                                           QuestionGateway questionGateway) {
        return new ListFeedbacksForQuestionUseCaseImpl(feedbackGateway, questionGateway);
    }

    // Metrics Domain - Vote
    @Bean
    public VoteGateway voteGateway(VoteJpaRepository voteJpaRepository, VoteMapper voteMapper) {
        return new VoteGatewayImpl(voteJpaRepository, voteMapper);
    }

    @Bean
    public CastVoteUseCase castVoteUseCase(UserGateway userGateway, QuestionGateway questionGateway, VoteGateway voteGateway) {
        return new CastVoteUseCaseImpl(userGateway, questionGateway, voteGateway);
    }

    @Bean
    public ListVotesForQuestionUseCase listVotesForQuestionUseCase(VoteGateway voteGateway, QuestionGateway questionGateway) {
        return new ListVotesForQuestionUseCaseImpl(voteGateway, questionGateway);
    }

    @Bean
    public ReactToFeedbackUseCase reactToFeedbackUseCase(FeedbackGateway feedbackGateway, UserGateway userGateway) {
        return new ReactToFeedbackUseCaseImpl(feedbackGateway, userGateway);
    }

    @Bean
    public UpdateFeedbackCommentUseCase updateFeedbackCommentUseCase(FeedbackGateway feedbackGateway) {
        return new UpdateFeedbackCommentUseCaseImpl(feedbackGateway);
    }

    // Assessment Domain
    @Bean
    public GenerateSystemAssessmentUseCase generateSystemAssessmentUseCase(QuestionGateway questionGateway) {
        return new GenerateSystemAssessmentUseCaseImpl(questionGateway);
    }

    @Bean
    public CreatePersonalizedAssessmentUseCase createPersonalizedAssessmentUseCase(AssessmentGateway assessmentGateway,
                                                                                   QuestionGateway questionGateway,
                                                                                   UserGateway userGateway) {
        return new CreatePersonalizedAssessmentUseCaseImpl(assessmentGateway, questionGateway, userGateway);
    }

    @Bean
    public BayesianConfig bayesianConfig() {
        // Configuração padrão para o MVP
        return BayesianConfig.defaults();
    }

    @Bean
    public BayesianNetworkUseCase bayesianNetworkUseCase(BayesianGateway bayesianGateway, BayesianConfig bayesianConfig) {
        return new BayesianNetworkUseCaseImpl(bayesianGateway, bayesianConfig);
    }

    @Bean
    public SuggestQuestionsForAssessmentUseCase suggestQuestionsForAssessmentUseCase(BayesianNetworkUseCase bayesianNetworkUseCase,
                                                                                     UserGateway userGateway) {
        return new SuggestQuestionsForAssessmentUseCaseImpl(bayesianNetworkUseCase, userGateway);
    }

    @Bean
    public StartNewAssessmentUseCase startNewAssessmentUseCase(AssessmentGateway assessmentGateway,
                                                               AssessmentAttemptGateway assessmentAttemptGateway,
                                                               UserGateway userGateway,
                                                               GenerateSystemAssessmentUseCase generateSystemAssessmentUseCase) {
        return new StartNewAssessmentUseCaseImpl(assessmentGateway, assessmentAttemptGateway, userGateway, generateSystemAssessmentUseCase);
    }

    @Bean
    public IssueCertificateUseCase issueCertificateUseCase(CertificateGateway certificateGateway) {
        return new IssueCertificateUseCaseImpl(certificateGateway, "https://liaprove.com/certificates");
    }

    @Bean
    public SubmitAssessmentUseCase submitAssessmentUseCase(AssessmentAttemptGateway assessmentAttemptGateway,
                                                           IssueCertificateUseCase issueCertificateUseCase) {
        return new SubmitAssessmentUseCaseImpl(assessmentAttemptGateway, issueCertificateUseCase);
    }

    @Bean
    public EvaluateAssessmentAttemptUseCase finalizeAssessmentAttemptUseCase(AssessmentAttemptGateway assessmentAttemptGateway,
                                                                             UserGateway userGateway) {
        return new EvaluateAssessmentAttemptUseCaseImpl(assessmentAttemptGateway, userGateway);
    }
}
